package ru.bezborodov.expensesbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yaml")
@Getter
@Setter
public class BotConfig {

    @Value("${telegram.botName}")
    private String botUserName;

    @Value("${telegram.botToken}")
    private String botToken;
}
