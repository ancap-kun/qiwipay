package ru.ancap.pay.plugin.plugin;

import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.Raw;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.command.api.commands.operator.exclusive.Exclusive;
import ru.ancap.framework.command.api.commands.operator.exclusive.OP;
import ru.ancap.framework.plugin.api.AncapBukkit;

public class MainInput extends CommandTarget {
    
    public MainInput(CommandOperator authors) {
        super(new Delegate(
            new Raw(authors),
            new SubCommand(
                new StringDelegatePattern("test"),
                new Exclusive(
                    new OP(),
                    new Delegate(
                        new SubCommand(
                            new StringDelegatePattern("command"),
                            new LeftLine(dispatch -> AncapBukkit.sendConsoleCommand(dispatch.leftLine()))
                        )
                    )
                )
            )
        ));
    }
    
}
