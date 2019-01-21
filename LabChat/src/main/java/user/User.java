package user;

import message.Message;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;

public abstract class User {
    private Session session;
    private final String name;
    private User[] companions;

    public User(Session session, String name) {
        this.session = session;
        this.name = name;
        companions = new User[1];
    }

    public User(Session session, String name, int companionsNumber) {
        this.session = session;
        this.name = name;
        companions = new User[companionsNumber];
    }

    public void sendMessage(Message message) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(message);
    }

    public abstract void sendMessageToCompanion(Message message) throws IOException, EncodeException;

    public int setCompanion(User user) {
        for (int i = 0; i < companions.length; i++) {
            if (companions[i] == null) {
                companions[i] = user;
                return i;
            }
        }
        return -1;
    }

    public void removeCompanion(int index) {
        companions[index] = null;
    }

    public Session getSession() {
        return session;
    }

    public String getName() {
        return name;
    }

    public User getCompanion(int index) {
        return companions[index];
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

    public boolean isReady() {
        for (User companion : companions
        ) {
            if (companion == null) return true;
        }
        return false;
    }

    public int getCompanionsNumber() {
        return companions.length;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isChatting() {
        for (User companion : companions
        ) {
            if (companion != null) return true;
        }
        return false;
    }
}
