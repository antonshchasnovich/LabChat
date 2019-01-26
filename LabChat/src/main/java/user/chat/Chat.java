package user.chat;

import user.Agent;
import user.Client;
import util.IdGenerator;

public class Chat {
    private int id;
    private Agent agent;
    private Client client;

    public Chat(Agent agent, Client client) {
        id = IdGenerator.getInstance().getChatId();
        this.agent = agent;
        this.client = client;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", agent=" + agent +
                ", client=" + client +
                '}';
    }

    public int getId() {
        return id;
    }

    public Agent getAgent() {
        return agent;
    }

    public Client getClient() {
        return client;
    }
}
