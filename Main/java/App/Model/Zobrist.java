package Model;

import java.security.SecureRandom;

public class Zobrist {
    static long ZobristMap[][][] = new long[2][6][64];
    static long ZobristMapEnPassant[] = new long[8];
    static long ZobristMapCastle[] = new long[4];
    static long ZobristMapBlackMove;
    public static int found = 0;
    public static long getSecureRandom() {
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }
    public static void fillZobristMap() {
        for (int color = 0; color < 2; color++)
        {
            for (int pieceType = 0; pieceType < 6; pieceType++)
            {
                for (int square = 0; square < 64; square++)
                {
                    ZobristMap[color][pieceType][square] = getSecureRandom();
                }
            }
        }
        for (int column = 0; column < 8; column++)
        {
            ZobristMapEnPassant[column] = getSecureRandom();
        }
        for (int i = 0; i < 4; i++)
        {
            ZobristMapCastle[i] = getSecureRandom();
        }
        ZobristMapBlackMove = getSecureRandom();
    }
    public static long getZobristHash(long whitePawn,long whiteKnight,long whiteBishop,long whiteRook,long whiteQueen,long whiteKing,long blackPawn,long blackKnight,long blackBishop,long blackRook,long blackQueen,long BK,boolean castleWhiteKingside,boolean castleWhiteQueenside,boolean castleBlackKingside,boolean castleBlackQueenside,boolean WhiteToMove) {
        long zobristKey = 0;
        for (int square = 0; square < 64; square++)
        {
            if (((whitePawn >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[0][0][square];
            }
            else if (((blackPawn >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[1][0][square];
            }
            else if (((whiteKnight >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[0][1][square];
            }
            else if (((blackKnight >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[1][1][square];
            }
            else if (((whiteBishop >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[0][2][square];
            }

            else if (((blackBishop >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[1][2][square];
            }
            else if (((whiteRook >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[0][3][square];
            }
            else if (((blackRook >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[1][3][square];
            }
            else if (((whiteQueen >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[0][4][square];
            }
            else if (((blackQueen >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[1][4][square];
            }
            else if (((whiteKing >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[0][5][square];
            }
            else if (((BK >> square) & 1) == 1)
            {
                zobristKey ^= ZobristMap[1][5][square];
            }
        }
        
        if (castleWhiteKingside)
            zobristKey ^= ZobristMapCastle[0];
        if (castleWhiteQueenside)
            zobristKey ^= ZobristMapCastle[1];
        if (castleBlackKingside)
            zobristKey ^= ZobristMapCastle[2];
        if (castleBlackQueenside)
            zobristKey ^= ZobristMapCastle[3];
        if (!WhiteToMove)
            zobristKey ^= ZobristMapBlackMove;
        return zobristKey;
    }

}
