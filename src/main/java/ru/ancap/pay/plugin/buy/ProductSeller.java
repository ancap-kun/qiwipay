package ru.ancap.pay.plugin.buy;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.arguments.Accept;
import ru.ancap.framework.command.api.commands.operator.arguments.Argument;
import ru.ancap.framework.command.api.commands.operator.arguments.Arguments;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.Extractor;
import ru.ancap.framework.communicate.Communicator;
import ru.ancap.framework.communicate.message.Message;
import ru.ancap.framework.communicate.replacement.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.framework.plugin.api.AncapBukkit;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.player.PayPlayer;

public class ProductSeller extends CommandTarget {
    public ProductSeller() {
        super(
                new Arguments(
                        new Accept(
                                new Argument("product", new Extractor<>(Product.class, Product::get))
                        ),
                        dispatch -> {
                            Product product = dispatch.arguments().get("product", Product.class);
                            PayPlayer payPlayer = PayPlayer.get(dispatch.source().sender().getName());
                            Communicator communicator = new Communicator(dispatch.source().sender());
                            if (payPlayer.balance() < product.price()) {
                                communicator.send(new LAPIMessage(AncapPay.class, "error.not-enough-money"));
                                return;
                            }
                            payPlayer.balance(payPlayer.balance() - product.price());
                            AncapBukkit.sendConsoleCommand(product.giveCommand().replace("%PLAYER%", dispatch.source().sender().getName()));
                            communicator.send(new LAPIMessage(
                                    AncapPay.class, "product-bought",
                                    new Placeholder("product", new Message(product.name()))
                            ));
                            dispatch.source().audience().playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.AMBIENT, 1f, 1f));
                        }
                )
        );
    }
}
