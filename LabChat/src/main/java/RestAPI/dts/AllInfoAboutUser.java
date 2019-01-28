package RestAPI.dts;

import user.User;

import java.util.Date;

public class AllInfoAboutUser {
        private long id;
        private String name;
        private Date regTime;
        private long[] currentChatsId;
        private long[] companionsId;
        private int maxCompanionsNumber;

        public AllInfoAboutUser(User user){
            this.id = user.getId();
            this.name = user.getName();
            this.regTime = user.getRegTime();
            this.currentChatsId = user.getCurrentChatsId();
            this.maxCompanionsNumber = user.getMaxCompanionsNumber();
            companionsId = new long[user.getCompanions().length];
            for(int i = 0; i < user.getCompanions().length; i++){
                if(user.getCompanions()[i] != null){
                    companionsId[i] = user.getCompanions()[i].getId();
                }
            }
        }
}
