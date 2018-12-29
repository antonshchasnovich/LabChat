package user;

import javax.websocket.Session;
import java.util.HashMap;

public class Pairs {

    private final HashMap<Session, Session> agentClient = new HashMap<>();
    private final HashMap<Session, Session> clientAgent = new HashMap<>();

    public void put(Session agent, Session client){
        agentClient.put(agent, client);
        clientAgent.put(client, agent);
    }

    public boolean containsClient(Session session){
        return clientAgent.containsKey(session);
    }

    public boolean containsAgent(Session session) {
        return agentClient.containsKey(session);
    }

    public Session getClient(Session agent){
        return agentClient.get(agent);
    }

    public Session getAgent(Session client){
        return clientAgent.get(client);
    }

    public void remove(Session agent, Session client){
        agentClient.remove(agent);
        clientAgent.remove(client);
    }
}
