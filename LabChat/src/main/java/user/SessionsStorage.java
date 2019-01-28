package user;

import message.Message;
import message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.chat.Chat;
import user.httpUsers.HttpUser;
import user.httpUsers.MessageStorage;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;

public class SessionsStorage {
    private static SessionsStorage instance;
    private Logger logger = LoggerFactory.getLogger(SessionsStorage.class);
    private final static String SERVER_NAME = "Server";
    private final HashMap<Object, User> allUsers = new HashMap<>();
    private ArrayDeque<Agent> freeAgents = new ArrayDeque<>();
    private ArrayDeque<Client> waitingClients = new ArrayDeque<>();
    private final HashMap<Long, Agent> allAgents = new HashMap<>();
    private final HashMap<Long, Client> allClients = new HashMap<>();
    private final HashMap<Long, Chat> allChats = new HashMap<>();

    private SessionsStorage() {
    }

    public static synchronized SessionsStorage getInstance() {
        if (instance == null) {
            instance = new SessionsStorage();
        }
        return instance;
    }

    public void regAgent(Agent agent) throws IOException, EncodeException {
        allUsers.put(agent.getSession(), agent);
        allAgents.put(agent.id, agent);
        agent.sendMessage(new Message(SERVER_NAME, "You registered like agent.", MessageType.SERVER_MESSAGE));
        logger.info("Agent " + agent.getName() + " registered.");
        tryFindCompanion(agent);
    }

    public void regClient(Client client) throws IOException, EncodeException {
        allUsers.put(client.getSession(), client);
        allClients.put(client.id, client);
        client.sendMessage(new Message(SERVER_NAME, "You registered like client.", MessageType.SERVER_MESSAGE));
        logger.info("Client " + client.getName() + " registered.");
        tryFindCompanion(client);
    }

    synchronized void tryFindCompanion(Agent agent) throws IOException, EncodeException {
        if (waitingClients.isEmpty()) {
            if (!freeAgents.contains(agent)) {
                freeAgents.addLast(agent);
            }
        } else {
            Client client = waitingClients.poll();
            createChat(agent, client);
        }
    }

    synchronized void tryFindCompanion(Client client) throws IOException, EncodeException {
        if (freeAgents.isEmpty()) {
            waitingClients.addLast(client);
            client.sendMessage(new Message(SERVER_NAME, "There are no free agents now. Please wait...",
                    MessageType.SERVER_MESSAGE));
        } else {
            Agent agent = freeAgents.poll();
            createChat(agent, client);
        }
    }

    private void createChat(Agent agent, Client client) throws IOException, EncodeException {
        int index = agent.setCompanion(client);
        client.setCompanion(agent);
        client.setIndex(index);
        handshake(agent, client);
        Chat chat = new Chat(agent, client);
        long chatId = chat.getId();
        allChats.put(chatId, chat);
        agent.addCurrentChatId(index, chatId);
        client.addCurrentChatId(0, chatId);
        if (agent.isReady()) {
            tryFindCompanion(agent);
        }
    }

    public void sendMessage(Object session, Message message) throws IOException, EncodeException {
        User user = allUsers.get(session);
        if (user.getCompanion(message.getIndex()) == null && user instanceof Client && !waitingClients.contains(user)) {
            tryFindCompanion((Client) user);
        } else if (user.getCompanion(message.getIndex()) == null && user instanceof Agent && !freeAgents.contains(user)) {
            tryFindCompanion((Agent) user);
        }
        user.sendMessageToCompanion(message);
    }

    public void leaveChat(Object session, int index) throws IOException, EncodeException {
        if (allUsers.get(session).isChatting(index)) {
            User user = allUsers.get(session);
            User companion = user.getCompanion(index);
            disconnect(session, index);
            user.sendMessage(new Message(SERVER_NAME, "You lived chat.", MessageType.SERVER_MESSAGE,
                    index));
            if (user instanceof Client) {
                tryFindCompanion((Agent) companion);
            } else if (user instanceof Agent) {
                tryFindCompanion((Client) companion);
                tryFindCompanion((Agent) user);
            }
        }
    }

    public synchronized void exitChat(Object session) throws IOException, EncodeException {
        User user = allUsers.get(session);
        for (int i = 0; i < user.getMaxCompanionsNumber(); i++) {
            User companion = user.getCompanion(i);
            disconnect(session, i);
            if (user instanceof Client) {
                allClients.remove(user.id);
                waitingClients.remove(user);
                if (companion != null) {
                    tryFindCompanion((Agent) companion);
                }
            } else if (user instanceof Agent) {
                allAgents.remove(user.id);
                freeAgents.remove(user);
                if (companion != null) {
                    tryFindCompanion((Client) companion);
                }
            }
        }
        if (user instanceof HttpUser){
            MessageStorage.getInstance().removeMessages(user.getId());
        }
        allUsers.remove(session);
    }

    private void disconnect(Object session, int index) throws IOException, EncodeException {
        User user = allUsers.get(session);
        User companion = user.getCompanion(index);
        allChats.remove(user.currentChatsId[index]);
        user.removeCurrentChatId(index);
        if (companion != null && user instanceof Client) {
            companion.removeCurrentChatId(((Client) user).getIndex());
            companion.removeCompanion(((Client) user).getIndex());
            user.removeCompanion(index);
            companion.sendMessage(new Message(SERVER_NAME, "Client " + user.getName() + " lived chat.",
                    MessageType.SERVER_MESSAGE, ((Client) user).getIndex()));
            logger.info("Agent " + companion.getName() + " and client " + user.getName() + " finished chat.");
        } else if (companion != null && user instanceof Agent) {
            companion.removeCurrentChatId(0);
            companion.removeCompanion(0);
            user.removeCompanion(index);
            companion.sendMessage(new Message(SERVER_NAME, "Agent " + user.getName() + " lived chat. You will be sended to " +
                    "another agent.", MessageType.SERVER_MESSAGE));
            logger.info("Agent " + user.getName() + " and client " + companion.getName() + " finished chat.");
        }
    }

    private void handshake(Agent agent, Client client) throws IOException, EncodeException {
        agent.sendMessage(new Message(SERVER_NAME, "Client " + client.getName() + " sended to you.", MessageType.SERVER_MESSAGE
                , client.getIndex()));
        client.sendMessage(new Message(SERVER_NAME, "You sended to agent " + agent.getName(), MessageType.SERVER_MESSAGE));
        logger.info("Agent " + agent.getName() + " and client " + client.getName() + " started chat.");
        if (!client.getHistory().isEmpty()) {
            client.sendHistory();
        }
        client.sendBufferedMessages();
    }


    void setLogger(Logger logger) {
        this.logger = logger;
    }

    void setFreeAgents(ArrayDeque<Agent> freeAgents) {
        this.freeAgents = freeAgents;
    }

    void setWaitingClients(ArrayDeque<Client> waitingClients) {
        this.waitingClients = waitingClients;
    }

    public Logger getLogger() {
        return logger;
    }

    public HashMap<Object, User> getAllUsers() {
        return allUsers;
    }

    public ArrayDeque<Agent> getFreeAgents() {
        return freeAgents;
    }

    public ArrayDeque<Client> getWaitingClients() {
        return waitingClients;
    }


    public HashMap<Long, Agent> getAllAgents() {
        return allAgents;
    }

    public HashMap<Long, Client> getAllClients() {
        return allClients;
    }

    public HashMap<Long, Chat> getAllChats() {
        return allChats;
    }


}
