package App;

import Controller.*;
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
public class Main {
    private static CountDownLatch latch;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private MessageEncoder me = new MessageEncoder();
    private MessageDecoder md = new MessageDecoder();

    //Config
    private String userName="Magnus";
    private long playerId;


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
    public String onMessage(String message, Session session) throws DecodeException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            logger.info("Received: " + message);
            switch (message) {

                //TODO handle here all possible messages including move generation etc.
                default: String userInput = bufferedReader.readLine();
                    return userInput;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            client.connectToServer(Main.class, new URI("ws://localhost:8025/websockets/game"));
            latch.await();
        } catch (DeploymentException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

        /*//create a user
        String user = "Allman";


        //get the ID for the user
        WSController controller = new WSController();
        String id = WSController.login(user);

        //try to join a game
        controller.joinGame(user, id);

        //create a new Game on the server if not successfull
        if (controller.board == null) {
            controller.createGame(user, id);
        }



        while (!controller.board.isGameOver()){

            //check if it's the turn of the KI
            if(controller.board.KiIsPlaying()){

            //TODO create and select moves and send it to the server

            //TODO if a move is represented as fen string, implement a method to get a fen String out of a Board or Move
                controller.commitMove("FEN");
                Thread.sleep(2000);

            } else {
                Thread.sleep(500);
                controller.updateGame(user, id);
            }
        }*/

        
 }
    


