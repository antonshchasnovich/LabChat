package RestAPI;

import RestAPI.dts.AllInfoAboutChat;
import RestAPI.dts.AllInfoAboutUser;
import RestAPI.dts.DTSChat;
import RestAPI.dts.DTSUser;
import message.Message;
import message.MessageType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import user.Agent;
import user.Client;
import user.SessionsStorage;
import user.User;
import user.chat.Chat;
import user.httpUsers.HttpAgent;
import user.httpUsers.HttpClient;
import user.httpUsers.HttpUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api")
public class ChatRestController {
    private String name;

    private static SessionsStorage storage = SessionsStorage.getInstance();

    @RequestMapping(value = "/allAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getAllAgents(ModelMap map) {
        ArrayList<DTSUser> result = new ArrayList<>();
        for (Agent agent : storage.getAllAgents().values()
        ) {
            result.add(new DTSUser(agent));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/agents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getAgents(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, ModelMap map) {
        if (storage.getAllAgents().size() <= pageNumber * pageSize) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ArrayList<User> agents = new ArrayList<>(storage.getAllAgents().values());
        return new ResponseEntity<>(subListAndConvertToDTS(agents, pageNumber, pageSize), HttpStatus.OK);
    }

    @RequestMapping(value = "/allFreeAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getAllFreeAgents(ModelMap map) {
        ArrayList<DTSUser> result = new ArrayList<>();
        for (Agent agent : storage.getFreeAgents()
        ) {
            result.add(new DTSUser(agent));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/freeAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getFreeAgents(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, ModelMap map) {
        if (storage.getFreeAgents().size() <= pageNumber * pageSize)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ArrayList<User> agents = new ArrayList<>(storage.getFreeAgents());
        return new ResponseEntity<>(subListAndConvertToDTS(agents, pageNumber, pageSize), HttpStatus.OK);
    }

    @RequestMapping(value = "/allWaitingClients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getAllWaitingClients(ModelMap map) {
        ArrayList<DTSUser> result = new ArrayList<>();
        for (Client client : storage.getWaitingClients()
        ) {
            result.add(new DTSUser(client));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/waitingClients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getWaitingClients(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, ModelMap map) {
        if (storage.getWaitingClients().size() <= pageNumber * pageSize)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ArrayList<User> clients = new ArrayList<>(storage.getWaitingClients());
        return new ResponseEntity<>(subListAndConvertToDTS(clients, pageNumber, pageSize), HttpStatus.OK);
    }

    @RequestMapping(value = "/infoAboutAgent/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AllInfoAboutUser> getInfoAboutAgent(@PathVariable("id") long id, ModelMap map) {
        Agent agent = storage.getAllAgents().get(id);
        if (agent == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new AllInfoAboutUser(agent), HttpStatus.OK);
    }

    @RequestMapping(value = "/infoAboutClient/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AllInfoAboutUser> getInfoAboutClient(@PathVariable("id") long id, ModelMap map) {
        Client client = storage.getAllClients().get(id);
        if (client == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new AllInfoAboutUser(client), HttpStatus.OK);
    }

    @RequestMapping(value = "/freeAgentsCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getFreeAgentsCount(ModelMap map) {
        return new ResponseEntity<>(storage.getFreeAgents().size(), HttpStatus.OK);
    }

    @RequestMapping(value = "/allChats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSChat>> getAllChats(ModelMap map) {
        ArrayList<DTSChat> result = new ArrayList<>();
        for (Chat chat : storage.getAllChats().values()
        ) {
            result.add(new DTSChat(chat));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/chats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSChat>> getChats(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, ModelMap map) {
        if (storage.getAllChats().size() <= pageNumber * pageSize) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ArrayList<Chat> chats = new ArrayList<>(storage.getAllChats().values());
        ArrayList<DTSChat> result = new ArrayList<>();
        if (chats.size() <= (pageNumber + 1) * pageSize) {
            for (Chat chat : chats.subList(pageNumber * pageSize, chats.size())
            ) {
                result.add(new DTSChat(chat));
            }
        } else {
            for (Chat chat : chats.subList(pageNumber * pageSize, (pageNumber + 1) * pageSize)
            ) {
                result.add(new DTSChat(chat));
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/infoAboutChat/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AllInfoAboutChat> getInfoAboutChat(@PathVariable("id") long id, ModelMap map) {
        Chat chat = storage.getAllChats().get(id);
        if (chat == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new AllInfoAboutChat(chat), HttpStatus.OK);
    }


    @RequestMapping(value = "/registerAgent/{name}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity regAgent(@PathVariable("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (storage.getAllUsers().keySet().contains(session)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        HttpAgent agent = new HttpAgent(request.getSession(), name);
        try {
            storage.regAgent(agent);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        this.name = name;
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/registerClient/{name}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity regClient(@PathVariable("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (storage.getAllUsers().keySet().contains(session)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        HttpClient client = new HttpClient(request.getSession(), name);
        try {
            storage.regClient(client);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        this.name = name;
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/sendMessage/{text}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity sendMessage(@PathVariable("text") String text, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!storage.getAllUsers().keySet().contains(session)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Message message = new Message(name, text, MessageType.TEXT_MESSAGE);
        try {
            storage.sendMessage(session, message);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<Message>> getMessages(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!storage.getAllUsers().keySet().contains(session)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        HttpUser httpUser = (HttpUser) storage.getAllUsers().get(session);
        return new ResponseEntity<>(httpUser.getMessages(), HttpStatus.OK);
    }

    @RequestMapping(value = "/leave", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity leaveChat(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!storage.getAllUsers().keySet().contains(session)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        try {
            storage.leaveChat(session, 0);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/exit", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity exitChat(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!storage.getAllUsers().keySet().contains(session)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        try {
            storage.exitChat(session);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    private ArrayList<DTSUser> subListAndConvertToDTS(ArrayList<User> users, int pageNumber, int pageSize) {
        ArrayList<DTSUser> result = new ArrayList<>();
        if (users.size() <= (pageNumber + 1) * pageSize) {
            for (User user : users.subList(pageNumber * pageSize, users.size())
            ) {
                result.add(new DTSUser(user));
            }
        } else {
            for (User user : users.subList(pageNumber * pageSize, (pageNumber + 1) * pageSize)
            ) {
                result.add(new DTSUser(user));
            }
        }
        return result;
    }
}
