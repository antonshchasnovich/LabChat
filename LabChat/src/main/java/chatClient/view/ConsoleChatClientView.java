package chatClient.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleChatClientView implements ChatClientView {
	BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

	public void showStrMessage(String message) {
		System.out.println(message);
	}

	public String getStrMessage() throws IOException {
		return consoleReader.readLine();
	}

}
