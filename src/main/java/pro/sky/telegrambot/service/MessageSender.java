package pro.sky.telegrambot.service;

public interface MessageSender {
    boolean sendMessage(Long chatId, String text);
}
