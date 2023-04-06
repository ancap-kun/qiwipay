/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package ru.ancap.pay.plugin.promocode.command;

import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;
import ru.ancap.framework.command.api.commands.operator.arguments.Accept;
import ru.ancap.framework.command.api.commands.operator.arguments.Argument;
import ru.ancap.framework.command.api.commands.operator.arguments.Arguments;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.DoubleExtractor;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.NumberExtractor;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.Self;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.Raw;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.communicate.Communicator;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.PromocodeType;
import ru.ancap.pay.plugin.promocode.exception.IllegalPromotionalCodeTypeException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsExpiredException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsSpentException;
import ru.ancap.pay.plugin.promocode.mapper.PromocodeExtractor;
import ru.ancap.pay.plugin.promocode.mapper.PromocodeTypeTransformer;
import ru.ancap.pay.plugin.speaker.PaySpeaker;

public class PromocodeOperator extends CommandTarget {

    public PromocodeOperator(CommandOperator authors) {
        super(new Delegate(
            new Raw(authors),
            new SubCommand(
                new StringDelegatePattern("create", "new"),
                new Arguments(
                    new Accept(
                        new Argument("name", new Self()),
                        new Argument("type", new PromocodeTypeTransformer()),
                        new Argument("value", new DoubleExtractor()),
                        new Argument("usages", new NumberExtractor()),
                        new Argument("expiration", new NumberExtractor())
                    ),
                    dispatch -> {
                        if (!dispatch.source().sender().isOp()) return;
                        String name = dispatch.arguments().get("name", String.class);
                        PromocodeType type = dispatch.arguments().get("type", PromocodeType.class);
                        double value = dispatch.arguments().get("value", Double.class);
                        long usages = dispatch.arguments().get("usages", Long.class);
                        long expiration = dispatch.arguments().get("expiration", Long.class);
                        
                        PromocodeAPI.create(name, type, value, usages, expiration);
                        new PaySpeaker(dispatch.source().sender()).sendPromocodeCreated(name, type, value, usages, expiration);
                    }
                )
            ),
            new SubCommand(
                new StringDelegatePattern("remove", "delete"),
                new Arguments(
                    new Accept(
                        new Argument("promocode", new PromocodeExtractor())
                    ),
                    dispatch -> {
                        if (!dispatch.source().sender().isOp()) return;
                        PromocodeAPI promocode = dispatch.arguments().get("promocode", PromocodeAPI.class);
                        
                        promocode.disable();
                    }
                )
            ),
            new SubCommand(
                new StringDelegatePattern("use"),
                new Arguments(
                    new Accept(
                        new Argument("promocode", new PromocodeExtractor())
                    ),
                    dispatch -> {
                        PromocodeAPI promocode = dispatch.arguments().get("promocode", PromocodeAPI.class);
                        try {
                            promocode.use(dispatch.source().sender().getName());
                            new PaySpeaker(dispatch.source().sender()).sendPromocodeSuccessfullyUsed(
                                promocode.getName(),
                                promocode.getReward()
                            );
                        } catch (PromotionalCodeIsSpentException exception) {
                            new Communicator(dispatch.source().sender()).send(new LAPIMessage(AncapPay.class, "error.promocode.spent"));
                        } catch (PromotionalCodeIsAlreadyUsedException exception) {
                            new Communicator(dispatch.source().sender()).send(new LAPIMessage(AncapPay.class, "error.promocode.already-used"));
                        } catch (IllegalPromotionalCodeTypeException exception) {
                            new Communicator(dispatch.source().sender()).send(new LAPIMessage(AncapPay.class, "error.promocode.illegal-type"));
                        } catch (PromotionalCodeIsExpiredException e) {
                            new Communicator(dispatch.source().sender()).send(new LAPIMessage(AncapPay.class, "error.promocode.expired"));
                        }
                    }
                )
            )
        ));
    }

}

