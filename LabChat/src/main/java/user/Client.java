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

    public Client(Session session, String name) {
        super(session, name);
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
        if (getCompanion() != null){
            getCompanion().sendMessage(message);
            sendMessage(message);
        }else {
            sendMessage(message);
            if (message.getType() == MessageType.TEXT_MESSAGE){
                bufferedMessages.add(message);
            }
        }
    }

    public void sendBufferedMessages() throws IOException, EncodeException {
        for (Message message:bufferedMessages
             ) {super.getCompanion().sendMessage(message);
        }
        bufferedMessages.clear();
    }

    public void sendStory() throws IOException, EncodeException {
        super.getCompanion().sendMessage(new Message("#######################################", "", MessageType.TEXT_MESSAGE));
        for (Message message: history
                ) {super.getCompanion().sendMessage(message);
        }
        super.getCompanion().sendMessage(new Message("#######################################", "", MessageType.TEXT_MESSAGE));
    }

    public ArrayList<Message> getHistory() {
        return history;
    }
}
