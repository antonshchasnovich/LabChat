package RestAPI;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import user.SessionsStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    @RequestMapping(value = "/sessionId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String testMestod(HttpServletRequest request){
        HttpSession session = request.getSession();
        return request.getSession().getId();
    }
}
