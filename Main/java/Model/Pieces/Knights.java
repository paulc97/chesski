package Model.Pieces;

import Model.Board;

import static Model.Mask.*;

public class Knights implements Piece {

    public String moves(Board b) {

        String moveList = "";

        if (b.isCurrentPlayerIsWhite()) {

            long currentPositionsWhiteKnights = b.getWhiteKnights();
            long i=currentPositionsWhiteKnights&~(currentPositionsWhiteKnights-1);
            long option;

            while(i != 0) {
                int currentLocation=Long.numberOfTrailingZeros(i);
                if (currentLocation <= 18) {
                    option=KNIGHT_C6>>(18-currentLocation);
                } else {
                    option=KNIGHT_C6<<(currentLocation-18);
                }
                if (currentLocation%8 >= 4) {
                    option &=~FILE_AB&(~(b.getWhitePieces()|b.getBlackKing()));
                } else {
                    option &=~FILE_GH&(~(b.getWhitePieces()|b.getBlackKing()));
                }

                long k=option&~(option - 1);
                while (k != 0) {
                    int index=Long.numberOfTrailingZeros(k);
                    moveList+=""+(currentLocation/8)+(currentLocation%8)+(index/8)+(index%8);
                    option&=~k;
                    k=option&~(option-1);
                }
                currentPositionsWhiteKnights&=~i;
                i=currentPositionsWhiteKnights&~(currentPositionsWhiteKnights-1);
            }

        } else {

            long currentPositionsBlackKnights = b.getBlackKnights();
            long i=currentPositionsBlackKnights&~(currentPositionsBlackKnights-1);
            long option;
            while(i != 0) {
                int iLocation=Long.numberOfTrailingZeros(i);
                if (iLocation <= 18) {
                    option=KNIGHT_C6>>(18-iLocation);
                } else {
                    option=KNIGHT_C6<<(iLocation-18);
                }
                if (iLocation%8 >= 4) {
                    option &=~FILE_AB&(~(b.getBlackPieces()|b.getWhiteKing()));
                } else {
                    option &=~FILE_GH&(~(b.getBlackPieces()|b.getWhiteKing()));
                }

                long k=option&~(option - 1);
                while (k != 0) {
                    int index=Long.numberOfTrailingZeros(k);
                    moveList+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    option&=~k;
                    k=option&~(option-1);
                }
                currentPositionsBlackKnights&=~i;
                i=currentPositionsBlackKnights&~(currentPositionsBlackKnights-1);
            }
        }
        return moveList;
    }
}
