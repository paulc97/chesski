package Model.Pieces;
import Model.Board;

import static Model.Mask.*;


public class Pawns implements Piece {

    public String moves(String history, Board b) {

        String list = "";

        //TODO Does it make sense to handle the black/white move generation this way?
        if (b.isCurrentPlayerIsWhite()) {
            //x1,y1,x2,y2
            long PAWN_MOVES = (b.getWhitePawns() >> 7) & b.getBlackPieces() & ~RANK_8 & ~FILE_A;//capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);
                    //TODO discuss: create a new board here, assess the value of it and add it to the result list?
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 9) & b.getBlackPieces() & ~RANK_8 & ~FILE_H;//capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8 + 1) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 8) & b.getEmptyFields() & ~RANK_8;//move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 16) & b.getEmptyFields() & (b.getEmptyFields() >> 8) & RANK_4;//move 2 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 2) + (i % 8) + (i / 8) + (i % 8);
                }
            }

            //TODO is this obsolete?
            //y1,y2,Promotion Type,"P"
            PAWN_MOVES = (b.getWhitePawns() >> 7) & b.getBlackPieces() & RANK_8 & ~FILE_A;//pawn promotion by capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 - 1) + (i % 8) + "QP" + (i % 8 - 1) + (i % 8) + "RP" + (i % 8 - 1) + (i % 8) + "BP" + (i % 8 - 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 9) & b.getBlackPieces() & RANK_8 & ~FILE_H;//pawn promotion by capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 + 1) + (i % 8) + "QP" + (i % 8 + 1) + (i % 8) + "RP" + (i % 8 + 1) + (i % 8) + "BP" + (i % 8 + 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 8) & b.getEmptyFields() & RANK_8;//pawn promotion by move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8) + (i % 8) + "QP" + (i % 8) + (i % 8) + "RP" + (i % 8) + (i % 8) + "BP" + (i % 8) + (i % 8) + "NP";
                }
            }
            //y1,y2,Space,"E

        } else {

            //TODO invert logic for black pieces

            //x1,y1,x2,y2
            long PAWN_MOVES = (b.getWhitePawns() >> 7) & b.getBlackPieces() & ~RANK_8 & ~FILE_A;//capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 9) & b.getBlackPieces() & ~RANK_8 & ~FILE_H;//capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8 + 1) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 8) & b.getEmptyFields() & ~RANK_8;//move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 16) & b.getEmptyFields() & (b.getEmptyFields() >> 8) & RANK_4;//move 2 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 2) + (i % 8) + (i / 8) + (i % 8);
                }
            }

            //TODO is this obsolete?
            //y1,y2,Promotion Type,"P"
            PAWN_MOVES = (b.getWhitePawns() >> 7) & b.getBlackPieces() & RANK_8 & ~FILE_A;//pawn promotion by capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 - 1) + (i % 8) + "QP" + (i % 8 - 1) + (i % 8) + "RP" + (i % 8 - 1) + (i % 8) + "BP" + (i % 8 - 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 9) & b.getBlackPieces() & RANK_8 & ~FILE_H;//pawn promotion by capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 + 1) + (i % 8) + "QP" + (i % 8 + 1) + (i % 8) + "RP" + (i % 8 + 1) + (i % 8) + "BP" + (i % 8 + 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 8) & b.getEmptyFields() & RANK_8;//pawn promotion by move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8) + (i % 8) + "QP" + (i % 8) + (i % 8) + "RP" + (i % 8) + (i % 8) + "BP" + (i % 8) + (i % 8) + "NP";
                }
            }
            //y1,y2,Space,"E

        }

        return list;

    }
}
