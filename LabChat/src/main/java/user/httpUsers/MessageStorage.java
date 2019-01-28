package user.httpUsers;

import message.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageStorage {
    private static MessageStorage instance;

    private MessageStorage(){}

    public static synchronized MessageStorage getInstance(){
        if (instance == null){
            instance = new MessageStorage();
        }
        return instance;
    }

    private HashMap<Long, ArrayList<Message>> allMessages = new HashMap<>();

    public synchronized void addMessage(long id, Message message){
        allMessages.computeIfAbsent(id, k -> new ArrayList<>());
        allMessages.get(id).add(message);
    }

    public synchronized ArrayList<Message> getMessages(long id){
        ArrayList<Message> result = new ArrayList<>(allMessages.get(id));
        allMessages.get(id).clear();
        return result;
    }

    public synchronized void removeMessages(long id){
        allMessages.remove(id);
    }
}
