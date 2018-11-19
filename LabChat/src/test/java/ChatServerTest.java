import message.Message;
import message.MessageType;
import mock.TestExecutorService;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static junit.framework.TestCase.assertEquals;

public class ChatServerTest {
    private ChatServer server;
    private TestExecutorService pool;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream stream;
    private Message testMessage;


    @Before
    public void resetFields() {
        server = new ChatServer();
        pool = new TestExecutorService(100);
        server.getUsersStorage().setPool(pool);
        testMessage = new Message(MessageType.TEST_MESSAGE, "text", "sender");
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            stream = new ObjectOutputStream(byteArrayOutputStream);
            server.setSocketOutStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerUserAgent() {
        try {
            server.setRegMessage(new Message(MessageType.AGENT_REG_MESSAGE, null, "TestAgent"));
            server.registerUser();
            assertEquals(server.getUsersStorage().getUnemployedAgents().size(), 1);
            assertEquals(server.getUsersStorage().getConnections().size(), 0);
            assertEquals(pool.getCount(), 0);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void registerUserClient() {
        try {
            server.setRegMessage(new Message(MessageType.CLIENT_REG_MESSAGE, null, "TestClient"));
            server.registerUser();
            assertEquals(server.getUsersStorage().getUnemployedAgents().size(), 0);
            assertEquals(server.getUsersStorage().getConnections().size(), 0);
            assertEquals(pool.getCount(), 1);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void getMessageFromUser(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(testMessage);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            server.setSocketInStream(in);
            server.getMessageFromUser();
            assertEquals(server.getRegMessage(), testMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendMessageToUser(){
        try {
            server.sendMessageToUser(testMessage);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            Message m = (Message) in.readObject();
            assertEquals(m, testMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}