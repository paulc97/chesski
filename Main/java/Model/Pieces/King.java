package Model.Pieces;

import Model.Board;

import static Model.Mask.*;
import static Model.Mask.FILE_AB;

public class King implements Piece {

    public String moves(Board b) {

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

            //CASTLING //TODO: Castling updaten? (er meint (Vid18) "castling isnt fully debugged yet) //unsafe for white?
            if (b.isWhiteToCastleKingside()&&(((1L<<63L)&b.getWhiteRooks())!=0))
            {
                if ((b.getAllPieces()&((1L<<61)|(1L<<62)))==0) {
                    list+="7476";
                }
            }
            if (b.isWhiteToCastleQueenside()&&(((1L<<56L)&b.getWhiteRooks())!=0))
            {
                if ((b.getAllPieces()&((1L<<57)|(1L<<58)|(1L<<59)))==0) {
                    list+="7472";
                }
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

            //CASTLING
            if (b.isBlackToCastleKingside()&&(((1L<<7L)&b.getBlackRooks())!=0))
            {
                if ((b.getAllPieces()&((1L<<5)|(1L<<6)))==0) {
                    list+="0406";
                }
            }
            if (b.isBlackToCastleQueenside()&&(((1L<<0L)&b.getBlackRooks())!=0))
            {
                if ((b.getAllPieces()&((1L<<1)|(1L<<2)|(1L<<3)))==0) {
                    list+="0402";
                }
            }

        }

        return list;

    }
}

