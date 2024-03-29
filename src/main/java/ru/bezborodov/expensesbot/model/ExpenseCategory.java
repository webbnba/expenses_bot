package ru.bezborodov.expensesbot.model;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;

@Getter
public enum ExpenseCategory {
    FOOD(":green_salad:"),
    HOUSEHOLD_CHEMISTRY(":lotion_bottle: "),
    HEALTH(":hospital::woman_health_worker:"),
    CAR(":automobile::fuel_pump:"),
    SERVICES(":nail_care::hammer_and_wrench:"),
    UTILITIES(":person_taking_bath:");

    private final String emoji;
    ExpenseCategory(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String toString() {
        return EmojiParser.parseToUnicode(emoji) + " - " + name().toLowerCase();
    }
}
