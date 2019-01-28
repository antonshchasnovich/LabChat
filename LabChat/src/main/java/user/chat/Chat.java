package user.chat;

import user.Agent;
import user.Client;
import util.IdGenerator;

import java.util.Date;

public class Chat {
    private int id;
    private Agent agent;
    private Client client;
    private Date startTime;

    public Chat(Agent agent, Client client) {
        id = IdGenerator.getInstance().getChatId();
        this.agent = agent;
        this.client = client;
        startTime = new Date();
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

    public Date getStartTime() {
        return startTime;
    }
}
