package ru.ancap.pay.plugin.speaker;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ancap.framework.api.LAPI;
import ru.ancap.framework.api.additional.LAPICommunicator;
import ru.ancap.pay.plugin.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeType;
import ru.ancap.util.Replacement;

@RequiredArgsConstructor
public class PaySpeaker {
    
    private final CommandSender receiver;
    
    public void sendAuthors() {
        new LAPICommunicator(this.receiver).send(
                AncapPay.MESSAGE_DOMAIN+"plugin-info",
                new Replacement("%VERSION%", AncapPay.INSTANCE.getDescription().getVersion()),
                new Replacement("%AUTHORS%", AncapPay.INSTANCE.getDescription().getAuthors().stream().reduce((s1, s2) -> s1 + " ," +s2))
        );
    }

    public void sendPayUrl(String payUrl) {
        String miniMessageClickIntegration = "<click:open_url:%URL%>%URL_CLICK_MESSAGE%</click>"
                .replace("%URL%", payUrl)
                .replace("%URL_CLICK_MESSAGE%", LAPI.localized(
                        AncapPay.MESSAGE_DOMAIN+"bill.pay-url-click",
                        this.receiver.getName()
                ));
        new LAPICommunicator(receiver).send(
                AncapPay.MESSAGE_DOMAIN+"bill.pay-url",
                new Replacement(
                        "%CLICK%",
                        miniMessageClickIntegration
                )
        );
        if (receiver instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
        }
    }

    public void sendBalanceFill(double moneyToGive) {
        new LAPICommunicator(this.receiver).send(
                AncapPay.MESSAGE_DOMAIN+"balance-fill",
                new Replacement("%AMOUNT%", moneyToGive)
        );
    }

    public void sendPromocodeCreated(String name, PromocodeType type, double value, long usages, long expiration) {
        new LAPICommunicator(this.receiver).send(
                AncapPay.MESSAGE_DOMAIN+"promocode-created",
                new Replacement("%NAME%", name),
                new Replacement("%VALUE%", value),
                new Replacement("%TYPE%", type),
                new Replacement("%USAGES%", usages),
                new Replacement("%EXPIRATION%", (expiration - System.currentTimeMillis()) / 3600000L)
        );
    }

    public void sendPromocodeSuccessfullyUsed(String name, double reward) {
        new LAPICommunicator(this.receiver).send(
                AncapPay.MESSAGE_DOMAIN+"fixed-promocode-used",
                new Replacement("%NAME%", name),
                new Replacement("%REWARD%", reward)
        );
    }
}
