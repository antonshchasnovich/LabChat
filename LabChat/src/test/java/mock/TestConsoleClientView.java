package mock;

import chatClient.view.ConsoleChatClientView;

import java.util.ArrayDeque;

public class TestConsoleClientView extends ConsoleChatClientView {
    private ArrayDeque<String> getedMessages = new ArrayDeque<>(3);
    private ArrayDeque<String> showedMessages = new ArrayDeque<>();

    //в конструкторе добавляю тестовые сообщения трех типов
    public TestConsoleClientView() {
        getedMessages.add("text");
        getedMessages.add("/leave");
        getedMessages.add("/exit");
    }

    //вместо вывода сообщений в консоль добавляем их в очередь для возможности проверки содержимого вывода
    @Override
    public void showStrMessage(String message) {
        showedMessages.add(message);
    }

    //вместо чтения строки из консоли забираем заранее заготовленные тестовые сообщения
    @Override
    public String getStrMessage(){
        return getedMessages.poll();
    }

    public ArrayDeque<String> getShowedMessages() {
        return showedMessages;
    }
}
