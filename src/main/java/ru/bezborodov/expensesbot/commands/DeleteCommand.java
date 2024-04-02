package ru.bezborodov.expensesbot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.model.Expense;
import ru.bezborodov.expensesbot.service.BotUtils;

import java.util.Optional;

@Component
public class DeleteCommand extends ExpenseBotCommand {
    public DeleteCommand() {
        super("delete", "delete a replied expense");
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        long chatId = update.getMessage().getChat().getId();
        if (update.getMessage().getReplyToMessage() != null) {
            int messageId = update.getMessage().getReplyToMessage().getMessageId();
            Optional<Expense> optionalExpense = expenseRepository.findByUserIdAndMessageId(chatId, messageId);
            if (optionalExpense.isEmpty()) {
                log.error("Can't be deleted. Forwarded message " + messageId + " by user " + chatId + " doesn't contain an expense");
            } else {
                Expense expenseToDelete = optionalExpense.get();
                expenseRepository.delete(expenseToDelete);
                BotUtils.deleteMessage(absSender, chatId, messageId);
                log.info("Deleted " + expenseToDelete);
            }
        }
    }
}
