package pro.sky.telegrambot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @Column(name = "chat_id")
    private Long chatId;

    @NotNull
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @NotNull
    @Column(name = "is_done")
    private Boolean isDone;

    @NotNull
    @Column(name = "text_message")
    private String textMessage;

}
