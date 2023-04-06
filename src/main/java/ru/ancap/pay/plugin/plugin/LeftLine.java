package ru.ancap.pay.plugin.plugin;

import lombok.AllArgsConstructor;
import ru.ancap.framework.command.api.commands.object.event.CommandDispatch;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public class LeftLine implements CommandOperator {

    private final Consumer<LeftLine.Dispatch> dispatchConsumer;

    @Override
    public void on(CommandDispatch dispatch) {
        List<String> collected = new ArrayList<>();
        var command = dispatch.command();
        while (!command.isRaw()) {
            collected.add(command.nextArgument());
            command = command.withoutArgument();
        }
        String result = String.join(" ", collected);
        this.dispatchConsumer.accept(new Dispatch(dispatch, result));
    }

    @AllArgsConstructor
    public static class Dispatch {
        
        private final CommandDispatch dispatch;
        private final String leftLine;
        
        public CommandDispatch dispatch() { return this.dispatch; }
        public String leftLine() { return this.leftLine; }
        
    }
    
}
