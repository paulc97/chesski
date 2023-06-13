package Model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public int visits = 0;
    public double score = 0.0;
    public Node parent;
    public List<Node> children = new ArrayList<Node>();
    public Board board;

    public Node(Node parent, Board board) {
        this.parent = parent;
        this.board = board;
    }

    public void expandAllChildren() {
        List<Node> children = new ArrayList<>();
        String allPossibleMoves = MoveGenerator.validMoves(this.board);
        if (allPossibleMoves.equals("")) return; //in case that selected leaf node is terminal node
        String[] moves = allPossibleMoves.split("(?<=\\G.{4})");
        for (String move : moves) {
            Node newNode = new Node(this, board.createBoardFromMove(move));
            newNode.board.setCreatedByMove(move);
            children.add(newNode);
        }
        this.children = children;
    }
}
