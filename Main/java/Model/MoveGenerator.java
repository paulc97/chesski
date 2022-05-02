package Model;

import Model.Pieces.Pawns;

import java.util.Arrays;
import static Model.Mask.*;


public class MoveGenerator {


        Pawns pawns = new Pawns();

        //TODO Does it make sense to handle the black/white move generation this way?
        //TODO: Check if "king of the hill" requires also to omit/add the king to the own/opposite pieces
        //TODO: Discuss - how we want to process the moves (String, Move object, in a new board...)

        public String ownPossibleMoves(String history, Board board) {
            String list = "";

            list=pawns.moves(history, board)/*+
                posibleNW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleBW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleRW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleQW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)+
                posibleKW(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK)*/;

            return list;
        }

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


}
