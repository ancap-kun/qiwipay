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
import ru.ancap.framework.api.LAPI;
import ru.ancap.pay.plugin.config.QiwiConfig;
import ru.ancap.pay.plugin.plugin.AncapPay;
import ru.ancap.util.AncapDebug;

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

    /**
     * @param name name of player
     */
    @SneakyThrows
    public void generateBill(String name, long amount, Runnable onPaid) {
        if (bills.containsKey(name)) client.cancelBill(bills.get(name).getBillId());
        String billId = UUID.randomUUID().toString();
        CreateBillInfo billInfo = new CreateBillInfo(
                billId,
                new MoneyAmount(
                        BigDecimal.valueOf(amount),
                        Currency.getInstance(QiwiConfig.loaded().getString("acquiring.qiwi.currency"))
                ),
                LAPI.localized(AncapPay.MESSAGE_DOMAIN+"bill.comment", name)
                        .replace("%PLAYER%", name),
                ZonedDateTime.now().plusMinutes(QiwiConfig.loaded().getLong("acquiring.qiwi.bill-expiration-time")),
                new Customer(
                        QiwiConfig.loaded().getString("acquiring.qiwi.email"),
                        UUID.randomUUID().toString(),
                        QiwiConfig.loaded().getString("acquiring.qiwi.phone")
                ),
                QiwiConfig.loaded().getString("acquiring.qiwi.phone")
        );
        bills.put(name, client.createBill(billInfo));
        this.runChecks(billId, onPaid);
    }

    private void runChecks(String billId, Runnable onPaid) {
        new Thread(() -> {
            int skippedChecks = 0;
            while (true) {
                BillResponse response = client.getBillInfo(billId);
                boolean breakFlag = false;
                switch (response.getStatus().getValue()) {
                    case PAID -> {
                        breakFlag = true;
                        onPaid.run();
                    }
                    case REJECTED, EXPIRED -> {
                        breakFlag = true;
                    }
                    case WAITING -> {
                        AncapDebug.debug("Waiting for paid...");
                        skippedChecks++;
                        LockSupport.parkUntil(skippedChecks < 120 ? System.currentTimeMillis()+5000 : System.currentTimeMillis()+30000);
                    }
                }
                if (breakFlag) break;
            }
        }).start();
    }

    public String payUrl(String name) {
        return this.bills.get(name).getPayUrl();
    }
}

