package mock;

import message.Message;
import user.Agent;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;

public class testAgent extends Agent {
    ArrayList<Message> receivedMessages = new ArrayList<>();


    public testAgent(Session session, String name) {
        super(session, name);
    }

    public testAgent(Session session, String name, int companionsNumber) {
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
