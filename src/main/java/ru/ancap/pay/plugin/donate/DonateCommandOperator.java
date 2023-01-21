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
import ru.ancap.framework.api.command.commands.CommandTarget;
import ru.ancap.framework.api.command.commands.operator.arguments.Accept;
import ru.ancap.framework.api.command.commands.operator.arguments.Argument;
import ru.ancap.framework.api.command.commands.operator.arguments.Arguments;
import ru.ancap.framework.api.command.commands.operator.arguments.bundle.ArgumentsBundle;
import ru.ancap.framework.api.command.commands.operator.speaking.Adviser;
import ru.ancap.framework.api.command.commands.transformer.basic.NumberTransformer;
import ru.ancap.framework.api.command.util.TypeNameProvider;
import ru.ancap.framework.api.plugin.plugins.AncapBukkit;
import ru.ancap.framework.api.plugin.plugins.util.LAPIAdviceProvider;
import ru.ancap.pay.plugin.config.QiwiConfig;
import ru.ancap.pay.plugin.player.PayPlayer;
import ru.ancap.pay.plugin.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.mapper.PromocodeTransformer;
import ru.ancap.pay.plugin.qiwi.QiwiModule;
import ru.ancap.pay.plugin.speaker.PaySpeaker;
import ru.ancap.pay.plugin.transaction.TransactionAPI;

public class DonateCommandOperator extends CommandTarget {

    public DonateCommandOperator(TypeNameProvider typeNameProvider, QiwiModule qiwiModule) {
        super(
                new Arguments(
                        new Adviser(new LAPIAdviceProvider(AncapPay.MESSAGE_DOMAIN+"donate-usage")),
                        typeNameProvider,
                        new Accept(
                                new Argument("amount", new NumberTransformer()),
                                new Argument("promocode", new PromocodeTransformer(), true)
                        ),
                        dispatch -> {
                            CommandSender sender = dispatch.sender();
                            ArgumentsBundle bundle = dispatch.arguments();
                            Long amount = bundle.get("amount", Long.class);
                            PromocodeAPI promocode = bundle.get("promocode", PromocodeAPI.class);
                            qiwiModule.generateBill(dispatch.sender().getName(), amount, () -> {
                                double moneyToGive = amount * QiwiConfig.loaded().getDouble("acquiring.qiwi.multiplication");
                                moneyToGive = PromocodeAPI.applyBonus(promocode, moneyToGive);
                                AncapBukkit.sendConsoleCommand(
                                        QiwiConfig.loaded().getString("acquiring.qiwi.command")
                                                .replace("%MONEY%", "" + (long) moneyToGive)
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
                            });
                            new PaySpeaker(sender).sendPayUrl(qiwiModule.payUrl(sender.getName()));
                        }
                )
        );
    }
    
}

