package pro.sky.telegrambot.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;

public interface TaskProcessor {
    boolean saveNotificationTask(@NotNull Long chatId, @NotNull String text);

    @Scheduled(cron = "0 0/1 * * * *")
    void processNotifications();
}
