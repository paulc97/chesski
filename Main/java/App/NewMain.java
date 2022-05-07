package App;

import Model.Board;
import Model.MoveGenerator;

public class NewMain {
    public static void main(String[] args) {

        MoveGenerator mg = new MoveGenerator();
        Board b1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");


        while(true){


            String validMoves = mg.validMoves(b1);
            mg.selectAndMakeMove(b1, validMoves);
            System.out.println(b1.getFen());

            /*


            -ende, wenn König im Mittelfeld ->p

            -ende, wenn keine legal moves von König ->p

             -ende, wenn 50 Züge ohne schlagen/bauer ->c

             //TODO: Reihenfolge beachten! (Ende nach 100 Halbzügen ohne schlagen/bauer, erst nach check ob könig in mitte/keine züge übrig!)
             //Note: Dass Halbzüge hochgezählt und zurückgesetzt werden, wenn Bauer bewegt/Figur geschlagen, hab ich implementiert
             //Dass das Game automatisch endet (-> Remis) wenn 100 Halbzüge erreicht, mach ich wenn du deine Spielenderkennungsaufgaben
             // fertig hast, oder du machst es selbst, wenn es schnell geht :) - Viktoria



               //TODO: Moves überprüfen


           -------------------------nachdem Random Move gefixt--------------
           rnbkqb1r/1p1ppp1p/7n/p1p3p1/1P2P2Q/P1P2P2/3P2PP/RNBK1BNR b KQkq - 0 7
I made this move: 0033 which is: h8->e5 (ROOK macht Bishopzug)
rnbkqb2/1p1ppp1p/7n/p1p1r1p1/1P2P2Q/P1P2P2/3P2PP/RNBK1BNR w KQkq - 1 7
//kann sein dass 0033 h8->e5 nciht mehr stimmt, da waren auch iwo bugs drin, aber der Rook macht trzd noch bishop züge ^^;
             */


            //TODO: why 80708071 wenn König fehlt möglich?




        }

    }
}
