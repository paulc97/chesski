package Controller;

public class ResponseDTO {
    public Header header;
    public Game game;

    public void print() {
        System.out.println("Header: "+header.playerID);
        System.out.println("Game: "+game.id+" "+game.move);
    }

    public class Header {
        public String playerID;
    }

    public class Game {
        public String id;
        public String move;
    }
}
