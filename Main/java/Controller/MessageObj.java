package Controller;

public class MessageObj {
        int type;
        String username;
        long playerID;
        String move;
        String gameName;
        int joinAsPlayer;
        long gameID;

    public MessageObj(int type) {
        this.type = type;
    }

    public MessageObj(int type, String username) {
        this.type = type;
        this.username = username;
    }

    public MessageObj(int type, String username, long playerID) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
    }

    public MessageObj(int type, String username, long playerID, String gameName) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
        this.gameName = gameName;
    }

    public MessageObj(int type, String username, long playerID, int gameId, int joinAsPlayer) {
        this.type = type;
        this.username = username;
        this.playerID = playerID;
        this.gameID = gameId;
        this.joinAsPlayer = joinAsPlayer;
    }

    public MessageObj(int type, String username, String move, long playerID, int gameId) {
        this.type = type;
        this.move = move;
        this.username = username;
        this.playerID = playerID;
        this.gameID = gameId;
    }
}
