package user;

import message.Message;
import message.MessageType;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;

public class Client extends User {
    private ArrayList<Message> history = new ArrayList<>();;
    private ArrayList<Message> bufferedMessages = new ArrayList<>();;
    private int index;

    public Client(Session session, String name) {
        super(session, name);
    }

    public Client(Session session, String name, int companionsNumber) {
        super(session, name, companionsNumber);
    }

    @Override
    public void sendMessage(Message message) throws IOException, EncodeException {
        super.sendMessage(message);
        if(message.getType() == MessageType.TEXT_MESSAGE) {
            history.add(message);
        }
    }

    @Override
    public void sendMessageToCompanion(Message message) throws IOException, EncodeException {
        if (getCompanion(0) != null){
            if(message.getType() == MessageType.TEXT_MESSAGE){
                sendMessage(message);
            }
            message.setIndex(this.index);
            getCompanion(0).sendMessage(message);
        }else {
            message.setType(MessageType.BUFFERED_MESSAGE);
            sendMessage(message);
            bufferedMessages.add(message);
        }
    }

    void sendBufferedMessages() throws IOException, EncodeException {
        for (Message message:bufferedMessages
             ) {
            message.setType(MessageType.TEXT_MESSAGE);
            history.add(message);
            message.setIndex(index);
            getCompanion(0).sendMessage(message);
        }
        bufferedMessages.clear();
    }

    void sendHistory() throws IOException, EncodeException {
        sendMessageToCompanion(new Message("Message History", "", MessageType.HISTORY_MESSAGE));
        sendMessageToCompanion(new Message(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::", "", MessageType.HISTORY_MESSAGE));
        for (Message message: history
                ) {
            message.setType(MessageType.HISTORY_MESSAGE);
            sendMessageToCompanion(message);
        }
        sendMessageToCompanion(new Message(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::", "", MessageType.HISTORY_MESSAGE));
    }

    ArrayList<Message> getHistory() {
        return history;
    }

    ArrayList<Message> getBufferedMessages() {
        return bufferedMessages;
    }

    int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
