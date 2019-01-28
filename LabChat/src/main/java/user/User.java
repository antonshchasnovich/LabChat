package user;

import message.Message;
import util.IdGenerator;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public abstract class User {
    protected final long id;
    protected final String name;
    protected final Date regTime;

    protected long[] currentChatsId;
    protected Session session;
    protected User[] companions;
    protected int maxCompanionsNumber;

    User(Session session, String name, int companionsNumber) {
        regTime = new Date();
        currentChatsId = new long[companionsNumber];
        id = IdGenerator.getInstance().getUserId();
        this.session = session;
        this.name = name;
        this.maxCompanionsNumber = companionsNumber;
        companions = new User[companionsNumber];
    }

    User(Session session, String name) {
        this(session, name, 1);
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

    public Object getSession() {
        return session;
    }

    User getCompanion(int index) {
        return companions[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(session, user.session) &&
                Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, session, name);
    }

    boolean isReady() {
        for (User companion : companions
        ) {
            if (companion == null) return true;
        }
        return false;
    }

    public int getMaxCompanionsNumber() {
        return maxCompanionsNumber;
    }

    void setSession(Session session) {
        this.session = session;
    }

    boolean isChatting(int index) {
        return companions[index] != null;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long[] getCurrentChatsId() {
        return currentChatsId;
    }

    public User[] getCompanions() {
        return companions;
    }

    public void addCurrentChatId(int index, long currentChatId) {
        currentChatsId[index] = currentChatId;
    }

    public void removeCurrentChatId(int index) {
        currentChatsId[index] = 0;
    }

    public Date getRegTime() {
        return regTime;
    }


}
