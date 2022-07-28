package App;

import Controller.*;
import Model.Board;
import Model.MoveGenerator;
import com.google.gson.Gson;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@ClientEndpoint
public class RemotePlayer2 {
    private static CountDownLatch latch;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private MessageEncoder me = new MessageEncoder();
    private long usedTime = 0;
    private MoveGenerator mg = new MoveGenerator();

    //Config
    private final String userName1 = "NextMagnus" + String.valueOf(Math.random());
    private long playerId1 = 0;
    private int gameId1 = 0;


    //Executed if connection gets established
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected. Session id: " + session.getId());
        Gson g = new Gson();
        try {
            //if no id is set create a new user
            //send the message to the server
            if (playerId1 == 0){
            MessageObj messageObj = new MessageObj(0, userName1, playerId1);
            session.getBasicRemote().sendText(me.encode(messageObj));
            } else {
                MessageObj messageObj = new MessageObj(1);
                session.getBasicRemote().sendText(g.toJson(messageObj));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    //Executed if message arrives
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, DecodeException {
        Gson g = new Gson();
        MessageObj mess = g.fromJson(message, MessageObj.class);

        if (mess.type < 0){
            System.out.println("An error occured. That's bad! Error code: "+mess.type);
        }


        try {
            logger.info("Received: " + message);
            if (mess.type == 3 || mess.type == 4) {
                //receiving an update for a game and returning a move

                    ActiveGame ag = g.fromJson(message, ActiveGame.class);
                    if (ag.over) {
                        System.out.println("Game " + ag.ID + ": Game over!");
                        gameId1 = 0;
                        usedTime = 0;
                        System.out.println("Request game list to play next game...");
                        MessageObj messageObj = new MessageObj(1);
                        session.getBasicRemote().sendText(g.toJson(messageObj));
                        return;
                    }
                    if (ag.draw) {
                        System.out.println("Game " + ag.ID + ": draw!");
                        gameId1 = 0;
                        usedTime = 0;
                        System.out.println("Request game list to play next game...");
                        MessageObj messageObj = new MessageObj(1);
                        session.getBasicRemote().sendText(g.toJson(messageObj));
                        return;
                    }


                    if (ag.currentPlayer.playerID == playerId1) {
                        long startTime = System.currentTimeMillis();
                        System.out.println("Starting move generation!");

                        Board b = new Board(ag.fen);
                        if (b.isCurrentPlayerIsWhite()) {
                            b.setKIPlaysWhite(true);
                        } else {
                            b.setKIPlaysWhite(false);
                        }

                        String validMoves = MoveGenerator.validMoves(b);
                        String moveBitboardPosition = MoveGenerator.moveSelector(b, validMoves, usedTime);
                        String move = MoveGenerator.convertInternalMoveToGameserverMove(moveBitboardPosition, b);
                        System.out.println("Active Player: " + userName1);
                        System.out.println("Received FEN: " + ag.fen + " for game with ID: " + ag.ID);
                        System.out.println("Received moves from engine: " + validMoves);
                        System.out.println("Selected move: " + moveBitboardPosition);
                        System.out.println("Going to Submit translated move: " + move);
                        System.out.println("-----------------------------------------------------");
                        MessageObj response = new MessageObj(4, userName1, move, playerId1, gameId1);
                        session.getBasicRemote().sendText(me.encode(response));
                        usedTime += (System.currentTimeMillis() - startTime);
                    } else {
                        return;
                    }
                }


            //Handling to register users on the game server
            if (mess.type == 0 && (playerId1 == 0)) {
                Player registeredPlayer = g.fromJson(message, Player.class);
                if (registeredPlayer.playerName.equals(userName1)) {
                    playerId1 = registeredPlayer.playerID;
                    System.out.println("Received player1 ID: " + playerId1);
                }

                // get the game List to join a game
                if (playerId1 != 0) {
                    MessageObj messageObj = new MessageObj(1);
                    session.getBasicRemote().sendText(g.toJson(messageObj));
                }
                return;
            }

            //Receiving a game list to join a game or to create a new one
            if (mess.type == 1 && gameId1 == 0) {
                System.out.println("Trying to parse games list...");
                Game[] games = mess.games;
                System.out.println("Found " + games.length + " games");

                for (Game game : games) {

                    if (game.activePlayerList.length <= 1) {

                        boolean player1registered = false;

                        //check if player is already registered
                        for (int i = 0; i < game.activePlayerList.length; i++) {
                            if (game.activePlayerList[i].playerName.equals(userName1)) player1registered = true;
                        }

                        if (gameId1 == 0 && !player1registered) {
                            MessageObj response2 = new MessageObj(3, userName1, playerId1, game.ID, 1);
                            session.getBasicRemote().sendText(g.toJson(response2, MessageObj.class));
                            gameId1 = game.ID;
                            System.out.println("Joined game... set game ID1 to: " + gameId1);
                            return;

                        }
                    }
                }


                //if empty create a new game
                if (gameId1 != 0) {
                    System.out.println("Going to create a new game");
                    MessageObj response = new MessageObj(2, userName1, playerId1, "KingOfTheHill");
                    session.getBasicRemote().sendText(me.encode(response));
                }

                return;
            }

            //handle game creation message
            if (mess.type == 2) {
                System.out.println("Joining created game...");
                Game game = g.fromJson(message, Game.class);

                MessageObj response2 = new MessageObj(3, userName1, playerId1, game.ID, 1);
                session.getBasicRemote().sendText(g.toJson(response2, MessageObj.class));
                gameId1 = game.ID;
                System.out.println("Joined game... set game ID1 to: " + gameId1);
                return;
            }

        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws IOException {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public static void main(String[] args) throws InterruptedException {

            //init Server
            latch = new CountDownLatch(100);
            ClientManager client = ClientManager.createClient();
            try {
                client.connectToServer(RemotePlayer2.class, new URI("ws://koth.df1ash.de:8026"));
                latch.await();
            } catch (DeploymentException | URISyntaxException | InterruptedException e) {
                throw new RuntimeException(e);
            }


    }


}
    


