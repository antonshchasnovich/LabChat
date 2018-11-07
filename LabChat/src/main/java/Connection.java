import java.io.IOException;
import java.util.ArrayDeque;

import message.Message;
import message.MessageType;
import user.Agent;
import user.Client;

//Поток, в котором мы слушаем агента создается при создании объекта этого класса
//Поток, в котором слушаем агента создается и возвращается методом getAgentClientThread()

public class Connection extends Thread {
	private boolean isConnected = true;
	private final Client client;
	private final ChatServer server;
	private final ArrayDeque<Message> bufferedMessages = new ArrayDeque<>(); // коллекция для хранения сообщений, написанных во время того как у клиента не
																				// было агента
	private Agent agent = null;

	public Connection(ChatServer server, Client client) {
		this.server = server;
		this.client = client;
	}

	// client-agent thread
	@Override
	public void run() {
		while (isConnected) {
			try {
				Message message = (Message) client.getInStream().readObject();
				if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
					server.getConnections().remove(this);
					if (agent != null) {
						sendToAgent(new Message(MessageType.LEAVE_MESSAGE, client.getName() + " leaved chat.", server.getServerName()));
					}
					isConnected = false;
				} else if (message.getType().equals(MessageType.LEAVE_MESSAGE)) {
					if (agent != null) {
						sendToAgent(new Message(MessageType.LEAVE_MESSAGE, client.getName() + " leaved chat.", server.getServerName()));
					}
				} else if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
					if (agent != null) {
						sendToAgent(message);
					} else {
						sendToClient(new Message(MessageType.TEXT_MESSAGE, "Agent is being selected. Please wait...", server.getServerName()));
						if (!server.getConnections().contains(this)) {
							server.addConnectionInQueue(Connection.this);
						}
						bufferedMessages.add(message);
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// returns agent-client thread
	public Thread getAgentClientThread() {
		return new Thread(new Runnable() {
			public void run() {
				while (isConnected && agent != null) {
					try {
						Message message = (Message) agent.getInStream().readObject();
						if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
							String strMessage = "Agent " + agent.getName() + " leaved chat. You will be connected with another agent.";
							sendToClient(new Message(MessageType.TEXT_MESSAGE, strMessage, server.getServerName()));
							agent = null;
							server.addConnectionInQueue(Connection.this);
						} else if (message.getType().equals(MessageType.LEAVE_MESSAGE)) {
							server.addAgentInQueue(agent);
							agent = null;
						} else if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
							if (agent != null && isConnected) {
								client.getOutStream().writeObject(message);
							}
						}
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void handShake() throws IOException {
		sendToClient(new Message(MessageType.TEXT_MESSAGE, "You are sent to agent " + agent.getName(), server.getServerName()));
		sendToAgent(new Message(MessageType.TEXT_MESSAGE, "Client " + client.getName() + " sent to you.", server.getServerName()));
	}

	public void sendBufferedMessages() throws IOException {
		while (!bufferedMessages.isEmpty()) {
			agent.getOutStream().writeObject(bufferedMessages.poll());
		}
	}

	private synchronized void sendToAgent(Message message) throws IOException {
		agent.getOutStream().writeObject(message);
	}

	private synchronized void sendToClient(Message message) throws IOException {
		client.getOutStream().writeObject(message);
	}
}
