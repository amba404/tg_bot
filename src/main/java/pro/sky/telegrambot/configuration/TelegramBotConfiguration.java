package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfiguration {

    private final String token = System.getenv("TG_TOKEN");

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(token);
    }

}
