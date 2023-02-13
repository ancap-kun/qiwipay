package ru.ancap.pay.plugin.speaker;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ancap.communicate.Communicator;
import ru.ancap.communicate.message.Message;
import ru.ancap.communicate.replacement.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeType;

@RequiredArgsConstructor
public class PaySpeaker {
    
    private final CommandSender receiver;

    public void sendPayUrl(String payUrl) {
        new Communicator(this.receiver).send(new LAPIMessage(
                AncapPay.class, "bill.pay-url",
                new Placeholder("click", new Message(
                        "<click:open_url:%URL%>%URL_CLICK_MESSAGE%</click>", 
                        new Placeholder("url", payUrl),
                        new Placeholder("url_click_message", new LAPIMessage(AncapPay.class, "bill.pay-url-click"))
                ))
        ));
        if (this.receiver instanceof Player) {
            Player player = (Player) receiver; 
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
        }
    }

    public void sendBalanceFill(double moneyToGive) {
        new Communicator(this.receiver).send(new LAPIMessage(
                AncapPay.class, "balance-fill",
                new Placeholder("amount", moneyToGive)
        ));
    }

    public void sendPromocodeCreated(String name, PromocodeType type, double value, long usages, long expiration) {
        new Communicator(this.receiver).send(new LAPIMessage(
                AncapPay.class, "promocode-created",
                new Placeholder("NAME", name),
                new Placeholder("VALUE", value),
                new Placeholder("TYPE", type),
                new Placeholder("USAGES", usages),
                new Placeholder("EXPIRATION", (expiration - System.currentTimeMillis()) / 3600000L)
        ));
    }

    public void sendPromocodeSuccessfullyUsed(String name, double reward) {
        new Communicator(this.receiver).send(new LAPIMessage(
                AncapPay.class, "fixed-promocode-used",
                new Placeholder("NAME", name),
                new Placeholder("REWARD", reward)
        ));
    }
}
