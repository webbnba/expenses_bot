package ru.bezborodov.expensesbot.markups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.repository.ExpenseRepository;

public abstract class BotMarkup {

    @Autowired
    protected ExpenseRepository expenseRepository;
    protected Logger log;
    private final int IDENTIFIER_MAX_LENGTH = 32;
    private final String markupIdentifier;

    protected BotMarkup(String markupIdentifier) {
        if (markupIdentifier != null && !markupIdentifier.isEmpty()) {
            if (markupIdentifier.length() > IDENTIFIER_MAX_LENGTH) {
                throw new IllegalArgumentException("Markup identifier cannot be longer than 32 characters");
            } else {
                this.markupIdentifier = markupIdentifier;
                this.log = LoggerFactory.getLogger(BotMarkup.class);
            }
        } else {
            throw new IllegalArgumentException("Markup identifier cannot be null or empty");
        }
    }

    public String getMarkupIdentifier() {
        return this.markupIdentifier;
    }

    public void processCallback(AbsSender absSender, Update update) {
        this.execute(absSender, update);
    }

    public abstract void execute(AbsSender absSender, Update update);
}

