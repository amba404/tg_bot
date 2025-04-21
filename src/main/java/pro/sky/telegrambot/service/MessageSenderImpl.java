package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderImpl implements MessageSender {
    private final Logger logger = LoggerFactory.getLogger(MessageSenderImpl.class);

    private final TelegramBot bot;

    public MessageSenderImpl(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean sendMessage(@NotNull Long chatId, @NotNull String text) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableNotification(true);

        SendResponse sendResponse = bot.execute(request);
        boolean isOk = sendResponse.isOk();
        if (isOk) {
            logger.error("Error sending message: {}. Response: {}", request, sendResponse);
        }
        return isOk;
    }
}
