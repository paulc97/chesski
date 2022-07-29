import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static Model.MoveGenerator.getQuiescenceSearchIterations;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Benchmarks {
    Board board;
    MoveGenerator moveGenerator;

    @BeforeEach
    void setUp() {
        //board = new Board("rnbqkbnr/1p1pp1p1/p1p2p1p/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        board = new Board("7r/Pn6/8/8/8/8/2K4p/k1n5 w KQkq - 4 265");
        moveGenerator = new MoveGenerator();
    }

    //Assessment Function

    //TODO: zu testende FUnktionen (kannst copypaste machen)
    //b.assessBoard()
    //b.assessBoardStaticPST()
    //b.assessBoardNoPST()
    //b.assessBoardOnlyMaterial()
    @Test
    void assessBoardbenchmark1(){
        long timesStart = 0;
        long startEpoch = 0;
        long endepoch = 0;

        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");

        for (int i = 0;i<1000;i++){
            startEpoch = System.currentTimeMillis();
            b.assessBoardStaticPST(); //TODO: nach jedem Durchlauf Funktion ändern
            endepoch = System.currentTimeMillis();
            timesStart += endepoch - startEpoch;
        }
        System.out.println("Assessment value: " + b.assessBoardStaticPST()); //TODO: auch hier nach jedem Durchlauf ändern
        System.out.println("Rating board took "+(timesStart*0.001)+" Milliseconds");
    }
    @Test
    void assessBoardbenchmark2(){
        long timesStart = 0;
        long startEpoch = 0;
        long endepoch = 0;

        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");

        for (int i = 0;i<1000;i++){
            startEpoch = System.currentTimeMillis();
            b.assessBoardStaticPST(); //TODO: nach jedem Durchlauf Funktion ändern
            endepoch = System.currentTimeMillis();
            timesStart += endepoch - startEpoch;
        }
        System.out.println("Assessment value: " + b.assessBoardStaticPST()); //TODO: auch hier nach jedem Durchlauf ändern
        System.out.println("Rating board took "+(timesStart*0.001)+" Milliseconds");
    }
    @Test
    void assessBoardbenchmark3(){
        long timesStart = 0;
        long startEpoch = 0;
        long endepoch = 0;

        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");

        for (int i = 0;i<1000;i++){
            startEpoch = System.currentTimeMillis();
            b.assessBoardStaticPST(); //TODO: nach jedem Durchlauf Funktion ändern
            endepoch = System.currentTimeMillis();
            timesStart += endepoch - startEpoch;
        }
        System.out.println("Assessment value: " + b.assessBoardStaticPST()); //TODO: auch hier nach jedem Durchlauf ändern
        System.out.println("Rating board took "+(timesStart*0.001)+" Milliseconds");
    }

    //PVS VS MCTS
    //TODO: bei MCTS folgende Infos notieren: bestNode Score, bestNode visits, best Move, total Simulation Count
    @Test
    void testMCTS(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        //b.drawBoard();
        Node root = new Node(null, b);
        String bestMove = MonteCarloTreeSearch.getBestMove(root, 100000);
        System.out.println("best Move was: " + bestMove);
        System.out.println("total Simulation Count: " + MonteCarloTreeSearch.totalSimulationCount);

    }
    
}
