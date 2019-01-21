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

    User(Session session, String name) {
        this.session = session;
        this.name = name;
        companions = new User[1];
    }

    User(Session session, String name, int companionsNumber) {
        this(session, name);
        companions = new User[companionsNumber];
    }

    public void sendMessage(Message message) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(message);
    }

    public abstract void sendMessageToCompanion(Message message) throws IOException, EncodeException;

    int setCompanion(User user) {
        for (int i = 0; i < companions.length; i++) {
            if (companions[i] == null) {
                companions[i] = user;
                return i;
            }
        }
        return -1;
    }

    void removeCompanion(int index) {
        companions[index] = null;
    }

    Session getSession() {
        return session;
    }

    String getName() {
        return name;
    }

    User getCompanion(int index) {
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

    boolean isReady() {
        for (User companion : companions
        ) {
            if (companion == null) return true;
        }
        return false;
    }

    int getCompanionsNumber() {
        return companions.length;
    }

    void setSession(Session session) {
        this.session = session;
    }

    boolean isChatting(int index) {
        return companions[index] != null;
    }
}
