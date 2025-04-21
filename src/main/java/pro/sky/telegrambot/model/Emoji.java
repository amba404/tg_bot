package pro.sky.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Emoji {
    HELLO(0x1F64B),
    SMILE(0x1F603),
    WIRK(0x1F609),
    DISAPOINTED(0x1F61E);

    private final int code;

    @Override
    public String toString() {
        return new String(Character.toChars(code));
    }
}
