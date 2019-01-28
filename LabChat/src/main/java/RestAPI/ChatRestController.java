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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import user.Agent;
import user.Client;
import user.SessionsStorage;
import user.chat.Chat;
import user.httpUsers.HttpAgent;
import user.httpUsers.HttpClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping(value = "/api")
public class ChatRestController {
    private String name;

    private static SessionsStorage storage = SessionsStorage.getInstance();

    @RequestMapping(value = "/allAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getAllAgents(ModelMap map){
        ArrayList<DTSUser> result = new ArrayList<>();
        for (Agent agent:storage.getAllAgents().values()
             ) {
            result.add(new DTSUser(agent));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/freeAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getFreeAgents(ModelMap map){
        ArrayList<DTSUser> result = new ArrayList<>();
        for (Agent agent:storage.getFreeAgents()
        ) {
            result.add(new DTSUser(agent));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/waitingClients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSUser>> getWaitingClients(ModelMap map){
        ArrayList<DTSUser> result = new ArrayList<>();
        for (Client client:storage.getWaitingClients()
        ) {
            result.add(new DTSUser(client));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/infoAboutAgent/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AllInfoAboutUser> getInfoAboutAgent(@PathVariable("id") long id, ModelMap map){
        Agent agent = storage.getAllAgents().get(id);
        if (agent == null)return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new AllInfoAboutUser(agent), HttpStatus.OK);
    }

    @RequestMapping(value = "/infoAboutClient/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AllInfoAboutUser> getInfoAboutClient(@PathVariable("id") long id, ModelMap map){
        Client client = storage.getAllClients().get(id);
        if (client == null)return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new AllInfoAboutUser(client), HttpStatus.OK);
    }

    @RequestMapping(value = "/freeAgentsCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getFreeAgentsCount(ModelMap map){
        return new ResponseEntity<>(storage.getFreeAgents().size(), HttpStatus.OK);
    }

    @RequestMapping(value = "/allChats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<DTSChat>> getAllChats(ModelMap map){
        ArrayList<DTSChat> result = new ArrayList<>();
        for (Chat chat:storage.getAllChats().values()
             ) {
            result.add(new DTSChat(chat));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/infoAboutChat/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AllInfoAboutChat> getInfoAboutChat(@PathVariable("id") long id, ModelMap map){
        Chat chat = storage.getAllChats().get(id);
        if (chat == null)return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new AllInfoAboutChat(chat), HttpStatus.OK);
    }



    @RequestMapping(value = "/registerAgent/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity regAgent(@PathVariable("name") String name, HttpServletRequest request){
        HttpSession session = request.getSession();
        if(storage.getAllUsers().keySet().contains(session))return new ResponseEntity(HttpStatus.BAD_REQUEST);
        HttpAgent agent = new HttpAgent(request.getSession(), name);
        try {
            storage.regAgent(agent);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        this.name = name;
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/registerClient/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity regClient(@PathVariable("name") String name, HttpServletRequest request){
        HttpSession session = request.getSession();
        if(storage.getAllUsers().keySet().contains(session))return new ResponseEntity(HttpStatus.BAD_REQUEST);
        HttpClient client = new HttpClient(request.getSession(), name);
        try {
            storage.regClient(client);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        this.name = name;
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/sendMessage/{text}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity sendMessage(@PathVariable("text") String text, HttpServletRequest request){
        HttpSession session = request.getSession();
        if(!storage.getAllUsers().keySet().contains(session))return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Message message = new Message(name, text, MessageType.TEXT_MESSAGE);
        try {
            storage.sendMessage(session, message);
        } catch (IOException | EncodeException e) {
            storage.getLogger().error("", e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
