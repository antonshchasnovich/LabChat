package message;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс для описания сообщения
 */

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private MessageType type;
	private String text;
	private String sender;

	public Message(MessageType type, String text, String sender) {
		this.type = type;
		this.text = text;
		this.sender = sender;
	}

	public Message(MessageType type) {
		this.type = type;
		text = null;
		sender = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Message message = (Message) o;
		return type == message.type &&
				Objects.equals(text, message.text) &&
				Objects.equals(sender, message.sender);
	}

	@Override
	public String toString() {
		return "Message{" +
				"type=" + type +
				", text='" + text + '\'' +
				", sender='" + sender + '\'' +
				'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, text, sender);
	}

	public MessageType getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public String getSender() {
		return sender;
	}

}
