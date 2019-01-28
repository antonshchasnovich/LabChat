package RestAPI.dts;

import user.chat.Chat;

public class DTSChat {
    private int id;
    private int clientId;
    private int agentId;

    public DTSChat(Chat chat) {
        id = chat.getId();
        clientId = chat.getClient().getId();
        agentId = chat.getAgent().getId();
    }
}
