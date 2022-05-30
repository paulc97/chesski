package Model.Pieces;

import Model.Mask;

import java.security.SecureRandom;

public class Zobris {

    static long ZobrisArray[][][] = new long [2][6][64];
    static long ZobrisPassant[] = new long [8];
    static long ZobrisCastle[] = new long [4];
    static long ZobrisBlackMove;

    public static long getRandom(){
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }

    // Ineffizient aber ok weil nur einmal ausgeführt
    public static void zobristFillArray() {
        for (int color = 0; color < 2; color++)
        {
            for (int pieceType = 0; pieceType < 6; pieceType++)
            {
                for (int square = 0; square < 64; square++)
                {
                    ZobrisArray[color][pieceType][square] = getRandom();
                }
            }
        }
        for (int column = 0; column < 8; column++)
        {
            ZobrisPassant[column] = getRandom();
        }
        for (int i = 0; i < 4; i++)
        {
            ZobrisCastle[i] = getRandom();
        }
        ZobrisBlackMove = getRandom();
    }

    public static long getZobristHash(long WP,long WN,long WB,long WR,long WQ,long WK,long BP,long BN,long BB,long BR,long BQ,long BK,boolean CWK,boolean CWQ,boolean CBK,boolean CBQ,boolean WhiteToMove) {
        long returnZKey = 0;
        for (int square = 0; square < 64; square++)
        {
            if (((WP >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[0][0][square];
            }
            else if (((BP >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[1][0][square];
            }
            else if (((WN >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[0][1][square];
            }
            else if (((BN >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[1][1][square];
            }
            else if (((WB >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[0][2][square];
            }

            else if (((BB >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[1][2][square];
            }
            else if (((WR >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[0][3][square];
            }
            else if (((BR >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[1][3][square];
            }
            else if (((WQ >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[0][4][square];
            }
            else if (((BQ >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[1][4][square];
            }
            else if (((WK >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[0][5][square];
            }
            else if (((BK >> square) & 1) == 1)
            {
                returnZKey ^= ZobrisArray[1][5][square];
            }
        }
        //for (int column = 0; column < 8; column++)
        //{
        //    if (EP == Mask.FileMasks8[column])
        //    {
        //        returnZKey ^= ZobrisPassant[column];
        //    }
        //}
        if (CWK)
            returnZKey ^= ZobrisCastle[0];
        if (CWQ)
            returnZKey ^= ZobrisCastle[1];
        if (CBK)
            returnZKey ^= ZobrisCastle[2];
        if (CBQ)
            returnZKey ^= ZobrisCastle[3];
        if (!WhiteToMove)
            returnZKey ^= ZobrisBlackMove;
        return returnZKey;
    }
}
