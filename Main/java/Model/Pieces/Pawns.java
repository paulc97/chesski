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
            //y1,y2,Space,"E //TODO: vllt En Passant Optimation im Vid 13 oder 14 oder 15...
            if (history.length()>=4)//1636
            {
                if ((history.charAt(history.length()-1)==history.charAt(history.length()-3)) && Math.abs(history.charAt(history.length()-2)-history.charAt(history.length()-4))==2)
                {
                    int eFile=history.charAt(history.length()-1)-'0';
                    long possibility = 0L; //TODO: entfernen, wenn Move-Optimation bei restlichen Pawn Moves -> schon deklariert
                    //en passant right
                    possibility = (b.getWhitePawns() << 1)&b.getBlackPawns()&RANK_5&~FILE_A&FileMasks8[eFile];//shows piece to remove, not the destination
                    if (possibility != 0)
                    {
                        int index=Long.numberOfTrailingZeros(possibility);
                        list+=""+(index%8-1)+(index%8)+" E";
                    }
                    //en passant left
                    possibility = (b.getWhitePawns() >> 1)&b.getBlackPawns()&RANK_5&~FILE_H&FileMasks8[eFile];//shows piece to remove, not the destination
                    if (possibility != 0)
                    {
                        int index=Long.numberOfTrailingZeros(possibility);
                        list+=""+(index%8+1)+(index%8)+" E";
                    }
                }
            }

        } else {



            //x1,y1,x2,y2
            long PAWN_MOVES = (b.getBlackPawns() << 7) & b.getWhitePieces() & ~RANK_1 & ~FILE_H;//capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 - 1) + (i % 8 + 1) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 9) & b.getWhitePieces() & ~RANK_1 & ~FILE_A;//capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 - 1) + (i % 8 - 1) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 8) & b.getEmptyFields() & ~RANK_1;//move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 - 1) + (i % 8) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 16) & b.getEmptyFields() & (b.getEmptyFields() << 8) & RANK_5;//move 2 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 - 2) + (i % 8) + (i / 8) + (i % 8);
                }
            }

            //TODO is this obsolete?
            //y1,y2,Promotion Type,"P"
            PAWN_MOVES = (b.getBlackPawns() << 7) & b.getWhitePieces() & RANK_1 & ~FILE_H;//pawn promotion by capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 + 1) + (i % 8) + "QP" + (i % 8 + 1) + (i % 8) + "RP" + (i % 8 + 1) + (i % 8) + "BP" + (i % 8 + 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 9) & b.getWhitePieces() & RANK_1 & ~FILE_A;//pawn promotion by capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 - 1) + (i % 8) + "QP" + (i % 8 - 1) + (i % 8) + "RP" + (i % 8 - 1) + (i % 8) + "BP" + (i % 8 - 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 8) & b.getEmptyFields() & RANK_1;//pawn promotion by move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8) + (i % 8) + "QP" + (i % 8) + (i % 8) + "RP" + (i % 8) + (i % 8) + "BP" + (i % 8) + (i % 8) + "NP";
                }
            }
            //y1,y2,"bE"
            if (history.length()>=4)
            {
                if ((history.charAt(history.length()-1)==history.charAt(history.length()-3)) && Math.abs(history.charAt(history.length()-2)-history.charAt(history.length()-4))==2)
                {
                    int eFile=history.charAt(history.length()-1)-'0';
                    long possibility = 0L;
                    //en passant right
                    possibility = (b.getBlackPawns() >> 1)&b.getWhitePawns()&RANK_4&~FILE_H&FileMasks8[eFile];//shows piece to remove, not the destination
                    if (possibility != 0)
                    {
                        int index=Long.numberOfTrailingZeros(possibility);
                        list+=""+(index%8+1)+(index%8)+"bE";
                    }
                    //en passant left
                    possibility = (b.getBlackPawns() << 1)&b.getWhitePawns()&RANK_4&~FILE_A&FileMasks8[eFile];//shows piece to remove, not the destination
                    if (possibility != 0)
                    {
                        int index=Long.numberOfTrailingZeros(possibility);
                        list+=""+(index%8-1)+(index%8)+"bE";
                    }
                }
            }

        }

        return list;

    }
}
