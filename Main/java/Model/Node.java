package Model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    //to-do: is player needed?
    public int visits = 0;
    public double winScore = 0.0; //to-do: change name

    public Node parent;
    public List<Node> children = new ArrayList<Node>();

    public Board board;

    public Node(Node parent, Board board) { //to-do: player?
        this.parent = parent;
        this.board = board;
    }
    //to-do: extra "move" (String) -oder ausreichend, da in board gespeichert??


    public void expandAllChildren() {
        List<Node> children = new ArrayList<>();
        String allPossibleMoves = MoveGenerator.validMoves(this.board);
        if (allPossibleMoves.equals("")) return; //in case that selected leaf node is terminal node
        String[] moves = allPossibleMoves.split("(?<=\\G.{4})");
        for (String move : moves) {
            Node newNode = new Node(this, board.createBoardFromMove(move));
            newNode.board.setCreatedByMove(move);
            //newNode.board.drawBoard();
            children.add(newNode);
        }
        this.children = children;
    }

}
