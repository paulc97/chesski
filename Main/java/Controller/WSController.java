package Controller;

import App.Main;
import Model.Board;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class WSController {

    private MessageEncoder messageEncoder = new MessageEncoder();
    private Logger logger;
    private CountDownLatch latch;

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected. Session id: " + session.getId());
        try {
            session.getBasicRemote().sendText("start");
            session.getBasicRemote().sendText(messageEncoder.encode(new Message(0, "Allman")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            logger.info("Received: " + message);
            String userInput = bufferedReader.readLine();
            return userInput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public WSController(CountDownLatch latch, Logger logger) {
        this.latch = latch;
        this.logger = logger;
    }

    /*    public Board board;

    public static String login (String user){

        //TODO: Implement login connection to websocket
        String id = "TODO";
        return id;
    }

    public void updateGame(String user, String id) {
        //TODO implement
    }


    public void joinGame(String user, String id) {
        //TODO implement
    }

    public void createGame(String user, String id) {
        //TODO implement
    }

    public void commitMove(String fen) {
        //TODO implement
    }*/


}

