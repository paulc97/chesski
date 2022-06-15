package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrincipalVariationSearch {

    private static boolean outOfTime = false; //used for Iterative DeepeningWithTimeLimit

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
            int score = b.assessBoardFromOwnPerspective();
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
        currentPv = null;
        //TODO: moveordering rausnehmen! <---???
        // System.out.println("Starting iterative deepening PVS with depth: " + depth);

        String bestMoveSoFar = "";

        Pv pv = null;

        for (int i = 1; i <= depth; i++) { //TODO: 0 oder 1 , < oder <= ?
            //System.out.println("current depth: " + i + " - assessed leaves so far (total): " + assessedLeaves);

            //System.out.println("current PV (before iteration): ");
            /*if(currentPv == null){
                System.out.println("is still null!");
            } else {
                currentPv.forEach(System.out::println);
                currentPv.forEach(MoveGenerator::print4digitMoveToField);
                System.out.println("yyyyyyyyyyyyyy");
            }*/


            pv = moPVSearchNoWindow(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);
            // pv.setEvalScore(pv.getEvalScore());//TODO: needed here? (no negamax)

            bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();


            currentPv = pv.getPrinVar();
            //System.out.println("current PV (after iteration): ");
            //currentPv.forEach(System.out::println);
            //currentPv.forEach(MoveGenerator::print4digitMoveToField);
            //System.out.println("yyyyyyyyyyyyyy");

            //System.out.println("current depth: " + i + " - assessed leaves after(total): " + assessedLeaves);
            //System.out.println("current depth: " + i + " - assessed leaves in this depth: " + assessedLeavesCurrent);
            //assessedLeavesCurrent = 0;

        }
        //System.out.println("Iteration Over; best Move: ");
        return bestMoveSoFar;

    }


    public static Pv moPVSearchNoWindow(Board b, int depth, int alpha, int beta, boolean isMaxPlayer) {

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
                //TODO: MoveSortierung effizienter ...oder doch ein Move doppelt suchen? (aber in tiefen tiefen wahrscheinlich nicht doppelt suchen ausschlaggebender!)
                // /*
                //System.out.println("xxxxxxxxxxx");
                //currentPv.forEach(System.out::println);

                String bestMove = currentPv.remove(0);
                //System.out.println("best Move is: " + bestMove);
                //System.out.println(moveList);
                //System.out.println("length of moveList "+ moveList.length());
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
                //System.out.println("length of moveListNow "+ newMoveList.length);
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
                //System.out.println(moveList);
                //System.out.println("length of moveList end"+ moveList.length());// */
                //moveList = currentPv.remove(0) + moveList; //currentPvMove wird zwar doppelt untersucht, d+rfte aber kein Problem sein wegen TT?
            }
        }

        if (isMaxPlayer) {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MIN_VALUE);
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
            Pv bestEval = new Pv(Integer.MAX_VALUE);
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

    ///////////IDS+PVS mit Zeitlimit

    public static String moiterativeDeepeningPVSWithTimeLimitNoWindow(Board b, long timeLimit) {
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

            pv = moPVSearchNoWindowWithTimeLimit(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, currentTime, newTimeLimit);
            // pv.setEvalScore(pv.getEvalScore());//TODO: needed here? (no negamax)


            if(!outOfTime){ //ohne den check könnte bestMoveSoFar mit move aus unkomplettierter Suche überschrieben werden, der könnte (momentan noch) schlechter sein
                bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();
                System.out.println("reached depth was: " + depth);
            }
            depth++;

            //System.out.println("Is out of time? " + outOfTime);

            currentPv = pv.getPrinVar();
            //System.out.println("current PV (after iteration): ");
            //currentPv.forEach(System.out::println);
            //currentPv.forEach(MoveGenerator::print4digitMoveToField);
            //System.out.println("yyyyyyyyyyyyyy");

            //System.out.println("current depth: " + depth + " - assessed leaves after(total): " + assessedLeaves);
            //System.out.println("current depth: " + depth + " - assessed leaves in this depth: " + assessedLeavesCurrent);
            //assessedLeavesCurrent = 0;

        }
        //System.out.println("Iteration Over; best Move: ");
        return bestMoveSoFar;


    }


    public static Pv moPVSearchNoWindowWithTimeLimit(Board b, int depth, int alpha, int beta, boolean isMaxPlayer, long startTime, long timeLimit) {


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
                //TODO: MoveSortierung effizienter ...oder doch ein Move doppelt suchen? (aber in tiefen tiefen wahrscheinlich nicht doppelt suchen ausschlaggebender!)
                // /*
                //System.out.println("xxxxxxxxxxx");
                //currentPv.forEach(System.out::println);

                String bestMove = currentPv.remove(0);
                //System.out.println("best Move is: " + bestMove);
                //System.out.println(moveList);
                //System.out.println("length of moveList "+ moveList.length());
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
                //System.out.println("length of moveListNow "+ newMoveList.length);
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
                //System.out.println(moveList);
                //System.out.println("length of moveList end"+ moveList.length());// */
                //moveList = currentPv.remove(0) + moveList; //currentPvMove wird zwar doppelt untersucht, d+rfte aber kein Problem sein wegen TT?
            }
        }

        if (isMaxPlayer) {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MIN_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                if(outOfTime) break; //TODO: hier richtig/sinnvoll?
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                Pv currentEval = moPVSearchNoWindowWithTimeLimit(newBoard, depth - 1, alpha, beta, false, startTime, timeLimit);
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
            Pv bestEval = new Pv(Integer.MAX_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {
                if(outOfTime) break; //TODO: hier richtig/sinnvoll?
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                Pv currentEval = moPVSearchNoWindowWithTimeLimit(newBoard, depth - 1, alpha, beta, true, startTime, timeLimit);
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




    ///////////

    ///(funzt nicht richtig)

//  return new Pv(-firstcurrentEval.getEvalScore(), firstcurrentEval.getPrinVar(), firstmove);

//wit Window und  Zugsortierung

    public static String moiterativeDeepeningPVSNoTimeLimitWithWindow(Board b, int depth, boolean isMaxPlayer) {
        //TODO: moveordering rausnehmen!
        System.out.println("Starting iterative deepening PVS with depth: " + depth);

        String bestMoveSoFar = "";

        Pv pv = null;

        for (int i = 1; i <= depth; i++) { //TODO: 0 oder 1 , < oder <= ?
            if (i == 1) { //erstes mal kein Window anwenden, weil noch keine MoveOrdering vorhanden
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
            } else { //ab Suchtiefe 2 (ist Info aus vorheriger Suchtiefe über PV vorhanden) -> WIndow kann angewandt werden
                System.out.println("current depth: " + i + " - assessed leaves so far (total): " + assessedLeaves);

                pv = moPVSearchWithWindow(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, 1); //TODO: !!!!!color 1 okay für beide Seiten, schwarze & weißeKI?
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


        }
        System.out.println("Iteration Over; best Move: ");
        return bestMoveSoFar;

    }


    public static Pv moPVSearchWithWindow(Board b, int depth, int alpha, int beta, int color) {

        String moveList = MoveGenerator.validMoves(b);

        if (depth == 0 || b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            assessedLeavesCurrent++;
            int score = b.assessBoardFromOwnPerspective(); //TODO: check if negative etc. https://en.wikipedia.org/wiki/Principal_variation_search
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

        String bestMove = "9999";
        Pv bestEval = new Pv(Integer.MIN_VALUE);
        for (int i = 0; i < moveList.length(); i += 4) {

            String move = moveList.substring(i, i + 4);
            Board newBoard = b.createBoardFromMove(move);
            newBoard.setCreatedByMove(move);

            Pv currentEval = null;
            if (i==0) {//if child is first child
                currentEval = moPVSearchWithWindow(newBoard, depth - 1, -beta, -alpha, -color);
                currentEval.setEvalScore(-currentEval.getEvalScore()); //TODO: oder gleich in assess-if-Block?
            } else {
                currentEval = moPVSearchWithWindow(newBoard, depth - 1, -alpha -1,-alpha, -color); //search with a null window
                currentEval.setEvalScore(-currentEval.getEvalScore()); //TODO: oder gleich in assess-if-Block?
                if (alpha < currentEval.getEvalScore() && currentEval.getEvalScore() < beta) {
                    System.out.println("Research needed!");
                    currentEval = moPVSearchWithWindow(newBoard, depth - 1, -beta, -currentEval.getEvalScore(), -color); //if it failed high, do a full research
                    currentEval.setEvalScore(-currentEval.getEvalScore()); //TODO: oder gleich in assess-if-Block?
                }
            }

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

    }


////////////////
//(neuer Versuch ohne negation)

    public static int researchNeeded = 0;

    public static String moiterativeDeepeningPVSNoTimeLimitWW(Board b, int depth, boolean isMaxPlayer) {
        currentPv = null;
        researchNeeded= 0;

        // System.out.println("Starting iterative deepening PVS with depth: " + depth);

        String bestMoveSoFar = "";

        Pv pv = null;

        for (int i = 1; i <= depth; i++) { //TODO: 0 oder 1 , < oder <= ?
            //System.out.println("current depth: " + i + " - assessed leaves so far (total): " + assessedLeaves);

            //System.out.println("current PV (before iteration): ");
            /*if(currentPv == null){
                System.out.println("is still null!");
            } else {
                currentPv.forEach(System.out::println);
                currentPv.forEach(MoveGenerator::print4digitMoveToField);
                System.out.println("yyyyyyyyyyyyyy");
            }*/


            pv = moPVSearchWW(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);
            // pv.setEvalScore(pv.getEvalScore());//TODO: needed here? (no negamax)

            bestMoveSoFar = pv.getPrinVar().get(0) + pv.getEvalScore();


            currentPv = pv.getPrinVar();
            //System.out.println("current PV (after iteration): ");
            //currentPv.forEach(System.out::println);
            //currentPv.forEach(MoveGenerator::print4digitMoveToField);
            //System.out.println("yyyyyyyyyyyyyy");

            //System.out.println("current depth: " + i + " - assessed leaves after(total): " + assessedLeaves);
            //System.out.println("current depth: " + i + " - assessed leaves in this depth: " + assessedLeavesCurrent);
            //assessedLeavesCurrent = 0;

        }
        //System.out.println("Iteration Over; best Move: ");
        return bestMoveSoFar;

    }


    public static Pv moPVSearchWW(Board b, int depth, int alpha, int beta, boolean isMaxPlayer) {

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
                //TODO: MoveSortierung effizienter ...oder doch ein Move doppelt suchen? (aber in tiefen tiefen wahrscheinlich nicht doppelt suchen ausschlaggebender!)
                // /*
                //System.out.println("xxxxxxxxxxx");
                //currentPv.forEach(System.out::println);

                String bestMove = currentPv.remove(0);
                //System.out.println("best Move is: " + bestMove);
                //System.out.println(moveList);
                //System.out.println("length of moveList "+ moveList.length());
                String[] strArr = moveList.split("(?<=\\G.{4})");
                String[] newMoveList = new String[strArr.length];
                //System.out.println("length of moveListNow "+ newMoveList.length);
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
                //System.out.println(moveList);
                //System.out.println("length of moveList end"+ moveList.length());// */
                //moveList = currentPv.remove(0) + moveList; //currentPvMove wird zwar doppelt untersucht, d+rfte aber kein Problem sein wegen TT?
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
                    currentEval = moPVSearchWW(newBoard, depth - 1, alpha, beta, false);
                } else {
                    if (i==0){ //if child is first child
                        currentEval = moPVSearchWW(newBoard, depth - 1, alpha, beta, false);
                    } else {
                        currentEval = moPVSearchWW(newBoard, depth - 1, alpha, alpha+50, false);//TODO: Werte anpassen!
                        if (currentEval.getEvalScore() >= alpha+50) { //TODO: Bedingung anpassen!
                            //System.out.println("Research needed!");
                            researchNeeded++;
                            currentEval = moPVSearchWW(newBoard, depth - 1, alpha, beta, false);//TODO: Werte anpassen!

                        }
                    }
                }




                if (currentEval.getEvalScore() > alpha) {
                    bestMove = move; //equiv. zu b.getCreatedByMove();
                    bestEval = currentEval;
                    alpha = currentEval.getEvalScore();
                }

                if (beta <= alpha) {
                    //System.out.println("Beta cutoff!");
                    break; //beta-cutoff
                }
            }
            return new Pv(bestEval.getEvalScore(), bestEval.getPrinVar(), bestMove);
        } else {
            String bestMove = "9999";
            Pv bestEval = new Pv(Integer.MAX_VALUE);
            for (int i = 0; i < moveList.length(); i += 4) {

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                Pv currentEval = null;

                if(depth == 1){
                    currentEval = moPVSearchWW(newBoard, depth - 1, alpha, beta, true);
                } else {
                    if (i==0){ //if child is first child
                        currentEval = moPVSearchWW(newBoard, depth - 1, alpha, beta, true);
                    } else {
                        currentEval = moPVSearchWW(newBoard, depth - 1, beta-50, beta, true);//TODO: Werte anpassen!
                        if (currentEval.getEvalScore() <= beta-50) { //TODO: Bedingung anpassen!
                            //System.out.println("Research needed!");
                            researchNeeded++;
                            currentEval = moPVSearchWW(newBoard, depth - 1, alpha, beta, true);//TODO: Werte anpassen!

                        }
                    }
                }





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