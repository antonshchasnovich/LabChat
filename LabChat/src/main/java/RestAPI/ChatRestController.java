package RestAPI;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import user.Agent;
import user.SessionsStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/api")
public class ChatRestController {

    private static SessionsStorage storage = SessionsStorage.getInstance();

    @RequestMapping(value = "/allAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAllAgents(ModelMap map){
        return storage.getAllAgents().toString();
    }

    @RequestMapping(value = "/allFreeAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAllFreeAgents(ModelMap map){
        return storage.getFreeAgents().toString();
    }

    @RequestMapping(value = "/getInfoAboutAgent/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getInfoAboutAgent(@PathVariable("id") int id, ModelMap map){
        Agent agent = storage.getAllAgents().get(id);
        if(agent == null) return "there is no agent with id = " + id;
        String result = "Id: " + agent.getId() + System.lineSeparator();
        result += "Name: " + agent.getName() + System.lineSeparator();
        result += "Registration time: " + agent.getRegTime().toString() + System.lineSeparator();
        result += "Current chats id: " + agent.getCurrentChatsId() + System.lineSeparator();
        result += "Current companions: " + agent.getCompanions() + System.lineSeparator();
        result += "Maximum number of companions: " + agent.getCompanionsNumber() + System.lineSeparator();
        return result;
    }

    @RequestMapping(value = "/freeAgentsCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public int getFreeAgentsCount(ModelMap map){
        return storage.getFreeAgents().size();
    }

    @RequestMapping(value = "/sessionId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String testMestod(HttpServletRequest request){
        HttpSession session = request.getSession();
        return request.getSession().getId();
    }
}
