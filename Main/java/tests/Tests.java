import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static Model.Mask.FileMasks8;
import static Model.MoveGenerator.getQuiescenceSearchIterations;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {
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
    void sthTest(){
        board = new Board("7r/Pn6/8/8/8/1P3P2/2K4p/k1n5 w - - 4 265");
        System.out.println(Long.numberOfTrailingZeros(65280));
        //1111111100000000
        //result: 8
        System.out.println(board.addPSTValues(board.getWhitePawns(), PieceSquareTables.MG_WHITE_PAWNS));
    }
    @Test
    void assessmentTest1(){
        board = new Board("3N4/6K1/1p6/1r1p1k2/1rP1P1N1/2Q1Rn2/2p5/1PPP4 w - - 0 1");
        MoveGenerator mg = new MoveGenerator();
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
        board = new Board("2Q5/6K1/1p2N3/1r1p1k2/1r2R1N1/5n2/2p5/8 w - - 0 1");
        String result = moveGenerator.minMax(board, 4, true);
        System.out.println("result is: " + result);
        String move = result.substring(0,4);
        System.out.println(moveGenerator.convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + moveGenerator.convertMoveDigitsToField(move.charAt(2),move.charAt(3)));
    }
    /*    @Test
        void pvsTest1(){
            board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
            String result = PrincipalVariationSearch.iterativeDeepeningPVSNoTimeLimit(board, 2, false);
            System.out.println("result is: " + result);
        }*/
    @Test
    void alphaBetaTest1(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.alphaBeta(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("result is: " + result);
    }
    @Test
    void alphaBetaTest2(){
        board = new Board("K7/8/8/8/8/8/8/2k5 w - - 0 1");
        String result = moveGenerator.alphaBeta(board, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("result is: " + result);
        String move = result.substring(0,4);
        System.out.println(moveGenerator.convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + moveGenerator.convertMoveDigitsToField(move.charAt(2),move.charAt(3)));
    }
    @Test
    void alphaBetaTest3(){
        board = new Board("2Q5/6K1/1p2N3/1r1p1k2/1r2R1N1/5n2/2p5/8 w - - 0 1");
        String result = moveGenerator.alphaBeta(board, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("result is: " + result);
        String move = result.substring(0,4);
        System.out.println(moveGenerator.convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + moveGenerator.convertMoveDigitsToField(move.charAt(2),move.charAt(3)));
    }
    @Test
    void alphaBetaTestForum(){
        board = new Board("krr3rr/pp6/8/8/8/3K4/8/8 b - - 0 1");
        String result = moveGenerator.alphaBeta(board, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        System.out.println("result is: " + result);
        String move = result.substring(0,4);
        System.out.println(moveGenerator.convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + moveGenerator.convertMoveDigitsToField(move.charAt(2),move.charAt(3)));
    }
    @Test
    void iterativeDeepeningSearch(){
        board = new Board("4k2n/8/N4q2/1P6/3PPPP1/6P1/1P6/2R1K3 w - - 0 1");
        String result = moveGenerator.iterativeDeepeningSearch(board, 3000);
        System.out.println("result is: " + result);
    }
    //TODO: bei timeLimit 6000 Error bei "move:2012ze(max): 3" -> IndexOutOfBounds (getCreatedByMove erfolgt nicht) in suchtiefe 4
    @Test
    void iterativeDeepeningSearch2(){
        board = new Board("2Q5/6K1/1p2N3/1r1p1k2/1r2R1N1/5n2/2p5/8 w - - 0 1");
        String result = moveGenerator.iterativeDeepeningSearch(board, 10000);
        System.out.println("result is: " + result);
        String move = result.substring(0,4);
        System.out.println(moveGenerator.convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + moveGenerator.convertMoveDigitsToField(move.charAt(2),move.charAt(3)));
    }
    @Test
    void validMovesTest2(){
        Board b1 = new Board("7r/Pn6/8/8/8/8/2K4p/k1n5 w KQkq - 4 265");
        System.out.println(b1.bitboardsToFenParser());
        String valMoves = moveGenerator.validMoves(b1);
        moveGenerator.makeMove(b1,moveGenerator.moveSelector(b1, valMoves, 1));
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
        moveGenerator.makeMove(b1,moveGenerator.moveSelector(b1, valMoves,1));
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

    @Test
    void BlackPawnPositionCheck() {
        assertEquals(65280L, board.getBlackPawns());
    }

    @Test
    void WhiteRookPositionCheck() {
        assertEquals(-9151314442816847872L, board.getWhiteRooks());
    }

    @Test
    void getAllPieces() {
        assertEquals(-281474976645121L, board.getAllPieces());
    }

    @Test
    void AssignedPlayerCheck() {
        assertEquals(true, board.isCurrentPlayerIsWhite());
    }

    @Test
    void EnPassantMoves() {
        assertEquals("f4", board.getEnPassants());
    }

    @Test
    void MovesCount() {
        assertEquals(0, board.getHalfMoveCount());
    }

    @Test
    void NextMovesCount() {
        assertEquals(1, board.getNextMoveCount());
    }



    @Test
    void makeMove(){
        MoveGenerator mg = new MoveGenerator();
        Board b1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f4 0 1");
        String validMoves = mg.validMoves(b1);
        mg.makeMove(b1, mg.moveSelector(b1, validMoves, 1));
        System.out.println(b1.bitboardsToFenParser());
    }

    @Test
    void testDigitConversion(){
        System.out.println(FileMasks8[("c7").charAt(0)-97]);
    }

    @Test
    void calculateGamePhaseCheck() {
        Board b = new Board("rnbqkbnr/ppp1pppp/8/8/8/8/PPPPPPPP/2B1KBNR w KQkq f4 0 1");
        int gamePhase = b.calcGamePhase();
        System.out.println(gamePhase);


    }

    @Test
    void assessmentWithStaticVSDynamicPST() {
        Board b = new Board("rnbqkbnr/pppppppp/8/8/8/8/R1PPPPPP/2BQKBNR w KQkq f4 0 1");

        int gamePhase = b.calcGamePhase();
        System.out.println(gamePhase);

        //um zu testen: bei einer Methode PST, bei anderer PST Tapered Eval auskommentieren
        System.out.println(b.assessBoard());
        System.out.println(b.assessBoardTPT(new HashMap<Long,Integer>(), new Zobrist()));


    }

    @Test
    void testBitCounter(){

        Board b = new Board("rnbqkbnr/pppppppp/8/8/8/8/R1PPPPPP/2BQKBNR w KQkq f4 0 1");

        assertEquals(8, Long.bitCount(board.getWhitePawns()));
    }

    @Test
    void testCaptureOrdering(){
        Board b = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        for(int i=0;i<1;i++){
            String a = MoveGenerator.ownPossibleMoves(b).replace("-","");
            System.out.println(a);
            System.out.println(a.length()%4);
            System.out.println(MoveGenerator.sortMovesCaptures(b,a));
        }

    }

    @Test
    void testZobristValid(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        MoveGenerator mg = new MoveGenerator();
        for (int i = 0;i<1000;i++){
            //  System.out.println("Key for Board:"+ mg.zobrist.getZobristHash(b.getWhitePawns(),b.getWhiteKnights(),b.getWhiteBishops(),b.getWhiteRooks(),b.getWhiteQueen(),b.getWhiteKing(),b.getBlackPawns(),b.getBlackKnights(),b.getBlackBishops(),b.getBlackRooks(),b.getBlackQueen(),b.getBlackKing(),b.isWhiteToCastleKingside(),b.isWhiteToCastleQueenside(),b.isBlackToCastleKingside(),b.isBlackToCastleQueenside(),b.isCurrentPlayerIsWhite()));
        }
    }
    @Test
    void testZobristTime(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        MoveGenerator mg = new MoveGenerator();
        long startEpoch = System.currentTimeMillis();
        for (int i = 0;i<100;i++){
            //b.assessBoardTPT(mg.assesedBoards,mg.zobrist);
        }
        long endEpoch = System.currentTimeMillis();
        long time = endEpoch - startEpoch;
        System.out.println("Took: "+ time);
    }
    @Test
    void testNoZobristTime(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        MoveGenerator mg = new MoveGenerator();
        long startEpoch = System.currentTimeMillis();
        for (int i = 0;i<100;i++){
            //b.assessBoard();
        }
        long endEpoch = System.currentTimeMillis();
        long time = endEpoch - startEpoch;
        System.out.println("Took: "+ time);
    }
    //ab hier MCTS-Tests
    @Test
    void testCalcUCT(){
        double calc = MonteCarloTreeSearch.calculateUCTvalue(6,4,3, Math.sqrt(2));
        System.out.println(calc);
    }
    @Test
    void selectionPhase(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        Node root = new Node(null, b);
        Node child1 = new Node(root, b);
        Node child2 = new Node(root, b);
        Node child3 = new Node(root, b);
        //root.children = new ArrayList<>();
        root.children.add(child1);
        root.children.add(child2);
        root.children.add(child3);
        root.visits = 5;
        child1.visits =2;
        child2.visits=2;
        child3.visits=1;

        child1.score = 6;
        child2.score = 5;
        child3.score = 6;
        System.out.println(child1);
        System.out.println(child2);
        System.out.println(child3);

        System.out.println(MonteCarloTreeSearch.calculateUCTvalue(child1.parent.visits, child1.visits, child1.score, Math.sqrt(2)));
        System.out.println(MonteCarloTreeSearch.calculateUCTvalue(child3.parent.visits, child3.visits, child3.score, Math.sqrt(2)));

        System.out.println(MonteCarloTreeSearch.selectNode(root));

    }
    @Test
    void testRandomMove(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        String allValidMoves = MoveGenerator.validMoves(b);
        System.out.println(allValidMoves);
        int randomNumber = 0 + (int)(Math.random() * (allValidMoves.length()/4 - 0));
        String randomMove = allValidMoves.substring(randomNumber*4, randomNumber*4+4);
        System.out.println(randomMove);
    }
    @Test
    void testSimulation(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        Node root = new Node(null,b);
        MonteCarloTreeSearch.simulate(root);
    }
    @Test
    void testBackpropagation(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        Node root = new Node(null, b);
        Node child1 = new Node(root, b);
        Node child2 = new Node(root, b);
        Node child3 = new Node(root, b);
        //root.children = new ArrayList<>();
        root.children.add(child1);
        root.children.add(child2);
        root.children.add(child3);

        root.visits = 5;
        root.score = 6;
        child1.visits =2;
        child2.visits=2;
        child3.visits=1;

        child1.score = 6;
        child2.score = 5;
        child3.score = 6;

        Node child11 = new Node(child1,b);
        MonteCarloTreeSearch.backpropagate(child11, 1);
        //MonteCarloTreeSearch.backpropagate(child11, -2);
        System.out.println(child11.visits);
        System.out.println(child11.score);
        System.out.println(child1.visits);
        System.out.println(child1.score);
        System.out.println(root.visits);
        System.out.println(root.score);
    }

    @Test
    void testExpandAllChildren(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        String allPossibleMoves = MoveGenerator.validMoves(b);
        int moveCount = MoveGenerator.getMoveCount(allPossibleMoves);
        System.out.println(moveCount);
        Node root = new Node(null, b);
        root.expandAllChildren();
        System.out.println("length of children: " + root.children.size());
        for (Node child: root.children){
            System.out.println(child);
            System.out.println(child.visits);
            System.out.println(child.score);
            System.out.println(child.parent);
        }
    }
    @Test
    void testMCTS(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        //b.drawBoard();
        Node root = new Node(null, b);
        String bestMove = MonteCarloTreeSearch.getBestMove(root, 100000);
        System.out.println("best Move was: " + bestMove);
    }
}
