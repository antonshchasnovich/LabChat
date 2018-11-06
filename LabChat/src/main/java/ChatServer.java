import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import message.Message;
import message.MessageType;
import user.Agent;
import user.Client;

public class ChatServer {
	private static final int PORT = 5556;
	private static final String SERVER_NAME = "Server";
	private ConcurrentLinkedQueue<Agent> unemployedAgents = new ConcurrentLinkedQueue<>();
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
		try {
			chatServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	// в цикле регистрирую пользователей
	// если агент то отправляю его в очередь свободных агентов
	// если клиент то создаю отдельный поток для его обслуживания
	private void start() throws IOException, ClassNotFoundException {
		serverSocket = new ServerSocket(PORT);
		System.out.println("Server started...");
		while (true) {
			Socket socket = serverSocket.accept();
			ObjectOutputStream socketOutStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream socketInStream = new ObjectInputStream(socket.getInputStream());

			Message regMessage = (Message) socketInStream.readObject();
			if (regMessage.getType().equals(MessageType.AGENT_REG_MESSAGE)) {
				unemployedAgents.add(new Agent(regMessage.getSender(), socket, socketInStream, socketOutStream));
				socketOutStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "You are registered like agent.", SERVER_NAME));
			} else if (regMessage.getType().equals(MessageType.CLIENT_REG_MESSAGE)) {
				new Thread(new Connection(new Client(regMessage.getSender(), socket, socketInStream, socketOutStream))).start();
				socketOutStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "You are registered like client.", SERVER_NAME));
			}

		}
	}

	// подбор агента из очереди
	private synchronized Agent selectAgent() throws InterruptedException {
		while (true) {
			if (!unemployedAgents.isEmpty()) {
				Agent agent = unemployedAgents.poll();
				try {
					agent.getOutStream().writeObject(new Message(MessageType.TEST_MESSAGE));
					return agent;
				} catch (IOException e) {
					try {
						agent.getSocket().close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				// если очередь пуста то проверяю ее раз в 1 сек
				Thread.sleep(1000);
			}
		}
	}

	// Класс для создания потоков после регистрации клиента
	public class Connection implements Runnable {
		final private Client client;
		volatile boolean isConnected;
		volatile private Agent agent = null;

		public Connection(Client client) {
			this.client = client;
		}

		public void run() {
			try {

				isConnected = true;

				// agent-client thread
				// в цикле пересылка сообщений от агента к клиенту
				new Thread(new Runnable() {
					public void run() {
						while (isConnected) {
							if (agent != null) {
								try {
									Message message = (Message) agent.getInStream().readObject();
									if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
										String strMessage = "Agent " + agent.getName() + " leaved chat. You will be connected with another agent.";
										client.getOutStream().writeObject(new Message(MessageType.TEXT_MESSAGE, strMessage, SERVER_NAME));
										agent = null;
									} else if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
										if (agent != null && isConnected) {
											client.getOutStream().writeObject(message);
										}
									}
								} catch (ClassNotFoundException | IOException e) {
									e.printStackTrace();
								}
							} else {
								try {
									// если сейчас момент для данного клиента нет агента проверить снова через 300мс
									Thread.sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}).start();

				// client-agent thread
				// в цикле пересылка сообщений от клиента к агенту
				while (isConnected) {
					Message message = (Message) client.getInStream().readObject();
					if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
						agent.getOutStream().writeObject(new Message(MessageType.EXIT_MESSAGE, client.getName() + " leaved chat.", "Server"));
						unemployedAgents.add(agent);
						isConnected = false;
					} else if (message.getType().equals(MessageType.LEAVE_MESSAGE)) {
						agent.getOutStream().writeObject(new Message(MessageType.EXIT_MESSAGE, client.getName() + " leaved chat.", "Server"));
						unemployedAgents.add(agent);
						agent = null;
					} else if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
						if (agent == null) {
							String strMessage1 = "Agent is being selected. Please wait...";
							client.getOutStream().writeObject(new Message(MessageType.TEXT_MESSAGE, strMessage1, SERVER_NAME));
							agent = selectAgent();
							String strMessage2 = "You are sent to agent " + agent.getName();
							client.getOutStream().writeObject(new Message(MessageType.TEXT_MESSAGE, strMessage2, SERVER_NAME));
							String strMessage3 = "Client " + client.getName() + " sent to you.";
							agent.getOutStream().writeObject(new Message(MessageType.TEXT_MESSAGE, strMessage3, SERVER_NAME));
						}
						agent.getOutStream().writeObject((Message) message);
					}
				}

			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
