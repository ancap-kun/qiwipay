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

import ru.ancap.framework.api.additional.LAPICommunicator;
import ru.ancap.framework.api.command.commands.CommandTarget;
import ru.ancap.framework.api.command.commands.operator.arguments.Accept;
import ru.ancap.framework.api.command.commands.operator.arguments.Argument;
import ru.ancap.framework.api.command.commands.operator.arguments.Arguments;
import ru.ancap.framework.api.command.commands.operator.delegator.CommandDelegator;
import ru.ancap.framework.api.command.commands.operator.delegator.subcommand.Raw;
import ru.ancap.framework.api.command.commands.operator.delegator.subcommand.SubCommand;
import ru.ancap.framework.api.command.commands.operator.delegator.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.api.command.commands.transformer.basic.DoubleTransformer;
import ru.ancap.framework.api.command.commands.transformer.basic.NumberTransformer;
import ru.ancap.framework.api.command.commands.transformer.basic.Self;
import ru.ancap.framework.api.command.util.TypeNameProvider;
import ru.ancap.pay.plugin.plugin.AncapPay;
import ru.ancap.pay.plugin.promocode.PromocodeAPI;
import ru.ancap.pay.plugin.promocode.PromocodeType;
import ru.ancap.pay.plugin.promocode.exception.IllegalPromotionalCodeTypeException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsAlreadyUsedException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsExpiredException;
import ru.ancap.pay.plugin.promocode.exception.PromotionalCodeIsSpentException;
import ru.ancap.pay.plugin.promocode.mapper.PromocodeTransformer;
import ru.ancap.pay.plugin.promocode.mapper.PromocodeTypeTransformer;
import ru.ancap.pay.plugin.speaker.PaySpeaker;

public class PromoCodeCommandOperator extends CommandTarget {
    
    public PromoCodeCommandOperator(TypeNameProvider typeNameProvider) {
        super(
                new CommandDelegator(
                        new Raw(dispatch -> { new PaySpeaker(dispatch.sender()).sendAuthors(); }),
                        new SubCommand(
                                new StringDelegatePattern("create", "new"),
                                new Arguments(
                                        typeNameProvider,
                                        new Accept(
                                                new Argument("name", new Self()),
                                                new Argument("type", new PromocodeTypeTransformer()),
                                                new Argument("value", new DoubleTransformer()),
                                                new Argument("usages", new NumberTransformer()),
                                                new Argument("expiration", new NumberTransformer())
                                        ),
                                        dispatch -> {
                                            if (!dispatch.sender().isOp()) return;
                                            String name        = dispatch.arguments().get("name", String.class);
                                            PromocodeType type = dispatch.arguments().get("type", PromocodeType.class);
                                            double value       = dispatch.arguments().get("value", Double.class);
                                            long usages        = dispatch.arguments().get("usages", Long.class);
                                            long expiration    = dispatch.arguments().get("expiration", Long.class);
                                            
                                            PromocodeAPI.create(name, type, value, usages, expiration);
                                            new PaySpeaker(dispatch.sender()).sendPromocodeCreated(name, type, value, usages, expiration);
                                        }
                                )
                        ),
                        new SubCommand(
                                new StringDelegatePattern("remove", "delete"),
                                new Arguments(
                                        typeNameProvider,
                                        new Accept(
                                                new Argument("promocode", new PromocodeTransformer())
                                        ),
                                        dispatch -> {
                                            if (!dispatch.sender().isOp()) return;
                                            PromocodeAPI promocode = dispatch.arguments().get("promocode", PromocodeAPI.class);
                                            
                                            promocode.disable();
                                        }
                                )
                        ),
                        new SubCommand(
                                new StringDelegatePattern("use"),
                                new Arguments(
                                        typeNameProvider,
                                        new Accept(
                                                new Argument("promocode", new PromocodeTransformer())
                                        ),
                                        dispatch -> {
                                            PromocodeAPI promocode = dispatch.arguments().get("promocode", PromocodeAPI.class);
                                            try {
                                                promocode.use(dispatch.sender().getName());
                                                new PaySpeaker(dispatch.sender()).sendPromocodeSuccessfullyUsed(
                                                        promocode.getName(), 
                                                        promocode.getReward()
                                                );
                                            } catch (PromotionalCodeIsSpentException exception) {
                                                new LAPICommunicator(dispatch.sender()).send(AncapPay.MESSAGE_DOMAIN+"error.promocode.spent");
                                            } catch (PromotionalCodeIsAlreadyUsedException exception) {
                                                new LAPICommunicator(dispatch.sender()).send(AncapPay.MESSAGE_DOMAIN+"error.promocode.already-used");
                                            } catch (IllegalPromotionalCodeTypeException exception) {
                                                new LAPICommunicator(dispatch.sender()).send(AncapPay.MESSAGE_DOMAIN+"error.promocode.illegal-type");
                                            } catch (PromotionalCodeIsExpiredException e) {
                                                new LAPICommunicator(dispatch.sender()).send(AncapPay.MESSAGE_DOMAIN+"error.promocode.expired");
                                            }
                                        }
                                )
                        )
                )
        );
    }
    
}

