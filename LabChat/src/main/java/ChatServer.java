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
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		try {
			chatServer.start();
		} catch (IOException | ClassNotFoundException e) {
			chatServer.logger.warn(e.toString());
		}
	}

	private void start() throws IOException, ClassNotFoundException {
		serverSocket = new ServerSocket(PORT);
		System.out.println("Server started...");
		while (true) {
			Socket socket = serverSocket.accept();
			registerUser(socket);
		}
	}

	private void registerUser(Socket socket) throws IOException, ClassNotFoundException {
		ObjectOutputStream socketOutStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream socketInStream = new ObjectInputStream(socket.getInputStream());
		Message regMessage = (Message) socketInStream.readObject();
		if (regMessage.getType().equals(MessageType.AGENT_REG_MESSAGE)) {
			logger.info("Agent " + regMessage.getSender() + " registered.");
			socketOutStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "You are registered like agent.", SERVER_NAME));
			socketOutStream.flush();
			usersStorage.addAgentInQueue(new Agent(regMessage.getSender(), socket, socketInStream, socketOutStream));
		} else if (regMessage.getType().equals(MessageType.CLIENT_REG_MESSAGE)) {
			logger.info("Client " + regMessage.getSender() + " registered.");
			socketOutStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "You are registered like client.", SERVER_NAME));
			socketOutStream.flush();
			Connection c = new Connection(usersStorage, new Client(regMessage.getSender(), socket, socketInStream, socketOutStream), SERVER_NAME);
			c.setServerName(SERVER_NAME);
			c.setLogger(logger);
			usersStorage.getPool().submit(c);
		}
	}

	public String getServerName() {
		return SERVER_NAME;
	}

	public Logger getLogger() {
		return logger;
	}
}
