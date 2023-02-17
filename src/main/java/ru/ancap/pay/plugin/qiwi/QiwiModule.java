/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package ru.ancap.pay.plugin.qiwi;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.ancap.framework.language.LAPI;
import ru.ancap.framework.language.additional.LAPIDomain;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.config.QiwiConfig;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

@RequiredArgsConstructor
public class QiwiModule {
    private final Map<String, BillResponse> bills = new HashMap<>();
    private final BillPaymentClient client;

    @SneakyThrows
    public void generateBill(String name, long amount, Runnable onPaid) {
        if (this.bills.containsKey(name)) this.client.cancelBill(this.bills.get(name).getBillId());
        String billId = UUID.randomUUID().toString();
        CreateBillInfo billInfo = new CreateBillInfo(
                billId,
                new MoneyAmount(
                        BigDecimal.valueOf(amount),
                        Currency.getInstance(QiwiConfig.loaded().getString("acquiring.qiwi.currency"))
                ),
                LAPI.localized(LAPIDomain.of(AncapPay.class, "bill.comment"), name)
                        .replace("%PLAYER%", name),
                ZonedDateTime.now().plusMinutes(QiwiConfig.loaded().getLong("acquiring.qiwi.bill-expiration-time")),
                new Customer(
                        "globalist@copro.org",
                        UUID.randomUUID().toString(),
                        "14884206969"
                ),
                QiwiConfig.loaded().getString("acquiring.qiwi.phone")
        );
        this.bills.put(name, this.client.createBill(billInfo));
        this.runChecks(billId, onPaid);
    }

    private void runChecks(String billId, Runnable onPaid) {
        new Thread(() -> {
            int skippedChecks = 0;
            while (true) {
                BillResponse response = this.client.getBillInfo(billId);
                boolean breakFlag = false;
                switch (response.getStatus().getValue()) {
                    case PAID:
                        breakFlag = true;
                        onPaid.run();
                        break;
                    case REJECTED: 
                    case EXPIRED:
                        breakFlag = true;
                        break;
                    case WAITING:
                        skippedChecks++;
                        LockSupport.parkUntil(skippedChecks < 120 ? System.currentTimeMillis()+5000 : System.currentTimeMillis()+30000);
                        break;
                }
                if (breakFlag) break;
            }
        }).start();
    }

    public String payUrl(String name) {
        return this.bills.get(name).getPayUrl();
    }
}

