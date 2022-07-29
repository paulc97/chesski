package App;

import Model.Board;
import Model.MonteCarloTreeSearch;
import Model.MoveGenerator;
import Model.Node;

public class LocalGame {
    /**
     * KI plays against itself, possibly using different KI techniques
     *
     * @param args
     */
    public static void main(String[] args) {
        MoveGenerator mg = new MoveGenerator();
        Board b1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); //starting position

        long usedTime = 0;

        while(true){

            //switch players
            if (b1.isCurrentPlayerIsWhite()){
                b1.setKIPlaysWhite(true);
            } else {
                b1.setKIPlaysWhite(false);
            }

            long currentTime = System.currentTimeMillis();
            String validMoves = mg.validMoves(b1);

            if (b1.isKIPlayingWhite()){ //white player KI executes MCTS
                long timeLimit = MoveGenerator.standardDeviationTimeLimit(b1.getNextMoveCount(), 300L);
                Node node = new Node (null, b1);
                MoveGenerator.makeMove(b1, MonteCarloTreeSearch.getBestMove(node, (int) timeLimit));
            } else { //black player KI executes alpha-beta-search
                mg.makeMove(b1, mg.moveSelector(b1, validMoves, usedTime));
            }

            b1.drawBoard();

            //check if game is over
            if(b1.isGameOver()){
                if(b1.isRemis()){
                    System.out.println("Game Over - Remis");
                    break;
                } else {
                    if(b1.isWhiteWon()){
                        System.out.println("Game Over - White Won");
                        break;
                    } else {
                        System.out.println("Game Over - Black Won");
                        break;
                    }
                }

            }
            usedTime += (System.currentTimeMillis()- currentTime);
        }

    }
}
