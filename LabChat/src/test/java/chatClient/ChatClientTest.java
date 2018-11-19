package chatClient;

import message.Message;
import message.MessageType;
import mock.TestConsoleClientView;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ChatClientTest {
    private ChatClient chatClient;
    private ByteArrayOutputStream forOutStream;

    @Before
    public void resetFields(){
        chatClient = new ChatClient();
        chatClient.setName("testUser");
        chatClient.setSocket(new Socket());
        TestConsoleClientView testView = new TestConsoleClientView();
        chatClient.setView(testView);
        forOutStream = new ByteArrayOutputStream();
        try {
            chatClient.setOut(new ObjectOutputStream(forOutStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void register_Agent() {
        chatClient.setStrRegMessage("/register agent TestAgent");
        try {
            chatClient.register();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(forOutStream.toByteArray()));
            Message m = (Message) in.readObject();
            assertEquals(m, new Message(MessageType.AGENT_REG_MESSAGE, null, "TestAgent"));
            assertEquals(chatClient.getName(), "TestAgent");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void register_Client() {
        chatClient.setStrRegMessage("/register client TestClient");
        try {
            chatClient.register();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(forOutStream.toByteArray()));
            Message m = (Message) in.readObject();
            assertEquals(m, new Message(MessageType.CLIENT_REG_MESSAGE, null, "TestClient"));
            assertEquals(chatClient.getName(), "TestClient");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createOutThread() {
        chatClient.createOutThread();
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(forOutStream.toByteArray()));
            assertEquals(in.readObject(), new Message(MessageType.TEXT_MESSAGE, "text", "testUser"));
            assertEquals(in.readObject(), new Message(MessageType.LEAVE_MESSAGE));
            assertEquals(in.readObject(), new Message(MessageType.EXIT_MESSAGE));
            assertTrue(chatClient.getSocket().isClosed());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createInThread() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(new Message(MessageType.TEXT_MESSAGE, "text1", "sender"));
            outputStream.writeObject(new Message(MessageType.LEAVE_MESSAGE, "text2", "sender"));
            outputStream.writeObject(new Message(MessageType.EXIT_MESSAGE));
            chatClient.setIn(new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())));
            Thread inThread = chatClient.createInThread();
            inThread.run();
            TestConsoleClientView view = (TestConsoleClientView) chatClient.getView();
            assertEquals("sender: text1", view.getShowedMessages().poll());
            assertEquals("sender: text2", view.getShowedMessages().poll());
            assertTrue(chatClient.getSocket().isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}