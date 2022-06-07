package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrincipalVariationSearch {

    public static int assessedLeaves = 0;
    public static int assessedLeavesCurrent = 0;

    public static String iterativeDeepeningPVSNoTimeLimit(Board b, int depth, boolean moveOrdering) {
        System.out.println("Starting iterative deepening PVS with depth: " + depth);

        String bestMoveSoFar = "";
        int bestScoreSoFar = 0;

        for (int i = 1; i <= depth; i++) { //TODO: 0 oder 1 , < oder <= ?
            //System.out.println("current depth left: " + depth + " - assessed leaves so far: " + assessedLeaves);
            bestMoveSoFar = PVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, moveOrdering);

            bestScoreSoFar = -(Integer.parseInt(bestMoveSoFar.substring(4))); //negative value
            bestMoveSoFar = bestMoveSoFar.substring(0, 4) + bestScoreSoFar;
            if (moveOrdering) {
                //TODO: orderMovesAccordingToPV
                //zB in "Board" eine instanzvariable PV anlegen, zB String oder Liste....
            }
        }
        return bestMoveSoFar;

    }

    //negaScout
    public static String PVSearch(Board b, int depth, int alpha, int beta, boolean moveOrdering) {

        String moveList = MoveGenerator.validMoves(b);

        if (depth == 0 || b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            String score = "1";//String.valueOf(b.assessBoardFromOwnPerspective());
            return b.getCreatedByMove() + score; //TODO:IMPORTANT! fehlt hie rnicht -???
        }

        if (moveOrdering) {
            moveList = moveList; //TODO: order Moves According to PV
        }

        String firstmove = moveList.substring(0, 0 + 4); //bestMove
        Board firstnewBoard = b.createBoardFromMove(firstmove);
        firstnewBoard.setCreatedByMove(firstmove);

        //TODO: does -Integer.MIN_VALUE etc. work?
        int firstcurrentEval = Integer.parseInt(PVSearch(firstnewBoard, depth - 1, -beta, -alpha, moveOrdering).substring(4));

        if (firstcurrentEval < beta) {
            for (int i = 4; i < moveList.length(); i += 4) {
                int lowerBound = alpha;
                if (firstcurrentEval > alpha) {
                    lowerBound = firstcurrentEval;
                }
                int upperBound = lowerBound + 50; //TODO: oder +50 wegen halber Bauer?

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                int currentEval = Integer.parseInt(PVSearch(newBoard, depth - 1, -upperBound, -lowerBound, moveOrdering).substring(4));

                if (currentEval >= upperBound && currentEval < beta) {
                    currentEval = Integer.parseInt(PVSearch(newBoard, depth - 1, -beta, -currentEval, moveOrdering).substring(4));
                }

                if (currentEval > firstcurrentEval) {
                    firstcurrentEval = currentEval;
                    firstmove = move;
                } else if (currentEval >= beta) {
                    break;
                }

            }
        }


        return firstmove + (-firstcurrentEval);
        //TODO: pvs ordering!!

    }

    ////////

    public static List<String> currentPv;

    public static String moiterativeDeepeningPVSNoTimeLimit(Board b, int depth, boolean moveOrdering) {
        System.out.println("Starting iterative deepening PVS with depth: " + depth);

        String bestMoveSoFar = "";

        Pv pv = null;

        for (int i = 1; i <= depth; i++) { //TODO: 0 oder 1 , < oder <= ?
            System.out.println("current depth: " + i + " - assessed leaves so far: " + assessedLeaves);

            pv = moPVSearch(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, moveOrdering);
            pv.setEvalScore(-pv.getEvalScore());

            bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();

            if (moveOrdering) {
                currentPv = pv.getPrinVar();
                currentPv.forEach(System.out::println);
                System.out.println("yyyyyyyyyyyyyy");
            }
            System.out.println("current depth: " + i + " - assessed leaves after: " + assessedLeaves);
        }
        return bestMoveSoFar;

    }

    //negaScout
    public static Pv moPVSearch(Board b, int depth, int alpha, int beta, boolean moveOrdering) {

        String moveList = MoveGenerator.validMoves(b);

        if (depth == 0 || b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            int score = b.assessBoardFromOwnPerspective(MoveGenerator.assesedBoards, MoveGenerator.zobrist);
            return new Pv(score); //TODO: negative or positive?
        }

        if (moveOrdering) {
            if (currentPv != null && !(currentPv.isEmpty())) {

                /*
                System.out.println("xxxxxxxxxxx");
                currentPv.forEach(System.out::println);

               String bestMove = currentPv.remove(0);
                System.out.println("best Move is: " + bestMove);
                System.out.println(moveList);
                System.out.println("length of moveList "+ moveList.length());
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
                System.out.println("length of moveListNow "+ newMoveList.length);
                int index = 1;
                boolean alreadyAppeared = false;
                for (int i = 0; i < strArr.length; i++){
                    if (strArr[i].equals(bestMove) && !(alreadyAppeared)){
                        newMoveList[0] = strArr[i];
                        alreadyAppeared = true;
                    } else {
                        newMoveList[index++] = strArr[i];
                    }
                }
                moveList = String.join("",newMoveList);
                System.out.println(moveList);
                System.out.println("length of moveList end"+ moveList.length());*/
                moveList = currentPv.remove(0) + moveList; //currentPvMove wird zwar doppelt untersucht, d+rfte aber kein Problem sein wegen TT?
            }
        }

        String firstmove = moveList.substring(0, 0 + 4); //bestMove
        Board firstnewBoard = b.createBoardFromMove(firstmove);
        firstnewBoard.setCreatedByMove(firstmove);

        //TODO: does -Integer.MIN_VALUE etc. work?
        Pv firstcurrentEval = moPVSearch(firstnewBoard, depth - 1, -beta, -alpha, moveOrdering);

        if (firstcurrentEval.getEvalScore() < beta) {
            for (int i = 4; i < moveList.length(); i += 4) {
                int lowerBound = alpha;
                if (firstcurrentEval.getEvalScore() > alpha) {
                    lowerBound = firstcurrentEval.getEvalScore();
                }
                int upperBound = lowerBound + 50; //TODO: oder +50 wegen halber Bauer?

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = moPVSearch(newBoard, depth - 1, -upperBound, -lowerBound, moveOrdering);

                if (currentEval.getEvalScore() >= upperBound && currentEval.getEvalScore() < beta) {
                    currentEval = moPVSearch(newBoard, depth - 1, -beta, -currentEval.getEvalScore(), moveOrdering);
                }

                if (currentEval.getEvalScore() > firstcurrentEval.getEvalScore()) {
                    firstcurrentEval = currentEval;
                    firstmove = move;
                } else if (currentEval.getEvalScore() >= beta) {
                    break;
                }

            }
        }


        return new Pv(-firstcurrentEval.getEvalScore(), firstcurrentEval.getPrinVar(), firstmove);//TODO: minus oder plus??

    }

    //without Window, nur Zugsortierung

    public static String moiterativeDeepeningPVSNoTimeLimitNoWindow(Board b, int depth, boolean isMaxPlayer) {
        //TODO: moveordering rausnehmen!
        System.out.println("Starting iterative deepening PVS with depth: " + depth);

        String bestMoveSoFar = "";

        Pv pv = null;

        for (int i = 1; i <= depth; i++) { //TODO: 0 oder 1 , < oder <= ?
            System.out.println("current depth: " + i + " - assessed leaves so far (total): " + assessedLeaves);

            pv = moPVSearchNoWindow(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            pv.setEvalScore(pv.getEvalScore());//TODO: needed here? (no negamax)

            bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();

            if (true) {
                currentPv = pv.getPrinVar();
                currentPv.forEach(System.out::println);
                currentPv.forEach(MoveGenerator::print4digitMoveToField);
                System.out.println("yyyyyyyyyyyyyy");
            }
            System.out.println("current depth: " + i + " - assessed leaves after(total): " + assessedLeaves);
            System.out.println("current depth: " + i + " - assessed leaves in this depth: " + assessedLeavesCurrent);
            assessedLeavesCurrent = 0;

        }
        System.out.println("Iteration Over; best Move: ");
        return bestMoveSoFar;

    }

    //negaScout
    public static Pv moPVSearchNoWindow(Board b, int depth, int alpha, int beta, boolean isMaxPlayer) {

        String moveList = MoveGenerator.validMoves(b);

        if (depth == 0 || b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective(MoveGenerator.assesedBoards, MoveGenerator.zobrist);
            return new Pv(score);
        }

        if (true) {
            if (currentPv != null && !(currentPv.isEmpty())) {
                //TODO: MoveSortierung effizienter ...oder doch ein Move doppelt suchen? (aber in tiefen tiefen wahrscheinlich nicht doppelt suchen ausschlaggebender!)
                // /*
                System.out.println("xxxxxxxxxxx");
                currentPv.forEach(System.out::println);

               String bestMove = currentPv.remove(0);
                System.out.println("best Move is: " + bestMove);
                System.out.println(moveList);
                System.out.println("length of moveList "+ moveList.length());
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
                System.out.println("length of moveListNow "+ newMoveList.length);
                int index = 1;
                boolean alreadyAppeared = false;
                for (int i = 0; i < strArr.length; i++){
                    if (strArr[i].equals(bestMove) && !(alreadyAppeared)){
                        newMoveList[0] = strArr[i];
                        alreadyAppeared = true;
                    } else {
                        newMoveList[index++] = strArr[i];
                    }
                }
                moveList = String.join("",newMoveList);
                System.out.println(moveList);
                System.out.println("length of moveList end"+ moveList.length());// */
                //moveList = currentPv.remove(0) + moveList; //currentPvMove wird zwar doppelt untersucht, d+rfte aber kein Problem sein wegen TT?
            }
        }

        if (isMaxPlayer) {
            String bestMove = "9999";
            Pv bestEval = new Pv(-1);
            for (int i = 0; i < moveList.length(); i += 4) {

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                Pv currentEval = moPVSearchNoWindow(newBoard, depth - 1, alpha, beta, false);
                //System.out.println("alpha was:" + alpha);
                //System.out.println("beta was:" + beta);
                if (currentEval.getEvalScore() > alpha) {
                    bestMove = move; //equiv. zu b.getCreatedByMove();
                    bestEval = currentEval;
                    alpha = currentEval.getEvalScore();
                }
                //System.out.println("alpha is:" + alpha);
                //alpha = Math.max(alpha,currentEval); wird redundant, siehe 2 Zeilen vorher

                if (beta <= alpha) {
                    //System.out.println("Beta cutoff!");
                    break; //beta-cutoff
                }
            }
            return new Pv(bestEval.getEvalScore(), bestEval.getPrinVar(), bestMove);
        } else {
            String bestMove = "9999";
            Pv bestEval = new Pv(-1);
            for (int i = 0; i < moveList.length(); i += 4) {

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                Pv currentEval = moPVSearchNoWindow(newBoard, depth - 1, alpha, beta, true);
                //System.out.println("beta was:" + beta);
                //System.out.println("alpha was:" + alpha);
                if (currentEval.getEvalScore() < beta) {
                    bestMove = move;
                    bestEval = currentEval;
                    beta = currentEval.getEvalScore();
                }
                //System.out.println("beta is:" + beta);
                //beta = Math.min(beta, currentEval);

                if (beta <= alpha) {
                    //System.out.println("Alpha cutoff!");
                    break; //alpha-cutoff
                }

            }
            return new Pv(bestEval.getEvalScore(), bestEval.getPrinVar(), bestMove);
        }
    }
}

//  return new Pv(-firstcurrentEval.getEvalScore(), firstcurrentEval.getPrinVar(), firstmove);

class Pv {
    private int evalScore;
    private List<String> prinVar = new ArrayList<>();

    public Pv(int evSc, List<String> moveValues, String move){
        this.evalScore = evSc;
        this.prinVar.add(move);
        this.prinVar.addAll(moveValues);
    }

    public Pv(int evSc){
        this.evalScore = evSc;

    }

    public int getEvalScore() {
        return evalScore;
    }

    public void setEvalScore(int evalScore) {
        this.evalScore = evalScore;
    }

    public List<String> getPrinVar() {
        return prinVar;
    }

    public void setPrinVar(List<String> prinVar) {
        this.prinVar = prinVar;
    }
}