package ru.bezborodov.expensesbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bezborodov.expensesbot.commands.ExpenseBotCommand;

import java.util.ArrayList;
import java.util.List;

public class BotUtils {
    public static final String CALLBACK_DELIMITER = ":";
    public static final String CATEGORY = "Category";
    public static final String PRICE = "Price";
    public static final String DATE = "Date";
    public static final String CANCEL = "Cancel";
    public static final String BACK = "Back";
    public static final String CURRENT_MONTH = "This month";
    public static final String PREVIOUS_MONTH = "Previous month";
    public static final String YTD = "YTD";
    public static final String PREVIOUS_YEAR = "Previous year";
    private static final Logger log = LoggerFactory.getLogger(BotUtils.class);

    private BotUtils() {
    }

    public static void updateMessage(AbsSender absSender, long chatId, int messageId, String textToSend, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText newMessage = new EditMessageText();
        newMessage.setChatId(chatId);
        newMessage.setMessageId(messageId);
        newMessage.setText(textToSend);
        newMessage.setReplyMarkup(keyboardMarkup);
        try {
            absSender.execute(newMessage);
        } catch (TelegramApiException e) {
            log.error("updateMessage() error occurred: " + e.getMessage());
        }
    }

    public static void deleteMessage(AbsSender absSender, long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            absSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            updateMessage(absSender, chatId, messageId, "DELETED", null);
            log.error(e.getMessage());
        }
    }

    public static void setMyCommands(AbsSender absSender, List<ExpenseBotCommand> myCommands) {
        List<BotCommand> commands = new ArrayList<>();
        myCommands.stream()
                .filter(ExpenseBotCommand::addInHelpMessage)
                .forEach(command -> commands.add(new BotCommand(command.getCommandIdentifier(), command.getDescription())));
        try{
            absSender.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting Bot command list" + e.getMessage());
        }
    }

    public static void sendMessage(AbsSender absSender,
                                   long chatId,
                                   String textToSend,
                                   InlineKeyboardMarkup keyboardMarkup) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .parseMode("HTML")
                .text(textToSend)
                .replyMarkup(keyboardMarkup)
                .build();
        try{
            ExpensesBot.CURRENT_BOT_MESSAGE = absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error("updateMessage() error occurred: " + e.getMessage());
        }
    }
}
