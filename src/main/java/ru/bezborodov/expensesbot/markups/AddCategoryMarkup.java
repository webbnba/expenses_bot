package ru.bezborodov.expensesbot.markups;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.model.Expense;
import ru.bezborodov.expensesbot.model.ExpenseCategory;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AddCategoryMarkup extends BotMarkup {

    private final Set<String> categorySet;
    private static final String MARKUP_IDENTIFIER = "Add category";
    public static final InlineKeyboardMarkup MARKUP = BotUtils.getCategoryMarkup(MARKUP_IDENTIFIER, false);

    public AddCategoryMarkup() {
        super(MARKUP_IDENTIFIER);
        categorySet = new HashSet<>();
        Arrays.stream(ExpenseCategory.values()).forEach(c -> categorySet.add(c.name()));
    }
    @Override
    public void execute(AbsSender absSender, Update update) {
        Message message = (Message) update.getCallbackQuery().getMessage();
        String callback = update.getCallbackQuery().getData();
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        Expense expense = ExpensesBot.EXPENSE;
        if(expense == null) {
            BotUtils.deleteMessage(absSender, chatId, messageId);
            return;
        }
        String category = callback.substring(getMarkupIdentifier().length() + 1);
        if (categorySet.contains(category)) {
            String textToSend = "Category: " + category + "\nPlease choose price";
            BotUtils.updateMessage(absSender, chatId, messageId, textToSend, AddPriceMarkup.MARKUP);
            expense.setCategory(ExpenseCategory.valueOf(category));
        }
    }
}
