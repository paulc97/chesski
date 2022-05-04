package Model.Pieces;

import Model.Board;

import static Model.Mask.*;
import static Model.Mask.FILE_AB;

public class King implements Piece {

    public String moves(String history, Board b) {

        String list = "";
        if (b.isCurrentPlayerIsWhite()) {
            long possibility;
            int iLocation=Long.numberOfTrailingZeros(b.getWhiteKing());
            if (iLocation>9)
            {
                possibility=KING_B7<<(iLocation-9);
            }
            else {
                possibility=KING_B7>>(9-iLocation);
            }
            if (iLocation%8<4)
            {
                possibility &=~FILE_GH&(~(b.getWhitePieces()|b.getBlackKing())); //TODO: (Verständnisfrage) Hier würde FILE_H auch reichen, oder?
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

        } else {

            long possibility;
            int iLocation=Long.numberOfTrailingZeros(b.getBlackKing());
            if (iLocation>9)
            {
                possibility=KING_B7<<(iLocation-9);
            }
            else {
                possibility=KING_B7>>(9-iLocation);
            }
            if (iLocation%8<4)
            {
                possibility &=~FILE_GH&(~(b.getBlackPieces()|b.getWhiteKing()));
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

        }

        return list;

    }
}

//TODO: Castling