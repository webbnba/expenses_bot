package ru.bezborodov.expensesbot.commands;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.repository.ExpenseRepository;
import ru.bezborodov.expensesbot.repository.UserRepository;

@Getter
public abstract class ExpenseBotCommand implements IBotCommand {
    @Autowired
    protected ExpenseRepository expenseRepository;
    @Autowired
    protected UserRepository userRepository;

    protected final Logger log = LoggerFactory.getLogger(ExpenseBotCommand.class);
    public static final String COMMAND_INIT_CHARACTER = "/";
    private static final int COMMAND_MAX_LENGTH = 32;
    private final String commandIdentifier;
    private final String description;

    protected ExpenseBotCommand(String commandIdentifier, String description) {
        if (commandIdentifier != null && !commandIdentifier.isEmpty()) {
            if (commandIdentifier.startsWith(COMMAND_INIT_CHARACTER)) {
                commandIdentifier = commandIdentifier.substring(1);
            }

            if (commandIdentifier.length() + 1 > COMMAND_MAX_LENGTH) {
                throw new IllegalArgumentException("commandIdentifier cannot be longer than 32 (including /)");
            } else {
                this.commandIdentifier = commandIdentifier.toLowerCase();
                this.description = description;
            }
        } else {
            throw new IllegalArgumentException("commandIdentifier for command cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return "/" + this.getCommandIdentifier() + " - " + this.getDescription();
    }

    public void processMessage(AbsSender absSender, Update update) {
        this.execute(absSender, update);
    }

    public abstract void execute(AbsSender absSender, Update update);
}

