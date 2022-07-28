package Model.Pieces;
import Model.Board;

import static Model.Mask.*;


public class Pawns implements Piece {

    public String moves(Board b) {

        String list = "";

        if (b.isCurrentPlayerIsWhite()) {
            //x1,y1,x2,y2
            long PAWN_MOVES = (b.getWhitePawns() >> 7) & (b.getBlackPieces() & ~b.getBlackKing()) & ~RANK_8 & ~FILE_A;//capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);
                    //TODO discuss: create a new board here, assess the value of it and add it to the result list?
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 9) & (b.getBlackPieces() & ~b.getBlackKing()) & ~RANK_8 & ~FILE_H;//capture left
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

            //TODO adjust promotion moves for game server implementation to source/target/piece
            //y1,y2,Promotion Type,"P"
            PAWN_MOVES = (b.getWhitePawns() >> 7) & (b.getBlackPieces() & ~b.getBlackKing()) & RANK_8 & ~FILE_A;//pawn promotion by capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 - 1) + (i % 8) + "QP" + (i % 8 - 1) + (i % 8) + "RP" + (i % 8 - 1) + (i % 8) + "BP" + (i % 8 - 1) + (i % 8) + "NP";
                }
            }
            PAWN_MOVES = (b.getWhitePawns() >> 9) & (b.getBlackPieces() & ~b.getBlackKing()) & RANK_8 & ~FILE_H;//pawn promotion by capture left
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
            //y1,y2,Space,"WE"
            //TODO: wenn enPassant Feld im FEN angegeben, vereinfachen evtl. möglich

            long EPBitboardFileFromFenString = 0L;
            if(b.getEnPassants().length()>1){
                EPBitboardFileFromFenString = FileMasks8[b.getEnPassants().charAt(0)-97];
            } //TODO: ändern, wenn auf enPassant-Methode/FEN-Format geeinigt


            long possibility = 0L; //TODO: entfernen, wenn Move-Optimation bei restlichen Pawn Moves -> schon deklariert
            //alt: possibility = (b.getWhitePawns() << 1)&b.getBlackPawns()&RANK_5&~FILE_A&b.getEnPassantBitboardFile();//shows piece to remove, not the destination
            possibility = (b.getWhitePawns() << 1)&b.getBlackPawns()&RANK_5&~FILE_A&EPBitboardFileFromFenString;
            if (possibility != 0)
            {
                int index=Long.numberOfTrailingZeros(possibility);
                list+=""+(index%8-1)+(index%8)+"WE";
            }
            //en passant left
            possibility = (b.getWhitePawns() >> 1)&b.getBlackPawns()&RANK_5&~FILE_H&EPBitboardFileFromFenString;//shows piece to remove, not the destination
            if (possibility != 0)
            {
                int index=Long.numberOfTrailingZeros(possibility);
                list+=""+(index%8+1)+(index%8)+"WE";
            }

        } else {



            //x1,y1,x2,y2
            long PAWN_MOVES = (b.getBlackPawns() << 7) & (b.getWhitePieces() & ~b.getWhiteKing()) & ~RANK_1 & ~FILE_H;//capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i / 8 - 1) + (i % 8 + 1) + (i / 8) + (i % 8);
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 9) & (b.getWhitePieces() & ~b.getWhiteKing()) & ~RANK_1 & ~FILE_A;//capture left
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

            //y1,y2,Promotion Type,"P"
            PAWN_MOVES = (b.getBlackPawns() << 7) & (b.getWhitePieces() & ~b.getWhiteKing()) & RANK_1 & ~FILE_H;//pawn promotion by capture right
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 + 1) + (i % 8) + "qP" + (i % 8 + 1) + (i % 8) + "rP" + (i % 8 + 1) + (i % 8) + "bP" + (i % 8 + 1) + (i % 8) + "nP";
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 9) & (b.getWhitePieces() & ~b.getWhiteKing()) & RANK_1 & ~FILE_A;//pawn promotion by capture left
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8 - 1) + (i % 8) + "qP" + (i % 8 - 1) + (i % 8) + "rP" + (i % 8 - 1) + (i % 8) + "bP" + (i % 8 - 1) + (i % 8) + "nP";
                }
            }
            PAWN_MOVES = (b.getBlackPawns() << 8) & b.getEmptyFields() & RANK_1;//pawn promotion by move 1 forward
            for (int i = Long.numberOfTrailingZeros(PAWN_MOVES); i < 64 - Long.numberOfLeadingZeros(PAWN_MOVES); i++) {
                if (((PAWN_MOVES >> i) & 1) == 1) {
                    list += "" + (i % 8) + (i % 8) + "qP" + (i % 8) + (i % 8) + "rP" + (i % 8) + (i % 8) + "bP" + (i % 8) + (i % 8) + "nP";
                }
            }
            //y1,y2,"BE"//TODO: check if bE or BE is correct

            long EPBitboardFileFromFenString = 0L;
            if(b.getEnPassants().length()>1){
                EPBitboardFileFromFenString = FileMasks8[b.getEnPassants().charAt(0)-97];
            }



            long possibility = 0L; //TODO: entfernen, wenn Move-Optimation bei restlichen Pawn Moves -> schon deklariert
            possibility = (b.getBlackPawns() >> 1)&b.getWhitePawns()&RANK_4&~FILE_H&EPBitboardFileFromFenString;//shows piece to remove, not the destination
            if (possibility != 0)
            {
                int index=Long.numberOfTrailingZeros(possibility);
                list+=""+(index%8+1)+(index%8)+"BE";
            }
            //en passant left
            possibility = (b.getBlackPawns() << 1)&b.getWhitePawns()&RANK_4&~FILE_A&EPBitboardFileFromFenString;//shows piece to remove, not the destination
            if (possibility != 0)
            {
                int index=Long.numberOfTrailingZeros(possibility);
                list+=""+(index%8-1)+(index%8)+"BE";
            }

        }

        return list;

    }
}
