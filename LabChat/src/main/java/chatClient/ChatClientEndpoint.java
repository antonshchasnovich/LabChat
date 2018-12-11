package chatClient;

import coders.MessageDecoder;
import coders.MessageEncoder;
import message.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint(decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatClientEndpoint {
    private Session userSession;
    private Message message;

    public ChatClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) throws IOException, EncodeException {
        this.userSession = userSession;
        sendMessage(message);
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }


    @OnMessage
    public void onMessage(Session session, Message message) {
        System.out.println(message.getName() + ": " + message.getText());
    }


    public void sendMessage(Message message) throws IOException, EncodeException {
        this.userSession.getBasicRemote().sendObject(message);
    }

    public Session getUserSession() {
        return userSession;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
