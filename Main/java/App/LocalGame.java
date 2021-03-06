package App;

import Model.Board;
import Model.MonteCarloTreeSearch;
import Model.MoveGenerator;
import Model.Node;

public class LocalGame {
    public static void main(String[] args) {


        MoveGenerator mg = new MoveGenerator();
        //System.out.println("MG was created");
        Board b1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        //System.out.println("Board was created");
        long usedTime = 0;

        while(true){
            //System.out.println("While");
            if (b1.isCurrentPlayerIsWhite()){
                b1.setKIPlaysWhite(true);
            } else {
                b1.setKIPlaysWhite(false);
            }

            long currentTime = System.currentTimeMillis();
            //System.out.println("Starte Valid Move generation");
            String validMoves = mg.validMoves(b1);
            //System.out.println("Bin da :D");
            if (b1.isKIPlayingWhite()){

                long timeLimit = MoveGenerator.standardDeviationTimeLimit(b1.getNextMoveCount(), 300L);
                Node node = new Node (null, b1);
                MoveGenerator.makeMove(b1, MonteCarloTreeSearch.getBestMove(node, (int) timeLimit));
            } else {
                mg.makeMove(b1, mg.moveSelector(b1, validMoves, usedTime));
            }

            //System.out.println("FEN Representation: " + b1.bitboardsToFenParser());

            b1.drawBoard();

            if(b1.isGameOver()){

                if(b1.isRemis()){
                    System.out.println("Game Over - Remis");
                    break;
                } else {
                    if(b1.isWhiteWon()){
                        System.out.println("Game Over - White Won");
                        break;
                    } else {
                        System.out.println("Game Over - Black Won");
                        break;
                    }
                }

            }

            usedTime += (System.currentTimeMillis()- currentTime);



            /*




            -ende, wenn K??nig im Mittelfeld ->p

            -ende, wenn keine legal moves von K??nig ->p

             -ende, wenn 50 Z??ge ohne schlagen/bauer ->c

             //TODO: Reihenfolge beachten! (Ende nach 100 Halbz??gen ohne schlagen/bauer, erst nach check ob k??nig in mitte/keine z??ge ??brig!)
             //Note: Dass Halbz??ge hochgez??hlt und zur??ckgesetzt werden, wenn Bauer bewegt/Figur geschlagen, hab ich implementiert
             //Dass das Game automatisch endet (-> Remis) wenn 100 Halbz??ge erreicht, mach ich wenn du deine Spielenderkennungsaufgaben
             // fertig hast, oder du machst es selbst, wenn es schnell geht :) - Viktoria



               //TODO: Moves ??berpr??fen


           -------------------------nachdem Random Move gefixt--------------
           rnbkqb1r/1p1ppp1p/7n/p1p3p1/1P2P2Q/P1P2P2/3P2PP/RNBK1BNR b KQkq - 0 7
I made this move: 0033 which is: h8->e5 (ROOK macht Bishopzug)
rnbkqb2/1p1ppp1p/7n/p1p1r1p1/1P2P2Q/P1P2P2/3P2PP/RNBK1BNR w KQkq - 1 7
//kann sein dass 0033 h8->e5 nciht mehr stimmt, da waren auch iwo bugs drin, aber der Rook macht trzd noch bishop z??ge ^^;
             */


            //TODO: why 80708071 wenn K??nig fehlt m??glich?




        }

    }
}
