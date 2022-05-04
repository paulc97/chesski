package Controller;

public class Message {
        int type;
        String username;
        long playerID;
        String move;
        String gameType;

    public Message(int type, String username) {
        this.type = type;
        this.username = username;
    }

    public Message(int type, String username, long playerID) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
    }
}
