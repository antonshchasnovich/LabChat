package user;

import message.Message;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;

public class Agent extends User {
    public Agent(Session session, String name) {
        super(session, name);
    }


    @Override
    public void sendMessageToCompanion(Message message) throws IOException, EncodeException {
        User companion = super.getCompanion(message.getIndex());
        if(companion != null){
            sendMessage(message);
            message.setIndex(0);
            companion.sendMessage(message);
        }
    }

    public Agent(Session session, String name, int companionsNumber) {
        super(session, name, companionsNumber);
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
