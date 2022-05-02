package tests;

import Model.Board;
import Model.MoveGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PawnTests {

    Board board;
    MoveGenerator moveGenerator;

    @BeforeEach
    void setUp() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f4 0 1");
        moveGenerator = new MoveGenerator();
    }


    @Test
    void PawnMoveTest() {
        assertEquals("1", moveGenerator.ownPossibleMoves("", board));
    }


}
