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
public class UpdateCategoryMarkup extends BotMarkup {
    private final Set<String> categorySet;
    private static final String MARKUP_IDENTIFIER = "Update category";
    public static final InlineKeyboardMarkup MARKUP = BotUtils.getCategoryMarkup(MARKUP_IDENTIFIER, true);

    public UpdateCategoryMarkup() {
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
        String[] lines = message.getText().split("\n");
        if (expense == null) {
            BotUtils.updateMessage(absSender, chatId, messageId, lines[0], null);
            return;
        }
        String button = callback.substring(getMarkupIdentifier().length() + 1);
        if (categorySet.contains(button)) {
            expense.setCategory(ExpenseCategory.valueOf(button));
            expenseRepository.save(expense);
            BotUtils.updateMessage(absSender, chatId, messageId, expense.getPrice() + " "
                    + " on " + expense.getCategory().name(), null);
        } else if (button.equals(BotUtils.BACK)) {
            BotUtils.updateMessage(absSender, chatId, messageId, lines[0] +
                    "\nChoose property to update", UpdatePropertyMarkup.MARKUP);
        }
    }
}
