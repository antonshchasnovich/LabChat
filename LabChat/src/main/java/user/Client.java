package user;

import message.Message;
import message.MessageType;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;

public class Client extends User {
    private ArrayList<Message> history;
    private ArrayList<Message> bufferedMessages;
    private int index;

    public Client(Session session, String name) {
        super(session, name);
        history = new ArrayList<>();
        bufferedMessages = new ArrayList<>();
    }

    public Client(Session session, String name, int companionsNumber) {
        super(session, name, companionsNumber);
        history = new ArrayList<>();
        bufferedMessages = new ArrayList<>();
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

    public void sendBufferedMessages() throws IOException, EncodeException {
        for (Message message:bufferedMessages
             ) {
            message.setType(MessageType.TEXT_MESSAGE);
            history.add(message);
            getCompanion(0).sendMessage(message);
        }
        bufferedMessages.clear();
    }

    public void sendHistory() throws IOException, EncodeException {
        sendMessageToCompanion(new Message("Message History", "", MessageType.HISTORY_MESSAGE));
        sendMessageToCompanion(new Message(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::", "", MessageType.HISTORY_MESSAGE));
        for (Message message: history
                ) {
            message.setType(MessageType.HISTORY_MESSAGE);
            sendMessageToCompanion(message);
        }
        sendMessageToCompanion(new Message(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::", "", MessageType.HISTORY_MESSAGE));
    }

    public ArrayList<Message> getHistory() {
        return history;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
