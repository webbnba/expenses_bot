package ru.bezborodov.expensesbot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.markups.ReportMarkup;
import ru.bezborodov.expensesbot.service.BotUtils;

@Component
public class ReportCommand extends ExpenseBotCommand {
    public ReportCommand() {
        super("report", "get your expense report");
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        long chatId = update.getMessage().getChatId();
        BotUtils.sendMessage(absSender, chatId, "Please choose report type", ReportMarkup.MARKUP);
    }
}
