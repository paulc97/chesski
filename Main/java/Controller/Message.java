package Controller;

public class Message {
        int type;
        String username;
        long playerID;
        String move;
        String gameName;
        int joinAsPlayer;
        int gameId;

    public Message(int type) {
        this.type = type;
    }

    public Message(int type, String username) {
        this.type = type;
        this.username = username;
    }

    public Message(int type, String username, long playerID) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
    }

    public Message(int type, String username, long playerID, String gameName) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
        this.gameName = gameName;
    }

    public Message(int type, String username, long playerID, int gameId, int joinAsPlayer) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
        this.gameId = gameId;
        this.joinAsPlayer = joinAsPlayer;
    }
}
