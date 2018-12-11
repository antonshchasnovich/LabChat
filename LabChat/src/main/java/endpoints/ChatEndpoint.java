package endpoints;

import coders.MessageDecoder;
import coders.MessageEncoder;
import message.Message;
import message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Agent;
import user.Client;
import user.SessionsStorage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chat",decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {
    private final static String SERVER_NAME = "Server";
    private static final Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);
    private static final SessionsStorage storage = new SessionsStorage(SERVER_NAME);
    static {
        storage.setLogger(logger);
    }


    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(new Message(SERVER_NAME, "Сonnection established.", MessageType.TEXT_MESSAGE));
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        storage.exitChat(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException, EncodeException {
        session.close();
    }

    @OnMessage
    public void onMessage(Session session, Message msg) throws IOException, EncodeException {

      if(msg.getType() == MessageType.TEXT_MESSAGE){
          storage.sendMessage(session, msg);
      }
      else if(msg.getType() == MessageType.AGENT_REG_MESSAGE){
          session.getBasicRemote().sendObject(new Message(SERVER_NAME, "You registered like agent.", MessageType.SERVER_MESSAGE));
          logger.info("Agent " + msg.getName() + " registered.");
          storage.addAgent(new Agent(session, msg.getName()));
      }
      else if(msg.getType() == MessageType.CLIENT_REG_MESSAGE){
          session.getBasicRemote().sendObject(new Message(SERVER_NAME, "You registered like client.", MessageType.SERVER_MESSAGE));
          logger.info("Client " + msg.getName() + " registered.");
          storage.addClient(new Client(session, msg.getName()));
      }
      else if(msg.getType() == MessageType.LEAVE_MESSAGE){
          storage.leaveChat(session);
      }
    }
}
