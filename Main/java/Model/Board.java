package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static Model.Mask.*;
import static Model.MoveGenerator.*;

public class Board implements Comparable <Board> {

    private boolean gameOver = false;
    private boolean whiteWon = false;
    private boolean remis = false;
    private boolean KIPlaysWhite = true;
    private boolean currentPlayerIsWhite = false;
    private int halfMoveCount = 0;
    private int nextMoveCount = 0;
    private boolean whiteToCastleKingside = false;
    private boolean whiteToCastleQueenside = false;
    private boolean blackToCastleKingside = false;
    private boolean blackToCastleQueenside = false;
    private String enPassants = "";
    private long
            whiteKing=0L,
            whiteQueen=0L,
            whiteKnights=0L,
            whiteBishops=0L,
            whiteRooks=0L,
            whitePawns=0L,
            blackKing=0L,
            blackQueen=0L,
            blackKnights=0L,
            blackBishops=0L,
            blackRooks=0L,
            blackPawns=0L;

    private ArrayList<Board> successorBoards = new ArrayList<>();
    private int assessmentValue = Integer.MAX_VALUE;
    private String createdByMove = "";
    private boolean inCheck = false;


    /**
     * Overloaded constructors - allows to create a board from a fen string
      * or to create a new board based on an existing board and a move
     */
    public Board(String fenString) {
        this.fenToBitboardParser(fenString);
    }

    /**
     * Takes a fen string and sets all bitboards and flags accordingly
     */
    public void fenToBitboardParser(String fenString) throws IllegalArgumentException {

        String positions = "";
        String emptyMask ="0000000000000000000000000000000000000000000000000000000000000000";

        if (fenString.contains(" ")) {
            // remove and evaluate all meta information from the fen string
            String[] fenStringParts = fenString.split(" ");

            if (fenStringParts.length != 6){
                throw new IllegalArgumentException("fenString " + fenString + " contains more or less than five ' '");
            }
            else{
                //get positions
                positions = fenStringParts[0];

                //get current color
                if (fenStringParts[1].charAt(0) == 'w') currentPlayerIsWhite = true;

                //get castling status
                if (fenStringParts[2].contains("K")) whiteToCastleKingside = true;
                if (fenStringParts[2].contains("Q")) whiteToCastleQueenside = true;
                if (fenStringParts[2].contains("k")) blackToCastleKingside = true;
                if (fenStringParts[2].contains("q")) blackToCastleQueenside = true;

                //get en passant moves
                if (!fenStringParts[3].contains(" ")) enPassants = fenStringParts[3];

                //get number of "half" moves
                halfMoveCount = Short.valueOf(fenStringParts[4]);

                //get number of "next" move
                nextMoveCount = Short.valueOf(fenStringParts[5]);
            }


        } else {
            throw new IllegalArgumentException("fenString " + fenString + " does not contain ' '");
        }

        for (int i=0, k = 0; i<positions.length(); i++) {
            //i is the iterator index for the fen string, k the iterator position for the chess field position

            //Debugging
            //System.out.println("starting to parse "+fenString.charAt(i));

            //build a board mask that points to the current board position
            String boardMask = emptyMask.substring(k+1) + "1" + emptyMask.substring(0, k);

            switch (fenString.charAt(i)) {

                //remove line breaks from fen string
                case '/':
                    continue;

                    //identify the respective piece board and add the piece - WHITE/black
                case 'K': this.whiteKing = this.whiteKing | parseStringToBitboard(boardMask);
                    break;
                case 'Q': this.whiteQueen = this.whiteQueen | parseStringToBitboard(boardMask);
                    break;
                case 'N': this.whiteKnights = this.whiteKnights | parseStringToBitboard(boardMask);
                    break;
                case 'B': this.whiteBishops = this.whiteBishops | parseStringToBitboard(boardMask);
                    break;
                case 'R': this.whiteRooks = this.whiteRooks | parseStringToBitboard(boardMask);
                    break;
                case 'P': this.whitePawns = this.whitePawns | parseStringToBitboard(boardMask);
                    break;

                case 'k': this.blackKing = this.blackKing | parseStringToBitboard(boardMask);
                    break;
                case 'q': this.blackQueen = this.blackQueen | parseStringToBitboard(boardMask);
                    break;
                case 'p': this.blackPawns = this.blackPawns | parseStringToBitboard(boardMask);
                    break;
                case 'n': this.blackKnights = this.blackKnights | parseStringToBitboard(boardMask);
                    break;
                case 'b': this.blackBishops = this.blackBishops | parseStringToBitboard(boardMask);
                    break;
                case 'r': this.blackRooks = this.blackRooks | parseStringToBitboard(boardMask);
                    break;

                //if no line break or piece was detected jump to the next board position
                default: k += (Character.getNumericValue(fenString.charAt(i)) -1);
                    break;
            }

            //move to the next board position
            k++;

        }
        //this.getAssessmentValue();

    }

    public Board (Board b){
        this.whitePawns = b.whitePawns;
        this.blackPawns = b.blackPawns;
        this.whiteBishops = b.whiteBishops;
        this.blackBishops = b.blackBishops;
        this.whiteKnights = b.whiteKnights;
        this.blackKnights = b.blackKnights;
        this.whiteRooks = b.whiteRooks;
        this.blackRooks = b.blackRooks;
        this.whiteKing = b.whiteKing;
        this.blackKing = b.blackKing;
        this.whiteQueen = b.whiteQueen;
        this.blackQueen = b.blackQueen;
        this.KIPlaysWhite = b.KIPlaysWhite;
        this.currentPlayerIsWhite = b.currentPlayerIsWhite;
        this.nextMoveCount = b.nextMoveCount;
        this.halfMoveCount = b.halfMoveCount;
        this.whiteToCastleKingside = b.whiteToCastleKingside;
        this.blackToCastleKingside = b.blackToCastleKingside;
        this.whiteToCastleQueenside = b.whiteToCastleQueenside;
        this.blackToCastleQueenside = b.blackToCastleQueenside;
        this.enPassants = b.enPassants;
        this.assessmentValue = Integer.MAX_VALUE;

    }

    /**
     * parses a String boardMask to its binary representation in form of a 64bit long
     */
    public long parseStringToBitboard (String boardMask) {
        //handle the sign bit at index 0
        if (boardMask.charAt(0)=='0') {
            return Long.parseLong(boardMask, 2);
        } else {
            return Long.parseLong("1" + boardMask.substring(2), 2)*2;
        }
    }

    public Board createBoardFromMove (String move){
        Board newBoard = new Board(this);
        //System.out.println("MakeMove: "+move);
        if (!this.isKIPlayingWhite()){
            newBoard.setKIPlaysWhite(false);
        }
        makeMove(newBoard, move);
        //newBoard.assessBoardTPT(assesedBoards, zobrist);
        return newBoard;}


    /**
     * Sets and returns the assessment value for the board
     * The board is assessed from the perspective of the current player
     */
    public int assessBoard(){

        this.assessmentValue = 0;

        String ownValidMoves = MoveGenerator.validMoves(this);

        //create board with same positions but opponents's turn to count their moves
        Board copyButOpponentsTurn = new Board(this.bitboardsToFenParser());
        copyButOpponentsTurn.setCurrentPlayerIsWhite(!copyButOpponentsTurn.isCurrentPlayerIsWhite());
        String opponentsValidMoves = MoveGenerator.validMoves(copyButOpponentsTurn);


        // define assessment values for certain positions
        int kingInExtendedCenter = 300;

        if((fieldsAttackedByBlack(this) & this.getWhiteKing()) != 0)
        {
            if(currentPlayerIsWhite){
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            } else {
                this.assessmentValue = 1000000;
            }
        }
        if((fieldsAttackedByWhite(this) & this.getBlackKing())!=0){
            if (currentPlayerIsWhite) {
                this.assessmentValue = 1000000;
            } else {
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            }
        }
        if ((this.getOwnKing() & CENTRE) != 0){
            return this.assessmentValue = 10000000; //player has won
        }
        if ((this.getOppositeKing() & CENTRE) != 0){
            return this.assessmentValue = -10000000; //player has lost
        }

        //Count material
        this.assessmentValue += Long.bitCount(this.getOwnPawns())-Long.bitCount(this.getOppositePawns())*100;
        this.assessmentValue += (Long.bitCount(this.getOwnKnights())-Long.bitCount(this.getOppositeKnights()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnRooks())-Long.bitCount(this.getOppositeRooks()))*500;
        this.assessmentValue += (Long.bitCount(this.getOwnBishops())-Long.bitCount(this.getOppositeBishops()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnQueen())-Long.bitCount(this.getOppositeQueen()))*900;

        if ((getOwnKing() & EXTENDED_CENTRE) != 0){
            this.assessmentValue += kingInExtendedCenter;
        }

        //Mobility
        this.assessmentValue += (int) MoveGenerator.getMoveCount(ownValidMoves)*10;
        this.assessmentValue -= (int) MoveGenerator.getMoveCount(opponentsValidMoves)*10;

        //Attacked Pieces
        if (currentPlayerIsWhite){
            long attackedPawns = Long.bitCount((fieldsAttackedByBlack(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByBlack(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByWhite(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByWhite(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        } else {
            long attackedPawns = Long.bitCount((fieldsAttackedByWhite(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByWhite(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByBlack(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByBlack(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        }

        //Hanging Pieces
        if(currentPlayerIsWhite){
            long hangingPawns = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        } else {
            long hangingPawns = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        }

        //Doubled pawns
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOwnPawns())>1){
                this.assessmentValue -= 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOppositePawns())>1){
                this.assessmentValue += 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }

        //PST Tapered Eval (dependent on game phase)
        int pstScoreWhite = 0;
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhitePawns(), PieceSquareTables.MG_WHITE_PAWNS, PieceSquareTables.EG_WHITE_PAWNS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteKnights(), PieceSquareTables.MG_WHITE_KNIGHTS, PieceSquareTables.EG_WHITE_KNIGHTS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteBishops(), PieceSquareTables.MG_WHITE_BISHOPS, PieceSquareTables.EG_WHITE_BISHOPS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteRooks(), PieceSquareTables.MG_WHITE_ROOKS, PieceSquareTables.EG_WHITE_ROOKS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteQueen(), PieceSquareTables.MG_WHITE_QUEEN, PieceSquareTables.EG_WHITE_QUEEN);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteKing(), PieceSquareTables.MG_WHITE_KING, PieceSquareTables.EG_WHITE_KING);
        int pstScoreBlack = 0;
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackPawns(), PieceSquareTables.MG_BLACK_PAWNS, PieceSquareTables.EG_BLACK_PAWNS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackKnights(), PieceSquareTables.MG_BLACK_KNIGHTS, PieceSquareTables.EG_BLACK_KNIGHTS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackBishops(), PieceSquareTables.MG_BLACK_BISHOPS, PieceSquareTables.EG_BLACK_BISHOPS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackRooks(), PieceSquareTables.MG_BLACK_ROOKS, PieceSquareTables.EG_BLACK_ROOKS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackQueen(), PieceSquareTables.MG_BLACK_QUEEN, PieceSquareTables.EG_BLACK_QUEEN);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackKing(), PieceSquareTables.MG_BLACK_KING, PieceSquareTables.EG_BLACK_KING);
        if(currentPlayerIsWhite){
            this.assessmentValue += (pstScoreWhite-pstScoreBlack);
        } else {
            this.assessmentValue += (pstScoreBlack-pstScoreWhite);
        }

        return (int)this.assessmentValue;
    }

    public int assessBoardTPT(HashMap<Long,Integer> assesedBoards, Zobrist z){

        this.assessmentValue = 0;

        long hashedBord = z.getZobristHash(this.getWhitePawns(),this.getWhiteKnights(),this.getWhiteBishops(),this.getWhiteRooks(),this.getWhiteQueen(),this.getWhiteKing(),this.getBlackPawns(),this.getBlackKnights(),this.getBlackBishops(),this.getBlackRooks(),this.getBlackQueen(),this.getBlackKing(),this.isWhiteToCastleKingside(),this.isWhiteToCastleQueenside(),this.isBlackToCastleKingside(),this.isBlackToCastleQueenside(),this.isCurrentPlayerIsWhite());

        if (assesedBoards.containsKey(hashedBord)){
            //System.out.println("FoundValue");
            this.assessmentValue = (int) assesedBoards.get(hashedBord);
            z.found++;
            return assesedBoards.get(hashedBord);
        }

        String ownValidMoves = MoveGenerator.validMoves(this);

        //create board with same positions but opponents's turn to count their moves
        Board copyButOpponentsTurn = new Board(this);
        copyButOpponentsTurn.setCurrentPlayerIsWhite(!copyButOpponentsTurn.isCurrentPlayerIsWhite());
        String opponentsValidMoves = MoveGenerator.validMoves(copyButOpponentsTurn);

        // define assessment values for certain positions
        int kingInExtendedCenter = 300;

        if((fieldsAttackedByBlack(this) & this.getWhiteKing()) != 0)
        {
            if(currentPlayerIsWhite){
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000;
                }
            } else {
                this.assessmentValue = 1000000;
            }
        }
        if((fieldsAttackedByWhite(this) & this.getBlackKing())!=0){
            if (currentPlayerIsWhite) {
                this.assessmentValue = 1000000;
            } else {
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000;
                }
            }
        }
        if ((this.getOwnKing() & CENTRE) != 0){
            return this.assessmentValue = 10000000;
        }
        if ((this.getOppositeKing() & CENTRE) != 0){
            return this.assessmentValue = -10000000;
        }

        //Count material
        this.assessmentValue += Long.bitCount(this.getOwnPawns())-Long.bitCount(this.getOppositePawns())*100;
        this.assessmentValue += (Long.bitCount(this.getOwnKnights())-Long.bitCount(this.getOppositeKnights()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnRooks())-Long.bitCount(this.getOppositeRooks()))*500;
        this.assessmentValue += (Long.bitCount(this.getOwnBishops())-Long.bitCount(this.getOppositeBishops()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnQueen())-Long.bitCount(this.getOppositeQueen()))*900;

        if ((getOwnKing() & EXTENDED_CENTRE) != 0){
            this.assessmentValue += kingInExtendedCenter;
        }

        //Mobility
        this.assessmentValue += (int) MoveGenerator.getMoveCount(ownValidMoves)*10;
        this.assessmentValue -= (int) MoveGenerator.getMoveCount(opponentsValidMoves)*10;

        //Attacked Pieces
        if (currentPlayerIsWhite){
            long attackedPawns = Long.bitCount((fieldsAttackedByBlack(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByBlack(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByWhite(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByWhite(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        } else {
            long attackedPawns = Long.bitCount((fieldsAttackedByWhite(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByWhite(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByBlack(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByBlack(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        }
        //Hanging Pieces
        if(currentPlayerIsWhite){
            long hangingPawns = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        } else {
            long hangingPawns = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        }

        //Doubled pawns
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOwnPawns())>1){
                this.assessmentValue -= 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOppositePawns())>1){
                this.assessmentValue += 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }

        //Choose either PST or PST Tapered Eval!
        //PST
        /*int pstScoreWhite = 0;
        pstScoreWhite += addPSTValues(this.getWhitePawns(), PieceSquareTables.MG_WHITE_PAWNS);
        pstScoreWhite += addPSTValues(this.getWhiteKnights(), PieceSquareTables.MG_WHITE_KNIGHTS);
        pstScoreWhite += addPSTValues(this.getWhiteBishops(), PieceSquareTables.MG_WHITE_BISHOPS);
        pstScoreWhite += addPSTValues(this.getWhiteRooks(), PieceSquareTables.MG_WHITE_ROOKS);
        pstScoreWhite += addPSTValues(this.getWhiteQueen(), PieceSquareTables.MG_WHITE_QUEEN);
        pstScoreWhite += addPSTValues(this.getWhiteKing(), PieceSquareTables.MG_WHITE_KING);
        int pstScoreBlack = 0;
        pstScoreBlack += addPSTValues(this.getBlackPawns(), PieceSquareTables.MG_BLACK_PAWNS);
        pstScoreBlack += addPSTValues(this.getBlackKnights(), PieceSquareTables.MG_BLACK_KNIGHTS);
        pstScoreBlack += addPSTValues(this.getBlackBishops(), PieceSquareTables.MG_BLACK_BISHOPS);
        pstScoreBlack += addPSTValues(this.getBlackRooks(), PieceSquareTables.MG_BLACK_ROOKS);
        pstScoreBlack += addPSTValues(this.getBlackQueen(), PieceSquareTables.MG_BLACK_QUEEN);
        pstScoreBlack += addPSTValues(this.getBlackKing(), PieceSquareTables.MG_BLACK_KING);
        if(currentPlayerIsWhite){
            this.assessmentValue += (pstScoreWhite-pstScoreBlack);
        } else {
            this.assessmentValue += (pstScoreBlack-pstScoreWhite);
        }*/

        //PST Tapered Eval (dependent on game phase)
        int pstScoreWhite = 0;
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhitePawns(), PieceSquareTables.MG_WHITE_PAWNS, PieceSquareTables.EG_WHITE_PAWNS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteKnights(), PieceSquareTables.MG_WHITE_KNIGHTS, PieceSquareTables.EG_WHITE_KNIGHTS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteBishops(), PieceSquareTables.MG_WHITE_BISHOPS, PieceSquareTables.EG_WHITE_BISHOPS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteRooks(), PieceSquareTables.MG_WHITE_ROOKS, PieceSquareTables.EG_WHITE_ROOKS);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteQueen(), PieceSquareTables.MG_WHITE_QUEEN, PieceSquareTables.EG_WHITE_QUEEN);
        pstScoreWhite += addPSTValuesTaperedEval(this.getWhiteKing(), PieceSquareTables.MG_WHITE_KING, PieceSquareTables.EG_WHITE_KING);
        int pstScoreBlack = 0;
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackPawns(), PieceSquareTables.MG_BLACK_PAWNS, PieceSquareTables.EG_BLACK_PAWNS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackKnights(), PieceSquareTables.MG_BLACK_KNIGHTS, PieceSquareTables.EG_BLACK_KNIGHTS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackBishops(), PieceSquareTables.MG_BLACK_BISHOPS, PieceSquareTables.EG_BLACK_BISHOPS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackRooks(), PieceSquareTables.MG_BLACK_ROOKS, PieceSquareTables.EG_BLACK_ROOKS);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackQueen(), PieceSquareTables.MG_BLACK_QUEEN, PieceSquareTables.EG_BLACK_QUEEN);
        pstScoreBlack += addPSTValuesTaperedEval(this.getBlackKing(), PieceSquareTables.MG_BLACK_KING, PieceSquareTables.EG_BLACK_KING);
        if(currentPlayerIsWhite){
            this.assessmentValue += (pstScoreWhite-pstScoreBlack);
        } else {
            this.assessmentValue += (pstScoreBlack-pstScoreWhite);
        }

        //System.out.println("InsertStart");
        assesedBoards.put(hashedBord,(int)this.assessmentValue);
        //System.out.println("InsertStop");
        return (int)this.assessmentValue;
    }


    public int assessBoardFromOwnPerspective() {

        if (KIPlaysWhite) {//momentan immer true
            if (currentPlayerIsWhite) {
                return this.getAssessmentValue();
            } else {
                return this.getAssessmentValue() * (-1);
            }
        } else {


            if (currentPlayerIsWhite) {
                return this.getAssessmentValue() * (-1);
            } else {
                return this.getAssessmentValue();
            }

        }
    }


    public int addPSTValues(long bitboard, int[] pst){
        int score = 0;
        while (bitboard != 0){
            score += pst[Long.numberOfTrailingZeros(bitboard)];
            bitboard = bitboard & bitboard-1;
        }
        return score;
    }

    static int figureCount = 32;
    static int gamePhase = 0;

    // https://www.chessprogramming.org/Tapered_Eval
    public int addPSTValuesTaperedEval(long bitboard, int[] pstMG, int[] pstEG){
        int phase = 50;//this.calcGamePhase();
        int newFigureCount = Long.bitCount(this.getAllPieces());
        if (newFigureCount == figureCount){
            phase = gamePhase;
        }else {
            phase = this.calcGamePhase();
            gamePhase = phase;
            figureCount = newFigureCount;
            //System.out.println("NewCalculated");
        }

        int opening = this.addPSTValues(bitboard, pstMG);
        int endgame = this.addPSTValues(bitboard, pstEG);
        int score = ((opening * (256 - phase)) + (endgame * phase)) / 256;
        return score;
    }

    /**
     * implemented according to: https://www.chessprogramming.org/Tapered_Eval
     *
     * @return game phase indicator, between 0 (opening/midgame) and 256 (late endgame)
     */
    public int calcGamePhase(){
        int pawnPhase = 0;
        int knightPhase = 1;
        int bishopPhase = 1;
        int rookPhase = 2;
        int queenPhase = 4;

        int totalPhase = 16*pawnPhase+4*(knightPhase+bishopPhase+rookPhase)+2*(queenPhase);
        int phase = totalPhase;

        phase -= pawnPhase * (Long.bitCount(this.getWhitePawns()) + Long.bitCount(this.getBlackPawns()));
        phase -= knightPhase * (Long.bitCount(this.getWhiteKnights()) + Long.bitCount(this.getBlackKnights()));
        phase -= bishopPhase * (Long.bitCount(this.getWhiteBishops()) + Long.bitCount(this.getBlackBishops()));
        phase -= rookPhase * (Long.bitCount(this.getWhiteRooks()) + Long.bitCount(this.getBlackRooks()));
        phase -= queenPhase * (Long.bitCount(this.getWhiteQueen()) + Long.bitCount(this.getBlackQueen()));

        if (phase < 0) phase = 0; //in case of early promotion

        phase = (phase * 256 + (totalPhase/2)) / totalPhase;

        return phase;
    }



    /**
     * Helpers and Getters
     */

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isWhiteWon() {
        return whiteWon;
    }

    public void setWhiteWon(boolean whiteWon) {
        this.whiteWon = whiteWon;
    }

    public boolean isRemis() {
        return remis;
    }

    public void setRemis(boolean remis) {
        this.remis = remis;
    }

    public boolean isCurrentPlayerIsWhite() {
        return currentPlayerIsWhite;
    }

    public void setCurrentPlayerIsWhite(boolean currentPlayerIsWhite) {
        this.currentPlayerIsWhite = currentPlayerIsWhite;
    }

    public boolean isKIPlayingWhite() {
        return KIPlaysWhite;
    }

    public boolean isKIPlaysWhite() {
        return KIPlaysWhite;
    }


    public long getOppositeKing() {
        if(isCurrentPlayerIsWhite()) {return blackKing;}
        else {
            return whiteKing;
        }
    }

    public long getOppositeQueen() {
        if(isCurrentPlayerIsWhite()) {return blackQueen;}
        else {
            return whiteQueen;
        }
    }

    public long getOppositeKnights() {
        if(isCurrentPlayerIsWhite()) {return blackKnights;}
        else {
            return whiteKnights;
        }
    }

    public long getOppositeBishops() {
        if(isCurrentPlayerIsWhite()) {return blackBishops;}
        else {
            return whiteBishops;
        }
    }

    public long getOppositeRooks() {
        if(isCurrentPlayerIsWhite()) {return blackRooks;}
        else {
            return whiteRooks;
        }
    }

    public long getOppositePawns() {
        if(isCurrentPlayerIsWhite()) {return blackPawns;}
        else {
            return whitePawns;
        }
    }

    public long getOwnKing() {
        if(isCurrentPlayerIsWhite()) {return whiteKing;}
        else {
            return blackKing;
        }
    }

    public long getOwnQueen() {
        if(isCurrentPlayerIsWhite()) {return whiteQueen;}
        else {
            return blackQueen;
        }
    }

    public long getOwnKnights() {
        if(isCurrentPlayerIsWhite()) {return whiteKnights;}
        else {
            return blackKnights;
        }
    }

    public long getOwnBishops() {
        if(isCurrentPlayerIsWhite()) {return whiteBishops;}
        else {
            return blackBishops;
        }
    }

    public long getOwnRooks() {
        if(isCurrentPlayerIsWhite()) {return whiteRooks;}
        else {
            return blackRooks;
        }
    }

    public long getOwnPawns() {
        if(isCurrentPlayerIsWhite()) {return whitePawns;}
        else {
            return blackPawns;
        }
    }

    public ArrayList<Board> getSuccessorBoards() {
        return successorBoards;
    }

    public String getCreatedByMove() {
        return createdByMove;
    }

    public void setCreatedByMove(String createdByMove) {
        this.createdByMove = createdByMove;
    }

    public int getAssessmentValue() {
        if (this.assessmentValue == Integer.MAX_VALUE){
            this.assessmentValue = assessBoardTPT(assesedBoards, zobrist);
            return this.assessmentValue;
        }
        return this.assessmentValue;
    }

    public void setWhiteKing(long whiteKing) {
        this.whiteKing = whiteKing;
    }

    public void setWhiteQueen(long whiteQueen) {
        this.whiteQueen = whiteQueen;
    }

    public void setWhiteKnights(long whiteKnights) {
        this.whiteKnights = whiteKnights;
    }

    public void setWhiteBishops(long whiteBishops) {
        this.whiteBishops = whiteBishops;
    }

    public void setWhiteRooks(long whiteRooks) {
        this.whiteRooks = whiteRooks;
    }

    public void setWhitePawns(long whitePawns) {
        this.whitePawns = whitePawns;
    }

    public void setBlackKing(long blackKing) {
        this.blackKing = blackKing;
    }

    public void setBlackQueen(long blackQueen) {
        this.blackQueen = blackQueen;
    }

    public void setBlackKnights(long blackKnights) {
        this.blackKnights = blackKnights;
    }

    public void setBlackBishops(long blackBishops) {
        this.blackBishops = blackBishops;
    }

    public void setBlackRooks(long blackRooks) {
        this.blackRooks = blackRooks;
    }

    public void setBlackPawns(long blackPawns) {
        this.blackPawns = blackPawns;
    }

    public long getBlackKing() {
        return blackKing;
    }

    public long getBlackQueen() {
        return blackQueen;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public long getWhiteKing() {
        return whiteKing;
    }

    public long getWhiteQueen() {
        return whiteQueen;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public int getHalfMoveCount() {
        return halfMoveCount;
    }

    public void setHalfMoveCount(int halfMoveCount) {
        this.halfMoveCount = halfMoveCount;
    }

    public int getNextMoveCount() {
        return nextMoveCount;
    }

    public void setNextMoveCount(int nextMoveCount) {
        this.nextMoveCount = nextMoveCount;
    }

    public boolean isWhiteToCastleKingside() {
        return whiteToCastleKingside;
    }

    public boolean isWhiteToCastleQueenside() {
        return whiteToCastleQueenside;
    }

    public boolean isBlackToCastleKingside() {
        return blackToCastleKingside;
    }

    public boolean isBlackToCastleQueenside() {
        return blackToCastleQueenside;
    }

    public void setWhiteToCastleKingside(boolean whiteToCastleKingside) {
        this.whiteToCastleKingside = whiteToCastleKingside;
    }

    public void setWhiteToCastleQueenside(boolean whiteToCastleQueenside) {
        this.whiteToCastleQueenside = whiteToCastleQueenside;
    }

    public void setBlackToCastleKingside(boolean blackToCastleKingside) {
        this.blackToCastleKingside = blackToCastleKingside;
    }

    public void setBlackToCastleQueenside(boolean blackToCastleQueenside) {
        this.blackToCastleQueenside = blackToCastleQueenside;
    }

    public String getEnPassants() {
        return enPassants;
    }

    public void setEnPassants(String enPassants) {
        this.enPassants = enPassants;
    }

    //TODO
    public boolean isInCheck() {
        return inCheck;
    }

    //TODO
    public boolean nullMoveOk() {
        return true;
    }

    /**
     * returns a long indicating the position of all white pieces
     */
    public long getWhitePieces() {
        return this.whiteKing | this.whiteQueen | this.whiteKnights | this.whiteBishops | this.whiteRooks | this.whitePawns;
    }

    /**
     * returns a long indicating the position of all black pieces
     */
    public long getBlackPieces() {
        return this.blackKing | this.blackQueen | this.blackKnights | this.blackBishops | this.blackRooks | this.blackPawns;
    }

    /**
     * returns a long indicating the position of all own pieces
     */
    public long getOwnPieces() {
        if (isKIPlayingWhite()){
            return this.whiteKing | this.whiteQueen | this.whiteKnights | this.whiteBishops | this.whiteRooks | this.whitePawns;
        } else {
            return this.blackKing | this.blackQueen | this.blackKnights | this.blackBishops | this.blackRooks | this.blackPawns;  
        }
    }

    /**
     * returns a long indicating the position of all opposite pieces
     */
    public long getOppositePieces() {
        if (!isKIPlayingWhite()){
            return this.whiteKing | this.whiteQueen | this.whiteKnights | this.whiteBishops | this.whiteRooks | this.whitePawns;
        } else {
            return this.blackKing | this.blackQueen | this.blackKnights | this.blackBishops | this.blackRooks | this.blackPawns;
        }
    }

    /**
     * returns a long indicating the position of all pieces
     */
    public long getAllPieces() {
        return this.getBlackPieces() | this.getWhitePieces();
    }

    /**
     * returns all empty fields
     */
    public long getEmptyFields() {
        return ~getAllPieces();
    }

    /**
     * Allows to increases the 'halfMoveCount' manually at the end of a move (for KI simulation)
     */
    public void increaseMoveCounts() {
        this.halfMoveCount++;
    }


    public String bitboardsToFenParser() {

        String chessBoard[][]=new String[8][8];
        for (int i=0;i<64;i++) {
            chessBoard[i/8][i%8]="1";
        }
        for (int i=0;i<64;i++) {
            if (((this.getWhitePawns()>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
            if (((this.getWhiteKnights()>>i)&1)==1) {chessBoard[i/8][i%8]="N";}
            if (((this.getWhiteBishops()>>i)&1)==1) {chessBoard[i/8][i%8]="B";}
            if (((this.getWhiteRooks()>>i)&1)==1) {chessBoard[i/8][i%8]="R";}
            if (((this.getWhiteQueen()>>i)&1)==1) {chessBoard[i/8][i%8]="Q";}
            if (((this.getWhiteKing()>>i)&1)==1) {chessBoard[i/8][i%8]="K";}
            if (((this.getBlackPawns()>>i)&1)==1) {chessBoard[i/8][i%8]="p";}
            if (((this.getBlackKnights()>>i)&1)==1) {chessBoard[i/8][i%8]="n";}
            if (((this.getBlackBishops()>>i)&1)==1) {chessBoard[i/8][i%8]="b";}
            if (((this.getBlackRooks()>>i)&1)==1) {chessBoard[i/8][i%8]="r";}
            if (((this.getBlackQueen()>>i)&1)==1) {chessBoard[i/8][i%8]="q";}
            if (((this.getBlackKing()>>i)&1)==1) {chessBoard[i/8][i%8]="k";}
        }


        //createFEN
        String fen = "";
        for (int i = 0;i<8;i++){
            for (int j = 0;j<8;j++){



                fen += chessBoard[i][j];


            }
            fen +="/";

        }

        String[] splittedFen = fen.split("/");

        String newFen ="";

        int counter =0;
        for (String fensplit : splittedFen){

            for (int i =0; i<8; i++){
                if(fensplit.charAt(i)=='1'){
                    counter++;
                } else {
                    if(counter>0){
                        newFen+= counter;
                        counter = 0;
                    }
                    newFen+=fensplit.charAt(i);

                }
            }
            if (counter!=0){
                newFen+=counter;
                counter=0;
            }
            newFen+="/";
        }

        newFen = newFen.substring(0,newFen.length()-1);

        if(this.isCurrentPlayerIsWhite()){
            newFen+=" w";
        } else {
            newFen+=" b";
        }

        String castleRights = "";
        if(this.whiteToCastleKingside) castleRights+= "K";
        if(this.whiteToCastleQueenside) castleRights+= "Q";
        if(this.blackToCastleKingside) castleRights+= "k";
        if(this.blackToCastleQueenside) castleRights+= "q";
        if (castleRights!=""){
            newFen+= " "+castleRights;
        } else {
            newFen +=" -";
        }

        newFen += " "+enPassants;

        newFen += " " +this.getHalfMoveCount();
        newFen += " " +this.getNextMoveCount();


        return newFen;
    }

    public void drawBoard() {

        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = " ";
        }
        for (int i = 0; i < 64; i++) {
            if (((this.getWhitePawns() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";
            }
            if (((this.getWhiteKnights() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "N";
            }
            if (((this.getWhiteBishops() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "B";
            }
            if (((this.getWhiteRooks() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "R";
            }
            if (((this.getWhiteQueen() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "Q";
            }
            if (((this.getWhiteKing() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "K";
            }
            if (((this.getBlackPawns() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "p";
            }
            if (((this.getBlackKnights() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "n";
            }
            if (((this.getBlackBishops() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "b";
            }
            if (((this.getBlackRooks() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "r";
            }
            if (((this.getBlackQueen() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "q";
            }
            if (((this.getBlackKing() >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "k";
            }
        }

        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }

    }

    @Override
    public int compareTo(Board o) {
        return Integer.compare(this.getAssessmentValue(), o.getAssessmentValue());
    }

    public void setKIPlaysWhite(boolean KIPlaysWhite) {
        this.KIPlaysWhite = KIPlaysWhite;
    }

    //TODO: only for benchmark comparison, not used by actual KI
    public int assessBoardStaticPST(){

        this.assessmentValue = 0;

        String ownValidMoves = MoveGenerator.validMoves(this);

        //create board with same positions but opponents's turn to count their moves
        Board copyButOpponentsTurn = new Board(this.bitboardsToFenParser());
        copyButOpponentsTurn.setCurrentPlayerIsWhite(!copyButOpponentsTurn.isCurrentPlayerIsWhite());
        String opponentsValidMoves = MoveGenerator.validMoves(copyButOpponentsTurn);


        // define assessment values for certain positions
        int kingInExtendedCenter = 300;

        if((fieldsAttackedByBlack(this) & this.getWhiteKing()) != 0)
        {
            if(currentPlayerIsWhite){
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            } else {
                this.assessmentValue = 1000000;
            }
        }
        if((fieldsAttackedByWhite(this) & this.getBlackKing())!=0){
            if (currentPlayerIsWhite) {
                this.assessmentValue = 1000000;
            } else {
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            }
        }
        if ((this.getOwnKing() & CENTRE) != 0){
            return this.assessmentValue = 10000000; //player has won
        }
        if ((this.getOppositeKing() & CENTRE) != 0){
            return this.assessmentValue = -10000000; //player has lost
        }

        //Count material
        this.assessmentValue += Long.bitCount(this.getOwnPawns())-Long.bitCount(this.getOppositePawns())*100;
        this.assessmentValue += (Long.bitCount(this.getOwnKnights())-Long.bitCount(this.getOppositeKnights()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnRooks())-Long.bitCount(this.getOppositeRooks()))*500;
        this.assessmentValue += (Long.bitCount(this.getOwnBishops())-Long.bitCount(this.getOppositeBishops()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnQueen())-Long.bitCount(this.getOppositeQueen()))*900;

        if ((getOwnKing() & EXTENDED_CENTRE) != 0){
            this.assessmentValue += kingInExtendedCenter;
        }

        //Mobility
        this.assessmentValue += (int) MoveGenerator.getMoveCount(ownValidMoves)*10;
        this.assessmentValue -= (int) MoveGenerator.getMoveCount(opponentsValidMoves)*10;

        //Attacked Pieces
        if (currentPlayerIsWhite){
            long attackedPawns = Long.bitCount((fieldsAttackedByBlack(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByBlack(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByWhite(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByWhite(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        } else {
            long attackedPawns = Long.bitCount((fieldsAttackedByWhite(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByWhite(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByBlack(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByBlack(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        }

        //Hanging Pieces
        if(currentPlayerIsWhite){
            long hangingPawns = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        } else {
            long hangingPawns = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        }

        //Doubled pawns
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOwnPawns())>1){
                this.assessmentValue -= 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOppositePawns())>1){
                this.assessmentValue += 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }

        //PST
        int pstScoreWhite = 0;
        pstScoreWhite += addPSTValues(this.getWhitePawns(), PieceSquareTables.MG_WHITE_PAWNS);
        pstScoreWhite += addPSTValues(this.getWhiteKnights(), PieceSquareTables.MG_WHITE_KNIGHTS);
        pstScoreWhite += addPSTValues(this.getWhiteBishops(), PieceSquareTables.MG_WHITE_BISHOPS);
        pstScoreWhite += addPSTValues(this.getWhiteRooks(), PieceSquareTables.MG_WHITE_ROOKS);
        pstScoreWhite += addPSTValues(this.getWhiteQueen(), PieceSquareTables.MG_WHITE_QUEEN);
        pstScoreWhite += addPSTValues(this.getWhiteKing(), PieceSquareTables.MG_WHITE_KING);
        int pstScoreBlack = 0;
        pstScoreBlack += addPSTValues(this.getBlackPawns(), PieceSquareTables.MG_BLACK_PAWNS);
        pstScoreBlack += addPSTValues(this.getBlackKnights(), PieceSquareTables.MG_BLACK_KNIGHTS);
        pstScoreBlack += addPSTValues(this.getBlackBishops(), PieceSquareTables.MG_BLACK_BISHOPS);
        pstScoreBlack += addPSTValues(this.getBlackRooks(), PieceSquareTables.MG_BLACK_ROOKS);
        pstScoreBlack += addPSTValues(this.getBlackQueen(), PieceSquareTables.MG_BLACK_QUEEN);
        pstScoreBlack += addPSTValues(this.getBlackKing(), PieceSquareTables.MG_BLACK_KING);
        if(currentPlayerIsWhite){
            this.assessmentValue += (pstScoreWhite-pstScoreBlack);
        } else {
            this.assessmentValue += (pstScoreBlack-pstScoreWhite);
        }

        return (int)this.assessmentValue;
    }

    public int assessBoardNoPST(){

        this.assessmentValue = 0;

        String ownValidMoves = MoveGenerator.validMoves(this);

        //create board with same positions but opponents's turn to count their moves
        Board copyButOpponentsTurn = new Board(this.bitboardsToFenParser());
        copyButOpponentsTurn.setCurrentPlayerIsWhite(!copyButOpponentsTurn.isCurrentPlayerIsWhite());
        String opponentsValidMoves = MoveGenerator.validMoves(copyButOpponentsTurn);


        // define assessment values for certain positions
        int kingInExtendedCenter = 300;

        if((fieldsAttackedByBlack(this) & this.getWhiteKing()) != 0)
        {
            if(currentPlayerIsWhite){
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            } else {
                this.assessmentValue = 1000000;
            }
        }
        if((fieldsAttackedByWhite(this) & this.getBlackKing())!=0){
            if (currentPlayerIsWhite) {
                this.assessmentValue = 1000000;
            } else {
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            }
        }
        if ((this.getOwnKing() & CENTRE) != 0){
            return this.assessmentValue = 10000000; //player has won
        }
        if ((this.getOppositeKing() & CENTRE) != 0){
            return this.assessmentValue = -10000000; //player has lost
        }

        //Count material
        this.assessmentValue += Long.bitCount(this.getOwnPawns())-Long.bitCount(this.getOppositePawns())*100;
        this.assessmentValue += (Long.bitCount(this.getOwnKnights())-Long.bitCount(this.getOppositeKnights()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnRooks())-Long.bitCount(this.getOppositeRooks()))*500;
        this.assessmentValue += (Long.bitCount(this.getOwnBishops())-Long.bitCount(this.getOppositeBishops()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnQueen())-Long.bitCount(this.getOppositeQueen()))*900;

        if ((getOwnKing() & EXTENDED_CENTRE) != 0){
            this.assessmentValue += kingInExtendedCenter;
        }

        //Mobility
        this.assessmentValue += (int) MoveGenerator.getMoveCount(ownValidMoves)*10;
        this.assessmentValue -= (int) MoveGenerator.getMoveCount(opponentsValidMoves)*10;

        //Attacked Pieces
        if (currentPlayerIsWhite){
            long attackedPawns = Long.bitCount((fieldsAttackedByBlack(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByBlack(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByWhite(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByWhite(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        } else {
            long attackedPawns = Long.bitCount((fieldsAttackedByWhite(this) & this.getOwnPawns()));
            long attackedQueens = Long.bitCount(fieldsAttackedByWhite(this) & this.getOwnQueen());
            long attackedPieces = Long.bitCount(fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue -= (150*attackedPieces+20*attackedPawns+400*attackedQueens);

            long attackedPawnsO = Long.bitCount((fieldsAttackedByBlack(this) & this.getOppositePawns()));
            long attackedQueensO = Long.bitCount(fieldsAttackedByBlack(this) & this.getOppositeQueen());
            long attackedPiecesO = Long.bitCount(fieldsAttackedByBlack(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            //System.out.println("APA" + attackedPawns + "AQ" + attackedQueens + "API" + attackedPieces);
            this.assessmentValue += (150*attackedPiecesO+20*attackedPawnsO+400*attackedQueensO);
        }

        //Hanging Pieces
        if(currentPlayerIsWhite){
            long hangingPawns = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        } else {
            long hangingPawns = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnPawns());
            long hangingPieces = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & (this.getOwnBishops() | this.getOwnKnights() | this.getOwnRooks()));
            long hangingQueen = Long.bitCount(~fieldsAttackedByBlack(this) &fieldsAttackedByWhite(this) & this.getOwnQueen());
            this.assessmentValue -= (300*hangingPieces+50*hangingPawns+700*hangingQueen);

            long hangingPawnsO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositePawns());
            long hangingPiecesO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & (this.getOppositeBishops() | this.getOppositeKnights() | this.getOppositeRooks()));
            long hangingQueenO = Long.bitCount(fieldsAttackedByBlack(this) &~fieldsAttackedByWhite(this) & this.getOppositeQueen());
            this.assessmentValue += (300*hangingPiecesO+50*hangingPawnsO+700*hangingQueenO);
        }

        //Doubled pawns
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOwnPawns())>1){
                this.assessmentValue -= 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }
        for (int i = 0; i<8; i++){
            if (Long.bitCount(FileMasks8[i]& this.getOppositePawns())>1){
                this.assessmentValue += 50;
                //System.out.println("Doppelbauer in" + i);
            }
        }
        return (int)this.assessmentValue;
    }

    public int assessBoardOnlyMaterial(){

        this.assessmentValue = 0;

        String ownValidMoves = MoveGenerator.validMoves(this);

        if((fieldsAttackedByBlack(this) & this.getWhiteKing()) != 0)
        {
            if(currentPlayerIsWhite){
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            } else {
                this.assessmentValue = 1000000;
            }
        }
        if((fieldsAttackedByWhite(this) & this.getBlackKing())!=0){
            if (currentPlayerIsWhite) {
                this.assessmentValue = 1000000;
            } else {
                this.assessmentValue = -1000000;
                if(ownValidMoves.equals("")){
                    this.assessmentValue = -10000000; //player is checkmate
                }
            }
        }
        if ((this.getOwnKing() & CENTRE) != 0){
            return this.assessmentValue = 10000000; //player has won
        }
        if ((this.getOppositeKing() & CENTRE) != 0){
            return this.assessmentValue = -10000000; //player has lost
        }

        //Count material
        this.assessmentValue += Long.bitCount(this.getOwnPawns())-Long.bitCount(this.getOppositePawns())*100;
        this.assessmentValue += (Long.bitCount(this.getOwnKnights())-Long.bitCount(this.getOppositeKnights()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnRooks())-Long.bitCount(this.getOppositeRooks()))*500;
        this.assessmentValue += (Long.bitCount(this.getOwnBishops())-Long.bitCount(this.getOppositeBishops()))*300;
        this.assessmentValue += (Long.bitCount(this.getOwnQueen())-Long.bitCount(this.getOppositeQueen()))*900;

        return (int)this.assessmentValue;
    }


}

