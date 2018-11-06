package message;

import java.io.Serializable;

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
