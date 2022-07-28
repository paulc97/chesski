package Model;

import java.util.ArrayList;
import java.util.List;

public class PrincipalVariationSearch {

    private static boolean outOfTime = false;
    public static int assessedLeaves = 0;
    public static int assessedLeavesCurrent = 0;
    public static List<String> currentPv;

    /**
     * performs iterative deepening search with prinicipal variation move ordering
     *
     * @param depth specifies maximal search depth
     * @return best move and evaluation of the leaf node at the end of the principal variation
     */
    public static String principalVariationSearchWithoutTimelimit(Board b, int depth, boolean isMaxPlayer) {
        currentPv = null;
        String bestMoveSoFar = "";
        Pv pv = null;
        for (int i = 1; i <= depth; i++) {
            pv = getPVnoTimelimit(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);
            bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();
            currentPv = pv.getPrinVar();
        }
        return bestMoveSoFar;
    }

    public static Pv getPVnoTimelimit(Board b, int depth, int alpha, int beta, boolean isMaxPlayer) {

        if (depth==0){
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective();
            return new Pv(score);
        }
        String moveList = MoveGenerator.validMoves(b);

        if (b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective();
            return new Pv(score);
        }

        if (true) {
            if (currentPv != null && !(currentPv.isEmpty())) {
                String bestMove = currentPv.remove(0);
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
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
            }
        }

        if (isMaxPlayer) {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MIN_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = getPVnoTimelimit(newBoard, depth - 1, alpha, beta, false);
                if (currentEval.getEvalScore() > alpha) {
                    bestMove = move;
                    bestEval = currentEval;
                    alpha = currentEval.getEvalScore();
                }
                if (beta <= alpha) {
                    break; //beta-cutoff
                }
            }
            return new Pv(alpha, bestEval.getPrinVar(), bestMove);
        } else {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MAX_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = getPVnoTimelimit(newBoard, depth - 1, alpha, beta, true);
                if (currentEval.getEvalScore() < beta) {
                    bestMove = move;
                    bestEval = currentEval;
                    beta = currentEval.getEvalScore();
                }
                if (beta <= alpha) {
                    break; //alpha-cutoff
                }
            }
            return new Pv(beta, bestEval.getPrinVar(), bestMove);
        }
    }

    /**
     * performs iterative deepening search with principal variation move ordering until a given timelimit exceeds
     *
     * @param timeLimit in ms
     * @return best move and evaluation of the leaf node at the end of the principal variation
     */
    public static String principalVariationSearchWithTimelimit(Board b, long timeLimit) {
        currentPv = null;
        System.out.println("Starting iterative deepening PVS with time limit: "+timeLimit);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        int depth = 1;
        String bestMoveSoFar ="";
        outOfTime = false;
        Pv pv = null;
        while(true){
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime){
                break;
            }
            long newTimeLimit = endTime-currentTime;
            pv = getPVwithTimelimit(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, currentTime, newTimeLimit);
            if(!outOfTime){ //only completed iterations will be considered
                bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();
                System.out.println("reached depth was: " + depth);
            }
            depth++;
            currentPv = pv.getPrinVar();
        }
        return bestMoveSoFar;
    }

    public static Pv getPVwithTimelimit(Board b, int depth, int alpha, int beta, boolean isMaxPlayer, long startTime, long timeLimit) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime);
        if(elapsedTime >= timeLimit){
            outOfTime = true;
        }
        if (depth==0||outOfTime){
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective();
            if(outOfTime){
                System.out.println("Out of Time!");
            }
            return new Pv(score);
        }
        String moveList = MoveGenerator.validMoves(b);
        if (outOfTime||b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective();
            if(outOfTime){
                System.out.println("Out of Time! (after MoveGeneration)");
            }
            return new Pv(score);
        }
        if (true) {
            if (currentPv != null && !(currentPv.isEmpty())) {
                String bestMove = currentPv.remove(0);
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
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
            }
        }
        if (isMaxPlayer) {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MIN_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                if(outOfTime) break;
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = getPVwithTimelimit(newBoard, depth - 1, alpha, beta, false, startTime, timeLimit);
                if (currentEval.getEvalScore() > alpha) {
                    bestMove = move;
                    bestEval = currentEval;
                    alpha = currentEval.getEvalScore();
                }
                if (beta <= alpha) {
                    break; //beta-cutoff
                }
            }
            return new Pv(alpha, bestEval.getPrinVar(), bestMove);
        } else {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MAX_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                if(outOfTime) break;
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = getPVwithTimelimit(newBoard, depth - 1, alpha, beta, true, startTime, timeLimit);
                if (currentEval.getEvalScore() < beta) {
                    bestMove = move;
                    bestEval = currentEval;
                    beta = currentEval.getEvalScore();
                }
                if (beta <= alpha) {
                    break; //alpha-cutoff
                }
            }
            return new Pv(beta, bestEval.getPrinVar(), bestMove);
        }
    }

    public static int researchNeeded = 0;

    /**
     * performs principal variation search with minimal window which has the size of half a pawn (50 points)
     * @param b
     * @param depth specifies maximal search depth
     * @param isMaxPlayer
     * @return best move and evaluation of the leaf node at the end of the principal variation
     */
    public static String nullWindowSearch(Board b, int depth, boolean isMaxPlayer) {
        currentPv = null;
        researchNeeded = 0;
        String bestMoveSoFar = "";
        Pv pv = null;
        for (int i = 1; i <= depth; i++) {
            pv = getPVwithMinimalWindow(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);
            bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();
            currentPv = pv.getPrinVar();
        }
        return bestMoveSoFar;
    }

    public static Pv getPVwithMinimalWindow(Board b, int depth, int alpha, int beta, boolean isMaxPlayer) {

        if (depth==0){
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective();
            return new Pv(score);
        }
        String moveList = MoveGenerator.validMoves(b);

        if (b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective();
            return new Pv(score);
        }
        if (true) {
            if (currentPv != null && !(currentPv.isEmpty())) {
                String bestMove = currentPv.remove(0);
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
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
            }
        }
        if (isMaxPlayer) {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MIN_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = null;
                if(depth == 1){
                    currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, beta, false);
                } else {
                    if (i==0){ //if child is first child
                        currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, beta, false);
                    } else {
                        currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, alpha+50, false);
                        if (currentEval.getEvalScore() >= alpha+50) {
                            researchNeeded++;
                            currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, beta, false);

                        }
                    }
                }
                if (currentEval.getEvalScore() > alpha) {
                    bestMove = move;
                    bestEval = currentEval;
                    alpha = currentEval.getEvalScore();
                }
                if (beta <= alpha) {
                    break; //beta-cutoff
                }
            }
            return new Pv(alpha, bestEval.getPrinVar(), bestMove);
        } else {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MAX_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                Pv currentEval = null;
                if(depth == 1){
                    currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, beta, true);
                } else {
                    if (i==0){ //if child is first child
                        currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, beta, true);
                    } else {
                        currentEval = getPVwithMinimalWindow(newBoard, depth - 1, beta-50, beta, true);
                        if (currentEval.getEvalScore() <= beta-50) {
                            researchNeeded++;
                            currentEval = getPVwithMinimalWindow(newBoard, depth - 1, alpha, beta, true);

                        }
                    }
                }
                if (currentEval.getEvalScore() < beta) {
                    bestMove = move;
                    bestEval = currentEval;
                    beta = currentEval.getEvalScore();
                }
                if (beta <= alpha) {
                    //System.out.println("Alpha cutoff!");
                    break; //alpha-cutoff
                }
            }
            return new Pv(beta, bestEval.getPrinVar(), bestMove);
        }
    }
}
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