package ru.ancap.pay.plugin.balance;

import ru.ancap.communicate.Communicator;
import ru.ancap.communicate.message.Message;
import ru.ancap.communicate.replacement.Placeholder;
import ru.ancap.framework.command.api.commands.object.event.CommandDispatch;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.pay.plugin.AncapPay;
import ru.ancap.pay.plugin.player.PayPlayer;

import java.text.DecimalFormat;

public class WalletRepresenter implements CommandOperator {
    
    private final DecimalFormat doubleFormat = new DecimalFormat("#.###");
    
    @Override
    public void on(CommandDispatch dispatch) {
        PayPlayer player = PayPlayer.get(dispatch.source().sender().getName());
        Communicator communicator = new Communicator(dispatch.source().sender());
        communicator.send(new LAPIMessage(
                AncapPay.class, "wallet",
                new Placeholder("amount", new Message(this.doubleFormat.format(player.balance())))
        ));
    }
    
}
