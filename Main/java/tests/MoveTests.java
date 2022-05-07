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
        //board = new Board("rnbqkbnr/1p1pp1p1/p1p2p1p/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        board = new Board("7r/Pn6/8/8/8/8/2K4p/k1n5 w KQkq - 4 265");
        moveGenerator = new MoveGenerator();
    }


    @Test
    void validMovesTest2(){
        Board b1 = new Board("7r/Pn6/8/8/8/8/2K4p/k1n5 w KQkq - 4 265");
        System.out.println(b1.getFen());
        String valMoves = moveGenerator.validMoves(b1);
        moveGenerator.selectAndMakeMove(b1,valMoves);
        System.out.println(b1.getFen());
    }

    @Test
    void enPassantTest(){
        Board b1 = new Board("rnbqkbn1/pppp4/8/3Ppp2/6pP/N2B1p1N/P1PP3R/2RQK3 w q e6 0 12");
        System.out.println(b1.getFen());
        String valMoves = moveGenerator.validMoves(b1);
        System.out.println(valMoves);
        moveGenerator.selectAndMakeMove(b1,"34WE");
        System.out.println(b1.getFen());
    }

    @Test
    void castlingTest(){
        Board b1 = new Board("r3k1R1/8/4P2P/8/2P5/N1RP3N/P3B3/3QK3 b q - 0 12");
        System.out.println(b1.getFen());
        String valMoves = moveGenerator.validMoves(b1);
        System.out.println(valMoves); //TODO: Why castling kein valid move hier um aus Schach zu bringen?
        moveGenerator.selectAndMakeMove(b1,valMoves);
        System.out.println(b1.getFen());
    }
    @Test
    void castlingTest2(){
        Board b1 = new Board("r3kb2/p2pppp1/4P2P/8/2P5/N1RP3N/P3B1R1/3QK3 b q - 0 12");
        System.out.println(b1.getFen());
        String valMoves = moveGenerator.validMoves(b1);
        System.out.println(valMoves);
        moveGenerator.selectAndMakeMove(b1,"0402");
        System.out.println(b1.getFen());
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


    @Test
    void validMovesTest(){
        assertEquals(0, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("2k5/R1P4R/8/8/8/3K4/8/8 b - - 0 1"))));
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

