package Model;

public class PieceSquareTables {

    private static int[] calcBlackPST(int[] whitePST){
        int[] blackPST = new int[64];

        for (int i = 0; i < 64; i++){
            blackPST[i] = whitePST[i ^ 63];
        }
        return blackPST;
    }

    //TODO: change values, copied from github aka https://www.chessprogramming.org/Simplified_Evaluation_Function
    public static final int[] PST_WHITE_PAWNS = {
            0,  0,   0,   0,   0,   0,  0,  0,
            50, 50,  50,  50,  50,  50, 50, 50,
            10, 10,  20,  30,  30,  20, 10, 10,
            5,  5,  10,  25,  25,  10,  5,  5,
            0,  0,   0,  20,  20,   0,  0,  0,
            5, -5, -10,   0,   0, -10, -5,  5,
            5, 10,  10, -20, -20,  10, 10,  5,
            0,  0,   0,   0,   0,   0,  0,  0
    };
    public static final int[] PST_WHITE_KNIGHTS = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };
    public static final int[] PST_WHITE_BISHOPS = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };
    public static final int[] PST_WHITE_ROOKS = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };
    public static final int[] PST_WHITE_QUEEN = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };
    public static final int[] PST_WHITE_KING = {
            -50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50
    };//NOTE: this is king end//TODO: change values (attention: pseudolegal moves!)

    public static final int[] PST_BLACK_PAWNS = calcBlackPST(PST_WHITE_PAWNS);
    public static final int[] PST_BLACK_KNIGHTS = calcBlackPST(PST_WHITE_KNIGHTS);
    public static final int[] PST_BLACK_BISHOPS = calcBlackPST(PST_WHITE_BISHOPS);
    public static final int[] PST_BLACK_ROOKS = calcBlackPST(PST_WHITE_ROOKS);
    public static final int[] PST_BLACK_QUEEN = calcBlackPST(PST_WHITE_QUEEN);
    public static final int[] PST_BLACK_KING = calcBlackPST(PST_WHITE_KING);
}
