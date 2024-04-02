package ru.bezborodov.expensesbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bezborodov.expensesbot.commands.ExpenseBotCommand;
import ru.bezborodov.expensesbot.config.BotConfig;
import ru.bezborodov.expensesbot.markups.BotMarkup;
import ru.bezborodov.expensesbot.model.Expense;
import ru.bezborodov.expensesbot.model.ExpenseCategory;
import ru.bezborodov.expensesbot.repository.ExpenseRepository;
import ru.bezborodov.expensesbot.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

@Component
@Scope(value = "singleton")
public class ExpensesBot extends TelegramLongPollingBot {
    public static Expense EXPENSE;
    public static Message CURRENT_BOT_MESSAGE;
    private static String HELP_MESSAGE;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private final BotConfig config;
    private final Map<String, ExpenseBotCommand> commandMap;
    private final Map<String, BotMarkup> markupMap;
    private final Set<String> categoriesSet;
    private static final Logger log = LoggerFactory.getLogger(ExpensesBot.class);
    private String lastCallbackData;

    public ExpensesBot(BotConfig config, List<ExpenseBotCommand> commands, List<BotMarkup> markups) {
        super(config.getBotToken());
        this.config = config;
        commandMap = new HashMap<>();
        commands.forEach(c -> commandMap.put(c.getCommandIdentifier(), c));
        markupMap = new HashMap<>();
        markups.forEach(m -> markupMap.put(m.getMarkupIdentifier(), m));
        HELP_MESSAGE = getHelpMessage(commands);
        BotUtils.setMyCommands(this, commands);

        categoriesSet = new HashSet<>();
        Arrays.stream(ExpenseCategory.values()).forEach(c -> categoriesSet.add(c.name()));
    }

    @Override
    public String getBotUsername() {
        return config.getBotUserName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Message replyMessage = message.getReplyToMessage();
            String reply = replyMessage == null ? "" : " as a reply to message " + replyMessage.getMessageId();
            log.info("Received message \"" + message.getText() + "\"" + reply + " from " + message.getChatId());
            BotUtils.deleteMessage(this, message.getChatId(), message.getMessageId());
            if (message.isCommand()) {
                processCommand(update);
            } else {
                processUserTextMessage(message.getText(), message.getChatId());
            }
            return;
        }
        processCallback(update);
    }

    public static String getHelpMessage() {
        return HELP_MESSAGE;
    }

    private void processUserTextMessage(String msgText, long chatId) {
        if (EXPENSE != null && CURRENT_BOT_MESSAGE != null) {
            String textToSend;
            try {
                EXPENSE.setPrice(Double.parseDouble(msgText));
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
                textToSend = "Category: " + EXPENSE.getCategory()
                        + "\nIncorrect input. Please, use numbers";
                BotUtils.updateMessage(this, chatId, CURRENT_BOT_MESSAGE.getMessageId(), textToSend, null);
                return;
            }
            if (categoriesSet.contains(lastCallbackData)) { // if previous clicked button was category in ADD process
                addExpense(chatId, CURRENT_BOT_MESSAGE.getMessageId());
            }
            if (lastCallbackData.equals(BotUtils.PRICE)) { // if previous clicked button was price in UPDATE process
                addExpense(chatId, CURRENT_BOT_MESSAGE.getMessageId());
                expenseRepository.save(EXPENSE);
                textToSend = EXPENSE.getPrice() + " on " + EXPENSE.getCategory().name();
                BotUtils.updateMessage(this, chatId, CURRENT_BOT_MESSAGE.getMessageId(), textToSend, null);
            }
            CURRENT_BOT_MESSAGE = null;
        }
    }

    private void addExpense(long chatId, int messageId) {
        if (userRepository.findById(chatId).isPresent()) {
            EXPENSE.setUser(userRepository.findById(chatId).get());
            EXPENSE.setDate(LocalDate.now());
            EXPENSE.setMessageId(messageId);
            expenseRepository.save(EXPENSE);
            BotUtils.updateMessage(this, chatId, messageId, EXPENSE.getPrice() + " "
                    + " on " + EXPENSE.getCategory().name(), null);
            log.info("Added new " + EXPENSE);
            EXPENSE = null;
        }
    }

    private void processCommand(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        if (text.startsWith(ExpenseBotCommand.COMMAND_INIT_CHARACTER)) {
            String command = text.substring(1);
            if (this.commandMap.containsKey(command)) {
                this.commandMap.get(command).processMessage(this, update);
            }
        }
    }

    private void processCallback(Update update) {
        if (update.hasCallbackQuery()) {
            CURRENT_BOT_MESSAGE = (Message) update.getCallbackQuery().getMessage();
            lastCallbackData = update.getCallbackQuery().getData();
            long chatId = CURRENT_BOT_MESSAGE.getChatId();
            log.info("Clicked button \"" + lastCallbackData + "\" by " + chatId);

            String[] tokens = lastCallbackData.split(BotUtils.CALLBACK_DELIMITER);
            String markupIdentifier = tokens[0];
            lastCallbackData = tokens[1];
            if (this.markupMap.containsKey(markupIdentifier)) {
                this.markupMap.get(markupIdentifier).processCallback(this, update);
            }
        }
    }

    private String getHelpMessage(List<ExpenseBotCommand> commands) {
        String header = "I can help you to track your expenses\uD83D\uDE09\n\nYou can control me by sending these commands:\n";
        StringBuilder builder = new StringBuilder(header);
        commands.stream().filter(ExpenseBotCommand::addInHelpMessage).forEach(command -> {
            builder.append(command);
            builder.append("\n");
        });
        builder.append("\n<b>Expense categories:</b>\n");
        Arrays.stream(ExpenseCategory.values()).forEach(category -> builder.append(category).append("\n"));
        return builder.toString();
    }

}