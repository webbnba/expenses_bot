package ru.bezborodov.expensesbot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.markups.UpdatePropertyMarkup;
import ru.bezborodov.expensesbot.model.Expense;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

import java.util.Optional;

@Component
public class UpdateCommand extends ExpenseBotCommand {
    public UpdateCommand() {
        super("update", "update a replied expense");
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        long chatId = update.getMessage().getChat().getId();
        if (update.getMessage().getReplyToMessage() != null) {
            int expenseMessageId = update.getMessage().getReplyToMessage().getMessageId();
            Optional<Expense> optionalExpense = expenseRepository.findByUserIdAndMessageId(chatId, expenseMessageId);
            if (optionalExpense.isEmpty()) {
                log.error("Can't be updated. Forwarded message " + expenseMessageId +
                        " by user " + chatId + " doesn't contain an expense");
            } else {
                ExpensesBot.EXPENSE = optionalExpense.get();
                String messageText = update.getMessage().getReplyToMessage().getText();
                BotUtils.updateMessage(absSender, chatId, expenseMessageId, messageText +
                        "\nChoose property to update", UpdatePropertyMarkup.MARKUP);
            }
        } else {
            log.error("Trying to update without a forwarded message by user " + chatId);
        }
    }
}
