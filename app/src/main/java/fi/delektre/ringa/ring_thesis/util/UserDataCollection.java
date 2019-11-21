package fi.delektre.ringa.ring_thesis.util;

public class UserDataCollection {
    protected String userData;
    protected int dataType;

    public UserDataCollection(){

    }

    public UserDataCollection(String userData, int dataType){
        this.dataType = dataType;
        this.userData = userData;

    }

    public int getDataType() {
        return dataType;
    }

    public String getUserData() {
        return userData;
    }


    public class UserHeight extends UserDataCollection {
        public UserHeight(int dataType, String userData) {
            this.dataType = dataType;
            this.userData = userData;
        }

        public String getDisplayString(){
            String receivedData[] = this.getUserData().split(":");
            if (receivedData.length == 3) {
                String user_height = receivedData[0] + "' " + receivedData[1] + "''";
                return user_height;
            } else if (receivedData.length == 2){
                String user_height = receivedData[0] + " " + receivedData[1];
                return user_height;
            }
            return null;
        }
    }

    public class UserWeight extends UserDataCollection {
        public UserWeight(int dataType, String userData) {
            this.dataType = dataType;
            this.userData = userData;
        }

        public String getDisplayString(){
            String receivedData[] = this.getUserData().split(":");
            String user_weight = receivedData[0] + "." + receivedData[1] + " " + receivedData[2];
            return user_weight;
        }
    }

    public class UserBirthday extends UserDataCollection {
        public UserBirthday(int dataType, String userData) {
            this.dataType = dataType;
            this.userData = userData;
        }
        public String getDisplayString(){
            String receivedData[] = this.getUserData().split(":");
            String user_birthday = receivedData[0] + "." + receivedData[1] + " " + receivedData[2];
            return user_birthday;
        }
    }

    public class UserGender extends  UserDataCollection {
        public UserGender(int dataType, String userData) {
            this.dataType = dataType;
            this.userData = userData;
        }
        public String getDisplayString(){
            return this.getUserData();
        }
    }

    public class UserName extends UserDataCollection {
        public UserName(int dataType, String userData) {
            this.dataType = dataType;
            this.userData = userData;
        }
        public String getDisplayString(){
            String receivedData[] = this.getUserData().split(":");
            String user_birthday = receivedData[0] + "." + receivedData[1] + " " + receivedData[2];
            return user_birthday;
        }
    }

}
