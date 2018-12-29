package mock;

import message.Message;
import user.Client;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;

public class testClient extends Client {
    ArrayList<Message> receivedMessages = new ArrayList<>();

    public testClient(Session session, String name) {
        super(session, name);
    }

    public testClient(Session session, String name, int companionsNumber) {
        super(session, name, companionsNumber);
    }

    @Override
    public void sendMessage(Message message) throws IOException, EncodeException {
        receivedMessages.add(message);
    }

    public ArrayList<Message> getReceivedMessages() {
        return receivedMessages;
    }
}
