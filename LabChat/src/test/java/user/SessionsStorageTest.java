package user;

import message.Message;
import message.MessageType;
import mock.TestBasic;
import mock.TestLogger;
import mock.TestSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayDeque;


public class SessionsStorageTest {
    private SessionsStorage storage;
    private Client client;
    private Agent agent;
    private TestSession agentSession;
    private TestSession clientSession;
    private TestLogger logger;
    private static final String CLIENT_NAME = "TestClient";
    private static final String AGENT_NAME = "TestAgent";
    private TestBasic clientBasic;
    private TestBasic agentBasic;


    @Before
    public void setUp() throws Exception {
        logger = new TestLogger();
        storage = new SessionsStorage();
        storage.setLogger(logger);
        agentSession = new TestSession();
        clientSession = new TestSession();
        client = new Client(clientSession, CLIENT_NAME);
        agent = new Agent(agentSession, AGENT_NAME, 3);
        client.setSession(clientSession);
        agent.setSession(agentSession);
        clientBasic = (TestBasic) clientSession.getBasicRemote();
        agentBasic = (TestBasic) agentSession.getBasicRemote();
    }

    @Test
    public void regAgent(){

    }

    @Test
    public void regClient() throws IOException, EncodeException {
        Message testClientRegMessage = new Message(CLIENT_NAME, "", MessageType.CLIENT_REG_MESSAGE);
        storage.regClient(clientSession, testClientRegMessage);
        Assert.assertTrue(storage.getWaitingClients().contains(client));
        Assert.assertEquals(1, logger.getInfoLoggies().size());
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertEquals(client, storage.getAllUsers().get(client.getSession()));
        Assert.assertTrue(storage.getFreeAgents().isEmpty());

    }

    //if we don't have waiting clients and we add a new agent
    @Test
    public void addAgent1() throws IOException, EncodeException {
        storage.addAgent(agent);
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertEquals(agent, storage.getAllUsers().get(agent.getSession()));
        Assert.assertTrue(storage.getFreeAgents().contains(agent));
        Assert.assertEquals(1, storage.getFreeAgents().size());
        Assert.assertTrue(storage.getWaitingClients().isEmpty());
    }

    //if we have one waiting client and we add a new agent
    @Test
    public void addAgent2() throws IOException, EncodeException {
        ArrayDeque<Client> waitingClients = new ArrayDeque<>();
        waitingClients.add(client);
        storage.setWaitingClients(waitingClients);
        storage.addAgent(agent);
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertEquals(agent, storage.getAllUsers().get(agent.getSession()));
        Assert.assertEquals(1, storage.getFreeAgents().size());
        Assert.assertTrue(storage.getWaitingClients().isEmpty());
        Assert.assertEquals(agent, client.getCompanion(0));
        Assert.assertEquals(client, agent.getCompanion(0));
        Assert.assertEquals(1, agentBasic.getSendedObjects().size());
        Assert.assertEquals(1, clientBasic.getSendedObjects().size());
        Assert.assertEquals(1, logger.getInfoLoggies().size());
    }

    //if we don't have free agents and we add a new client
    @Test
    public void addClient1() throws IOException, EncodeException {
        storage.addClient(client);
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertEquals(client, storage.getAllUsers().get(client.getSession()));
        Assert.assertTrue(storage.getWaitingClients().contains(client));
        Assert.assertEquals(1, storage.getWaitingClients().size());
        Assert.assertTrue(storage.getFreeAgents().isEmpty());
    }

    //if we have one free agent and we add a new client
    @Test
    public void addClient2() throws IOException, EncodeException {
        ArrayDeque<Agent> freeAgents = new ArrayDeque<>();
        freeAgents.add(agent);
        storage.setFreeAgents(freeAgents);
        storage.addClient(client);
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertEquals(client, storage.getAllUsers().get(client.getSession()));
        Assert.assertEquals(1, storage.getFreeAgents().size());
        Assert.assertTrue(storage.getWaitingClients().isEmpty());
        Assert.assertEquals(agent, client.getCompanion(0));
        Assert.assertEquals(client, agent.getCompanion(0));
        Assert.assertEquals(1, agentBasic.getSendedObjects().size());
        Assert.assertEquals(1, clientBasic.getSendedObjects().size());
        Assert.assertEquals(1, logger.getInfoLoggies().size());
    }

    //if client doesn't have a companion and doesn't be in queue waitingClients tries to send message
    @Test
    public void sendMessage1() throws IOException, EncodeException {
        Message testMessage = new Message(CLIENT_NAME, "test text", MessageType.TEXT_MESSAGE);
        storage.getAllUsers().put(clientSession, client);
        storage.sendMessage(clientSession, testMessage);
        testMessage.setType(MessageType.BUFFERED_MESSAGE);
        Assert.assertTrue(client.getBufferedMessages().contains(testMessage));
        Assert.assertTrue(storage.getWaitingClients().contains(client));
    }

    //if agent doesn't have a companion and doesn't be in queue freeAgents tries to send message
    @Test
    public void sendMessage2() throws IOException, EncodeException {
        Message testMessage = new Message(AGENT_NAME, "test text", MessageType.TEXT_MESSAGE);
        storage.getAllUsers().put(agentSession, agent);
        storage.sendMessage(agentSession, testMessage);
        Assert.assertTrue(storage.getFreeAgents().contains(agent));
    }

    //the client sends message to his companion
    @Test
    public void sendMessage3() throws IOException, EncodeException {
        Message testMessage = new Message(CLIENT_NAME, "test text", MessageType.TEXT_MESSAGE);
        client.setCompanion(agent);
        storage.getAllUsers().put(clientSession, client);
        storage.sendMessage(clientSession, testMessage);
        Assert.assertTrue(agentBasic.getSendedObjects().contains(testMessage));
        Assert.assertTrue(clientBasic.getSendedObjects().contains(testMessage));
    }

    //client leaves chat
    @Test
    public void leaveChat1() throws IOException, EncodeException {
        Message testLeaveMsg = new Message("name", "text", MessageType.LEAVE_MESSAGE, 0);
        connectTestAgentAndClient();
        storage.leaveChat(clientSession, testLeaveMsg);
        Assert.assertNull(client.getCompanion(0));
        Assert.assertNull(agent.getCompanion(0));
        Assert.assertTrue(storage.getFreeAgents().contains(agent));
        Assert.assertEquals(1, logger.getInfoLoggies().size());
        Assert.assertEquals(2, storage.getAllUsers().size());
    }

    //agent leaves chat (if agent leaves chat and queues freeAgents and waitingClients are empty, the algorithm will
    // add the agent in freeAgents queue and the client in waitingClients queue, therefore the agent and client will
    // connect again.
    @Test
    public void leaveChat2() throws IOException, EncodeException {
        Message testLeaveMsg = new Message("name", "text", MessageType.LEAVE_MESSAGE, 0);
        connectTestAgentAndClient();
        storage.leaveChat(agentSession, testLeaveMsg);
        Assert.assertEquals(agent, client.getCompanion(0));
        Assert.assertEquals(client, agent.getCompanion(0));
        //the agent has three tabs (look setUp() method), so he added in freeAgents again
        Assert.assertTrue(storage.getFreeAgents().contains(agent));
        Assert.assertTrue(storage.getWaitingClients().isEmpty());
        //must be two info logs, for end chat and for start new chat
        Assert.assertEquals(2, logger.getInfoLoggies().size());
        Assert.assertEquals(2, storage.getAllUsers().size());
    }

    //agent and client are connected and agent exited
    @Test
    public void exitChat1() throws IOException, EncodeException {
        connectTestAgentAndClient();
        storage.exitChat(agentSession);
        Assert.assertTrue(storage.getWaitingClients().contains(client));
        Assert.assertTrue(storage.getFreeAgents().isEmpty());
        Assert.assertEquals(client, storage.getAllUsers().get(clientSession));
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertNull(client.getCompanion(0));
    }

    //agent and client are connected and client exited
    @Test
    public void exitChat2() throws IOException, EncodeException {
        connectTestAgentAndClient();
        connectTestAgentAndClient();
        storage.exitChat(clientSession);
        Assert.assertTrue(storage.getFreeAgents().contains(agent));
        Assert.assertTrue(storage.getWaitingClients().isEmpty());
        Assert.assertEquals(agent, storage.getAllUsers().get(agentSession));
        Assert.assertEquals(1, storage.getAllUsers().size());
        Assert.assertNull(agent.getCompanion(0));
    }

    private void connectTestAgentAndClient() {
        agent.setCompanion(client);
        client.setCompanion(agent);
        storage.getAllUsers().put(clientSession, client);
        storage.getAllUsers().put(agentSession, agent);
    }
}
