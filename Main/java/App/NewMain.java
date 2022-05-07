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
               //nachdem ich von ersten Moves nach Random Move auswählen gewechselt hab, folgende Ausgabe bekommen:
r1bkqbnr/pppnpppp/8/8/8/8/PPPPPPPP/RNBKQBN b KQkq - 0 2
r1bkqbnr/pppnpppp/8/8/8/8/PPPPPPPP/RNBKQBN w KQkq - 1 2
r1bkqbnr/pppnpppp/8/8/4P3/8/PPPP1PPP/RNBKQBN b KQkq - 0 3
r1bkqbnr/pp1npppp/8/8/4P3/8/PPPP1PPP/RNBKQBN w KQkq - 0 3
r1bkqbnr/pp1npppp/8/8/4P3/8/PP1P1PPP/RNBKQBN b KQkq - 0 4
r1bkqbnr/pp1npppp/8/8/8/7P/PP1P1PPP/RNBKQBN w KQkq - 0 4 //
r1bkqbnr/pp1npppp/8/8/8/7P/PP1P1PPP/RNB1QBN b KQkq - 0 5 // Der weiße König löst sich einfach in Luft auf? Warum?
r1bkqbnr/pp1nppp1/8/7p/8/7P/PP1P1PPP/RNB1QBN w KQkq - 0 5
r1bkqbnr/pp1nppp1/8/7p/8/7P/PP1P1PPP/RNB1QBN b KQkq - 1 6

anderer Durchlauf:
rnbkqbnr/ppp1pp1p/3P4/6p1/1P4R1/N4P2/P2PP2P/R1BK1BN b KQkq - 0 6
rnb1qbnr/ppp1pp1p/3P4/6p1/1P4Rk/N4P2/P2PP2P/R1BK1BN w KQkq - 1 6 hier springt der König einfach nach vorne??


             */



        }

    }
}
