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
                //int simulationResult = simulate(expandedNode);

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
            Node currentChild = node.children.get(0);
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



}

class Node{
    //to-do: is player needed?
    int visits = 0;
    double winScore =0.0; //to-do: change name

    Node parent;
    List<Node> children = new ArrayList<Node>();

    Board board;

    Node(Node parent, Board board){ //to-do: player?
        this.parent = parent;
        this.board = board;
    }
    //to-do: extra "move" (String) -oder ausreichend, da in board gespeichert??

    void expandAllChildren(){
        List<Node> children = new ArrayList<>();
        String allPossibleMoves = MoveGenerator.validMoves(this.board);
        String[] moves = allPossibleMoves.split("(?<=\\G.{4})");
        for(String move: moves){
            Node newNode = new Node(this, board.createBoardFromMove(move));
            children.add(newNode);
        }
        this.children = children;
    }

}