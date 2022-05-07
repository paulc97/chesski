package App;

import Model.Board;
import Model.MoveGenerator;

public class NewMain {
    public static void main(String[] args) {

        MoveGenerator mg = new MoveGenerator();
        Board b1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f4 0 1");


        while(true){


            String validMoves = mg.validMoves(b1);
            mg.selectAndMakeMove(b1, validMoves);
            System.out.println(b1.getFen());

            /*


            -ende, wenn König im Mittelfeld ->p

            -ende, wenn keine legal moves von König ->p

             -ende, wenn 50 Züge ohne schlagen/bauer ->c

             //TODO: Reihenfolge beachten! (Ende nach 100 Halbzügen ohne schlagen/bauer, erst nach check ob könig in mitte/keine züge übrig!)





             */



        }

    }
}
