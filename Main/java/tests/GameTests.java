package tests;

import Model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTests {

    Game game;

    @BeforeEach
    void setUp() {
        game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }


    @Test
   void PawnCheck() {
        assertEquals(game.getBlackPawns(), 0L);
    }

}
