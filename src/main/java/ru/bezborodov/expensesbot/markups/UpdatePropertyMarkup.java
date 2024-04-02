package ru.bezborodov.expensesbot.markups;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

@Component
public class UpdatePropertyMarkup extends BotMarkup {
    private static final String MARKUP_IDENTIFIER = "Update property";
    public static final InlineKeyboardMarkup MARKUP = BotUtils.getPropertyMarkup(MARKUP_IDENTIFIER);

    public UpdatePropertyMarkup() {
        super(MARKUP_IDENTIFIER);
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        Message message = (Message) update.getCallbackQuery().getMessage();
        String callback = update.getCallbackQuery().getData();
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        String[] lines = message.getText().split("\n");
        if (ExpensesBot.EXPENSE == null) {
            BotUtils.updateMessage(absSender, chatId, messageId, lines[0], null);
            return;
        }
        String button = callback.substring(getMarkupIdentifier().length() + 1);
        switch (button) {
            case BotUtils.CATEGORY ->
                    BotUtils.updateMessage(absSender, chatId, messageId, lines[0] + "\nPlease choose expense category", UpdateCategoryMarkup.MARKUP);
            case BotUtils.PRICE ->
                    BotUtils.updateMessage(absSender, chatId, messageId, lines[0] + "\nPlease enter sum", null);
            case BotUtils.DATE ->
                    BotUtils.updateMessage(absSender, chatId, messageId, lines[0] + "\nPlease choose month", UpdateMonthMarkup.MARKUP);
            case BotUtils.CANCEL -> BotUtils.updateMessage(absSender, chatId, messageId, lines[0], null);
        }
    }
}
