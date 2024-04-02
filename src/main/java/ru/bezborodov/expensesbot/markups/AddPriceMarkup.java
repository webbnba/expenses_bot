package ru.bezborodov.expensesbot.markups;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.model.Expense;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

@Component
public class AddPriceMarkup extends BotMarkup{
    private static final String MARKUP_IDENTIFIER = "Add price";
    public static final InlineKeyboardMarkup MARKUP = BotUtils.getPriceMarkup(MARKUP_IDENTIFIER);
    public AddPriceMarkup() {
        super(MARKUP_IDENTIFIER);
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        Message message = (Message) update.getCallbackQuery().getMessage();
        String callback = update.getCallbackQuery().getData();
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        Expense expense = ExpensesBot.EXPENSE;
        if (expense == null) {
            BotUtils.deleteMessage(absSender, chatId, messageId);
            return;
        }
        String button = callback.substring(getMarkupIdentifier().length() + 1);
        if (button.equals(BotUtils.BACK)) {
            String textToSend = "Category: " + expense.getCategory() + "\nPlease choose category";
            BotUtils.updateMessage(absSender, chatId, messageId, textToSend, AddCategoryMarkup.MARKUP);
        }
    }
}
