package RestAPI.dts;

import user.Agent;

import java.util.Date;

public class AllInfoAboutUser {
        private int id;
        private String name;
        private Date regTime;
        private int[] currentChatsId;
        private int[] companionsId;
        private int companionsNumber;

        public AllInfoAboutUser(Agent agent){
            this.id = agent.getId();
            this.name = agent.getName();
            this.regTime = agent.getRegTime();
            this.currentChatsId = agent.getCurrentChatsId();
            this.companionsNumber = agent.getCompanionsNumber();
            companionsId = new int[agent.getCompanions().length];
            for(int i = 0; i < agent.getCompanions().length; i++){
                if(agent.getCompanions()[i] != null){
                    companionsId[i] = agent.getCompanions()[i].getId();
                }
            }
        }
}
