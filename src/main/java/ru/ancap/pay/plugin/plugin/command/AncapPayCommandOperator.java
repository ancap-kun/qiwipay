package ru.ancap.pay.plugin.plugin.command;

import ru.ancap.framework.api.command.commands.command.event.CommandDispatch;
import ru.ancap.framework.api.command.commands.command.executor.CommandOperator;
import ru.ancap.pay.plugin.speaker.PaySpeaker;

public class AncapPayCommandOperator implements CommandOperator {

    @Override
    public void on(CommandDispatch commandDispatch) {
        new PaySpeaker(commandDispatch.sender()).sendAuthors();
    }
    
}
