package App;

import Controller.*;
import Model.Board;
import Model.MoveGenerator;
import com.google.gson.Gson;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@ClientEndpoint
public class GameserverMain {
    private static CountDownLatch latch;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private MessageEncoder me = new MessageEncoder();
    private MessageDecoder md = new MessageDecoder();

    //Config
    private String userName1="Player3781";
    private long playerId1 = 0;
    private String userName2="Player4700";
    private long playerId2 = 0;
    private int gameId1 = 0;
    private int gameId2 = 0;



    //Executed if connection gets established
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected. Session id: " + session.getId());
        try {
            //if no id is set create a new user
                //send the message to the server
                MessageObj messageObj = new MessageObj(0, userName1, playerId1);
                session.getBasicRemote().sendText(me.encode(messageObj));
                messageObj = new MessageObj(0, userName2, playerId2);
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

            //Handling to register users on the game server
            if (message.contains("\"playerID\":") && (playerId1 == 0 || playerId2 == 0)){
                Response response = md.decode(message);
                if (response.playerName.equals(userName1)){
                playerId1 = response.playerID;
                System.out.println("Received player1 ID: "+ playerId1);}
                else {
                    playerId2 = response.playerID;
                    System.out.println("Received player2 ID: "+ playerId2);}

                // get the game List to join a game
                MessageObj messageObj = new MessageObj(1);
                session.getBasicRemote().sendText(g.toJson(messageObj));
                return;
            }

            //Receiving a game list to join a game or to create a new one
            if (message.charAt(0) == '[') {
                System.out.println("Trying to parse games list...");
                Game[] games = g.fromJson(message, Game[].class);
                System.out.println("Found "+games.length+" games");

                    for (Game game : games) {
                        //change to <=1 to find games with one player
                        if (game.players.length == 0) {

                            if (gameId1 == 0) {
                                MessageObj response = new MessageObj(3, userName1, playerId1, game.id, 1);
                                session.getBasicRemote().sendText(me.encode(response));
                                gameId1 = game.id;
                                System.out.println("Joined game... set game ID1 to: " + gameId1);
                                if (gameId2 == 0) {
                                    // get the other player is still waiting for a game request the new game list
                                    MessageObj messageObj = new MessageObj(1);
                                    session.getBasicRemote().sendText(g.toJson(messageObj));
                                }
                                return;
                            }
                            if (gameId2 == 0) {
                                //Uncomment the next two lines to play with two players
                                MessageObj response = new MessageObj(3, userName2, playerId2, game.id, 1);
                                session.getBasicRemote().sendText(me.encode(response));

                                gameId2 = game.id;
                                System.out.println("Joined game... set game ID2 to: " + gameId2);

                                return;
                            }
                        }
                    }

                //if empty create a new game
                    if((gameId1 == 0 || gameId2 == 0)) {
                        MessageObj response = new MessageObj(2, userName1, playerId1, "KingOfTheHill");
                        session.getBasicRemote().sendText(me.encode(response));
                        MessageObj response2 = new MessageObj(1);
                        session.getBasicRemote().sendText(me.encode(response2));
                    }

                return;
            }

            //receiving an update for a game and returning a move
            if(message.contains("activePlayerList")){
                ActiveGame ag = g.fromJson(message, ActiveGame.class);
                if (ag.over || ag.draw){
                    System.out.println("Game "+ag.ID+": Game over or draw!");
                    return;
                }

                MoveGenerator mg = new MoveGenerator();
                Board b = new Board(ag.fen);
                String validMoves = mg.validMoves(b);
                String moveBitboardPosition = mg.moveSelector(b, validMoves);
                System.out.println(moveBitboardPosition);
                String move = "";
                move += MoveGenerator.convert0IndexMoveDigitsToField(moveBitboardPosition.charAt(0), moveBitboardPosition.charAt(1));
                move += MoveGenerator.convert0IndexMoveDigitsToField(moveBitboardPosition.charAt(2), moveBitboardPosition.charAt(3));
                System.out.println("Received FEN: "+ag.fen+" for game with ID: "+ag.ID);
                System.out.println("Received moves from engine: "+validMoves);
                System.out.println("Selected move: "+moveBitboardPosition);
                System.out.println("Going to Submit translated move: "+move);

                if (ag.currentPlayer.playerName.equals(userName1) && ag.currentPlayer.playerID == playerId1){
                    MessageObj response = new MessageObj(4,  userName1, move, playerId1, gameId1);
                    session.getBasicRemote().sendText(me.encode(response));
                } else if (ag.currentPlayer.playerName.equals(userName2) && ag.currentPlayer.playerID == playerId2){
                    MessageObj response = new MessageObj(4,  userName2, move, playerId2, gameId2);
                    session.getBasicRemote().sendText(me.encode(response));
                }
                return;

            }

        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        } catch (DecodeException e) {
            e.printStackTrace();
        }


        return;

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
            client.connectToServer(GameserverMain.class, new URI("ws://localhost:8025/websockets/game"));
            latch.await();
        } catch (DeploymentException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

        
 }
    


