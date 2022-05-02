package App;

import Controller.WSController;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        //create a user
        String user = "Allman";

        //get the ID for the user
        WSController controller = new WSController();
        String id = WSController.login(user);

        //try to join a game
        controller.joinGame();

        //create a new Game on the server if not successfull
        if (controller.board == null) {
            controller.createGame();
        }



        while (!controller.board.isGameOver()){

            //check if it's the turn of the KI
            if(controller.board.KiIsPlaying()){

            //TODO create and select moves and send it to the server
                Thread.sleep(2000);

            } else {
                Thread.sleep(500);
                controller.updateGame();
            }
        }

        

    }
    

}
