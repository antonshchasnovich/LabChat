import mock.TestExecutorService;
import org.junit.Before;
import org.junit.Test;
import user.Agent;
import user.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class UsersStorageTest {
    private UsersStorage storage;
    private Client testClient;
    private Agent testAgent;
    private TestExecutorService pool;


    @Before
    public void createStorage(){
        storage = new UsersStorage();
        pool = new TestExecutorService(100);
        storage.setPool(pool);
        testClient = new Client("TestClient", null, null, null);
        try {
            testAgent = new Agent("TestAgent", null, null, new ObjectOutputStream(new ByteArrayOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //When both queues is empty
    @Test
    public void addAgentInQueue1() {
        storage.addAgentInQueue(testAgent);
        assertTrue(storage.getUnemployedAgents().contains(testAgent));
        assertEquals(storage.getUnemployedAgents().size(), 1);
        assertTrue(storage.getConnections().isEmpty());
        assertEquals(pool.getCount(), 0);
    }

    //When queue connections contains one connection
    @Test
    public void addAgentInQueue2() {
        Connection connection = new Connection(storage, testClient, "Server");
        storage.getConnections().add(connection);
        storage.addAgentInQueue(testAgent);
        assertEquals(connection.getAgent(), testAgent);
        assertTrue(storage.getConnections().isEmpty());
        assertTrue(storage.getUnemployedAgents().isEmpty());
        assertEquals(pool.getCount(), 1);
    }

    //When both queues is empty
    @Test
    public void addConnectionInQueue1() {
        UsersStorage storage = new UsersStorage();
        Connection connection = new Connection(storage, new Client("TestClient", null, null, null), "Server");
        try {
            storage.addConnectionInQueue(connection);
            assertTrue(storage.getConnections().contains(connection));
            assertEquals(storage.getConnections().size(), 1);
            assertTrue(storage.getUnemployedAgents().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //When queue unemployedAgents contains one agent
    @Test
    public void addConnectionInQueue2() {
        try {
            storage.getUnemployedAgents().add(testAgent);
            Connection connection = new Connection(storage, new Client("TestClient", null, null, null), "Server");
            storage.addConnectionInQueue(connection);
            assertEquals(connection.getAgent(), testAgent);
            assertTrue(storage.getConnections().isEmpty());
            assertTrue(storage.getUnemployedAgents().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}