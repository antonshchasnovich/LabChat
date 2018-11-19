package chatClient;

import chatClient.view.ChatClientView;
import chatClient.view.ConsoleChatClientView;
import message.Message;
import message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatClient {
    private final static String IP = "localhost";
    private final static int PORT = 5556;

    private ChatClientView view; // для отображения информации пользователю
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String strRegMessage;
    private String name; // имя пользователя, инициализируется при регистрации
    private boolean isRegistered = false;
    private boolean isWorked = true;

    public static void main(String[] args) {
        try {
            ChatClient chatClient = new ChatClient();
            ChatClientView view = new ConsoleChatClientView();
            chatClient.setView(view);
            chatClient.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException{
        showCommands();
        setStrRegMessage(view.getStrMessage());
        initStreams();
        register();
        Thread inThread = createInThread();
        inThread.setDaemon(true);
        inThread.start();
        createOutThread();
    }

    void register() throws IOException {
        if (strRegMessage.startsWith("/register agent ")) {
            setName(strRegMessage.substring(16, strRegMessage.length()));
            sendMessage(new Message(MessageType.AGENT_REG_MESSAGE, null, name));
        } else if (strRegMessage.startsWith("/register client ")) {
            setName(strRegMessage.substring(17, strRegMessage.length()));
            sendMessage(new Message(MessageType.CLIENT_REG_MESSAGE, null, name));
        } else if (strRegMessage.equals("/exit")) {
            System.exit(0);
        } else if (strRegMessage.equals("/leave")) {
            view.showStrMessage("You are not in a chat. Please register for start.");
            setStrRegMessage(view.getStrMessage());
            register();
        } else {
            view.showStrMessage("Invalid command. Please, try again.");
            setStrRegMessage(view.getStrMessage());
            register();
        }
    }

    void createOutThread() {
        // поток для отправки сообщений
        while (isWorked) {
            try {
                String strMessage = view.getStrMessage();
                if (strMessage.equals("/exit")) {
                    sendMessage(new Message(MessageType.EXIT_MESSAGE));
                    socket.close();
                    isWorked = false;
                } else if (strMessage.equals("/leave")) {
                    sendMessage(new Message(MessageType.LEAVE_MESSAGE));
                } else {
                    sendMessage(new Message(MessageType.TEXT_MESSAGE, strMessage, name));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Thread createInThread(){
        // поток для приема сообщений
        return new Thread(new Runnable() {
            public void run() {
                try {
                    while (isWorked) {
                        Message message = (Message) in.readObject();
                        if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
                            view.showStrMessage(message.getSender() + ": " + message.getText());
                        } else if (message.getType().equals(MessageType.LEAVE_MESSAGE)) {
                            view.showStrMessage(message.getSender() + ": " + message.getText());
                            sendMessage(new Message(MessageType.LEAVE_MESSAGE));
                        } else if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
                            socket.close();
                            isWorked = false;
                        }
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMessage(Message message) throws IOException {
            out.writeObject(message);
            out.flush();
    }

    private void initStreams() throws IOException {
        socket = new Socket(IP, PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    // показать пользавателю список возможных команд
    private void showCommands() {
        view.showStrMessage("Commands:");
        view.showStrMessage("/register client nickname");
        view.showStrMessage("/register agent nickname");
        view.showStrMessage("/exit");
        view.showStrMessage("/leave");
    }

    void setStrRegMessage(String strRegMessage) {
        this.strRegMessage = strRegMessage;
    }

    void setView(ChatClientView view) {
        this.view = view;
    }

    static int getPORT() {
        return PORT;
    }

    void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    void setIn(ObjectInputStream in) {
        this.in = in;
    }

    Socket getSocket() {
        return socket;
    }

    void setSocket(Socket socket) {
        this.socket = socket;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    ChatClientView getView() {
        return view;
    }

    public void setWorked(boolean worked) {
        isWorked = worked;
    }
}
