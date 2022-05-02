package Model.Pieces;

import Model.Board;

import static Model.Mask.*;


public class SlidingPieces {

    public String rookMoves(String history, Board b) {

        String list = "";
        if (b.isCurrentPlayerIsWhite()) {

            long i=b.getWhiteRooks()&~(b.getWhiteRooks()-1);
            long possibility;
            long WR = b.getWhiteRooks();
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=getHorrizontalAndVerticalMoves(iLocation,b)&b.getWhitePieces();
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                WR&=~i;
                i=WR&~(WR-1);
            }

        } else {

            long i=b.getBlackRooks()&~(b.getBlackRooks()-1);
            long possibility;
            long BR = b.getBlackRooks();
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=getHorrizontalAndVerticalMoves(iLocation,b)&b.getBlackPieces();
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                BR&=~i;
                i=BR&~(BR-1);
            }

        }

        return list;

    }

    public String bishopMoves(String history, Board b) {

        String list = "";
        if (b.isCurrentPlayerIsWhite()) {

            long i=b.getWhiteBishops()&~(b.getWhiteBishops()-1);
            long possibility;
            long WB = b.getWhiteBishops();
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=getDiagonalMoves(iLocation,b)&b.getWhitePieces();
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                WB&=~i;
                i=WB&~(WB-1);
            }



        } else {

            long i=b.getBlackBishops()&~(b.getBlackBishops()-1);
            long possibility;
            long BB = b.getBlackBishops();
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=getDiagonalMoves(iLocation,b)&b.getBlackPieces();
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                BB&=~i;
                i=BB&~(BB-1);
            }



        }

        return list;

    }

    public String queenMoves(String history, Board b) {

        String list = "";
        if (b.isCurrentPlayerIsWhite()) {

            long i=b.getWhiteQueen()&~(b.getWhiteQueen()-1);
            long possibility;
            long WQ = b.getWhiteQueen();
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=getHorrizontalAndVerticalMoves(iLocation,b)&b.getWhitePieces()&getDiagonalMoves(iLocation,b);
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                WQ&=~i;
                i=WQ&~(WQ-1);
            }

        } else {

            long i=b.getBlackQueen()&~(b.getBlackQueen()-1);
            long possibility;
            long BQ = b.getBlackQueen();
            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=getHorrizontalAndVerticalMoves(iLocation,b)&b.getBlackPieces()&getDiagonalMoves(iLocation,b);
                long j=possibility&~(possibility-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    list+=""+(iLocation/8)+(iLocation%8)+(index/8)+(index%8);
                    possibility&=~j;
                    j=possibility&~(possibility-1);
                }
                BQ&=~i;
                i=BQ&~(BQ-1);
            }

        }

        return list;

    }

    private long getDiagonalMoves(int p,Board b){

        long binaryS=1L<<p;
        long possibilitiesDiagonal = ((b.getAllPieces()&DiagonalMasks8[(p / 8) + (p % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(b.getAllPieces()&DiagonalMasks8[(p / 8) + (p % 8)]) - (2 * Long.reverse(binaryS)));
        long possibilitiesAntiDiagonal = ((b.getAllPieces()&AntiDiagonalMasks8[(p / 8) + 7 - (p % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(b.getAllPieces()&AntiDiagonalMasks8[(p / 8) + 7 - (p % 8)]) - (2 * Long.reverse(binaryS)));
        return (possibilitiesDiagonal&DiagonalMasks8[(p / 8) + (p % 8)]) | (possibilitiesAntiDiagonal&AntiDiagonalMasks8[(p / 8) + 7 - (p % 8)]);

    }

    private long getHorrizontalAndVerticalMoves(int p,Board b){

        long binaryS=1L<<p;
        long possibilitiesHorizontal = (b.getAllPieces() - 2 * binaryS) ^ Long.reverse(Long.reverse(b.getAllPieces()) - 2 * Long.reverse(binaryS));
        long possibilitiesVertical = ((b.getAllPieces()&FileMasks8[p % 8]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(b.getAllPieces()&FileMasks8[p % 8]) - (2 * Long.reverse(binaryS)));
        return (possibilitiesHorizontal&RankMasks8[p / 8]) | (possibilitiesVertical&FileMasks8[p % 8]);

    }
}
