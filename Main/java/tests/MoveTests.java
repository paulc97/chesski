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
        assertEquals("0", moveGenerator.getMoveCount(moveGenerator.checkBishopMoves("", board)));
    }

    @Test
    void RookMoveTest() {
        assertEquals("0", moveGenerator.getMoveCount(moveGenerator.checkRookMoves("", board)));
    }

    @Test
    void QueenMoveTest() {
        assertEquals("18", moveGenerator.getMoveCount(moveGenerator.checkQueenMoves("", board)));
    }

    @Test
    void movesTest(){
        assertEquals("18", moveGenerator.ownPossibleMoves("", board));
    }


}

