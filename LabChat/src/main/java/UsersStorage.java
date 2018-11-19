import message.Message;
import message.MessageType;
import user.Agent;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersStorage {
    private final ConcurrentLinkedQueue<Agent> unemployedAgents = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Connection> connections = new ConcurrentLinkedQueue<>();
    private ExecutorService pool = Executors.newFixedThreadPool(100);

    public synchronized void addAgentInQueue(Agent agent) {
        if (!connections.isEmpty()) {
            Connection c = connections.poll();
            c.setAgent(agent);
            pool.submit(c.getAgentClientThread());
        } else {
            unemployedAgents.add(agent);
        }
    }

    public synchronized void addConnectionInQueue(Connection c) throws IOException {
        if (!unemployedAgents.isEmpty()) {
            try {
                Agent agent = unemployedAgents.poll();
                agent.getOutStream().writeObject(new Message(MessageType.TEST_MESSAGE));
                c.setAgent(agent);
                pool.submit(c.getAgentClientThread());
            } catch (SocketException e) {
                addConnectionInQueue(c);
            }
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

    public ExecutorService getPool(){
        return pool;
    }

    public void setPool(ExecutorService pool) {
        this.pool = pool;
    }
}
