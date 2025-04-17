package pro.sky.telegrambot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findAllByDateTimeIsBeforeAndIsDoneIsFalse(LocalDateTime dateTime);
}
