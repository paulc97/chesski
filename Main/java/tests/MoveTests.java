import Model.Board;
import Model.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveTests {

    Board board;
    MoveGenerator moveGenerator;

    @BeforeEach
    void setUp() {
        board = new Board("rnbqkbnr/1p1pp1p1/p1p2p1p/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        moveGenerator = new MoveGenerator();
    }


    @Test
    void BishopMoveTest() {
        assertEquals("0", moveGenerator.getMoveCount(moveGenerator.checkBishopMoves(board)));
    }

    @Test
    void RookMoveTest() {
        assertEquals("0", moveGenerator.getMoveCount(moveGenerator.checkRookMoves(board)));
    }

    @Test
    void QueenMoveTest() {
        assertEquals("18", moveGenerator.getMoveCount(moveGenerator.checkQueenMoves(board)));
    }

    @Test
    void movesTest(){
        assertEquals("18", moveGenerator.ownPossibleMoves(board));
    }

    //@Test
    //void testMovesCount(){

     //   TestBoard[] boards = new TestBoard[1000];

     //   for(TestBoard oneBoard:boards){
     //       assertEquals(oneBoard.moves,moveGenerator.ownPossibleMoves("",new Board(oneBoard.Board)));
     //   }

    //}

    @Test
    void benchmark(){

        //StartPosition
        long timesStart = 0;
        long startEpoch = 0;
        long endepoch = 0;

        for (int i = 0;i<1000;i++){
            startEpoch = System.currentTimeMillis();
            moveGenerator.ownPossibleMoves(new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            endepoch = System.currentTimeMillis();
            timesStart += endepoch - startEpoch;
        }

        long timesMid = 0;

        for (int i = 0;i<1000;i++){
            startEpoch = System.currentTimeMillis();
            moveGenerator.ownPossibleMoves(new Board("r1bqk2r/ppp1bppp/2np1n2/4p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 0 1"));
            endepoch = System.currentTimeMillis();
            timesMid += endepoch - startEpoch;
        }

        long timesEnd = 0;

        for (int i = 0;i<1000;i++){
            startEpoch = System.currentTimeMillis();
            moveGenerator.ownPossibleMoves(new Board("4k3/8/8/3PP3/3pp3/8/8/3K4 w - - 0 1"));
            endepoch = System.currentTimeMillis();
            timesEnd += endepoch - startEpoch ;
        }

        System.out.println("Generating Start Position took "+(timesStart*0.001)+" Milliseconds");
        System.out.println("Generating Mid Position took "+(timesMid*0.001)+" Milliseconds");
        System.out.println("Generating End Position took "+(timesEnd*0.001)+" Milliseconds");


    }


}

