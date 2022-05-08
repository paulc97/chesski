package Model.Pieces;

import Model.Board;

import static Model.Mask.*;

//TODO: implements Piece interface?

public class SlidingPieces {

    public String rookMoves(Board b) {

        long OCCUPIED = b.getAllPieces();
        String list="";
        if (b.isCurrentPlayerIsWhite()){
            long WR = b.getWhiteRooks();
            long i=WR&~(WR-1);
            long possibility;

            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=HAndVMoves(iLocation,OCCUPIED)&~(b.getWhitePieces()|b.getBlackKing());
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
        }else{
            long BR = b.getBlackRooks();
            long i=BR&~(BR-1);
            long possibility;

            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=HAndVMoves(iLocation,OCCUPIED)&~(b.getBlackPieces()|b.getWhiteKing());
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

    public String bishopMoves(Board b) {

        long OCCUPIED = b.getAllPieces();
        String list="";
        if (b.isCurrentPlayerIsWhite()){
            long WB = b.getWhiteBishops();
            long i=WB&~(WB-1);
            long possibility;

            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=DAndAntiDMoves(iLocation,OCCUPIED)&~(b.getWhitePieces()|b.getBlackKing());
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
        }else{
            long BB = b.getBlackBishops();
            long i=BB&~(BB-1);
            long possibility;

            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=DAndAntiDMoves(iLocation,OCCUPIED)&~(b.getBlackPieces()|b.getWhiteKing());
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

    public String queenMoves(Board b) {

        long OCCUPIED = b.getAllPieces();
        String list="";
        if (b.isCurrentPlayerIsWhite()){
            long WQ = b.getWhiteQueen();
            long i=WQ&~(WQ-1);
            long possibility;

            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=(HAndVMoves(iLocation,OCCUPIED)|DAndAntiDMoves(iLocation,OCCUPIED))&~(b.getWhitePieces()|b.getBlackKing());
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
        }else{
            long BQ = b.getBlackQueen();
            long i=BQ&~(BQ-1);
            long possibility;

            while(i != 0)
            {
                int iLocation=Long.numberOfTrailingZeros(i);
                possibility=(HAndVMoves(iLocation,OCCUPIED)|DAndAntiDMoves(iLocation,OCCUPIED))&~(b.getBlackPieces()|b.getWhiteKing());
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


    public static long DAndAntiDMoves(int s,long OCCUPIED)
    {
        long binaryS=1L<<s;
        long possibilitiesDiagonal = ((OCCUPIED&DiagonalMasks8[(s / 8) + (s % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(OCCUPIED&DiagonalMasks8[(s / 8) + (s % 8)]) - (2 * Long.reverse(binaryS)));
        long possibilitiesAntiDiagonal = ((OCCUPIED&AntiDiagonalMasks8[(s / 8) + 7 - (s % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(OCCUPIED&AntiDiagonalMasks8[(s / 8) + 7 - (s % 8)]) - (2 * Long.reverse(binaryS)));
        return (possibilitiesDiagonal&DiagonalMasks8[(s / 8) + (s % 8)]) | (possibilitiesAntiDiagonal&AntiDiagonalMasks8[(s / 8) + 7 - (s % 8)]);
    }

    public static long HAndVMoves(int s,long OCCUPIED)
    {
        long binaryS=1L<<s;
        long possibilitiesHorizontal = (OCCUPIED - 2 * binaryS) ^ Long.reverse(Long.reverse(OCCUPIED) - 2 * Long.reverse(binaryS));
        long possibilitiesVertical = ((OCCUPIED&FileMasks8[s % 8]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(OCCUPIED&FileMasks8[s % 8]) - (2 * Long.reverse(binaryS)));
        return (possibilitiesHorizontal&RankMasks8[s / 8]) | (possibilitiesVertical&FileMasks8[s % 8]);

    }
}
