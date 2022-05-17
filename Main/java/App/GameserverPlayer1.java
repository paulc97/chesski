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
public class GameserverPlayer1 {
    private static CountDownLatch latch;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private MessageEncoder me = new MessageEncoder();
    private long usedTime = 0;

    //Config
    private final String userName1 = "Player1";
    private long playerId1 = 0;
    private int gameId1 = 0;


    //Executed if connection gets established
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected. Session id: " + session.getId());
        try {
            //if no id is set create a new user
            //send the message to the server
            MessageObj messageObj = new MessageObj(0, userName1, playerId1);
            session.getBasicRemote().sendText(me.encode(messageObj));
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

        try {
            logger.info("Received: " + message);

            //receiving an update for a game and returning a move
            if (message.contains("activePlayerList")) {
                ActiveGame ag = g.fromJson(message, ActiveGame.class);
                if (ag.over || ag.draw) {
                    System.out.println("Game " + ag.ID + ": Game over or draw!");
                    return;
                }

                if (ag.currentPlayer.playerName.equals(userName1)) {
                    long startTime = System.currentTimeMillis();
                    System.out.println("Starting move generation!");
                    MoveGenerator mg = new MoveGenerator();
                    Board b = new Board(ag.fen);

                    String validMoves = mg.validMoves(b);
                    String moveBitboardPosition = mg.moveSelector(b, validMoves, usedTime);
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
                }
            }

            //Handling to register users on the game server
            if (message.contains("\"playerID\":") && (playerId1 == 0)) {
                Player registeredPlayer = g.fromJson(message, Player.class);
                if (registeredPlayer.playerName.equals(userName1)) {
                    playerId1 = registeredPlayer.playerID;
                    System.out.println("Received player1 ID: " + playerId1);
                }

                // get the game List to join a game
                if (!(playerId1 == 0)) {
                    MessageObj messageObj = new MessageObj(1);
                    session.getBasicRemote().sendText(g.toJson(messageObj));
                }
                return;
            }

            //Receiving a game list to join a game or to create a new one
            if (message.charAt(0) == '[') {
                System.out.println("Trying to parse games list...");
                Game[] games = g.fromJson(message, Game[].class);
                System.out.println("Found " + games.length + " games");

                for (Game game : games) {
                    //change to <=1 to find games with one player
                    if (game.players.length <= 1) {

                        boolean player1registered = false;
                        boolean player2registered = false;

                        //check if player is already registered
                        for (int i = 0; i < game.players.length; i++) {
                            if (game.players[i].contains(userName1)) player1registered = true;
                        }

                        if (gameId1 == 0 && !player1registered) {
                            MessageObj response2 = new MessageObj(3, userName1, playerId1, game.id, 1);
                            session.getBasicRemote().sendText(g.toJson(response2, MessageObj.class));
                            gameId1 = game.id;
                            System.out.println("Joined game... set game ID1 to: " + gameId1);

                        }

                        if (!(gameId1 == 0)) {
                            return;
                        }
                    }
                }


                //if empty create a new game
                if (gameId1 == 0) {
                    MessageObj response = new MessageObj(2, userName1, playerId1, "KingOfTheHill");
                    session.getBasicRemote().sendText(me.encode(response));
                }

                return;
            }

            //handle created game
            if (message.substring(0, 3).equals("{\"n")) {
                Game game = g.fromJson(message, Game.class);
                System.out.println("New game created!");

                //request game list to trigger join game procedure
                MessageObj response2 = new MessageObj(1);
                session.getBasicRemote().sendText(me.encode(response2));

                return;
            }


        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public static void main(String[] args) throws InterruptedException {

        //init Server
        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(GameserverPlayer1.class, new URI("ws://localhost:8025/websockets/game"));
            latch.await();
        } catch (DeploymentException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


}
    


