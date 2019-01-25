package user.httpUsers;

import message.Message;
import user.Client;

import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;

public class httpClient extends Client {
    private HttpSession session;

    public httpClient(HttpSession session, String name){
        super(null, name);
        this.session = session;
    }

    @Override
    public synchronized void sendMessage(Message message) throws IOException, EncodeException {
        MessageStorage.addMessage(id, message);
    }

    public synchronized ArrayList<Message> getMessages() {
        return MessageStorage.getMessages(id);
    }

    protected void finalize(){
        MessageStorage.removeMessages(id);
    }
}
