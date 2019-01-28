package RestAPI.dts;

import user.User;
import java.util.Date;

public class DTSUser {
    protected final long id;
    protected final String name;
    protected final Date regTime;

    public DTSUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.regTime = user.getRegTime();
    }
}
