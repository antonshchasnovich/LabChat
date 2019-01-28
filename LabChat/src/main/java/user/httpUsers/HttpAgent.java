package user.httpUsers;

import message.Message;
import user.Agent;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;

public class HttpAgent extends Agent implements HttpUser {
    private HttpSession session;
    MessageStorage storage;

    public HttpAgent(HttpSession session, String name){
        super(null, name);
        this.session = session;
        storage = MessageStorage.getInstance();
    }

    @Override
    public synchronized void sendMessage(Message message) throws IOException, EncodeException {
        storage.addMessage(id, message);
    }

    public synchronized ArrayList<Message> getMessages() {
        return storage.getMessages(id);
    }

    @Override
    public Object getSession() {
        return session;
    }
}
