package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TelegramBotUpdatesListenerTest {

    Update update = mock(Update.class);
    Message message = mock(Message.class);
    Chat chat = mock(Chat.class);
    HashMap<Long, NotificationTask> notificationTasks = new HashMap<>();
    SendResponse sendResponse = mock(SendResponse.class);

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private NotificationTaskRepository notificationTaskRepository;

    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @BeforeEach
    public void setUp() {
        when(notificationTaskRepository.findAllByDateTimeEqualsAndIsDoneIsFalse(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(notificationTasks.values()));
        when(notificationTaskRepository.save(any(NotificationTask.class)))
                .thenAnswer(i -> {
                    NotificationTask t = (NotificationTask) i.getArguments()[0];
                    notificationTasks.put(t.getId(), t);
                    return t;
                });
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(-1L);
        when(chat.firstName()).thenReturn("Test");
        when(chat.username()).thenReturn("Test");
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);
        when(sendResponse.isOk()).thenReturn(true);
    }

    @Test
    public void testProcessNotifications() {
        when(message.text()).thenReturn("01.01.2022 12:00 Привет");

        telegramBotUpdatesListener.saveNotificationTask(message);
        telegramBotUpdatesListener.processNotifications();

        verify(notificationTaskRepository, times(1)).findAllByDateTimeEqualsAndIsDoneIsFalse(any(LocalDateTime.class));
    }

    @Test
    public void testProcessUpdate() {
        when(message.text()).thenReturn("/start");

        telegramBotUpdatesListener.processUpdate(update);

        verify(telegramBot, times(1)).execute(any(SendMessage.class));
    }

    @Test
    public void testSaveNotificationTaskOk() {
        when(message.text()).thenReturn("01.01.2022 12:00 Привет");

        telegramBotUpdatesListener.saveNotificationTask(message);

        verify(notificationTaskRepository, times(1)).save(any(NotificationTask.class));
        assertEquals("Привет",
                notificationTasks
                        .values()
                        .toArray(NotificationTask[]::new)[0]
                        .getTextMessage());
    }

    @Test
    public void testSaveNotificationTaskFails() {
        when(message.text()).thenReturn("01/01/2022 12:00 Привет");

        telegramBotUpdatesListener.saveNotificationTask(message);

        verify(notificationTaskRepository, times(0)).save(any(NotificationTask.class));
    }
}
