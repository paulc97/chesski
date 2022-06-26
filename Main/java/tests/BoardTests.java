

import Model.Board;
import Model.MoveGenerator;
import Model.Pieces.Zobrist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static Model.Mask.FileMasks8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTests {

    Board board;

    @BeforeEach
    void setUp() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f4 0 1");
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


}
