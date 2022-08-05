package Model;

import Model.Pieces.*;


import java.util.HashMap;

import static Model.Mask.*;


public class MoveGenerator {

    private static boolean outOfTime = false; //used for Iterative Deepening search

    static Pawns pawns = new Pawns();
    static SlidingPieces slidingPieces = new SlidingPieces();
    static Knights knights = new Knights();
    static King king = new King();
    public static HashMap<Long, Integer> assesedBoards = new HashMap<Long, Integer>();
    public static final Zobrist zobrist = new Zobrist();

    static int assessedLeaves = 0;
    static int quiescenceSearchIterations = 0;
    static int cutoffs = 0;

    static int averageNumberOfMoves = 40;
    static private long[] timeDistribution = new long[averageNumberOfMoves];
    static double gameTimeLimit = 120000;
    static double panicModeTimeBuffer = 10000;
    static long totalTime;

    public MoveGenerator() {
        double expectationValue = averageNumberOfMoves / 2;
        double variance = 30;
        zobrist.fillZobristMap();
        System.out.println("MG Created");
        for (int i = 0; i < this.timeDistribution.length; i++) {
            double k = (double) i + 1;
            //if(k<expectationValue){k=k*(-1);}
            double standardDistribution = (1 / Math.sqrt(2 * Math.PI * (variance))) * Math.exp(-1 * ((((k) - expectationValue) * ((k) - expectationValue)) / (2 * (variance))));
            this.timeDistribution[i] = (long) Math.abs((standardDistribution) * (gameTimeLimit - panicModeTimeBuffer));
        }
    }

    public static String sortMovesCaptures(Board board, String moves) {

        String QueenCaptures = "";
        String RookCaptures = "";
        String PieceCaptures = "";
        String PawnCaptures = "";
        String nonCaptures = "";
        String QueenPromotion = "";
        String RookPromotion = "";
        String PiecePromotion = "";

        int Queencount;
        int RookCount;
        int PieceCount;
        int PawnCount;

        int newQueencount;
        int newRookCount;
        int newPieceCount;
        int newPawnCount;

        moves = moves.replace("-", "");


        if (board.isCurrentPlayerIsWhite()) {
            Queencount = Long.bitCount(board.getBlackQueen());
            RookCount = Long.bitCount(board.getBlackRooks());
            PieceCount = Long.bitCount(board.getBlackBishops()) + Long.bitCount(board.getBlackKnights());
            PawnCount = Long.bitCount(board.getBlackPawns());

        } else {
            Queencount = Long.bitCount(board.getWhiteQueen());
            RookCount = Long.bitCount(board.getWhiteRooks());
            PieceCount = Long.bitCount(board.getWhiteBishops()) + Long.bitCount(board.getWhiteKnights());
            PawnCount = Long.bitCount(board.getWhitePawns());
        }

        Board tempB = new Board(board);

        for (int i = 0; i < moves.length(); i += 4) {


            String move = moves.substring(i, i + 4);

            if (move.contains("P") || move.contains("p")) {
                // PromotionMove
                if (move.contains("Q") || move.contains("q")) {
                    QueenPromotion += move;
                    continue;
                } else if (move.contains("R") || move.contains("r")) {
                    RookPromotion += move;
                    continue;
                } else if (move.contains("N") || move.contains("n")) {
                    PiecePromotion += move;
                    continue;
                } else if (move.contains("B") || move.contains("b")) {
                    PiecePromotion += move;
                    continue;
                }
            }

            Board newBoard = tempB.createBoardFromMove(move);
            newBoard.setCreatedByMove(move);

            if (board.isCurrentPlayerIsWhite()) {
                newQueencount = Long.bitCount(newBoard.getBlackQueen());
                newRookCount = Long.bitCount(newBoard.getBlackRooks());
                newPieceCount = Long.bitCount(newBoard.getBlackBishops()) + Long.bitCount(newBoard.getBlackKnights());
                newPawnCount = Long.bitCount(newBoard.getBlackPawns());

            } else {
                newQueencount = Long.bitCount(newBoard.getWhiteQueen());
                newRookCount = Long.bitCount(newBoard.getWhiteRooks());
                newPieceCount = Long.bitCount(newBoard.getWhiteBishops()) + Long.bitCount(newBoard.getWhiteKnights());
                newPawnCount = Long.bitCount(newBoard.getWhitePawns());
            }

            if (Queencount > newQueencount) {
                //System.out.println("Capture");
                //System.out.println(move);
                QueenCaptures += move;
            } else if (RookCount > newRookCount) {
                //System.out.println("Non-Capture");
                RookCaptures += move;
            } else if (PieceCount > newPieceCount) {
                //System.out.println("Non-Capture");
                PieceCaptures += move;
            } else if (PawnCount > newPawnCount) {
                //System.out.println("Non-Capture");
                PawnCaptures += move;
            } else {
                nonCaptures += move;
            }
        }

        return QueenPromotion + RookPromotion + PiecePromotion + QueenCaptures + RookCaptures + PieceCaptures + PawnCaptures + nonCaptures;

    }

    public static String ownPossibleMoves(Board board) {

        String list = "";
        list += slidingPieces.rookMoves(board) + "-";
        list += slidingPieces.bishopMoves(board) + "-";
        list += slidingPieces.queenMoves(board);
        list += knights.moves(board) + "-";
        list += pawns.moves(board) + "-";
        list += king.moves(board) + "-";

        return list;
    }


    /**
     * @param b
     * @return bitboard which includes all positions which can be reached by white figures
     */
    public static long fieldsAttackedByWhite(Board b) {
        long attackedPositions;
        //pawn
        attackedPositions = ((b.getWhitePawns() >>> 7) & ~COLUMN_A);//pawn capture right
        attackedPositions |= ((b.getWhitePawns() >>> 9) & ~COLUMN_H);//pawn capture left
        long option;

        //knight
        long whiteKnightsPositions = b.getWhiteKnights();
        long i = whiteKnightsPositions & ~(whiteKnightsPositions - 1);
        while (i != 0) {
            int currentPosition = Long.numberOfTrailingZeros(i);
            if (currentPosition <= 18) {
                option = KNIGHT_C6 >> (18 - currentPosition);
            } else {

                option = KNIGHT_C6 << (currentPosition - 18);
            }
            if (currentPosition % 8 >= 4) {
                option &= ~COLUMN_AB;
            } else {
                option &= ~COLUMN_GH;
            }
            attackedPositions |= option;
            whiteKnightsPositions &= ~i;
            i = whiteKnightsPositions & ~(whiteKnightsPositions - 1);
        }

        //bishop and queen
        long whiteQueenBishopPositions = b.getWhiteQueen() | b.getWhiteBishops();
        i = whiteQueenBishopPositions & ~(whiteQueenBishopPositions - 1);
        while (i != 0) {
            int currentPosition = Long.numberOfTrailingZeros(i);
            option = SlidingPieces.DaigonalMoves(currentPosition, b.getAllPieces());
            attackedPositions |= option;
            whiteQueenBishopPositions &= ~i;
            i = whiteQueenBishopPositions & ~(whiteQueenBishopPositions - 1);
        }

        //rook and queen
        long whiteRookQueenPositions = b.getWhiteQueen() | b.getWhiteRooks();
        i = whiteRookQueenPositions & ~(whiteRookQueenPositions - 1);
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            option = SlidingPieces.horizonralAndVerticalMoves(iLocation, b.getAllPieces());
            attackedPositions |= option;
            whiteRookQueenPositions &= ~i;
            i = whiteRookQueenPositions & ~(whiteRookQueenPositions - 1);
        }

        //king
        int currentPosition = Long.numberOfTrailingZeros(b.getWhiteKing());
        if (currentPosition <= 9) {
            option = KING_B7 >> (9 - currentPosition);
        } else {
            option = KING_B7 << (currentPosition - 9);
        }
        if (currentPosition % 8 >= 4) {
            option &= ~COLUMN_AB;
        } else {
            option &= ~COLUMN_GH;
        }
        attackedPositions |= option;
        return attackedPositions;
    }

    public static long fieldsAttackedByBlack(Board b) {
        long attackedPositions;

        //pawn
        attackedPositions = ((b.getBlackPawns() << 7) & ~COLUMN_H);//pawn capture right
        attackedPositions |= ((b.getBlackPawns() << 9) & ~COLUMN_A);//pawn capture left
        long option;

        //knight
        long blackKnightsPositions = b.getBlackKnights();
        long i = blackKnightsPositions & ~(blackKnightsPositions - 1);
        while (i != 0) {
            int currentPosition = Long.numberOfTrailingZeros(i);
            if (currentPosition <= 18) {
                option = KNIGHT_C6 >> (18 - currentPosition);
            } else {
                option = KNIGHT_C6 << (currentPosition - 18);
            }
            if (currentPosition % 8 >= 4) {
                option &= ~COLUMN_AB;
            } else {
                option &= ~COLUMN_GH;
            }
            attackedPositions |= option;
            blackKnightsPositions &= ~i;
            i = blackKnightsPositions & ~(blackKnightsPositions - 1);
        }

        //bishop and queen
        long blackQueenBishopPositions = b.getBlackQueen() | b.getBlackBishops();
        i = blackQueenBishopPositions & ~(blackQueenBishopPositions - 1);
        while (i != 0) {
            int currentPosition = Long.numberOfTrailingZeros(i);
            option = SlidingPieces.DaigonalMoves(currentPosition, b.getAllPieces());
            attackedPositions |= option;
            blackQueenBishopPositions &= ~i;
            i = blackQueenBishopPositions & ~(blackQueenBishopPositions - 1);
        }

        //rook and queen
        long blackQueenRookPositions = b.getBlackQueen() | b.getBlackRooks();
        i = blackQueenRookPositions & ~(blackQueenRookPositions - 1);
        while (i != 0) {
            int currentPosition = Long.numberOfTrailingZeros(i);
            option = SlidingPieces.horizonralAndVerticalMoves(currentPosition, b.getAllPieces());
            attackedPositions |= option;
            blackQueenRookPositions &= ~i;
            i = blackQueenRookPositions & ~(blackQueenRookPositions - 1);
        }

        //king
        int currentPosition = Long.numberOfTrailingZeros(b.getBlackKing());
        if (currentPosition <= 9) {
            option = KING_B7 >> (9 - currentPosition);
        } else {
            option = KING_B7 << (currentPosition - 9);
        }
        if (currentPosition % 8 >= 4) {
            option &= ~COLUMN_AB;
        } else {
            option &= ~COLUMN_GH;
        }
        attackedPositions |= option;
        return attackedPositions;
    }

    public static long executeMoveforOneBitboard(long board, String move, char type) {
        if (Character.isDigit(move.charAt(3))) {
            int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));

            int end = (Character.getNumericValue(move.charAt(2)) * 8) + (Character.getNumericValue(move.charAt(3)));

            if (((board >>> start) & 1) == 1) {
                board &= ~(1L << start);
                board |= (1L << end);
            } else {
                board &= ~(1L << end);
            }

        } else if (move.charAt(3) == 'P') {
            //pawn promotion
            int start, end;
            if (Character.isUpperCase(move.charAt(2))) {
                start = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(0) - '0'] & RowMasks8[1]);
                end = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(1) - '0'] & RowMasks8[0]);
            } else {
                start = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(0) - '0'] & RowMasks8[6]);
                end = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(1) - '0'] & RowMasks8[7]);
            }
            if (type == move.charAt(2)) {
                board |= (1L << end);
            } else {
                board &= ~(1L << start);
                board &= ~(1L << end);
            }
        } else if (move.charAt(3) == 'E') {

            int start, end;
            //en passant
            if (move.charAt(2) == 'W') {
                start = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(0) - '0'] & RowMasks8[3]);
                end = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(1) - '0'] & RowMasks8[2]);
                board &= ~(ColumnMasks8[move.charAt(1) - '0'] & RowMasks8[3]);
            } else {
                start = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(0) - '0'] & RowMasks8[4]);
                end = Long.numberOfTrailingZeros(ColumnMasks8[move.charAt(1) - '0'] & RowMasks8[5]);
                board &= ~(ColumnMasks8[move.charAt(1) - '0'] & RowMasks8[4]);
            }
            if (((board >>> start) & 1) == 1) {
                board &= ~(1L << start);
                board |= (1L << end);
            }
        } else {
            System.out.print("ERROR: Invalid move");
        }
        return board;
    }

    public static long executeCastling(long rooks, long kings, String move, char type) {

        int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));

        //regular move
        if ((((kings >>> start) & 1) == 1) && (("7472".equals(move))
                || ("0406".equals(move))
                || ("0402".equals(move))
                || ("7476".equals(move)))) {
            if (type == 'R') {
                switch (move) {
                    case "7476":
                        rooks &= ~(1L << 63L);
                        rooks |= (1L << (63L - 2));
                        break;
                    case "7472":
                        rooks &= ~(1L << 56L);
                        rooks |= (1L << (56L + 3));
                        break;
                }
            } else {
                switch (move) {
                    case "0406":
                        rooks &= ~(1L << 7L);
                        rooks |= (1L << (7L - 2));
                        break;
                    case "0402":
                        rooks &= ~(1L << 0L);
                        rooks |= (1L << (0L + 3));
                        break;
                }
            }
        }
        return rooks;
    }

    public static String executeEnPassant(long board, String move) {
        if (Character.isDigit(move.charAt(3))) {

            int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));

            if ((Math.abs(move.charAt(0) - move.charAt(2)) == 2)
                    && (((board >>> start) & 1) == 1)) {
                char rank;
                if (move.charAt(0) > move.charAt(2)) {
                    rank = (char) (move.charAt(0) - 1);
                } else {
                    rank = (char) (move.charAt(0) + 1);
                }
                char file = move.charAt(1);

                return convertMoveDigitsToField(rank, file);
            }
        }
        return "-";
    }


    public static String convertMoveDigitsToField(char rank, char file) {
        char f = (char) (97 + (file - 48));
        int r = (Math.abs(rank - 48 - 8));
        return "" + f + r;
    }

    public static String convert4digitMoveToField(String move) {
        return convertMoveDigitsToField(move.charAt(0), move.charAt(1)) + "->" + convertMoveDigitsToField(move.charAt(2), move.charAt(3));
    }

    public static void print4digitMoveToField(String move) {
        System.out.println(convertMoveDigitsToField(move.charAt(0), move.charAt(1)) + "->" + convertMoveDigitsToField(move.charAt(2), move.charAt(3)));
    }


    public static String convertInternalMoveToGameserverMove(String moveBitboardPosition, Board b) {

        String move = "";

        //check for promotion
        if (moveBitboardPosition.charAt(3) == 'P') {
            if (b.isCurrentPlayerIsWhite()) {
                move += Character.toString((char) ((97 + (moveBitboardPosition.charAt(0) - 48))));
                move += "7";
                move += Character.toString((char) (97 + (moveBitboardPosition.charAt(1) - 48)));
                move += "8";
                move += Character.toString(moveBitboardPosition.charAt(2));
                return move;
            } else {
                move += Character.toString((char) ((97 + (moveBitboardPosition.charAt(0) - 48))));
                move += "2";
                move += Character.toString((char) (97 + (moveBitboardPosition.charAt(1) - 48)));
                move += "1";
                move += Character.toString(moveBitboardPosition.charAt(2));
                return move;
            }

        }

        if (moveBitboardPosition.charAt(3) == 'E') {
            return move;
        }

        move += (char) (97 + (moveBitboardPosition.charAt(1) - 48));
        move += (Math.abs(moveBitboardPosition.charAt(0) - 48 - 8));
        move += (char) (97 + (moveBitboardPosition.charAt(3) - 48));
        move += (Math.abs(moveBitboardPosition.charAt(2) - 48 - 8));
        return move;

    }

    /**
     * @param b
     * @return list of valid moves, i. e. moves after which own king is not in check
     */
    public static String validMoves(Board b) {
        String validMoves = "";
        String moves = ownPossibleMoves(b).replace("-", "");

        for (int i = 0; i < moves.length(); i += 4) {

            Board tempB = new Board(b);

            tempB.setWhitePawns(executeMoveforOneBitboard(b.getWhitePawns(), moves.substring(i, i + 4), 'P'));
            tempB.setWhiteKnights(executeMoveforOneBitboard(b.getWhiteKnights(), moves.substring(i, i + 4), 'N'));
            tempB.setWhiteBishops(executeMoveforOneBitboard(b.getWhiteBishops(), moves.substring(i, i + 4), 'B'));
            tempB.setWhiteRooks(executeMoveforOneBitboard(b.getWhiteRooks(), moves.substring(i, i + 4), 'R'));
            tempB.setWhiteQueen(executeMoveforOneBitboard(b.getWhiteQueen(), moves.substring(i, i + 4), 'Q'));
            tempB.setWhiteKing(executeMoveforOneBitboard(b.getWhiteKing(), moves.substring(i, i + 4), 'K'));
            tempB.setBlackPawns(executeMoveforOneBitboard(b.getBlackPawns(), moves.substring(i, i + 4), 'p'));
            tempB.setBlackKnights(executeMoveforOneBitboard(b.getBlackKnights(), moves.substring(i, i + 4), 'n'));
            tempB.setBlackBishops(executeMoveforOneBitboard(b.getBlackBishops(), moves.substring(i, i + 4), 'b'));
            tempB.setBlackRooks(executeMoveforOneBitboard(b.getBlackRooks(), moves.substring(i, i + 4), 'r'));
            tempB.setBlackQueen(executeMoveforOneBitboard(b.getBlackQueen(), moves.substring(i, i + 4), 'q'));
            tempB.setBlackKing(executeMoveforOneBitboard(b.getBlackKing(), moves.substring(i, i + 4), 'k'));
            tempB.setEnPassants(executeEnPassant(b.getWhitePawns() | b.getBlackPawns(), moves.substring(i, i + 4)));

            tempB.setWhiteRooks(executeCastling(tempB.getWhiteRooks(), b.getWhiteKing() | b.getBlackKing(), moves.substring(i, i + 4), 'R'));
            tempB.setBlackRooks(executeCastling(tempB.getBlackRooks(), b.getWhiteKing() | b.getBlackKing(), moves.substring(i, i + 4), 'r'));

            tempB.setWhiteToCastleKingside(b.isWhiteToCastleKingside());
            tempB.setWhiteToCastleQueenside(b.isWhiteToCastleQueenside());
            tempB.setBlackToCastleKingside(b.isBlackToCastleKingside());
            tempB.setBlackToCastleQueenside(b.isBlackToCastleQueenside());
            if (Character.isDigit(moves.charAt(3))) {
                int start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));
                if (((1L << start) & b.getWhiteKing()) != 0) {
                    tempB.setWhiteToCastleKingside(false);
                    tempB.setWhiteToCastleQueenside(false);
                }
                if (((1L << start) & b.getBlackKing()) != 0) {
                    tempB.setBlackToCastleKingside(false);
                    tempB.setBlackToCastleQueenside(false);
                }
                if (((1L << start) & b.getWhiteRooks() & (1L << 63)) != 0) {
                    tempB.setWhiteToCastleKingside(false);
                }
                if (((1L << start) & b.getWhiteRooks() & (1L << 56)) != 0) {
                    tempB.setWhiteToCastleQueenside(false);
                }
                if (((1L << start) & b.getBlackRooks() & (1L << 7)) != 0) {
                    tempB.setBlackToCastleKingside(false);
                }
                if (((1L << start) & b.getBlackRooks() & 1L) != 0) {
                    tempB.setBlackToCastleQueenside(false);
                }
            }

            tempB.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());

            //check if own King is NOT in danger after move
            if (((tempB.getWhiteKing() & fieldsAttackedByBlack(tempB)) == 0 && b.isCurrentPlayerIsWhite()) ||
                    ((tempB.getBlackKing() & fieldsAttackedByWhite(tempB)) == 0 && !b.isCurrentPlayerIsWhite())) {
                //add current move to validMoves if king is not in danger
                validMoves += moves.substring(i, i + 4);
            }
        }
        return validMoves;
    }

    /**
     * executes one moves for all bitboards of a given board
     *
     * @param b
     * @param move
     */
    public static void makeMove(Board b, String move) {
        //game is over
        if (move == null || move.equals("")) {
            if (b.isCurrentPlayerIsWhite()) {
                if ((fieldsAttackedByBlack(b) & b.getWhiteKing()) != 0) {//white king is attacked
                    b.setGameOver(true);
                    b.setWhiteWon(false);
                    return;
                }
            } else {
                if ((fieldsAttackedByWhite(b) & b.getBlackKing()) != 0) {
                    b.setGameOver(true);
                    b.setWhiteWon(true);
                    return;
                }
            }
            //no king is attacked -> remis
            b.setGameOver(true);
            b.setRemis(true);
            return;
        }

        //Debugging:
        String player = "";
        if (b.isCurrentPlayerIsWhite()) {
            player = "White";
        } else {
            player = "Black";
        }
        //System.out.println(player+" played: "+convertMoveDigitsToField(move.charAt(0),move.charAt(1))+"->"+convertMoveDigitsToField(move.charAt(2),move.charAt(3)));


        String oldFEN = b.bitboardsToFenParser().split(" ")[0];
        long oldWhitePawns = b.getWhitePawns();
        long oldBlackPawns = b.getBlackPawns();
        long oldWhiteKing = b.getWhiteKing();
        long oldBlackKing = b.getBlackKing();

        //Castling
        if (Character.isDigit(move.charAt(3))) {
            int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(0 + 1)));
            if (((1L << start) & b.getWhiteKing()) != 0) {
                b.setWhiteToCastleKingside(false);
                b.setWhiteToCastleQueenside(false);
            }
            if (((1L << start) & b.getBlackKing()) != 0) {
                b.setBlackToCastleKingside(false);
                b.setBlackToCastleQueenside(false);
            }
            if (((1L << start) & b.getWhiteRooks() & (1L << 63)) != 0) {
                b.setWhiteToCastleKingside(false);
            }
            if (((1L << start) & b.getWhiteRooks() & (1L << 56)) != 0) {
                b.setWhiteToCastleQueenside(false);
            }
            if (((1L << start) & b.getBlackRooks() & (1L << 7)) != 0) {
                b.setBlackToCastleKingside(false);
            }
            if (((1L << start) & b.getBlackRooks() & 1L) != 0) {
                b.setBlackToCastleQueenside(false);
            }
        }

        //En Passant
        //b.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),move));
        b.setEnPassants(executeEnPassant(b.getWhitePawns() | b.getBlackPawns(), move));

        b.setWhitePawns(executeMoveforOneBitboard(b.getWhitePawns(), move, 'P'));
        b.setWhiteKnights(executeMoveforOneBitboard(b.getWhiteKnights(), move, 'N'));
        b.setWhiteBishops(executeMoveforOneBitboard(b.getWhiteBishops(), move, 'B'));
        b.setWhiteRooks(executeMoveforOneBitboard(b.getWhiteRooks(), move, 'R'));
        b.setWhiteQueen(executeMoveforOneBitboard(b.getWhiteQueen(), move, 'Q'));
        b.setWhiteKing(executeMoveforOneBitboard(b.getWhiteKing(), move, 'K'));
        b.setBlackPawns(executeMoveforOneBitboard(b.getBlackPawns(), move, 'p'));
        b.setBlackKnights(executeMoveforOneBitboard(b.getBlackKnights(), move, 'n'));
        b.setBlackBishops(executeMoveforOneBitboard(b.getBlackBishops(), move, 'b'));
        b.setBlackRooks(executeMoveforOneBitboard(b.getBlackRooks(), move, 'r'));
        b.setBlackQueen(executeMoveforOneBitboard(b.getBlackQueen(), move, 'q'));
        b.setBlackKing(executeMoveforOneBitboard(b.getBlackKing(), move, 'k'));

        b.setWhiteRooks(executeCastling(b.getWhiteRooks(), oldWhiteKing | oldBlackKing, move, 'R'));
        b.setBlackRooks(executeCastling(b.getBlackRooks(), oldWhiteKing | oldBlackKing, move, 'r'));

        b.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());

        //pawn was moved or captured check
        if (b.getWhitePawns() != oldWhitePawns || b.getBlackPawns() != oldBlackPawns) {
            b.setHalfMoveCount(-1);
            //figure captured check
        } else {
            String newFEN = b.bitboardsToFenParser().split(" ")[0];
            int oldFigureCount = 0;
            for (int i = 0; i < oldFEN.length(); i++) {
                if (oldFEN.charAt(i) == 'r' || oldFEN.charAt(i) == 'n' || oldFEN.charAt(i) == 'b' || oldFEN.charAt(i) == 'k' || oldFEN.charAt(i) == 'q' || oldFEN.charAt(i) == 'p' || oldFEN.charAt(i) == 'P' || oldFEN.charAt(i) == 'R' || oldFEN.charAt(i) == 'N' || oldFEN.charAt(i) == 'B' || oldFEN.charAt(i) == 'K' || oldFEN.charAt(i) == 'Q') {
                    oldFigureCount++;
                }
            }
            int newFigureCount = 0;
            for (int i = 0; i < newFEN.length(); i++) {
                if (newFEN.charAt(i) == 'r' || newFEN.charAt(i) == 'n' || newFEN.charAt(i) == 'b' || newFEN.charAt(i) == 'k' || newFEN.charAt(i) == 'q' || newFEN.charAt(i) == 'p' || newFEN.charAt(i) == 'P' || newFEN.charAt(i) == 'R' || newFEN.charAt(i) == 'N' || newFEN.charAt(i) == 'B' || newFEN.charAt(i) == 'K' || newFEN.charAt(i) == 'Q') {
                    newFigureCount++;
                }
            }
            if (oldFigureCount != newFigureCount) b.setHalfMoveCount(-1);
        }


        if (b.isCurrentPlayerIsWhite()) {
            b.setHalfMoveCount(b.getHalfMoveCount() + 1);

        } else {
            b.setHalfMoveCount(b.getHalfMoveCount() + 1);
            b.setNextMoveCount(b.getNextMoveCount() + 1); //"full" move after black
        }

        //Check if King is in center
        if ((CENTRE & b.getWhiteKing()) != 0) {
            b.setGameOver(true);
            b.setWhiteWon(true);
            return;
        }
        if ((CENTRE & b.getBlackKing()) != 0) {
            b.setGameOver(true);
            b.setWhiteWon(false);
            return;
        }

        //50-Moves-Remis-Rule
        if (b.getHalfMoveCount() >= 100) {
            b.setGameOver(true);
            b.setRemis(true);
        }

    }

    public static String moveSelector(Board b, String validMoves, long usedTimeInMs) {
        if (validMoves == null || validMoves.equals("")) {
            return validMoves;
        }

        //sortMovesCaptures(b,validMoves);

        if ((gameTimeLimit - usedTimeInMs) < panicModeTimeBuffer) {
            System.out.println("Entered panic mode");
            int depth = 2;
            return alphaBeta(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true).substring(0, 4);
        }

        System.out.println("Using principal variation search for move generation");
        //Iterative Deepening Search + PVS (mit PV Zugsortierung)
        long timeLimit = standardDeviationTimeLimit(b.getNextMoveCount(), 300L); //gamePhaseTimeLimit(b, 250);
        return PrincipalVariationSearch.principalVariationSearchWithTimelimit(b, timeLimit).substring(0, 4);

        //System.out.println("Using alpha beta search for move generation");
        //return alphaBeta(b, suchtiefe, Integer.MIN_VALUE, Integer.MAX_VALUE, true).substring(0, 4);
        //String bestMoveFromMinMax = minMax(b, suchtiefe, true).substring(0,4);

    }


    public static long standardDeviationTimeLimit(int NextMoveCount, long minTimeLimit) {
        long timeLimit = minTimeLimit;
        if (NextMoveCount < timeDistribution.length) {
            timeLimit += timeDistribution[NextMoveCount];
        }
        return timeLimit;
    }

    public static long gamePhaseTimeLimit(Board b, long minTimeLimit) {
        long timeLimit = minTimeLimit;
        long phase = b.calcGamePhase();
        if (phase > 20 && phase < 50) {
            timeLimit += 2500;
        }
        if (phase > 50) {
            timeLimit += 1500;
        }
        return timeLimit;
    }

    /**
     * performs alphaBeta search in order to get the best move
     *
     * @param b
     * @param depth       maximal search depth
     * @param alpha       initialized with Integer.MIN_VALUE
     * @param beta        initialized with Integer.MAX_VALUE
     * @param isMaxPlayer
     * @return String in this format: XXXXY.. where XXXX is representation of move and Y.. value of assessment function
     */
    public static String alphaBeta(Board b, int depth, int alpha, int beta, boolean isMaxPlayer) {
        if (depth == 0) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if (b.getCreatedByMove().equals("")) return "9999" + score;
            return b.getCreatedByMove() + score;
        }
        String moveList = validMoves(b);
        if (b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if (b.getCreatedByMove().equals("")) return "9999" + score;
            return b.getCreatedByMove() + score;
        }
        if (isMaxPlayer) {
            String bestMove = "9999"; //prevents NullPointerException, value will be overwritten
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                int currentEval = Integer.parseInt(alphaBeta(newBoard, depth - 1, alpha, beta, false).substring(4));
                if (currentEval > alpha) {
                    bestMove = move;
                    alpha = currentEval;
                }
                if (beta <= alpha) {
                    break; //beta-cutoff
                }
            }
            return bestMove + alpha;
        } else {
            String bestMove = "9999";
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                int currentEval = Integer.parseInt(alphaBeta(newBoard, depth - 1, alpha, beta, true).substring(4));
                if (currentEval < beta) {
                    bestMove = move;
                    beta = currentEval;
                }
                if (beta <= alpha) {
                    break; //alpha-cutoff
                }
            }
            return bestMove + beta;
        }
    }

    public static String minMax(Board b, int depth, boolean isMaxPlayer) {

        if (depth == 0) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            return b.getCreatedByMove() + score;
        }
        String moveList = validMoves(b);
        if (b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            return b.getCreatedByMove() + score;
        }
        if (isMaxPlayer) {
            String bestMove = "";
            int max = -Integer.MAX_VALUE;
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                String temp = minMax(newBoard, depth - 1, false);
                int currentEval = Integer.parseInt(temp.substring(4));
                if (currentEval > max) {
                    bestMove = move;
                    max = currentEval;
                }
            }
            return bestMove + max;
        } else {
            String bestMove = "";
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < moveList.length(); i += 4) {
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                int currentEval = Integer.parseInt(minMax(newBoard, depth - 1, true).substring(4));
                if (currentEval < min) {
                    bestMove = move;
                    min = currentEval;
                }
            }
            return bestMove + min;
        }
    }

    /**
     * performs iterative deepening search (without move ordering) until timeLimit exceeds
     *
     * @param b
     * @param timeLimit
     * @return
     */
    public static String iterativeDeepeningSearch(Board b, long timeLimit) {
        System.out.println("Starting iterative deepening search with time limit: " + timeLimit);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        int depth = 1;
        String bestMoveSoFar = "";
        outOfTime = false;

        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }
            long newTimeLimit = endTime - currentTime;
            String result = alphaBetaTimeLimit(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, currentTime, newTimeLimit);
            if (!outOfTime) { //only consider completed iterations
                bestMoveSoFar = result;
                System.out.println("depth was: " + depth);
            }
            depth++;
        }
        return bestMoveSoFar;
    }

    public static String alphaBetaTimeLimit(Board b, int depth, int alpha, int beta, boolean isMaxPlayer, long startTime, long timeLimit) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime);
        if (elapsedTime >= timeLimit) {
            outOfTime = true;
        }
        if (depth == 0 || outOfTime) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if (outOfTime) {
                System.out.println("Out of Time!");
            }
            if (b.getCreatedByMove().equals(""))
                return "9999" + score; //um mögliche Errors zu vermeiden (kann nur == "" wenn mit Suchtiefe 0 gestartet -> kommt im richtigen Spiel nciht vor)
            return b.getCreatedByMove() + score;
        }
        String moveList = validMoves(b);
        if (outOfTime || b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if (outOfTime) {
                System.out.println("Out of Time! (after Move Generation)");
            }
            if (b.getCreatedByMove().equals("")) return "9999" + score;
            return b.getCreatedByMove() + score;
        }
        if (isMaxPlayer) {
            String bestMove = "9999";
            for (int i = 0; i < moveList.length(); i += 4) {
                if (outOfTime) break;
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                String temp = alphaBetaTimeLimit(newBoard, depth - 1, alpha, beta, false, startTime, timeLimit);
                int currentEval = Integer.parseInt(temp.substring(4));
                if (currentEval > alpha) {
                    bestMove = move;
                    alpha = currentEval;
                }
                if (beta <= alpha) {
                    cutoffs++;
                    break; //beta-cutoff
                }

            }
            return bestMove + alpha;
        } else {
            String bestMove = "9999";
            for (int i = 0; i < moveList.length(); i += 4) {
                if (outOfTime) break;
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                String temp = alphaBetaTimeLimit(newBoard, depth - 1, alpha, beta, true, startTime, timeLimit);
                int currentEval = Integer.parseInt(temp.substring(4));
                if (currentEval < beta) {
                    bestMove = move;
                    beta = currentEval;
                }
                if (beta <= alpha) {
                    cutoffs++;
                    break; //alpha-cutoff
                }
            }
            return bestMove + beta;
        }
    }

    //based on https://www.researchgate.net/publication/297377298_Verified_Null-Move_Pruning
    public static String alphaBetaNullMoveTimeLimit(Board b, int depth, int alpha, int beta, boolean isMaxPlayer, long startTime, long timeLimit) {
        int R = 2; //
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime);
        if (elapsedTime >= timeLimit) {
            outOfTime = true;
        }

        if (depth == 0 || outOfTime) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if (outOfTime) {
                System.out.println("Out of Time!");
            }
            //System.out.println("b currently assessed, was created by move" + b.getCreatedByMove());
            //System.out.println(b.getCreatedByMove() + score);
            if (b.getCreatedByMove().equals("")) return "9999" + score;
            return b.getCreatedByMove() + score;
        }

        //here happens the null move magic
        if (!b.isInCheck() && b.nullMoveOk()) {
            b.setKIPlaysWhite(!b.isKIPlayingWhite());
            b.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());

            int value = Integer.parseInt(alphaBetaNullMoveTimeLimit(b, depth - R - 1, -beta, -beta + 1, !isMaxPlayer, startTime, timeLimit).substring(4));

            if (value >= beta) {
                cutoffs++;
                return b.getCreatedByMove() + value;
            }


        }

        String moveList = validMoves(b);

        if (outOfTime || b.isGameOver() || moveList.equals("")) {
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if (outOfTime) {
                System.out.println("Out of Time! (after Move Generation)");
            }
            //System.out.println("b currently assessed, was created by move" + b.getCreatedByMove());
            //System.out.println(b.getCreatedByMove() + score);
            if (b.getCreatedByMove().equals("")) return "9999" + score;
            return b.getCreatedByMove() + score;
        }


        if (isMaxPlayer) {
            String bestMove = "9999";
            for (int i = 0; i < moveList.length(); i += 4) {
                if (outOfTime) break;
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String zwischenergebnis = alphaBetaTimeLimit(newBoard, depth - 1, alpha, beta, false, startTime, timeLimit);

                int currentEval = Integer.parseInt(zwischenergebnis.substring(4));

                if (currentEval > alpha) {
                    bestMove = move;
                    alpha = currentEval;
                }

                if (beta <= alpha) {
                    cutoffs++;
                    break;
                }
            }
            return bestMove + alpha;
        } else {
            String bestMove = "9999";
            for (int i = 0; i < moveList.length(); i += 4) {
                if (outOfTime) break;
                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String zwischenergebnis = alphaBetaTimeLimit(newBoard, depth - 1, alpha, beta, true, startTime, timeLimit);

                int currentEval = Integer.parseInt(zwischenergebnis.substring(4));
                if (currentEval < beta) {
                    bestMove = move;
                    beta = currentEval;
                }

                if (beta <= alpha) {
                    cutoffs++;
                    break;
                }
            }

            return bestMove + beta;
        }
    }


    /**
     * performs iterative deepening search (without move ordering) until a given maximal depth
     * method is used for benchmark comparison with principal variation search
     *
     * @param b
     * @param depth
     * @param isMaxPlayer
     * @return
     */
    public static String iterativeDeepeningSearchNoTimeLimit(Board b, int depth, boolean isMaxPlayer) {
        String result = "";
        for (int i = 1; i <= depth; i++) {
            result = alphaBeta(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);

        }
        return result;

    }

    public static String quiescenceSearch(Board b, int alpha, int beta, boolean isMaxPlayer) {
        String moveList = validMoves(b);

        if (isMaxPlayer) {
            String bestMove = "";

            //recursion anchor
            if (b.assessBoardFromOwnPerspective() >= beta || moveList.equals("")) {
                //System.out.println("Finished quiescence search with evalValue: "+ b.assessBoardFromOwnPerspective() +" >= alpha: "+alpha);
                assessedLeaves++;
                //System.out.println("returning move "+b.getCreatedByMove()+" from qs");
                return b.getCreatedByMove() + beta;
            }
            quiescenceSearchIterations++;
            //System.out.println("Using quiescence search (evalValue:"+b.assessBoardFromOwnPerspective()+"(>=) alpha: "+alpha+")");
            alpha = Math.max(alpha, b.assessBoardFromOwnPerspective());

            for (int i = 0; i < moveList.length(); i += 4) {

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String intermediateResult = quiescenceSearch(newBoard, alpha, beta, false);
                int currentEval = Integer.parseInt(intermediateResult.substring(4));

                if (currentEval >= alpha) {
                    bestMove = move;
                    alpha = currentEval;
                }

                if (bestMove.equals("")) {
                    bestMove = "9999";
                }

                alpha = Math.max(alpha, currentEval);
                //System.out.println("new alpha: "+alpha);

            }

            return bestMove + alpha;

        } else {

            //recursion anchor
            if (b.assessBoardFromOwnPerspective() <= alpha || moveList.equals("")) {
                //System.out.println("Finished quiescence search with evalValue: "+ b.assessBoardFromOwnPerspective() +" <= beta: "+beta);
                assessedLeaves++;
                return b.getCreatedByMove() + alpha;
            }
            quiescenceSearchIterations++;
            //System.out.println("Using quiescence search (evalValue:"+b.assessBoardFromOwnPerspective()+"(<=) beta: "+beta+")");
            beta = Math.min(beta, b.assessBoardFromOwnPerspective());

            String bestMove = "";
            for (int i = 0; i < moveList.length(); i += 4) {

                String move = moveList.substring(i, i + 4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String intermediateResult = quiescenceSearch(newBoard, alpha, beta, true);

                int currentEval = Integer.parseInt(intermediateResult.substring(4));

                if (currentEval <= beta) {
                    bestMove = move;
                    beta = currentEval;
                }

                if (bestMove.equals("")) {
                    bestMove = "9999";
                }

                beta = Math.min(beta, currentEval);
                //System.out.println("new beta: "+beta);

            }
            return bestMove + beta;
        }
    }


    public static int getAssessedLeaves() {
        return assessedLeaves;
    }

    public static void setAssessedLeaves(int assessedLeavesNew) {
        assessedLeaves = assessedLeavesNew;
    }

    public static int getMoveCount(String list) {

        return (list.replace("-", "").length() / 4);

    }

    public static int getQuiescenceSearchIterations() {
        return quiescenceSearchIterations;
    }

    public static void setQuiescenceSearchIterations(int quiescenceSearchIterations) {
        MoveGenerator.quiescenceSearchIterations = quiescenceSearchIterations;
    }

    public static void setCutoffs(int cutoffs) {
        MoveGenerator.cutoffs = cutoffs;
    }

    public static int getCutoffs() {
        return cutoffs;
    }

    public String checkBishopMoves(Board board) {

        return slidingPieces.bishopMoves(board);

    }

    public String checkRookMoves(Board board) {

        return slidingPieces.rookMoves(board);
    }

    public String checkQueenMoves(Board board) {

        return slidingPieces.queenMoves(board);
    }


}
