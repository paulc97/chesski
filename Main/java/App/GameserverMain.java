package App;

import Controller.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private String userName="MagsummmsCarlsen";
    private long playerId;
    private int gameId;


    //Executed if connection gets established
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected. Session id: " + session.getId());
        try {
            //if no id is set create a new user
                //send the message to the server
                Message message = new Message(0, userName, playerId);
                session.getBasicRemote().sendText(me.encode(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    //Executed if message arrives
    @OnMessage
    public String onMessage(String message, Session session) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        Gson g = new Gson();

        try {
            logger.info("Received: " + message);
            if (message.contains("\"playerID\":")){
                Response messageObject = md.decode(message);
                playerId = messageObject.playerID;
                System.out.println("Received player ID: "+ playerId);
                // try to join a game
                Message response = new Message(1);
                session.getBasicRemote().sendText(g.toJson(response));
                return bufferedReader.readLine();
            }
            if (message.charAt(0) == '[') {
                System.out.println("Trying to parse games list...");
                Game[] games = g.fromJson(message, Game[].class);
                System.out.println("Found "+games.length+" games");

                if(games.length>0){
                    for (Game game : games) {
                        if (game.players.length <= 1) {
                            Message response = new Message(3, userName, playerId, game.id, 1);
                            session.getBasicRemote().sendText(me.encode(response));
                            gameId = game.id;
                            System.out.println("Joined game... set game ID to: " + gameId);
                            break;
                        }
                    }
                }

                //if empty create a new game
                else {
                    Message response = new Message(2, userName, playerId, "KingOfTheHill");
                    session.getBasicRemote().sendText(me.encode(response));
                    Message response2 = new Message(1);
                    session.getBasicRemote().sendText(me.encode(response2));
                }
                return bufferedReader.readLine();
            }
        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        } catch (DecodeException e) {
            e.printStackTrace();
        }


        return bufferedReader.readLine();

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
    


