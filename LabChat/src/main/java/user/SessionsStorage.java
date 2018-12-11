package user;

import message.Message;
import message.MessageType;
import org.slf4j.Logger;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;

public class SessionsStorage {
    private Logger logger;
    private final String serverName;
    private final HashMap<Session, User> allUsers = new HashMap<>();
    private final ArrayDeque<Agent> freeAgents = new ArrayDeque<>();
    private final ArrayDeque<Client> waitingClients = new ArrayDeque<>();

    public SessionsStorage(String serverName) {
        this.serverName = serverName;
    }

    public synchronized void addAgent(Agent agent) throws IOException, EncodeException {
        allUsers.put(agent.getSession(), agent);
        if (waitingClients.isEmpty()) {
            freeAgents.addLast(agent);
        } else {
            Client client = waitingClients.poll();
            client.setCompanion(agent);
            agent.setCompanion(client);
            handshake(agent, client);
        }
    }

    public synchronized void addClient(Client client) throws IOException, EncodeException {
        allUsers.put(client.getSession(), client);
        if (freeAgents.isEmpty()) {
            waitingClients.addLast(client);
            client.sendMessage(new Message(serverName, "There are no free agents now. Please wait...", MessageType.SERVER_MESSAGE));
        } else {
            Agent agent = freeAgents.poll();
            agent.setCompanion(client);
            client.setCompanion(agent);
            handshake(agent, client);
        }
    }

    public void sendMessage(Session session, Message message) throws IOException, EncodeException {
        User user = allUsers.get(session);
         if (user.getCompanion() == null && user instanceof Client && !waitingClients.contains(user)) {
            addClient((Client) user);
        } else if (user.getCompanion() == null && user instanceof Agent && !freeAgents.contains(user)) {
            addAgent((Agent) user);
        }
        user.sendMessageToCompanion(message);
    }

    public void leaveChat(Session session) throws IOException, EncodeException {
        User user = allUsers.get(session);
        User companion = user.getCompanion();
        disconnect(session);
        if(user instanceof Client){
            companion.removeCompanion();
            addAgent((Agent)companion);
        }else if(user instanceof Agent){
            addClient((Client) companion);
            addAgent((Agent)user);
        }
    }

    public synchronized void exitChat(Session session) throws IOException, EncodeException {
        User user = allUsers.get(session);
        User companion = user.getCompanion();
        disconnect(session);
        if(user instanceof Client){
            waitingClients.remove(user);
            addAgent((Agent)companion);
        }else if(user instanceof Agent){
            freeAgents.remove(user);
            addClient((Client) companion);
        }
        allUsers.remove(session);
    }

    private void disconnect(Session session) throws IOException, EncodeException {
        User user = allUsers.get(session);
        User companion = user.getCompanion();
        if(user instanceof Client){
            companion.sendMessage(new Message(serverName, "Client " + user.getName() + " lived chat.", MessageType.SERVER_MESSAGE));
            logger.info("Agent " + companion.getName() + " and client " + user.getName() + " finished chat.");
            companion.removeCompanion();
            user.removeCompanion();
        }else if(user instanceof Agent){
            companion.sendMessage(new Message(serverName, "Agent " + user.getName() + " lived chat. You will be sended to another agent.", MessageType.SERVER_MESSAGE));
            logger.info("Agent " + user.getName() + " and client " + companion.getName() + " finished chat.");
            companion.removeCompanion();
            user.removeCompanion();
        }
    }

    private void handshake(Agent agent, Client client) throws IOException, EncodeException {
        agent.sendMessage(new Message(serverName, "Client " + client.getName() + " sended to you.", MessageType.SERVER_MESSAGE));
        client.sendMessage(new Message(serverName, "You sended to agent " + agent.getName(), MessageType.SERVER_MESSAGE));
        logger.info("Agent " + agent.getName() + " and client " + client.getName() + " started chat.");
        if (!client.getHistory().isEmpty()){
            client.sendStory();
        }
        client.sendBufferedMessages();
    }


    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
