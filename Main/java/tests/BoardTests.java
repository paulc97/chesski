package tests;

import Model.Board;
import Model.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testDrawBoard(){
        board.drawArray();
    }

    @Test
    void makeMove(){
        MoveGenerator mg = new MoveGenerator();
        Board b1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f4 0 1");
        String validMoves = mg.validMoves(b1);
        mg.selectAndMakeMove(b1, validMoves);
        System.out.println(b1.getFen());
    }


}
