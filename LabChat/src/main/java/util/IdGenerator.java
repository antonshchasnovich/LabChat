package util;

public class IdGenerator {
    private static int userId = 0;
    private static int chatId = 0;

    public static synchronized int getUserId(){
        return ++userId;
    }

    public static synchronized int getChatId(){
        return ++chatId;
    }
}
