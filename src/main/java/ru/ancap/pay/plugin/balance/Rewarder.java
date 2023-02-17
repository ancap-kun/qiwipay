package ru.ancap.pay.plugin.balance;

import org.bukkit.Bukkit;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.arguments.Accept;
import ru.ancap.framework.command.api.commands.operator.arguments.Argument;
import ru.ancap.framework.command.api.commands.operator.arguments.Arguments;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.Extractor;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.NumberExtractor;
import ru.ancap.framework.command.api.event.classic.NotEnoughPermissionsEvent;
import ru.ancap.framework.communicate.Communicator;
import ru.ancap.framework.communicate.replacement.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.player.PayPlayer;

public class Rewarder extends CommandTarget {
    public Rewarder() {
        super(new Arguments(
                new Accept(
                        new Argument("player", new Extractor<>(PayPlayer.class, PayPlayer::get)),
                        new Argument("reward", new NumberExtractor())
                ), 
                dispatch -> {
                    if (!dispatch.source().sender().isOp()) {
                        Bukkit.getPluginManager().callEvent(new NotEnoughPermissionsEvent(dispatch.source().sender(), "OP"));
                        return;
                    }
                    PayPlayer player = dispatch.arguments().get("player", PayPlayer.class);
                    long donated = dispatch.arguments().get("reward", Long.class);
                    player.balance(player.balance() + donated);
                    new Communicator(dispatch.source().sender()).send(new LAPIMessage(
                            AncapPay.class, "wallet-filled",
                            new Placeholder("amount", donated)
                    ));
                }
        ));
    }
}
