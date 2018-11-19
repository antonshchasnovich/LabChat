import message.Message;
import message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Agent;
import user.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer {
	private static final int PORT = 5556;
	private static final String SERVER_NAME = "Server";
	private final UsersStorage usersStorage = new UsersStorage();
	private final Logger logger = LoggerFactory.getLogger(ChatServer.class);
	private Message regMessage;
	private Socket socket;
	private ObjectOutputStream socketOutStream;
	private ObjectInputStream socketInStream;

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		try {
			chatServer.start();
		} catch (IOException | ClassNotFoundException e) {
			chatServer.logger.warn(e.toString());
		}
	}

	private void start() throws IOException, ClassNotFoundException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server started...");
		while (true) {
			socket = serverSocket.accept();
			initStreams();
			getMessageFromUser();
			registerUser();
		}
	}

	void registerUser() throws IOException {
		if (regMessage.getType().equals(MessageType.AGENT_REG_MESSAGE)) {
			logger.info("Agent " + regMessage.getSender() + " registered.");
			sendMessageToUser(new Message(MessageType.TEXT_MESSAGE, "You are registered like agent.", SERVER_NAME));
			usersStorage.addAgentInQueue(new Agent(regMessage.getSender(), socket, socketInStream, socketOutStream));
		} else if (regMessage.getType().equals(MessageType.CLIENT_REG_MESSAGE)) {
			logger.info("Client " + regMessage.getSender() + " registered.");
			sendMessageToUser(new Message(MessageType.TEXT_MESSAGE, "You are registered like client.", SERVER_NAME));
			Connection c = new Connection(usersStorage, new Client(regMessage.getSender(), socket, socketInStream, socketOutStream), SERVER_NAME);
			c.setServerName(SERVER_NAME);
			c.setLogger(logger);
			usersStorage.getPool().submit(c);
		}
	}

	void getMessageFromUser() throws IOException, ClassNotFoundException{
		regMessage = (Message) socketInStream.readObject();
	}

	void sendMessageToUser(Message message) throws IOException {
		socketOutStream.writeObject(message);
		socketOutStream.flush();
	}

	private void initStreams() throws IOException {
		socketOutStream = new ObjectOutputStream(socket.getOutputStream());
		socketInStream = new ObjectInputStream(socket.getInputStream());
	}

	UsersStorage getUsersStorage() {
		return usersStorage;
	}

	void setRegMessage(Message regMessage) {
		this.regMessage = regMessage;
	}

	void setSocketOutStream(ObjectOutputStream socketOutStream) {
		this.socketOutStream = socketOutStream;
	}

	void setSocketInStream(ObjectInputStream socketInStream) {
		this.socketInStream = socketInStream;
	}

	Message getRegMessage() {
		return regMessage;
	}
}
