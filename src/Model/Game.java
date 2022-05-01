package Model;

public class Game {

    //TODO: get the info which color the KI is playing (from the game server)
    private boolean gameOver = false;
    private boolean KIPlaysWhite = false;
    private boolean currentPlayerIsWhite = false;
    private short halfMoveCount = 0;
    private short nextMoveCount = 0;
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

    /**
     * Constructor of game class. Takes a fen string to build the current status
     */
    public void Game(String fenString) throws IllegalArgumentException {

        String reducedFenString = "";
        String emptyMask ="0000000000000000000000000000000000000000000000000000000000000000";

        if (fenString.contains(" ")) {
            // remove and evaluate all meta information from the fen string
            String[] fenStringParts = fenString.split(" ");

            if (fenStringParts.length != 5){
                throw new IllegalArgumentException("fenString " + fenString + " contains more or less than five ' '");
            }
            else{
                //get positions
                reducedFenString = fenStringParts[0];

                //get current color
                if (fenStringParts[1].charAt(0) == 'w') currentPlayerIsWhite = true;

                //get castling status
                if (fenStringParts[2].contains("K")) whiteToCastleKingside = true;
                if (fenStringParts[2].contains("Q")) whiteToCastleKingside = true;
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

        for (int i=0, k = 0; i<reducedFenString.length(); i++) {
            //i is the iterator index for the fen string, k the iterator position for the chess field position

            //build a board mask that points to the current board position
            String boardMask = emptyMask.substring(k+1) + "1" + emptyMask.substring(0, k);

            switch (fenString.charAt(i)) {

                //remove line breaks from fen string
                case '/': continue;

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
                default: k += Character.getNumericValue(fenString.charAt(i));
                    break;
            }

            //TODO Test if the "/" case does not reach this statement
            //move to the next board position
            k++;

        }

    }

    /**
     * parses a String boardMask to its binary representation in form of a 64bit long
     */
    private long parseStringToBitboard (String boardMask) {
        //handle the sign bit at index 0
        if (boardMask.charAt(0)=='0') {
            return Long.parseLong(boardMask, 2);
        } else {
            return Long.parseLong("1" + boardMask.substring(2), 2)*2;
        }
    }




    /**
     * Convenience and general Getters
     */

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isCurrentPlayerIsWhite() {
        return currentPlayerIsWhite;
    }

    public boolean isKIPlayingWhite() {
        return KIPlaysWhite;
    }

    public boolean isKIPlaysWhite() {
        return KIPlaysWhite;
    }


    public long getOppositeKing() {
        if(isKIPlayingWhite()) {return blackKing;}
        else {
            return whiteKing;
        }
    }

    public long getOppositeQueen() {
        if(isKIPlayingWhite()) {return blackQueen;}
        else {
            return whiteQueen;
        }
    }

    public long getOppositeKnights() {
        if(isKIPlayingWhite()) {return blackKnights;}
        else {
            return whiteKnights;
        }
    }

    public long getOppositeBishops() {
        if(isKIPlayingWhite()) {return blackBishops;}
        else {
            return whiteBishops;
        }
    }

    public long getOppositeRooks() {
        if(isKIPlayingWhite()) {return blackRooks;}
        else {
            return whiteRooks;
        }
    }

    public long getOppositePawns() {
        if(isKIPlayingWhite()) {return blackPawns;}
        else {
            return whitePawns;
        }
    }

    public long getOwnKing() {
        if(isKIPlayingWhite()) {return whiteKing;}
        else {
            return blackKing;
        }
    }

    public long getOwnQueen() {
        if(isKIPlayingWhite()) {return whiteQueen;}
        else {
            return blackQueen;
        }
    }

    public long getOwnKnights() {
        if(isKIPlayingWhite()) {return whiteKnights;}
        else {
            return blackKnights;
        }
    }

    public long getOwnBishops() {
        if(isKIPlayingWhite()) {return whiteBishops;}
        else {
            return blackBishops;
        }
    }

    public long getOwnRooks() {
        if(isKIPlayingWhite()) {return whiteRooks;}
        else {
            return blackRooks;
        }
    }

    public long getOwnPawns() {
        if(isKIPlayingWhite()) {return whitePawns;}
        else {
            return blackPawns;
        }
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

    public short getHalfMoveCount() {
        return halfMoveCount;
    }

    public short getNextMoveCount() {
        return nextMoveCount;
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

    public String getEnPassants() {
        return enPassants;
    }

    /**
     * returns a long indicating the position of all white pieces
     */
    public long getWhitePieces() {
        return this.whiteKing & this.whiteQueen & this.whiteKnights & this.whiteBishops & this.whiteRooks & this.whitePawns;
    }

    /**
     * returns a long indicating the position of all black pieces
     */
    public long getBlackPieces() {
        return this.blackKing & this.blackQueen & this.blackKnights & this.blackBishops & this.blackRooks & this.blackPawns;
    }

    /**
     * returns a long indicating the position of all own pieces
     */
    public long getOwnPieces() {
        if (isKIPlayingWhite()){
            return this.whiteKing & this.whiteQueen & this.whiteKnights & this.whiteBishops & this.whiteRooks & this.whitePawns;
        } else {
            return this.blackKing & this.blackQueen & this.blackKnights & this.blackBishops & this.blackRooks & this.blackPawns;  
        }
    }

    /**
     * returns a long indicating the position of all opposite pieces
     */
    public long getOppositePieces() {
        if (!isKIPlayingWhite()){
            return this.whiteKing & this.whiteQueen & this.whiteKnights & this.whiteBishops & this.whiteRooks & this.whitePawns;
        } else {
            return this.blackKing & this.blackQueen & this.blackKnights & this.blackBishops & this.blackRooks & this.blackPawns;
        }
    }

    /**
     * returns a long indicating the position of all pieces
     */
    public long getAllPieces() {
        return this.getBlackPieces() & this.getWhitePieces();
    }


    /**
     * Allows to increases the 'halfMoveCount' manually at the end of a move (for KI simulation)
     */
    public void increaseMoveCounts() {
        this.halfMoveCount++;
    }

    public boolean KiIsPlaying(){
        return KIPlaysWhite == currentPlayerIsWhite;
    }
}