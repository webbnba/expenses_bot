package ru.bezborodov.expensesbot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

@Component
public class HelpCommand extends ExpenseBotCommand {
    public HelpCommand() {
        super("help", "get info how to use this bot");
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        long chatId = update.getMessage().getChat().getId();
        if (ExpensesBot.CURRENT_BOT_MESSAGE != null) { // if there is unfinished process
            BotUtils.deleteMessage(absSender, ExpensesBot.CURRENT_BOT_MESSAGE.getChatId(), ExpensesBot.CURRENT_BOT_MESSAGE.getMessageId());
        }
        BotUtils.sendMessage(absSender, chatId, ExpensesBot.getHelpMessage(), null);
        ExpensesBot.CURRENT_BOT_MESSAGE = null; // saves from delete message
    }
}
