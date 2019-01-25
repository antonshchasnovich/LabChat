package user.httpUsers;

import message.Message;
import user.Agent;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;

public class httpAgent extends Agent {
    private HttpSession session;

    public httpAgent(HttpSession session, String name){
        super(null, name);
        this.session = session;
    }

    @Override
    public synchronized void sendMessage(Message message) throws IOException, EncodeException {
        MessageStorage.addMessage(getId(), message);
    }

    public synchronized ArrayList<Message> getMessages() {
        return MessageStorage.getMessages(getId());
    }

    protected void finalize(){
        MessageStorage.removeMessages(getId());
    }
}
