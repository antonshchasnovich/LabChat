package chatClient;

import message.Message;
import message.MessageType;

import javax.websocket.EncodeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class ChatClient {
    private BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    private String name = "";
    private String text = "";
    private MessageType type = MessageType.TEXT_MESSAGE;

   private ChatClientEndpoint endpoint = null;

    public void register() throws IOException, URISyntaxException, EncodeException {
        String RegMessage = consoleReader.readLine();
        if(RegMessage.startsWith("/register agent ")){
            name = RegMessage.substring(16, RegMessage.length());
            type = MessageType.AGENT_REG_MESSAGE;
            endpoint = new ChatClientEndpoint(new URI("ws://localhost:8080/Chat/chat"));
            endpoint.sendMessage(new Message(name, text, type));
            type = MessageType.TEXT_MESSAGE;
        }
        else if(RegMessage.startsWith("/register client ")){
            name = RegMessage.substring(17, RegMessage.length());
            type = MessageType.CLIENT_REG_MESSAGE;
            endpoint = new ChatClientEndpoint(new URI("ws://localhost:8080/Chat/chat"));
            endpoint.sendMessage(new Message(name, text, type));
            type = MessageType.TEXT_MESSAGE;
        }
        else if(RegMessage.equals("/exit")){
            System.exit(0);
        }
        else{
            System.out.println("Invalid command. Please, try again...");
            register();
        }
    }

    public void start() throws IOException, EncodeException {
        while(true){
            text = consoleReader.readLine();
            if(text.equals("/exit")){
                System.exit(0);
            }
            else if(text.equals("/leave")){
                endpoint.sendMessage(new Message(name, "", MessageType.LEAVE_MESSAGE));
            }
            else {
                endpoint.sendMessage(new Message(name, text, type));
            }
        }
    }
}
