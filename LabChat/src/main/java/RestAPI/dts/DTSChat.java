package RestAPI.dts;

import user.chat.Chat;

public class DTSChat {
    private long id;
    private long clientId;
    private long agentId;

    public DTSChat(Chat chat) {
        id = chat.getId();
        clientId = chat.getClient().getId();
        agentId = chat.getAgent().getId();
    }
}
