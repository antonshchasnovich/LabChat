package chatClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import chatClient.view.ChatClientView;
import chatClient.view.ConsoleChatClientView;
import message.Message;
import message.MessageType;

public class ChatClient {
	private final static String IP = "localhost";
	private final static int PORT = 5556;

	private ChatClientView view;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String name;

	public static void main(String[] args) {
		try {
			ChatClient chatClient = new ChatClient();
			ChatClientView view = new ConsoleChatClientView();
			chatClient.setView(view);
			chatClient.showCommands();
			chatClient.register();
			chatClient.createThreads();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void register() throws IOException {
		String strRegMessage = view.getStrMessage();
		if (strRegMessage.startsWith("/register agent ")) {
			initStreams();
			name = strRegMessage.substring(16, strRegMessage.length());
			sendMessage(new Message(MessageType.AGENT_REG_MESSAGE, null, name));
		} else if (strRegMessage.startsWith("/register client ")) {
			initStreams();
			name = strRegMessage.substring(17, strRegMessage.length());
			sendMessage(new Message(MessageType.CLIENT_REG_MESSAGE, null, name));
		} else if (strRegMessage.equals("/exit")) {
			System.exit(0);
		} else if (strRegMessage.equals("/leave")) {
			view.showStrMessage("You are not in a chat. Please register for start.");
			register();
		} else {
			view.showStrMessage("Invalid command. Please, try again.");
			register();
		}
	}

	private void createThreads() {
		Thread inThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Message message = (Message) in.readObject();
						if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
							view.showStrMessage(message.getSender() + ": " + message.getText());
						} else if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
							view.showStrMessage(message.getSender() + ": " + message.getText());
							sendMessage(new Message(MessageType.EXIT_MESSAGE));
						}
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		});

		Thread outThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						String strMessage = view.getStrMessage();
						if (strMessage.equals("/exit")) {
							sendMessage(new Message(MessageType.EXIT_MESSAGE));
							socket.close();
							System.exit(0);
						} else if (strMessage.equals("/leave")) {
							sendMessage(new Message(MessageType.LEAVE_MESSAGE));
						} else {
							sendMessage(new Message(MessageType.TEXT_MESSAGE, strMessage, name));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		inThread.start();
		outThread.start();
	}

	private void sendMessage(Message message) throws IOException {
		if (!socket.isClosed()) {
			out.writeObject(message);
		} else {
			view.showStrMessage("Message is not sended. Connection is closed.");
		}
	}

	private void initStreams() throws IOException {
		socket = new Socket(IP, PORT);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}

	private void showCommands() {
		view.showStrMessage("Commands:");
		view.showStrMessage("/register client nickname");
		view.showStrMessage("/register agent nickname");
		view.showStrMessage("/exit");
		view.showStrMessage("/leave");
	}

	public void setView(ChatClientView view) {
		this.view = view;
	}
}
