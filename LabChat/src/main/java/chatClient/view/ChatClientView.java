package chatClient.view;

import java.io.IOException;

public interface ChatClientView {

	public void showStrMessage(String message);

	public String getStrMessage() throws IOException;
}
