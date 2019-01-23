package endpoints;

import coders.MessageDecoder;
import coders.MessageEncoder;
import message.Message;
import message.MessageType;
import user.SessionsStorage;


import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chat",decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {
    private static final SessionsStorage storage = SessionsStorage.getInstance();


    @OnOpen
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendObject(new Message("Server", "Сonnection established.",
                    MessageType.SERVER_MESSAGE));
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            storage.exitChat(session);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        storage.getLogger().error("", throwable);
        try {
            session.close();
        } catch (IOException e) {
            storage.getLogger().error("", e);
        }
    }

    @OnMessage
    public void onMessage(Session session, Message msg) {

      try {
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
      }catch (IOException | EncodeException e){
          storage.getLogger().error("", e);
      }
    }
}
