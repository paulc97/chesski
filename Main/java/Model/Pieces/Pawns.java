package Model.Pieces;
import Model.Board;

import static Model.Mask.*;


public class Pawns implements Piece {

    public String moves(Board b) {

        String moveList = "";

        if (b.isCurrentPlayerIsWhite()) {

            //1 forward
            long pawnMoves = (b.getWhitePawns() >> 8)
                    & ~ROW_8
                    & b.getEmptyFields();
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8);
                }
            }

            //2 forward
            pawnMoves = (b.getWhitePawns() >> 16)
                    & ROW_4
                    & b.getEmptyFields() & (b.getEmptyFields() >> 8) ;
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 + 2) + (i % 8) + (i / 8) + (i % 8);
                }
            }

            //right capture
            pawnMoves = (b.getWhitePawns() >> 7)
                    & ~ROW_8 & ~COLUMN_A &
                    (b.getBlackPieces() & ~b.getBlackKing());
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);
                }
            }

            //left capture
            pawnMoves = (b.getWhitePawns() >> 9)
                    & ~ROW_8 & ~COLUMN_H
                    & (b.getBlackPieces() & ~b.getBlackKing());
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 + 1) + (i % 8 + 1) + (i / 8) + (i % 8);
                }
            }


            //Promotions
            //pawn promotion forward
            pawnMoves = (b.getWhitePawns() >> 8)
                    & ROW_8
                    & b.getEmptyFields();
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i % 8) + (i % 8) + "QP"
                            + (i % 8) + (i % 8) + "BP"
                            + (i % 8) + (i % 8) + "RP"
                            + (i % 8) + (i % 8) + "NP";
                }
            }

            //pawn promotion, capture right
            pawnMoves = (b.getWhitePawns() >> 7)
                    & ROW_8 & ~COLUMN_A
                    & (b.getBlackPieces() & ~b.getBlackKing());
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i % 8 - 1) + (i % 8) + "QP"
                            + (i % 8 - 1) + (i % 8) + "BP"
                            + (i % 8 - 1) + (i % 8) + "RP"
                            + (i % 8 - 1) + (i % 8) + "NP";
                }
            }

            //pawn promotion by capture left
            pawnMoves = (b.getWhitePawns() >> 9) & (b.getBlackPieces() & ~b.getBlackKing()) & ROW_8 & ~COLUMN_H;
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i % 8 + 1) + (i % 8) + "QP"
                            + (i % 8 + 1) + (i % 8) + "BP"
                            + (i % 8 + 1) + (i % 8) + "RP"
                            + (i % 8 + 1) + (i % 8) + "NP";
                }
            }

            long EPBitboardFileFromFenString = 0L;
            if(b.getEnPassants().length()>1){
                EPBitboardFileFromFenString = ColumnMasks8[b.getEnPassants().charAt(0)-97];
            }

            long option = 0L;
            option = (b.getWhitePawns() << 1)
                    & ROW_5 & ~COLUMN_A
                    & b.getBlackPawns() & EPBitboardFileFromFenString;
            if (option != 0)
            {
                int index=Long.numberOfTrailingZeros(option);
                moveList+=""+(index%8-1)+(index%8)+"WE";
            }

            //en passant left
            option = (b.getWhitePawns() >> 1) & b.getBlackPawns()
                    & ROW_5 & ~COLUMN_H
                    & EPBitboardFileFromFenString;
            if (option != 0)
            {
                int index=Long.numberOfTrailingZeros(option);
                moveList+=""+(index%8+1)+(index%8)+"WE";
            }

        } else {

            //move 1 forward
            long pawnMoves = (b.getBlackPawns() << 8)
                    & ~ROW_1
                    & b.getEmptyFields();

            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 - 1) + (i % 8) + (i / 8) + (i % 8);
                }
            }


            //move 2 forward
            pawnMoves = (b.getBlackPawns() << 16)
                    & ROW_5
                    & b.getEmptyFields() & (b.getEmptyFields() << 8);

            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 - 2) + (i % 8) + (i / 8) + (i % 8);
                }
            }

            //capture right
            pawnMoves = (b.getBlackPawns() << 7)
                    & ~ROW_1 & ~COLUMN_H
                    & (b.getWhitePieces() & ~b.getWhiteKing());

            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 - 1) + (i % 8 + 1) + (i / 8) + (i % 8);
                }
            }


            //capture left
            pawnMoves = (b.getBlackPawns() << 9)
                    & ~ROW_1 & ~COLUMN_A
                    & (b.getWhitePieces() & ~b.getWhiteKing());

            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += "" + (i / 8 - 1) + (i % 8 - 1) + (i / 8) + (i % 8);
                }
            }


            //Promotions
            //pawn promotion forward
            pawnMoves = (b.getBlackPawns() << 8)
                    & ROW_1
                    & b.getEmptyFields();

            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += ""
                            + (i % 8) + (i % 8) + "qP"
                            + (i % 8) + (i % 8) + "rP"
                            + (i % 8) + (i % 8) + "bP"
                            + (i % 8) + (i % 8) + "nP";
                }
            }


            //pawn promotion, capture left
            pawnMoves = (b.getBlackPawns() << 9)
                    & ROW_1 & ~COLUMN_A
                    & (b.getWhitePieces() & ~b.getWhiteKing());

            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += ""
                            + (i % 8 - 1) + (i % 8) + "qP"
                            + (i % 8 - 1) + (i % 8) + "rP"
                            + (i % 8 - 1) + (i % 8) + "bP"
                            + (i % 8 - 1) + (i % 8) + "nP";
                }
            }


            //pawn promotion, capture right
            pawnMoves = (b.getBlackPawns() << 7) & (b.getWhitePieces() & ~b.getWhiteKing()) & ROW_1 & ~COLUMN_H;
            for (int i = Long.numberOfTrailingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
                if (((pawnMoves >> i) & 1) == 1) {
                    moveList += ""
                            + (i % 8 + 1) + (i % 8) + "qP"
                            + (i % 8 + 1) + (i % 8) + "rP"
                            + (i % 8 + 1) + (i % 8) + "bP"
                            + (i % 8 + 1) + (i % 8) + "nP";
                }
            }


            long EpColumn = 0L;
            if(b.getEnPassants().length()>1){
                EpColumn = ColumnMasks8[b.getEnPassants().charAt(0)-97];
            }

            //en passant right
            long option = 0L;
            option = (b.getBlackPawns() >> 1)&b.getWhitePawns()
                    & ROW_4 & ~COLUMN_H
                    & EpColumn;

            if (option != 0)
            {
                int index=Long.numberOfTrailingZeros(option);
                moveList+=""+(index%8+1)+(index%8)+"BE";
            }

            //en passant left
            option = (b.getBlackPawns() << 1)&b.getWhitePawns()
                    & ROW_4 & ~COLUMN_A
                    &EpColumn;

            if (option != 0)
            {
                int index=Long.numberOfTrailingZeros(option);
                moveList+=""+(index%8-1)+(index%8)+"BE";
            }
        }

        return moveList;

    }
}
