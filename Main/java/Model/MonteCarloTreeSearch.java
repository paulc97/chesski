package Model;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloTreeSearch {

    public static int totalSimulationCount = 0;

    /**
     * performs MCTS with UCT until timelimit is up
     *
     * @param node root node of Monte Carlo Tree Search search tree
     * @param timelimit in ms
     * @return safest move with highest score
     */
    public static String getBestMove(Node node, int timelimit){
        long startTime = System.currentTimeMillis();

        Node root = node;
        root.expandAllChildren();

        if(root.children.size() == 0){ //no possible Moves -> current player lost game
            return null;
        }

        while(System.currentTimeMillis() - startTime < timelimit) {
            //1. Selection
            Node selectedNode = selectNode(root);
            //if selected leaf node is terminal node, directly backpropagate its value
            if (selectedNode.board.isGameOver()) {
                int simulationResult = simulate(selectedNode);
                if (root.board.isCurrentPlayerIsWhite()) {
                    if (simulationResult == 1) {
                        simulationResult = 1; //white won
                    } else if (simulationResult == 2) {
                        simulationResult = -1; //black won
                    }
                } else {
                    if (simulationResult == 1) {
                        simulationResult = -1; //white won
                    } else if (simulationResult == 2) {
                        simulationResult = 1; //black won
                    }
                }
                backpropagate(selectedNode, simulationResult);

            } else {

                //2. Expansion (skip if visits of selected node == 0)
                if (selectedNode.visits > 0) {
                    selectedNode.expandAllChildren();
                    if (selectedNode.children.size() == 0) { //skip further selection, if selectedNode has no children (i.e. is Terminal Node)
                    } else {
                        selectedNode = selectNode(selectedNode);
                    }
                }

                //3. Simulation
                int simulationResult = simulate(selectedNode);
                if (root.board.isCurrentPlayerIsWhite()) {
                    if (simulationResult == 1) {
                        simulationResult = 1; //weiss gewonnen
                    } else if (simulationResult == 2) {
                        simulationResult = -1; //schwarz gewonnen
                    }
                } else {
                    if (simulationResult == 1) {
                        simulationResult = -1; //weiss gewonnen
                    } else if (simulationResult == 2) {
                        simulationResult = 1; //schwarz gewonnen
                    }
                }

                //4. Backpropagation
                backpropagate(selectedNode, simulationResult);
            }
        }
        //select direct child with most visits (safest node)
        Node bestNode = childWithBestScore(root);
        System.out.println("bestNode Score" + bestNode.score);
        System.out.println("bestNode visits: " + bestNode.visits);
        return bestNode.board.getCreatedByMove();
    }

    public static Node selectNode(Node node){
        double maxUCTvalue = Integer.MIN_VALUE;
        Node bestChild = null;
        for (int i = 0; i < node.children.size(); i++){
            Node currentChild = node.children.get(i);
            double UCTvalue = calculateUCTvalue(node.visits, currentChild.visits, currentChild.score,Math.sqrt(2));
            if(UCTvalue > maxUCTvalue){
                maxUCTvalue = UCTvalue;
                bestChild = currentChild;
            }
        }
        if(bestChild == null) {
            System.out.println("bestChild was null!");
            System.out.println(node.children.size());
            System.out.println(node.children.get(0).score);
            return node; //in case that  selected leaf node was terminal node
        }
        if (bestChild.children.size() != 0) return selectNode(bestChild);
        return bestChild;
    }
    public static double calculateUCTvalue (int parentVisits, int childVisits, double childScore, double C){
        if (childVisits == 0) {
            return Integer.MAX_VALUE;
        }
        double exploitation = (double) childScore / (double) childVisits;
        double exploration = Math.sqrt(Math.log((double) parentVisits)/(double) childVisits);
        return exploitation + C * exploration;
    }

    /**
     * random move selection until no possible moves exist
     * @param node from which simulation starts
     * @return 0 (draw) 1 (white has won) 2 (black has won)
     */
    public static int simulate(Node node){
        totalSimulationCount++;
        Board b = node.board;
        while(!b.isGameOver()){
            String allValidMoves = MoveGenerator.validMoves(b);
            if (allValidMoves.equals("")) break;
            int randomNumber = 0 + (int)(Math.random() * (allValidMoves.length()/4 - 0));
            String randomMove = allValidMoves.substring(randomNumber*4, randomNumber*4+4);
            b = b.createBoardFromMove(randomMove);

        }
        if(b.isRemis()){
            return 0;
        } else {
            if(b.isWhiteWon()){
                return 1;
            } else {
                return 2;
            }
        }
    }

    public static void backpropagate(Node node, int result){
        Node currentNode = node;
        while(currentNode != null){
            currentNode.visits+=1;
            currentNode.score +=result;
            currentNode = currentNode.parent;
        }
    }

    /**
     *
     * @param node root of MCTS search tree
     * @return direct child node of root with the highest visits (if multiple nodes have the same number of visits, choose the node with the highest score)
     */
    public static Node childWithBestScore(Node node){
        Node bestChild = null;
        int maxVisits = -1;
        double maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < node.children.size(); i++){
            Node currentChild = node.children.get(i);
            if(currentChild.visits > maxVisits){
                bestChild = currentChild;
                maxVisits = currentChild.visits;
                maxScore = currentChild.score;
            } else if (currentChild.visits == maxVisits){
                if(currentChild.score >maxScore){
                    bestChild = currentChild;
                    maxScore = currentChild.score;
                }
            }
        }
        return bestChild;
    }
}