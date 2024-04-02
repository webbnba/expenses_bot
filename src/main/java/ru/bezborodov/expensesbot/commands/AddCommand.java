package ru.bezborodov.expensesbot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.ExpensesBotApplication;
import ru.bezborodov.expensesbot.markups.AddCategoryMarkup;
import ru.bezborodov.expensesbot.model.Expense;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

@Component
public class AddCommand extends ExpenseBotCommand{

    public AddCommand() {
        super("add", "add new expense");
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        long chatId = update.getMessage().getChatId();
        if(ExpensesBot.CURRENT_BOT_MESSAGE != null) {  // if there is unfinished process
            BotUtils.deleteMessage(absSender, ExpensesBot.CURRENT_BOT_MESSAGE.getChatId(),
                    ExpensesBot.CURRENT_BOT_MESSAGE.getMessageId());
        }
        BotUtils.sendMessage(absSender, chatId, "Please choose expense category", AddCategoryMarkup.MARKUP);
        ExpensesBot.EXPENSE = new Expense();
    }
}
