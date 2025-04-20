package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(
            TelegramBot telegramBot,
            NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
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

    void processUpdate(Update update) {
        logger.info("Processing update: {}", update);

        Message message = update.message() == null ? update.editedMessage() : update.message();

        if ("/start".equals(message.text())) {
            sendHello(message);
        } else if (saveNotificationTask(message)) {
            sendSimpleMessage(message, "Понял! Сделаю " + Emoji.WIRK);
        } else {
            sendSimpleMessage(message, "Я не понял... " + Emoji.DISAPOINTED);
        }
    }

    boolean saveNotificationTask(Message message) {
        String text = message.text();
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String textDate = matcher.group(1);
            String textMessage = matcher.group(3);
            LocalDateTime dateTime = LocalDateTime.parse(textDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            NotificationTask newTask = new NotificationTask();
            newTask.setDateTime(dateTime);
            newTask.setTextMessage(textMessage);
            newTask.setChatId(message.chat().id());
            newTask.setIsDone(false);
            notificationTaskRepository.save(newTask);

            return true;
        }

        return false;
    }

    void sendSimpleMessage(@NotNull Message message, String text) {
        Long chatId = message.chat().id();
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableNotification(true)
                .replyParameters(new ReplyParameters(message.messageId()));

        SendResponse sendResponse = telegramBot.execute(request);
        if (!sendResponse.isOk()) {
            logger.error("Error sending message: {}. Response: {}", request, sendResponse);
        }
    }

    void sendHello(@NotNull Message message) {

        String text = "Hello " +
                message.chat().firstName() +
                " aka " +
                message.chat().username() +
                "! " + Emoji.HELLO;

        sendSimpleMessage(message, text);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void processNotifications() {
        logger.info("Scheduled task executed");
        List<NotificationTask> notificationTasks = notificationTaskRepository
                .findAllByDateTimeEqualsAndIsDoneIsFalse(LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES));

        notificationTasks.forEach(this::processTask);
    }

    void processTask(NotificationTask task) {
        SendMessage message = new SendMessage(task.getChatId(), task.getTextMessage());
        SendResponse response = telegramBot.execute(message);
        if (response.isOk()) {
            task.setIsDone(true);
            notificationTaskRepository.save(task);
        } else {
            logger.error("Task {} was not sent. Response: {}", task, response);
        }
    }

    @Getter
    enum Emoji {
        HELLO(0x1F64B),
        SMILE(0x1F603),
        WIRK(0x1F609),
        DISAPOINTED(0x1F61E);

        private final int code;

        Emoji(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return new String(Character.toChars(code));
        }
    }
}
