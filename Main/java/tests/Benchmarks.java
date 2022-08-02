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
    void minimaxBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 6; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.minMax(b, i, true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result,b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
        }
    }
    @Test
    void minimaxBenchmark2(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        for (int i = 1; i <= 4; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.minMax(b, i, true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result,b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
        }
    }
    @Test
    void minimaxBenchmark3(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        for (int i = 1; i <= 7; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.minMax(b, i, false);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result,b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
        }
    }
    @Test
    void alphaBetaBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.alphaBeta(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void alphaBetaBenchmark2(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.alphaBeta(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void alphaBetaBenchmark3(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        for (int i = 1; i <= 7; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.alphaBeta(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void alphaBetaTimeBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 15; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            MoveGenerator.setCutoffs(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.alphaBetaTimeLimit(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,true, startEpoch, 25000);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("Number of cutoffs "+MoveGenerator.getCutoffs());
        }
    }
    @Test
    void alphaBetaTimeBenchmark2(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        for (int i = 1; i <= 15; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            MoveGenerator.setCutoffs(0);
            startEpoch = System.currentTimeMillis();
            String result = moveGenerator.alphaBetaTimeLimit(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,true, startEpoch, 25000);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("Number of cutoffs "+MoveGenerator.getCutoffs());
        }
    }
    @Test
    void alphaBetaTimeBenchmark3(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        for (int i = 1; i <= 15; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            MoveGenerator.setAssessedLeaves(0);
            MoveGenerator.setQuiescenceSearchIterations(0);
            MoveGenerator.setCutoffs(0);
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.alphaBetaTimeLimit(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false, startEpoch, 25000);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("Number of cutoffs "+MoveGenerator.getCutoffs());
        }
    }
    ///////HIER BENCHMARKS FÜR PVS//////
    @Test
    void IDSwithPVSBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            PrincipalVariationSearch.assessedLeaves = 0;
            startEpoch = System.currentTimeMillis();
            //String result = PrincipalVariationSearch.PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false); //Achtung: negativer Wert für bewertung!
            String result = PrincipalVariationSearch.principalVariationSearchWithoutTimelimit(b, i, true);
            PrincipalVariationSearch.currentPv= null; //Zurücksetzen für next "Großiteration"
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+PrincipalVariationSearch.assessedLeaves);
            System.out.println("Assessed leaves per second: "+(PrincipalVariationSearch.assessedLeaves/(time*0.001)));
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void IDSwithPVSBenchmark2(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            PrincipalVariationSearch.assessedLeaves = 0;
            startEpoch = System.currentTimeMillis();
            //String result = PrincipalVariationSearch.PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false); //Achtung: negativer Wert für bewertung!
            String result = PrincipalVariationSearch.principalVariationSearchWithoutTimelimit(b, i, true);
            PrincipalVariationSearch.currentPv= null; //Zurücksetzen für next "Großiteration"
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+PrincipalVariationSearch.assessedLeaves);
            System.out.println("Assessed leaves per second: "+(PrincipalVariationSearch.assessedLeaves/(time*0.001)));
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void IDSwithPVSBenchmark3(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        for (int i = 1; i <= 7; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            PrincipalVariationSearch.assessedLeaves = 0;
            startEpoch = System.currentTimeMillis();
            //String result = PrincipalVariationSearch.PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false); //Achtung: negativer Wert für bewertung!
            String result = PrincipalVariationSearch.principalVariationSearchWithoutTimelimit(b, i, false);
            PrincipalVariationSearch.currentPv= null; //Zurücksetzen für next "Großiteration"
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+PrincipalVariationSearch.assessedLeaves);
            System.out.println("Assessed leaves per second: "+(PrincipalVariationSearch.assessedLeaves/(time*0.001)));
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    //////HIER BENCHMARKS FÜR ISOLIERTE IDS (OHNE ZEITLIMIT) ZUM VERGLEICH MIT PVS/////////////
    //iterativeDeepeningSearchNoTimeLimit
    @Test
    void IDSwithOUTPVSBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.iterativeDeepeningSearchNoTimeLimit(b, i, true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void IDSwithOUTPVSBenchmark2(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.iterativeDeepeningSearchNoTimeLimit(b, i, true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    @Test
    void IDSwithOUTPVSBenchmark3(){
        Board b = new Board("7k/5ppp/8/R7/5n2/3B4/2K5/8 b - - 0 1");
        for (int i = 1; i <= 7; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            startEpoch = System.currentTimeMillis();
            String result = MoveGenerator.iterativeDeepeningSearchNoTimeLimit(b, i, false);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+ MoveGenerator.getAssessedLeaves());
            System.out.println("Assessed leaves per second: "+(MoveGenerator.getAssessedLeaves()/(time*0.001)));
            System.out.println("Quiescence Search Iterations: "+getQuiescenceSearchIterations());
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    ///////HIER BENCHMARKS FÜR PVS mit Nullfenstersuche//////
    @Test
    void IDSwithPVSMinimalWindowBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 5; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            PrincipalVariationSearch.assessedLeaves = 0;
            startEpoch = System.currentTimeMillis();
            //String result = PrincipalVariationSearch.PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false); //Achtung: negativer Wert für bewertung!
            String result = PrincipalVariationSearch.nullWindowSearch(b, i, true);
            PrincipalVariationSearch.currentPv= null; //Zurücksetzen für next "Großiteration"
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+PrincipalVariationSearch.assessedLeaves);
            System.out.println("Assessed leaves per second: "+(PrincipalVariationSearch.assessedLeaves/(time*0.001)));
            System.out.println("LeavesFoundAsHash: " + MoveGenerator.zobrist.found);
            System.out.println("Needed research: " + PrincipalVariationSearch.researchNeeded + " times");
            PrincipalVariationSearch.researchNeeded = 0;
            MoveGenerator.zobrist.found = 0;
            MoveGenerator.assesedBoards = new HashMap<Long,Integer>();
        }
    }
    //DIE SIND NUR ZUM PERSÖNLICHEN TESTEN!
/*    @Test
    void pvsBenchmark1(){
        Board b = new Board("6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1");
        for (int i = 1; i <= 4; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            PrincipalVariationSearch.assessedLeaves = 0;
            startEpoch = System.currentTimeMillis();
            //String result = PrincipalVariationSearch.PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false); //Achtung: negativer Wert für bewertung!
            String result = PrincipalVariationSearch.moiterativeDeepeningPVSNoTimeLimitWithWindow(b, i, true);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+PrincipalVariationSearch.assessedLeaves);
            System.out.println("Assessed leaves per second: "+(PrincipalVariationSearch.assessedLeaves/(time*0.001)));
        }
    }*/
/*    @Test
    void pvsBenchmark2(){
        Board b = new Board("Q4R2/3kr3/1q3n1p/2p1p1p1/1p1bP1P1/1B1P3P/2PBK3/8 w - - 1 0");
        for (int i = 1; i <= 4; i++) {
            //StartPosition
            long time = 0;
            long startEpoch = 0;
            long endepoch = 0;
            PrincipalVariationSearch.assessedLeaves = 0;
            startEpoch = System.currentTimeMillis();
            //String result = PrincipalVariationSearch.PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE,false); //Achtung: negativer Wert für bewertung!
            String result = PrincipalVariationSearch.moiterativeDeepeningPVSNoTimeLimit(b, i, false);
            endepoch = System.currentTimeMillis();
            time += endepoch - startEpoch;
            System.out.println(result);
            String move = MoveGenerator.convertInternalMoveToGameserverMove(result, b);
            System.out.println("Best move: " + move +" in depth "+i);
            System.out.println("Elapsed time: "+time+" ms");
            System.out.println("Assessed leaves: "+PrincipalVariationSearch.assessedLeaves);
            System.out.println("Assessed leaves per second: "+(PrincipalVariationSearch.assessedLeaves/(time*0.001)));
        }
    }*/
    ////////////////7
    
}
