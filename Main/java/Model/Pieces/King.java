package Model.Pieces;

import Model.Board;
import Model.MoveGenerator;

import static Model.Mask.*;
import static Model.Mask.COLUMN_AB;

public class King implements Piece {

    public String moves(Board b) {

        String moveList = "";

        if (b.isCurrentPlayerIsWhite()) {
            long option;
            int currentLocation=Long.numberOfTrailingZeros(b.getWhiteKing());

            if (currentLocation <= 9) {
                option=KING_B7>>(9 - currentLocation);
            } else {
                option=KING_B7<<(currentLocation - 9);
            }
            if (currentLocation%8 >= 4) {
                option &=~COLUMN_AB &(~(b.getWhitePieces()|b.getBlackKing()));
            } else {
                option &=~COLUMN_GH &(~(b.getWhitePieces()|b.getBlackKing())); //TODO: (Verständnisfrage) Hier würde FILE_H auch reichen, oder?
            }

            //normal moves
            long i=option&~(option - 1);
            while (i != 0) {
                int index=Long.numberOfTrailingZeros(i);
                moveList+=""+(currentLocation/8)+(currentLocation%8)+(index/8)+(index%8);
                option&=~i;
                i=option&~(option - 1);
            }

            //Castling
            long unsafePositions = MoveGenerator.fieldsAttackedByBlack(b);
            if((unsafePositions&b.getWhiteKing())==0){
                //Queenside castling
                if (b.isWhiteToCastleQueenside()&&(((1L<<56L)&b.getWhiteRooks())!=0)) {
                    if (((b.getAllPieces()|(unsafePositions&~(1L<<57)))&((1L<<57)|(1L<<58)|(1L<<59)))==0) moveList+="7472";
                }
                //Kingside castling
                if (b.isWhiteToCastleKingside()&&(((1L<<63L)&b.getWhiteRooks())!=0)) {
                    if (((b.getAllPieces()|unsafePositions)&((1L<<61)|(1L<<62)))==0) moveList+="7476";
                }
            }

        } else {

            long option;
            int currentPosition = Long.numberOfTrailingZeros(b.getBlackKing());

            if (currentPosition <= 9) {
                option = KING_B7 >> (9 - currentPosition);
            } else {
                option = KING_B7 << (currentPosition - 9);
            }
            if (currentPosition % 8 >= 4) {
                option &= ~COLUMN_AB & (~(b.getBlackPieces() | b.getWhiteKing()));
            } else {
                option &= ~COLUMN_GH & (~(b.getBlackPieces() | b.getWhiteKing()));
            }

            //normal moves
            long i = option & ~(option - 1);
            while (i != 0) {
                int index = Long.numberOfTrailingZeros(i);
                moveList += "" + (currentPosition / 8) + (currentPosition % 8) + (index / 8) + (index % 8);
                option &= ~i;
                i = option & ~(option - 1);
            }

            //Castling
            long unsafePositions = MoveGenerator.fieldsAttackedByWhite(b);
            if ((unsafePositions & b.getBlackKing()) == 0) {
                //Queenside castling
                if (b.isBlackToCastleQueenside() && (((1L << 0L) & b.getBlackRooks()) != 0)) {
                    if ((b.getAllPieces()|(unsafePositions&~(1L<<1)) & ((1L << 1) | (1L << 2) | (1L << 3))) == 0) moveList += "0402";
                }
                //Kingside castling
                if (b.isBlackToCastleKingside() && (((1L << 7L) & b.getBlackRooks()) != 0)) {
                    if (((b.getAllPieces()|unsafePositions )& ((1L << 5) | (1L << 6))) == 0) moveList += "0406";
                }
            }
        }
        return moveList;
    }
}
