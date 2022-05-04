package Model.Pieces;

import Model.Board;

import static Model.Mask.*;
import static Model.Mask.RANK_8;

public class Knights implements Piece {

    public String moves(String history, Board b) {

        String list = "";
        if (b.isCurrentPlayerIsWhite()) {
            long WN = b.getWhiteKnights();
            long i=WN&~(WN-1);
            long possibility;
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                if (iLocation>18) //TODO: (maybe) change C6 to different one
                {
                    possibility=KNIGHT_C6<<(iLocation-18);
                }
                else {
                    possibility=KNIGHT_C6>>(18-iLocation);
                }
                if (iLocation%8<4)
                {
                    possibility &=~FILE_GH&(~(b.getWhitePieces()|b.getBlackKing()));
                    //TODO: NOT_WHITE_PIECES == ~(b.getWhitePieces()|b.getBlackKing()) ? Gegner-König immer dazunehmen nötig?
                }
                else {
                    possibility &=~FILE_AB&(~(b.getWhitePieces()|b.getBlackKing()));
                }
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                WN&=~i;
                i=WN&~(WN-1);
            }

        } else {

            long BN = b.getBlackKnights();
            long i=BN&~(BN-1);
            long possibility;
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                if (iLocation>18) //TODO: (maybe) change C6 to different one
                {
                    possibility=KNIGHT_C6<<(iLocation-18);
                }
                else {
                    possibility=KNIGHT_C6>>(18-iLocation);
                }
                if (iLocation%8<4)
                {
                    possibility &=~FILE_GH&(~(b.getBlackPieces()|b.getWhiteKing()));
                    //TODO: s.o. ? Gegner-König immer dazunehmen nötig?
                }
                else {
                    possibility &=~FILE_AB&(~(b.getBlackPieces()|b.getWhiteKing()));
                }
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                BN&=~i;
                i=BN&~(BN-1);
            }

        }

        return list;

    }
}
