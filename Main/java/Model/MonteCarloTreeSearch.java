package Model;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloTreeSearch {

    public static String getBestMove(Node node, int timelimit){
        long startTime = System.currentTimeMillis();

        Node root = node;
        root.expandAllChildren();

        while(System.currentTimeMillis() - startTime < timelimit){
            //1. Selection
            Node selectedNode = selectNode(root);



            //2. Expansion
                //Node expandedNode = selectedNode.expandOne();

            //TODO: nur einen expanden VS alle Kinder von selectedNode?

            //3. Simulation
            int simulationResult = 0;//
              //int simulationResult = simulate(expandedNode);

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
                //backpropagate(expandedNode, simulationResult);



        }

            //Node bestNode = root.childWithBestScore();
        //return bestNode.move;


        return "move which leads to child with max score bzw. auswahlkriterium";

    }

    public static Node selectNode(Node node){//TODO: ausprobieren/testen!
        //TODO: was tun, wnen children empty? (wie reagiert es jetzt?)

        double maxUCTvalue = -1;
        Node bestChild = null;
        for (int i = 0; i < node.children.size(); i++){
            Node currentChild = node.children.get(i);
            double UCTvalue = calculateUCTvalue(node.visits, currentChild.visits, currentChild.winScore,Math.sqrt(2));
            if(UCTvalue > maxUCTvalue){
                maxUCTvalue = UCTvalue;
                bestChild = currentChild;
            }

        }
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
        b.drawBoard();

        //sollen knoten auf dem simulationsweg generiert/kreiert werden?
        while(!b.isGameOver()){
            String allValidMoves = MoveGenerator.validMoves(b);
            if (allValidMoves.equals("")) break;
            System.out.println(allValidMoves);
            int randomNumber = 0 + (int)(Math.random() * (allValidMoves.length()/4 - 0));
            System.out.println(randomNumber);
            String randomMove = allValidMoves.substring(randomNumber*4, randomNumber*4+4);
            b = b.createBoardFromMove(randomMove);

        }
        b.drawBoard();

        if(b.isRemis()){
            System.out.println("Game Over - Remis");
            return 0;
        } else {
            if(b.isWhiteWon()){
                System.out.println("Game Over - White Won");
                return 1;
            } else {
                System.out.println("Game Over - Black Won");
                return 2;
            }
        }

        //in case of win: 1, loss: -1, draw: 0 //TODO: (differenziert?)
    }

    //implementierung nach VL-Foliensatz
    public static void backpropagate(Node node, int result){
        Node currentNode = node;
        while(currentNode != null){
            currentNode.visits+=1;
            currentNode.winScore+=result;
            currentNode = currentNode.parent;
        }
    }



}

