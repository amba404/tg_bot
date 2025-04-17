package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        logger.info("Processing update: {}", update);
        // Process your updates here
        String text = update.message().text();
        if (text.equals("/start")) {
            sendHello(update);
        } else {
            sendSimpleMessage(update, "Я не понял... :-( ");
//            throw new IllegalStateException("Unexpected value: " + update.message().text());
        }
    }

    private void sendSimpleMessage(@NotNull Update update, String text) {
        Long chatId = update.message().chat().id();
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableNotification(true)
                .replyParameters( new ReplyParameters(update.message().messageId())) ;

        SendResponse sendResponse = telegramBot.execute(request);
        boolean ok = sendResponse.isOk();
        Message message = sendResponse.message();
    }

    private void sendHello(@NotNull Update update) {

        String text = "Hello " +
                update.message().chat().firstName() +
                " aka " +
                update.message().chat().username() +
                "!";

        sendSimpleMessage(update, text);
    }
}
