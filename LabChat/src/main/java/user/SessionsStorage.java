package user;

import message.Message;
import message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;

public class SessionsStorage {
    private Logger logger = LoggerFactory.getLogger(SessionsStorage.class);
    private final static String SERVER_NAME = "Server";
    private final HashMap<Session, User> allUsers = new HashMap<>();
    private ArrayDeque<Agent> freeAgents = new ArrayDeque<>();
    private ArrayDeque<Client> waitingClients = new ArrayDeque<>();

    public void regAgent(Session session, Message msg) throws IOException, EncodeException {
        Agent agent = new Agent(session, msg.getName(), msg.getIndex());
        allUsers.put(agent.getSession(), agent);
        session.getBasicRemote().sendObject(new Message(SERVER_NAME, "You registered like agent.", MessageType.SERVER_MESSAGE));
        logger.info("Agent " + msg.getName() + " registered.");
        tryFindCompanion(agent);
    }

    public void regClient(Session session, Message msg) throws IOException, EncodeException {
        Client client = new Client(session, msg.getName());
        allUsers.put(client.getSession(), client);
        session.getBasicRemote().sendObject(new Message(SERVER_NAME, "You registered like client.", MessageType.SERVER_MESSAGE));
        logger.info("Client " + msg.getName() + " registered.");
        tryFindCompanion(client);
    }

    synchronized void tryFindCompanion(Agent agent) throws IOException, EncodeException {
        if (waitingClients.isEmpty()) {
            freeAgents.addLast(agent);
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
        if(agent.isReady()){
            tryFindCompanion(agent);
        }
    }

    public void sendMessage(Session session, Message message) throws IOException, EncodeException {
        User user = allUsers.get(session);
         if (user.getCompanion(message.getIndex()) == null && user instanceof Client && !waitingClients.contains(user)) {
            tryFindCompanion((Client) user);
        } else if (user.getCompanion(message.getIndex()) == null && user instanceof Agent && !freeAgents.contains(user)) {
            tryFindCompanion((Agent) user);
        }
        user.sendMessageToCompanion(message);
    }

    public void leaveChat(Session session, Message msg) throws IOException, EncodeException {
            int index = msg.getIndex();
        if(allUsers.get(session).isChatting(index)){
            session.getBasicRemote().sendObject(new Message(SERVER_NAME, "You lived chat.", MessageType.SERVER_MESSAGE,
                    index));
            User user = allUsers.get(session);
            User companion = user.getCompanion(index);
            disconnect(session, index);
            if(user instanceof Client){
                tryFindCompanion((Agent)companion);
            }else if(user instanceof Agent){
                tryFindCompanion((Client) companion);
                tryFindCompanion((Agent)user);
            }
        }
    }

    public synchronized void exitChat(Session session) throws IOException, EncodeException {
        User user = allUsers.get(session);
        for (int i = 0; i < user.getCompanionsNumber(); i++) {
            User companion = user.getCompanion(i);
            disconnect(session, i);
            if (user instanceof Client) {
                waitingClients.remove(user);
                if (companion!=null){
                    tryFindCompanion((Agent) companion);
                }
            } else if (user instanceof Agent) {
                freeAgents.remove(user);
                if (companion!=null){
                    tryFindCompanion((Client) companion);
                }
            }
        }
        allUsers.remove(session);
    }

    private void disconnect(Session session, int index) throws IOException, EncodeException {
        User user = allUsers.get(session);
        User companion = user.getCompanion(index);
        if(companion != null && user instanceof Client){
            logger.info("Agent " + companion.getName() + " and client " + user.getName() + " finished chat.");
            companion.sendMessage(new Message(SERVER_NAME, "Client " + user.getName() + " lived chat.",
                    MessageType.SERVER_MESSAGE, ((Client) user).getIndex()));
            companion.removeCompanion(((Client) user).getIndex());
            user.removeCompanion(index);
        }else if(companion != null && user instanceof Agent){
            companion.sendMessage(new Message(SERVER_NAME, "Agent " + user.getName() + " lived chat. You will be sended to " +
                    "another agent.", MessageType.SERVER_MESSAGE));
            logger.info("Agent " + user.getName() + " and client " + companion.getName() + " finished chat.");
            companion.removeCompanion(0);
            user.removeCompanion(index);
        }
    }

    private void handshake(Agent agent, Client client) throws IOException, EncodeException {
        agent.sendMessage(new Message(SERVER_NAME, "Client " + client.getName() + " sended to you.", MessageType.SERVER_MESSAGE
                , client.getIndex()));
        client.sendMessage(new Message(SERVER_NAME, "You sended to agent " + agent.getName(), MessageType.SERVER_MESSAGE));
        logger.info("Agent " + agent.getName() + " and client " + client.getName() + " started chat.");
        if (!client.getHistory().isEmpty()){
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

    HashMap<Session, User> getAllUsers() {
        return allUsers;
    }

    ArrayDeque<Agent> getFreeAgents() {
        return freeAgents;
    }

    ArrayDeque<Client> getWaitingClients() {
        return waitingClients;
    }


}
