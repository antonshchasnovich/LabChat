package util;

public class IdGenerator {
    private long userId = 0;
    private long chatId = 0;
    private static IdGenerator instance;

    public static synchronized IdGenerator getInstance(){
        if (instance == null){
            instance = new IdGenerator();
        }
        return instance;
    }

    private IdGenerator(){}

    public synchronized long getUserId(){
        return ++userId;
    }

    public synchronized long getChatId(){
        return ++chatId;
    }
}
