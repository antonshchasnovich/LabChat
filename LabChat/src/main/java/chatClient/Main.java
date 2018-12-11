package chatClient;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        try {
            chatClient.register();
            chatClient.start();
        } catch (URISyntaxException | EncodeException | IOException e) {
            e.printStackTrace();
        }
    }
}
