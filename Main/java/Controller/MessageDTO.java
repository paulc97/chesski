package Controller;

public class MessageDTO {
    int type;
    String username;
    String playerID;
    String move;
    String gameType;

    public MessageDTO(int type, String name, String playerID, String move, String gameType) {
        this.type = type;
        this.username = name;
        this.playerID = playerID;
        this.move = move;
        this.gameType = gameType;
    }

    public MessageDTO(int type, String name) {
        this.type = type;
        this.username = name;
    }
}
