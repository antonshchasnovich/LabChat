package user.httpUsers;

import message.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageStorage {
    private static HashMap<Integer, ArrayList<Message>> allMessages = new HashMap<>();

    public static synchronized void addMessage(int id, Message message){
        allMessages.computeIfAbsent(id, k -> new ArrayList<>());
        allMessages.get(id).add(message);
    }

    public static synchronized ArrayList<Message> getMessages(int id){
        ArrayList<Message> result = new ArrayList<>(allMessages.get(id));
        allMessages.get(id).clear();
        return result;
    }

    public static synchronized void removeMessages(int id){
        allMessages.remove(id);
    }
}
