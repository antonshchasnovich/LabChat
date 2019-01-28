package RestAPI.dts;

import user.chat.Chat;

import java.util.Date;

public class AllInfoAboutChat {
    private int id;
    private Date startTime;
    private long duration;
    private DTSUser agent;
    private DTSUser client;

    public AllInfoAboutChat(Chat chat) {
        id = chat.getId();
        startTime = chat.getStartTime();
        duration = (new Date().getTime() - startTime.getTime())/1000;
        agent = new DTSUser(chat.getAgent());
        client = new DTSUser(chat.getClient());
    }
}
