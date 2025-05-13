package pro.sky.telegrambot.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaskProcessorImpl implements TaskProcessor {

    private final Logger logger = LoggerFactory.getLogger(TaskProcessorImpl.class);
    private final NotificationTaskRepository taskRepo;
    private final MessageSender messageSender;

    public TaskProcessorImpl(NotificationTaskRepository taskRepo, MessageSenderImpl messageSender) {
        this.taskRepo = taskRepo;
        this.messageSender = messageSender;
    }

    @Override
    public boolean saveNotificationTask(@NotNull Long chatId, @NotNull String text) {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String textDate = matcher.group(1);
            String textMessage = matcher.group(3);
            LocalDateTime dateTime = LocalDateTime.parse(textDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            NotificationTask newTask = new NotificationTask();
            newTask.setDateTime(dateTime);
            newTask.setTextMessage(textMessage);
            newTask.setChatId(chatId);
            newTask.setIsDone(false);
            taskRepo.save(newTask);

            return true;
        }

        return false;
    }


    @Override
    @Scheduled(cron = "0 0/1 * * * *")
    public void processNotifications() {
        logger.info("Scheduled task executed");
        List<NotificationTask> notificationTasks = taskRepo
                .findAllByDateTimeEqualsAndIsDoneIsFalse(LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES));

        notificationTasks.forEach(this::processTask);
    }

    void processTask(@NotNull NotificationTask task) {
        if (messageSender.sendMessage(task.getChatId(), task.getTextMessage())) {
            task.setIsDone(true);
            taskRepo.save(task);
        }
    }

}
