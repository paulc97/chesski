package Model;

import Model.Pieces.King;
import Model.Pieces.Knights;
import Model.Pieces.Pawns;
import Model.Pieces.SlidingPieces;

import java.util.Arrays;
import static Model.Mask.*;


public class MoveGenerator {


        Pawns pawns = new Pawns();
        SlidingPieces slidingPieces = new SlidingPieces();
        Knights knights = new Knights();
        King king = new King();


        public String ownPossibleMoves(String history, Board board) {
            String list = "";
            long startTime = System.currentTimeMillis();
            long maxtime = 10000L;
            while (true) {

                list += pawns.moves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += knights.moves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += king.moves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += slidingPieces.rookMoves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;


                list += slidingPieces.bishopMoves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += slidingPieces.queenMoves(history, board);
                break;
            }

            //TODO: king in check/check mate: Video: King Movement & Safety  Minute 2

            return list;
        }

        public int getMoveCount(String list){

            int lenght = 0;


            return lenght;

        }



/*
        public static void drawBitboard(long bitBoard) {
            String chessBoard[][]=new String[8][8];
            for (int i=0;i<64;i++) {
                chessBoard[i/8][i%8]="";
            }
            for (int i=0;i<64;i++) {
                if (((bitBoard>>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
                if ("".equals(chessBoard[i/8][i%8])) {chessBoard[i/8][i%8]=" ";}
            }
            for (int i=0;i<8;i++) {
                System.out.println(Arrays.toString(chessBoard[i]));
            }
        }

        public static void tEMethodA(int loopLength, Board b) {
            for (int loop=0;loop<loopLength;loop++)
            {
                long PAWN_MOVES=(b.getOwnPawns()>>7) & b.getOppositePieces() & ~RANK_8 & ~FILE_A;//capture right
                String list="";
                for (int i=0;i<64;i++) {
                    if (((PAWN_MOVES>>i)&1)==1) {
                        list+=""+(i/8+1)+(i%8-1)+(i/8)+(i%8);
                    }
                }
            }
        }

        public static void tEMethodB(int loopLength, Board b) {
            for (int loop=0;loop<loopLength;loop++)
            {
                long PAWN_MOVES=(b.getOwnPawns()>>7)&b.getOppositePieces()&~RANK_8&~FILE_A;//capture right
                String list="";
                long possibility=PAWN_MOVES&~(PAWN_MOVES-1);
                while (possibility != 0)
                {
                    int index=Long.numberOfTrailingZeros(possibility);
                    list+=""+(index/8+1)+(index%8-1)+(index/8)+(index%8);
                    PAWN_MOVES&=~(possibility);
                    possibility=PAWN_MOVES&~(PAWN_MOVES-1);
                }
            }
        }
*/


}
