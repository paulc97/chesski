package Model;

import java.util.ArrayList;
import java.util.Arrays;

import static Model.Mask.CENTRE;
import static Model.Mask.EXTENDED_CENTRE;
import static Model.MoveGenerator.*;

public class Board implements Comparable <Board> {

    //TODO: get the info which color the KI is playing (from the game server)
    private boolean gameOver = false;
    private boolean whiteWon = false;
    private boolean remis = false;
    private boolean KIPlaysWhite = true;
    private boolean currentPlayerIsWhite = false;
    private int halfMoveCount = 0; //TODO: change to short again if wished
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
    private int assessmentValue = 0;
    private String createdByMove = "";

    //WIP EP start
    //ist File in der EnPassant möglich/erlaubt ist
    /*private long enPassantBitboardFile =0L;
    // eingefügt, um makeMove wie Video 16&17 zu machen
    //TODO: combine/integrate with privateString enPassants, fix BitboardGeneration correct


    public long getEnPassantBitboardFile() {
        return enPassantBitboardFile;
    }

    public void setEnPassantBitboardFile(long enPassantBitboardFile) {
        this.enPassantBitboardFile = enPassantBitboardFile;
    }*/
    //WIP EP ende

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
                halfMoveCount = (short)Character.getNumericValue(fenStringParts[4].charAt(0));

                //get number of "next" move
                nextMoveCount = (short)Character.getNumericValue(fenStringParts[5].charAt(0));
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
        Board newBoard = new Board(this.bitboardsToFenParser());
        makeMove(newBoard, move);
        return newBoard;}
        //TODO: vllt hier schon createdByMove(move) unterbringen?


    /**
     * Creates all possible successor boards for this board (1 level), including assessment and sorting by assessment
     */
    public void generateSuccessorBoards(Board b, String validMoves){
        for (int i=0;i<validMoves.length();i+=4) {
            String move = validMoves.substring(i,i+4);
            Board newBoard = createBoardFromMove(move);
            newBoard.setCreatedByMove(move);
            newBoard.assessBoard();
            successorBoards.add(newBoard);
        }
        successorBoards.sort(Board::compareTo);
    }


    /**
     * Sets and returns the assessment value for the board
     * The board is assessed from the perspective of the current player
     */
    public int assessBoard(){

        // define assessment values for certain positions
        int kingInExtendedCenter = 3;

        if((fieldsAttackedByBlack(this) & this.getWhiteKing()) != 0)
        {
            if(currentPlayerIsWhite){
            this.assessmentValue = -1000000;
            } else {
            this.assessmentValue = 1000000;
            }
        }
        if((fieldsAttackedByWhite(this) & this.getBlackKing())!=0){
            if (currentPlayerIsWhite) {
                this.assessmentValue = 1000000;
            } else {
                this.assessmentValue = -1000000;
            }
        }
        if ((this.getOwnKing() & CENTRE) != 0){
            return this.assessmentValue = 1000000;
        }
        if ((this.getOppositeKing() & CENTRE) != 0){
            return this.assessmentValue = -1000000;
        }

        //Count material
        this.assessmentValue += Long.bitCount(this.getOwnPawns())-Long.bitCount(this.getOppositePawns());
        this.assessmentValue += (Long.bitCount(this.getOwnKnights())-Long.bitCount(this.getOppositeKnights()))*3;
        this.assessmentValue += (Long.bitCount(this.getOwnRooks())-Long.bitCount(this.getOppositeRooks()))*5;
        this.assessmentValue += (Long.bitCount(this.getOwnBishops())-Long.bitCount(this.getOppositeBishops()))*3;
        this.assessmentValue += (Long.bitCount(this.getOwnQueen())-Long.bitCount(this.getOppositeQueen()))*9;

        //TODO: Assess positions
        if ((getOwnKing() & EXTENDED_CENTRE) != 0){
            this.assessmentValue += kingInExtendedCenter;
        }


        //TODO: AssessBoard wird jetzt immer verändert je öfter man die Methode aufruft, richtig? kann das geändert werden?
        //Til: Bitte genauer erklären, verstehe die Frage nicht so richtig.

        return this.assessmentValue;
    }

    //Für MiniMax brauchen wir aber tatsächlich immer nur die Bewertungsfunktion von einer perspektive
    //bei den MinKnoten sollen ja die kleinsten Werte (schlechtesten aus MaxPlayers perspektive ausgewählt werden)
    public int assessBoardFromOwnPerspective(){ //TODO: kann weg, wenn wir uns drauf geeignigt haben, was assessBoard zurückgibt
        if (KIPlaysWhite){//momentan immer true
            if(currentPlayerIsWhite){
                return this.assessBoard();
            } else {
                return this.assessBoard() *(-1);
            }
        } else {
            if(currentPlayerIsWhite){
                return this.assessBoard()  *(-1);
            } else {
                return this.assessBoard();
            }
        }
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
        return assessmentValue;
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
        return Integer.compare(this.assessmentValue, o.getAssessmentValue());
    }

}

