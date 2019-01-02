package endpoints;

import coders.MessageDecoder;
import coders.MessageEncoder;
import message.Message;
import message.MessageType;
import user.SessionsStorage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chat",decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {
    private static final SessionsStorage storage = new SessionsStorage();


    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(new Message("Server", "Сonnection established.",
                MessageType.SERVER_MESSAGE));
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        storage.exitChat(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException, EncodeException {
        storage.getLogger().error(String.valueOf(throwable));
        session.close();
    }

    @OnMessage
    public void onMessage(Session session, Message msg) throws IOException, EncodeException {

      if(msg.getType() == MessageType.TEXT_MESSAGE){
          storage.sendMessage(session, msg);
      }
      else if(msg.getType() == MessageType.AGENT_REG_MESSAGE){
          storage.regAgent(session, msg);
      }
      else if(msg.getType() == MessageType.CLIENT_REG_MESSAGE){
          storage.regClient(session, msg);
      }
      else if(msg.getType() == MessageType.LEAVE_MESSAGE){
          storage.leaveChat(session, msg);
      }
    }
}
