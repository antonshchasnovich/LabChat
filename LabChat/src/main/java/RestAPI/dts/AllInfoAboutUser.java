package RestAPI.dts;

import user.User;

import java.util.Date;

public class AllInfoAboutUser {
        private int id;
        private String name;
        private Date regTime;
        private int[] currentChatsId;
        private int[] companionsId;
        private int companionsNumber;

        public AllInfoAboutUser(User user){
            this.id = user.getId();
            this.name = user.getName();
            this.regTime = user.getRegTime();
            this.currentChatsId = user.getCurrentChatsId();
            this.companionsNumber = user.getCompanionsNumber();
            companionsId = new int[user.getCompanions().length];
            for(int i = 0; i < user.getCompanions().length; i++){
                if(user.getCompanions()[i] != null){
                    companionsId[i] = user.getCompanions()[i].getId();
                }
            }
        }
}
