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
    void substringTest(){
        System.out.println("12345000".substring(4));
    }

    @Test
    void assessmentTest1(){
        board = new Board("3k4/8/3q4/4P3/8/8/8/4K3 b - - 0 1");
        System.out.println(board.assessBoard());
    }

    @Test
    void minmaxTest1(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.minMax(board, 1, true);
        System.out.println("result is: " + result);
    }

    @Test
    void minmaxTest2(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.minMax(board, 2, true);
        System.out.println("result is: " + result);
    }

    @Test
    void alphaBetaTest1(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.alphaBeta(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("result is: " + result);
    }

    @Test
    void alphaBetaTest2(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.alphaBeta(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("result is: " + result);
    }


    @Test
    void iterativeDeepeningSearch(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.iterativeDeepeningSearch(board, 3000);
        System.out.println("result is: " + result);
    }
    //bei timeLimit 6000 Error bei "move:2012ze(max): 3" -> IndexOutOfBounds (getCreatedByMove erfolgt nicht) in suchtiefe 4


    @Test
    void validMovesTest2(){
        Board b1 = new Board("7r/Pn6/8/8/8/8/2K4p/k1n5 w KQkq - 4 265");
        System.out.println(b1.bitboardsToFenParser());
        String valMoves = moveGenerator.validMoves(b1);
        moveGenerator.makeMove(b1,moveGenerator.moveSelector(b1, valMoves));
        System.out.println(b1.bitboardsToFenParser());
    }

    @Test
    void enPassantTest(){
        Board b1 = new Board("rnbqkbn1/pppp4/8/3Ppp2/6pP/N2B1p1N/P1PP3R/2RQK3 w q e6 0 12");
        System.out.println(b1.bitboardsToFenParser());
        String valMoves = moveGenerator.validMoves(b1);
        System.out.println(valMoves);
        moveGenerator.makeMove(b1,"34WE");
        System.out.println(b1.bitboardsToFenParser());
    }

    @Test
    void castlingTest(){
        Board b1 = new Board("r3k1R1/8/4P2P/8/2P5/N1RP3N/P3B3/3QK3 b q - 0 12");
        System.out.println(b1.bitboardsToFenParser());
        String valMoves = moveGenerator.validMoves(b1);
        System.out.println(valMoves); //TODO: Why castling kein valid move hier um aus Schach zu bringen?
        moveGenerator.makeMove(b1,moveGenerator.moveSelector(b1, valMoves));
        System.out.println(b1.bitboardsToFenParser());
    }
    @Test
    void castlingTest2(){
        Board b1 = new Board("r3kb2/p2pppp1/4P2P/8/2P5/N1RP3N/P3B1R1/3QK3 b q - 0 12");
        System.out.println(b1.bitboardsToFenParser());
        String valMoves = moveGenerator.validMoves(b1);
        System.out.println(valMoves);
        moveGenerator.makeMove(b1,"0402");
        System.out.println(b1.bitboardsToFenParser());
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

    @Test
    void KingMoves(){
        //assertEquals(26, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("4k3/8/8/8/8/8/8/R3K2R w - - 0 1")))); //TODO: erkennt Castle nicht
        assertEquals(2, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("8/8/8/8/8/4k3/8/4K3 w - - 0 1"))));
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

        for (int i = 0;i<500000;i++){
            startEpoch = System.currentTimeMillis();
            moveGenerator.ownPossibleMoves(new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            endepoch = System.currentTimeMillis();
            timesStart += endepoch - startEpoch;
        }

        long timesMid = 0;

        for (int i = 0;i<500000;i++){
            startEpoch = System.currentTimeMillis();
            moveGenerator.ownPossibleMoves(new Board("r1bqk2r/ppp1bppp/2np1n2/4p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 0 1"));
            endepoch = System.currentTimeMillis();
            timesMid += endepoch - startEpoch;
        }

        long timesEnd = 0;

        for (int i = 0;i<500000;i++){
            startEpoch = System.currentTimeMillis();
            moveGenerator.ownPossibleMoves(new Board("4k3/8/8/3PP3/3pp3/8/8/3K4 w - - 0 1"));
            endepoch = System.currentTimeMillis();
            timesEnd += endepoch - startEpoch ;
        }

        System.out.println("Generating Start Position took "+(timesStart*0.000002)+" Milliseconds");
        System.out.println("Generating Mid Position took "+(timesMid*0.000002)+" Milliseconds");
        System.out.println("Generating End Position took "+(timesEnd*0.000002)+" Milliseconds");


    }

    @Test
    void debugForumMoves(){
        System.out.println(moveGenerator.convertMoveDigitsToField('5','2'));
        System.out.println(moveGenerator.convertMoveDigitsToField('4','2'));
        System.out.println(moveGenerator.convertMoveDigitsToField('2','2'));
        assertEquals(32, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbqkb1r/ppp1pppp/5n2/3p4/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 1"))));//TODO:Pawn
        //assertEquals(2, moveGenerator.checkpawnMoves(new Board("r7/P1P5/p7/P4k2/n7/P1P2K2/1BP2PBN/1QR2R1n w - - 0 1"))); //TODO:Does not Check if Field is blocked
        //assertEquals(3, moveGenerator.getMoveCount(moveGenerator.checkValidKingMoves(new Board("r3k2r/ppp1npbp/b3p1p1/8/8/4P3/PPP2PPP/R3K2R w KQkq - 0 1")))); //TODO: King does not check if Field is attacked. Does not check if he castles trough check.
        //assertEquals(13, moveGenerator.getMoveCount(moveGenerator.checkpawnMoves(new Board("rnbqkb1r/ppp1pppp/5n2/3p4/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 1"))));//TODO: Pawn does not check if Figure is infront of him.
        //assertEquals(2, moveGenerator.getMoveCount(moveGenerator.checkValidKingMoves(new Board("r2kq3/3r4/8/8/7b/p7/P3BN2/R3K2R w KQq - 0 1"))));

    }

    @Test
    void ForumsMoves(){
        assertEquals(20, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"))));
        assertEquals(18, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbqkbnr/1pppppp1/p6p/8/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3"))));
        assertEquals(28, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbqkbnr/ppppp1pp/8/5p2/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 2"))));
        assertEquals(22, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbqkbnr/pp1p1ppp/4p3/1Pp5/8/2N5/P1PPPPPP/R1BQKBNR w KQkq - 0 1"))));
        assertEquals(53, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("1rbqk2r/3nBppp/p2p4/3Qp3/Pp2P3/6PB/1PP1NP1P/R3K2R w KQ - 0 1"))));
        assertEquals(7, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnb1kbnr/pp1ppppp/2p5/q7/8/3P1P2/PPP1P1PP/RNBQKBNR w KQkq - 0 1"))));
        assertEquals(24, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("7k/7q/5pqp/6p1/NP3P1P/1KP3P1/2B5/8 b - - 0 1"))));
        assertEquals(10, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("r2kq3/3r4/8/8/7b/p7/P3BN2/R3K2R w KQq - 0 1"))));
        assertEquals(19, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("r7/P1P5/p7/P4k2/n7/P1P2K2/1BP2PBN/1QR2R1n w - - 0 1"))));//TODO:CheckPawnMovetest 16->19 weil Promotion 4 Möglichkeiten einzeln bewertet
        assertEquals(37, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("B3k2r/p1pqbppp/n2p3n/4p3/6b1/1PPPP1P1/P4P1P/RNBQK1NR b KQk - 0 16"))));
        assertEquals(29, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnb1kbnr/ppp2ppp/8/K2pP2q/5p2/3P4/PPP1B1PP/RNBQ2NR w - d6 0 14"))));
        assertEquals(40, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("r1bq1r2/pp2n3/4N2k/3pPppP/1b1n2Q1/2N5/PP3PP1/R1B1K2R w KQ g6 0 1"))));
        assertEquals(16, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("6k1/B5q1/KR6/8/8/8/6p1/8 w - - 0 1"))));
        assertEquals(28, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("6k1/B5q1/KR6/8/8/8/6p1/8 b - - 0 1"))));
        assertEquals(21, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("r3k2r/ppp1npbp/b3p1p1/8/8/4P3/PPP2PPP/R3K2R w KQkq - 0 1")))); //TODO:Check second Test from debugForumMives
        assertEquals(2, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("r1b3k1/ppb2ppp/8/2B1p1P1/1P2N2P/P3P3/2P2P2/3rK2R w - - 0 19"))));
        assertEquals(13, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("8/8/6k1/5n2/5P2/7p/2KN4/8 w - - 0 57"))));
        assertEquals(21, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("r3k1nr/pp3ppp/3p4/3P4/8/3P4/PP3PPP/RN2K2R w KQkq - 0 1"))));
        assertEquals(8, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("8/8/1k6/8/8/8/6K1/8 b - - 0 1"))));
        assertEquals(21, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("1r6/8/2pbk1p1/p4p2/2KPnP2/4P3/PB4PP/5R2 w - - 0 1"))));
        assertEquals(20, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("2R5/2r2bkp/2n4p/1p6/4p2N/2K1n3/7B/8 w - - 0 1"))));
        assertEquals(2, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("3k4/8/8/8/8/8/7r/3K4 w - - 0 1"))));
        assertEquals(34, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnq1kb1r/p1ppppp1/1p6/7p/2PPb1nP/5N2/PP3P2/RNBQKBR1 w Qkq - 0 8"))));
        assertEquals(33, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("1rbqk3/p1p3p1/np1p1n1r/B1b1pp1p/2P4P/3PP1P1/PP1QBP2/RN3KNR b - - 1 11"))));
        assertEquals(32, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("2bqk2r/ppp4p/3npp2/n7/2pPPbBR/1Pr2N2/PBP4P/RN1QK3 w Qk - 0 1"))));
        assertEquals(31, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("7k/1P4pp/4B3/3b1n2/6P1/5P2/3P2N1/3KQ3 w - - 0 1"))));
        assertEquals(30, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbkqbnr/ppp1pppp/8/3p4/2PP4/8/PP1PPPPP/RNBKQBNR b - - 0 2"))));
        assertEquals(31, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("5k1r/8/4B3/8/8/8/P7/KQ1N4 w - - 3 40"))));
        assertEquals(12, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("5k1r/8/4B3/p7/PP6/8/P7/KQ1N4 b - - 0 30"))));
        assertEquals(33, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("rnbqkb1r/ppp1pppp/5n2/3p4/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 1"))));//TODO:Pawn
        assertEquals(16, moveGenerator.getMoveCount(moveGenerator.validMoves(new Board("8/8/8/8/2R5/6pk/1r6/6K1 w - – 0 51"))));

    }


}

