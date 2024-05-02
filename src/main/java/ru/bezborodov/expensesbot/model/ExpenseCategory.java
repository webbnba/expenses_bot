package ru.bezborodov.expensesbot.model;

import com.vdurmont.emoji.EmojiParser;

public enum ExpenseCategory {
    FOOD(":cut_of_meat::green_apple::leafy_green:"),
    HOUSEHOLD_CHEMISTRY(":lotion_bottle::soap::roll_of_paper: "),
    HEALTH(":hospital::woman_health_worker:"),
    CAR(":oncoming_automobile::gear::wrench:"),
    SERVICES(":hammer_and_wrench::briefcase::partying_face:"),
    UTILITIES(":bulb::potable_water::thermometer:"),
    CLOTHES(":dress::jeans::high_heel:"),
    GREAT_BUY(":moneybag::dollar:"),
    BAD_HABITS(":nauseated_face::champagne:");
    private final String emoji;
    ExpenseCategory(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return EmojiParser.parseToUnicode(emoji);
    }

    @Override
    public String toString() {
        return EmojiParser.parseToUnicode(emoji) + " - " + name().toLowerCase();
    }
}
