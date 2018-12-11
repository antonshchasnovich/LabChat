package message;

import lombok.Builder;
import lombok.Data;

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
}
