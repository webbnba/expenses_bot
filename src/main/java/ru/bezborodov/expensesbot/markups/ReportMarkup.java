package ru.bezborodov.expensesbot.markups;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.bezborodov.expensesbot.model.ExpenseCategory;
import ru.bezborodov.expensesbot.service.BotUtils;
import ru.bezborodov.expensesbot.service.ExpensesBot;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Component
public class ReportMarkup extends BotMarkup {
    private static final String MARKUP_IDENTIFIER = "Report";
    public static final InlineKeyboardMarkup MARKUP = BotUtils.getReportMarkup(MARKUP_IDENTIFIER);

    public ReportMarkup() {
        super(MARKUP_IDENTIFIER);
    }

    @Override
    public void execute(AbsSender absSender, Update update) {
        Message message = (Message) update.getCallbackQuery().getMessage();
        String callback = update.getCallbackQuery().getData();
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        BotUtils.deleteMessage(absSender, chatId, messageId);
        LocalDateTime today = LocalDateTime.now();
        int year = today.getYear();
        Month month = today.getMonth();
        String button = callback.substring(getMarkupIdentifier().length() + 1);
        String textToSend = "";
        switch (button) {
            case BotUtils.CURRENT_MONTH -> textToSend = getMonthReport(chatId, month, year);
            case BotUtils.PREVIOUS_MONTH -> textToSend = getMonthReport(chatId, month.minus(1),
                    today.minusMonths(1).getYear());
            case BotUtils.YTD -> textToSend = getYearReport(chatId, year);
            case BotUtils.PREVIOUS_YEAR -> textToSend = getYearReport(chatId, year - 1);
        }
        if (!textToSend.isEmpty()) {
            BotUtils.sendMessage(absSender, chatId, textToSend, null);
        }
        ExpensesBot.CURRENT_BOT_MESSAGE = null;
    }

    private String getMonthReport(long userId, Month month, int year) {
        List<Object[]> expenses = expenseRepository.getMonthReportByCategories(userId, year, month.getValue());
        ExpenseCategory expenseCategory;
        if (expenses.isEmpty()) {
            return "<b>No expenses in " + month + "</b>";
        } else {
            StringBuilder builder = new StringBuilder("<b>Expenses in " +
                    month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year + "</b>\n<i>");
            String category = "";
            for (Object[] expense : expenses) {
                expenseCategory = (ExpenseCategory) expense[0];
                if (category.equals(expenseCategory.name())) {
                    builder.append(" + ").append(expense[1]);
                } else {
                    builder.append("\n").append(expenseCategory.getEmoji()).append(": ")
                            .append(expense[1]).append(" ");
                }
                category = expenseCategory.name();
            }
            expenses = expenseRepository.getTotalMonthReport(userId, year, month.getValue());
            builder.append("\n\nTotal:");
            builder.append(" ").append(expenses.get(0)[0]);
            if (expenses.size() > 1) {
                builder.append(" + ").append(expenses.get(1)[0]);
            }
            builder.append("</i>");
            return builder.toString();
        }
    }

    private String getYearReport(long userId, int year) {
        List<Object[]> expenses = expenseRepository.getTotalYearReport(userId, year);
        if (expenses.isEmpty()) {
            return "<b>No expenses in " + year + "</b>";
        } else {
            StringBuilder builder = new StringBuilder("<b>Expenses in " + year + "</b>\n<i>");
            int month = -1;
            for (Object[] expense : expenses) {
                if (month == (int) expense[0]) {
                    builder.append(" + ").append(expense[1]);
                } else {
                    builder.append("\n")
                            .append(Month.of((int) expense[0]).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                            .append(": ")
                            .append(expense[1]);
                }
                month = (int) expense[0];
            }
            builder.append("</i>");
            return builder.toString();
        }
    }
}
