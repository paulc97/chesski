package Model;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloTreeSearch {

    public static String getBestMove(Node node, int timelimit){
        long startTime = System.currentTimeMillis();

        Node root = node;
        root.expandAllChildren();

        if(root.children.size() == 0){ //no possible Moves
            System.out.println("MCTS-KI lost Game");
            return null;
        }

        while(System.currentTimeMillis() - startTime < timelimit){
            //1. Selection
            Node selectedNode = selectNode(root);

            //if selected leaf node is terminal node, directly backpropagate its value
            if (selectedNode.board.isGameOver()){
                System.out.println("Selected Node was terminal node!");
                System.out.println(selectedNode.board.isWhiteWon());
                int simulationResult = simulate(selectedNode);

                //Perspektive der Bewertung festlegen (to-do: iscurrentplayer vs is ki playing..)
                if(root.board.isCurrentPlayerIsWhite()){
                    if(simulationResult == 1){
                        simulationResult = 1; //weiss gewonnen
                    } else if (simulationResult == 2){
                        simulationResult = -1; //schwarz gewonnen
                    }
                } else {
                    if(simulationResult == 1){
                        simulationResult = -1; //weiss gewonnen
                    } else if (simulationResult == 2){
                        simulationResult = 1; //schwarz gewonnen
                    }
                }

                backpropagate(selectedNode, simulationResult);
                break;
            }

            //2. Expansion (skip when visits of selected node == 0)
            if (selectedNode.visits>0){
                selectedNode.expandAllChildren();
                if(selectedNode.children.size()==0){ //skip further selection, if selectedNode has no children (i.e. is Terminal Node)
                    System.out.println("tried to expand, but no children there!");
                } else {
                    selectedNode = selectNode(selectedNode);
                }


            }

            //3. Simulation
            int simulationResult = simulate(selectedNode);

            //Perspektive der Bewertung festlegen (to-do: iscurrentplayer vs is ki playing..)
            if(root.board.isCurrentPlayerIsWhite()){
                if(simulationResult == 1){
                    simulationResult = 1; //weiss gewonnen
                } else if (simulationResult == 2){
                    simulationResult = -1; //schwarz gewonnen
                }
            } else {
                if(simulationResult == 1){
                    simulationResult = -1; //weiss gewonnen
                } else if (simulationResult == 2){
                    simulationResult = 1; //schwarz gewonnen
                }
            }

            //4. Backpropagation
            backpropagate(selectedNode, simulationResult);



        }
        //System.out.println(root);
        Node bestNode = childWithBestScore(root);

        //System.out.println(bestNode);
        System.out.println("bestNode Score" + bestNode.winScore);
        System.out.println("bestNode visits: " + bestNode.visits);
        return bestNode.board.getCreatedByMove();

        //TODO: was tun wenn bestChild null? also timelimit zu wenig?--Kann nicht vorkommen, da visit mind. 0
        //return "move which leads to child with max score bzw. auswahlkriterium";

    }

    public static Node selectNode(Node node){//TODO: ausprobieren/testen!
        //TODO: was tun, wnen children empty? (wie reagiert es jetzt?)

        double maxUCTvalue = Double.MIN_VALUE;
        Node bestChild = null;
        for (int i = 0; i < node.children.size(); i++){
            Node currentChild = node.children.get(i);
            double UCTvalue = calculateUCTvalue(node.visits, currentChild.visits, currentChild.winScore,Math.sqrt(2));
            if(UCTvalue > maxUCTvalue){
                maxUCTvalue = UCTvalue;
                bestChild = currentChild;
            }

        }
        if(bestChild == null) {
            System.out.println("bestChild was null!");
            System.out.println(node.children.size());
            System.out.println(node.children.get(0).winScore);
            return node; //in case that  selected leaf node was terminal node
        }
        if (bestChild.children.size() != 0) return selectNode(bestChild);
        return bestChild;
    }

    public static double calculateUCTvalue (int parentVisits, int childVisits, double childScore, double C){
        if (childVisits == 0) {
            return Integer.MAX_VALUE; //TODO: was soll passieren, wenn child noch unbesucht?
        }
        double exploitation = (double) childScore / (double) childVisits;
        double exploration = Math.sqrt(Math.log((double) parentVisits)/(double) childVisits);
        return exploitation + C * exploration;
    }

    /**
     * random move selection
     * @param node
     * @return 0 (draw) 1 (white has won) 2 (black has won)
     */
    public static int simulate(Node node){

        Board b = node.board;
        //b.drawBoard();

        //sollen knoten auf dem simulationsweg generiert/kreiert werden?
        while(!b.isGameOver()){
            String allValidMoves = MoveGenerator.validMoves(b);
            if (allValidMoves.equals("")) break;
            //System.out.println(allValidMoves);
            int randomNumber = 0 + (int)(Math.random() * (allValidMoves.length()/4 - 0));
            //System.out.println(randomNumber);
            String randomMove = allValidMoves.substring(randomNumber*4, randomNumber*4+4);
            b = b.createBoardFromMove(randomMove);
            //createdByMove hier nicht nötig...

        }
        //b.drawBoard();

        if(b.isRemis()){
            //System.out.println("Game Over - Remis");
            return 0;
        } else {
            if(b.isWhiteWon()){
                //System.out.println("Game Over - White Won");
                return 1;
            } else {
                //System.out.println("Game Over - Black Won");
                return 2;
            }
        }

        //in case of win: 1, loss: -1, draw: 0 //TODO: (differenziert?)
    }

    //implementierung nach VL-Foliensatz //to-do: rekursiv?
    public static void backpropagate(Node node, int result){
        Node currentNode = node;
        while(currentNode != null){
            currentNode.visits+=1;
            currentNode.winScore+=result;
            currentNode = currentNode.parent;
        }
    }

    //returns Node which will be selected by MCTS, i.e. child with highest number of visits (if equal, then score!)
    public static Node childWithBestScore(Node node){
        Node bestChild = null;
        int maxVisits = -1;
        double maxScore = Integer.MIN_VALUE;

        for (int i = 0; i < node.children.size(); i++){
            Node currentChild = node.children.get(i);

            if(currentChild.visits > maxVisits){
                bestChild = currentChild;
                maxVisits = currentChild.visits;
                maxScore = currentChild.winScore;
            } else if (currentChild.visits == maxVisits){
                if(currentChild.winScore>maxScore){
                    bestChild = currentChild;
                    maxScore = currentChild.winScore;
                }
            }

        }
        if(bestChild == null){
            System.out.println("BESTCHILDErROR!");
            return node;
        }
        return bestChild;
    }



}

