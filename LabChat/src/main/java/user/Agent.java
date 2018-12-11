package user;

import javax.websocket.Session;

public class Agent extends User {
    public Agent(Session session, String name) {
        super(session, name);
    }
}
