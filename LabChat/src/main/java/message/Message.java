package message;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class Message {
    private String name;
    private String text;

    private MessageType type;

    public Message(String name, String text, MessageType type) {
        this.name = name;
        this.text = text;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(name, message.name) &&
                Objects.equals(text, message.text) &&
                type == message.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, text, type);
    }
}
