import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import message.Message;
import message.MessageType;
import user.Agent;
import user.Client;

public class ChatServer {
	private static final int PORT = 5556;
	private static final String SERVER_NAME = "Server";
	private final ConcurrentLinkedQueue<Agent> unemployedAgents = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<Connection> connections = new ConcurrentLinkedQueue<>();
	private final ExecutorService pool = Executors.newFixedThreadPool(100);
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		try {
			chatServer.start();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
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
			socketOutStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "You are registered like agent.", SERVER_NAME));
			addAgentInQueue(new Agent(regMessage.getSender(), socket, socketInStream, socketOutStream));
		} else if (regMessage.getType().equals(MessageType.CLIENT_REG_MESSAGE)) {
			socketOutStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "You are registered like client.", SERVER_NAME));
			Connection c = new Connection(this, new Client(regMessage.getSender(), socket, socketInStream, socketOutStream));
			pool.submit(c);
		}
	}

	public synchronized void addAgentInQueue(Agent agent) throws IOException {
		if (!connections.isEmpty()) {
			Connection c = connections.poll();
			c.setAgent(agent);
			c.handShake();
			c.sendBufferedMessages();
			pool.submit(c.getAgentClientThread());
		} else {
			unemployedAgents.add(agent);
		}
	}

	public synchronized void addConnectionInQueue(Connection c) throws IOException {
		if (!unemployedAgents.isEmpty()) {
			Agent agent = unemployedAgents.poll();
			c.setAgent(agent);
			c.handShake();
			pool.submit(c.getAgentClientThread());
		} else {
			connections.add(c);
		}
	}

	public ConcurrentLinkedQueue<Agent> getUnemployedAgents() {
		return unemployedAgents;
	}

	public ConcurrentLinkedQueue<Connection> getConnections() {
		return connections;
	}

	public String getServerName() {
		return SERVER_NAME;
	}
}
