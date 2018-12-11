package user;

import message.Message;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;

public class User {
    private final Session session;
    private final String name;
    private User companion;

    public User(Session session, String name) {
        this.session = session;
        this.name = name;
        companion = null;

    }

    public void sendMessage(Message message) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(message);
    }

    public void sendMessageToCompanion(Message message) throws IOException, EncodeException {
        if (companion!=null){
            sendMessage(message);
            companion.sendMessage(message);}
    }

    public void setCompanion(User user){
        companion = user;
    }

    public void removeCompanion(){
        companion = null;
    }

    public Session getSession() {
        return session;
    }

    public String getName() {
        return name;
    }

    public User getCompanion() {
        return companion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(session, user.session) &&
                Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(session, name);
    }
}
