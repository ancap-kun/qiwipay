/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package ru.ancap.pay.plugin.donate;

import org.bukkit.command.CommandSender;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.arguments.Accept;
import ru.ancap.framework.command.api.commands.operator.arguments.Argument;
import ru.ancap.framework.command.api.commands.operator.arguments.Arguments;
import ru.ancap.framework.command.api.commands.operator.arguments.bundle.ArgumentsBundle;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.NumberExtractor;
import ru.ancap.pay.plugin.config.QiwiConfig;
import ru.ancap.pay.plugin.player.PayPlayer;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.mapper.PromocodeExtractor;
import ru.ancap.pay.plugin.qiwi.QiwiModule;
import ru.ancap.pay.plugin.speaker.PaySpeaker;
import ru.ancap.pay.plugin.transaction.TransactionAPI;

public class DonateAcceptor extends CommandTarget {

    public DonateAcceptor(QiwiModule qiwiModule) {
        super(new Arguments(
            new Accept(
                new Argument("amount", new NumberExtractor()),
                new Argument("promocode", new PromocodeExtractor(), true)
            ),
            dispatch -> {
                CommandSender sender = dispatch.source().sender();
                ArgumentsBundle bundle = dispatch.arguments();
                Long amount = bundle.get("amount", Long.class);
                PromocodeAPI promocode = bundle.get("promocode", PromocodeAPI.class);
                qiwiModule.generateBill(dispatch.source().sender().getName(), amount, () -> {
                    double moneyToGive = amount * QiwiConfig.loaded().getDouble("payments.multiplication");
                    moneyToGive = PromocodeAPI.applyBonus(promocode, moneyToGive);
                    QiwiPayBukkit.sendConsoleCommand(QiwiConfig.loaded().getString("payments.command")
                        .replace("%AMOUNT%", "" + (long) moneyToGive)
                        .replace("%PLAYER%", sender.getName())
                    );
                    TransactionAPI transaction = TransactionAPI.create(
                        System.currentTimeMillis(),
                        amount,
                        sender.getName(),
                        promocode != null ? promocode.getName() : null
                    );
                    PayPlayer.get(sender.getName()).saveDonate(transaction);
                    new PaySpeaker(sender).sendBalanceFill(moneyToGive);
                    AncapDebug.debug("onPaid", amount, sender.getName(), promocode, QiwiConfig.loaded().getString("payments.command"), moneyToGive);
                });
                new PaySpeaker(sender).sendPayUrl(qiwiModule.payUrl(sender.getName()));
            })
        );
    }

}

