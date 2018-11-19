import message.Message;
import message.MessageType;
import org.slf4j.Logger;
import user.Agent;
import user.Client;

import java.io.IOException;
import java.util.ArrayDeque;

//Поток, в котором мы слушаем агента создается при создании объекта этого класса
//Поток, в котором слушаем агента создается и возвращается методом getAgentClientThread()

public class Connection extends Thread {
    private boolean isConnected = true;
    private final Client client;
    private final UsersStorage usersStorage;
    private final ArrayDeque<Message> bufferedMessages = new ArrayDeque<>();
    private String serverName;
    private Logger logger;

    private Agent agent = null;

    public Connection(UsersStorage usersStorage, Client client, String serverName) {
        this.usersStorage = usersStorage;
        this.client = client;
        this.serverName = serverName;
    }

    // client-agent thread
    @Override

    public void run() {
        while (isConnected) {
            try {
                Message message;
                try{
                    message = (Message) client.getInStream().readObject();
                }catch(IOException e){
                    close();
                    break;
                }
                if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
                    close();
                } else if (message.getType().equals(MessageType.LEAVE_MESSAGE)) {
                    if (agent != null) {
                        sendToAgent(new Message(MessageType.LEAVE_MESSAGE, client.getName() + " leaved chat.", serverName));
                        logger.info("Agent " + agent.getName() + " and client " + client.getName() + " finished chat.");
                    }
                } else if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
                    if (agent != null) {
                        sendToAgent(message);
                    } else {
                        sendToClient(new Message(MessageType.TEXT_MESSAGE, "Agent is being selected. Please wait...", serverName));
                        bufferedMessages.add(message);
                        if (!usersStorage.getConnections().contains(this)) {
                            usersStorage.addConnectionInQueue(Connection.this);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.warn(e.toString());
            }
        }
    }

    // returns agent-client thread
    public Thread getAgentClientThread() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    handShake();
                    sendBufferedMessages();
                    while (isConnected && agent != null) {
                        Message message;
                        try{
                            message = (Message) agent.getInStream().readObject();
                        }catch (IOException e){
                            closeAgentThread();
                            break;
                        }
                        if (message.getType().equals(MessageType.EXIT_MESSAGE)) {
                            closeAgentThread();
                        } else if (message.getType().equals(MessageType.LEAVE_MESSAGE)) {
                            usersStorage.addAgentInQueue(agent);
                            agent = null;
                        } else if (message.getType().equals(MessageType.TEXT_MESSAGE)) {
                            if (agent != null && isConnected) {
                                client.getOutStream().writeObject(message);
                            }
                        }
                    }
                } catch (ClassNotFoundException |
                        IOException e) {
                    logger.warn(e.toString());
                }
            }
        });
    }

    private void close()throws IOException{
        usersStorage.getConnections().remove(this);
        if (agent != null) {
            sendToAgent(new Message(MessageType.LEAVE_MESSAGE, client.getName() + " leaved chat.", serverName));
            logger.info("Agent " + agent.getName() + " and client " + client.getName() + " finished chat.");
        }
        isConnected = false;
    }

    private void closeAgentThread() throws IOException{
        String strMessage = "Agent " + agent.getName() + " leaved chat. You will be connected with another agent.";
        sendToClient(new Message(MessageType.TEXT_MESSAGE, strMessage, serverName));
        logger.info("Agent " + agent.getName() + " and client " + client.getName() + " finished chat.");
        agent = null;
        usersStorage.addConnectionInQueue(Connection.this);
    }

    public Agent getAgent() {
        return agent;
    }


    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void handShake() throws IOException {
        sendToClient(new Message(MessageType.TEXT_MESSAGE, "You are sent to agent " + agent.getName(), serverName));
        sendToAgent(new Message(MessageType.TEXT_MESSAGE, "Client " + client.getName() + " sent to you.", serverName));
        logger.info("Agent " + agent.getName() + " and client " + client.getName() + " started chat.");
    }

    public void sendBufferedMessages() throws IOException {
        while (!bufferedMessages.isEmpty()) {
            sendToAgent(bufferedMessages.poll());
        }
    }

    private synchronized void sendToAgent(Message message) throws IOException {
        agent.getOutStream().writeObject(message);
        agent.getOutStream().flush();
    }

    private synchronized void sendToClient(Message message) throws IOException {
        client.getOutStream().writeObject(message);
        client.getOutStream().flush();
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
