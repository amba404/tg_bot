package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Emoji;
import pro.sky.telegrambot.service.MessageSender;
import pro.sky.telegrambot.service.MessageSenderImpl;
import pro.sky.telegrambot.service.TaskProcessor;
import pro.sky.telegrambot.service.TaskProcessorImpl;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot bot;
    private final MessageSender messageSender;
    private final TaskProcessor taskProcessor;

    public TelegramBotUpdatesListener(TelegramBot bot, MessageSenderImpl messageSender, TaskProcessorImpl taskProcessor) {
        this.bot = bot;
        this.taskProcessor = taskProcessor;
        this.messageSender = messageSender;
    }


    @PostConstruct
    public void init() {
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    void processUpdate(@NotNull Update update) {
        logger.info("Processing update: {}", update);

        Message message = update.message() == null ? update.editedMessage() : update.message();

        Long chatId = message.chat().id();
        String text = message.text();

        if ("/start".equals(text)) {
            messageSender.sendMessage(chatId, "Привет! " + Emoji.HELLO);
        } else if (taskProcessor.saveNotificationTask(chatId, text)) {
            messageSender.sendMessage(chatId, "Понял! Сделаю " + Emoji.WIRK);
        } else {
            messageSender.sendMessage(chatId, "Я не понял... " + Emoji.DISAPOINTED);
        }
    }
}
