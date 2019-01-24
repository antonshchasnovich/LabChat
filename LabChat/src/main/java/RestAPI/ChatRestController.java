package RestAPI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import user.SessionsStorage;

@Controller
@RequestMapping(value = "/api")
public class ChatRestController {

    private static SessionsStorage storage = SessionsStorage.getInstance();

    @RequestMapping(value = "/allUsersCount", method = RequestMethod.GET)
    @ResponseBody
    public int sayHello(ModelMap map){
        return storage.getAllUsers().size();
    }
}
