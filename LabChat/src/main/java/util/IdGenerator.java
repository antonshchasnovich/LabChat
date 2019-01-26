package util;

public class IdGenerator {
    private int userId = 0;
    private int chatId = 0;
    private static IdGenerator instance;

    public static synchronized IdGenerator getInstance(){
        if (instance == null){
            instance = new IdGenerator();
        }
        return instance;
    }

    private IdGenerator(){}

    public synchronized int getUserId(){
        return ++userId;
    }

    public synchronized int getChatId(){
        return ++chatId;
    }
}
