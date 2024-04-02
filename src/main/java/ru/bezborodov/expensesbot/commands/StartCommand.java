package ru.bezborodov.expensesbot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.model.User;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

import java.time.LocalDateTime;

@Component
public class StartCommand extends ExpenseBotCommand {
    public StartCommand() {
        super("start", "start bot");
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        Chat chat = update.getMessage().getChat();
        long chatId = chat.getId();
        if (userRepository.findById(chatId).isEmpty()) {
            User user = new User();
            user.setId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(LocalDateTime.now());

            userRepository.save(user);
            log.info("New User saved: " + user);
        }
        BotUtils.sendMessage(absSender, chatId, ExpensesBot.getHelpMessage(), null);
        ExpensesBot.CURRENT_BOT_MESSAGE = null; // saves from delete message
    }

    @Override
    public boolean addInHelpMessage() {
        return false;
    }
}
