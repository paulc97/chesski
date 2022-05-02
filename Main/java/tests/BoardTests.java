package tests;

import Model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTests {

    Board board;

    @BeforeEach
    void setUp() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }


    @Test
   void PawnCheck() {
        assertEquals(board.getBlackPawns(), 0L);
    }

}
