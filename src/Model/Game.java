package Model;

public class Game {

    private final boolean KIPlaysWhite;
    private short moveCount = 0;
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
     * Takes a fen string and sets the piece bitboards for each peace accordingly
     */
    public void setBitboards(String fenString) {
        String emptyMask ="0000000000000000000000000000000000000000000000000000000000000000";

        for (int i=0, k = 0; i<fenString.length(); i++) {
            //i is the iterator index for the fen string, k the iterator position for the chess field position

            //build a board mask that points to the current board position
            String boardMask = emptyMask.substring(k+1) + "1" + emptyMask.substring(0, k);

            switch (fenString.charAt(i)) {

                //remove line breaks from fen string
                case '/': continue;

                //identify the respective piece board and add the piece - WHITE/black
                case 'K': this.whiteKing += parseStringToBitboard(boardMask);
                    break;
                case 'Q': this.whiteQueen += parseStringToBitboard(boardMask);
                    break;
                case 'N': this.whiteKnights += parseStringToBitboard(boardMask);
                    break;
                case 'B': this.whiteBishops += parseStringToBitboard(boardMask);
                    break;
                case 'R': this.whiteRooks += parseStringToBitboard(boardMask);
                    break;
                case 'P': this.whitePawns += parseStringToBitboard(boardMask);
                    break;

                case 'k': this.blackKing += parseStringToBitboard(boardMask);
                    break;
                case 'q': this.blackQueen += parseStringToBitboard(boardMask);
                    break;
                case 'p': this.blackPawns += parseStringToBitboard(boardMask);
                    break;
                case 'n': this.blackKnights += parseStringToBitboard(boardMask);
                    break;
                case 'b': this.blackBishops += parseStringToBitboard(boardMask);
                    break;
                case 'r': this.blackRooks += parseStringToBitboard(boardMask);
                    break;

                //if no line break or piece was detected jump to the next board position
                default: k += Character.getNumericValue(fenString.charAt(i));
                    break;
            }

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
     * returns a long indicating the position of all white pieces
     */
    public long getWhitePieces() {
        return this.whiteKing += this.whiteQueen += this.whiteKnights += this.whiteBishops += this.whiteRooks += this.whitePawns;
    }

    /**
     * returns a long indicating the position of all black pieces
     */
    public long getBlackPieces() {
        return this.blackKing += this.blackQueen += this.blackKnights += this.blackBishops += this.blackRooks += this.blackPawns;
    }

    /**
     * Getters for all relevant fields
     */

    public boolean isKIPlayingWhite() {
        return KIPlaysWhite;
    }

    public short getMoveCount() {
        return moveCount;
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

    /**
     * Constructor of game class. Takes a fen string to build the current status, a flag if the KI plays white and the current move count
     */
    public Game(String fenString, boolean KIPlaysWhite, short moveCount) {
        this.setBitboards(fenString);
        this.KIPlaysWhite = KIPlaysWhite;
        this.moveCount = moveCount;
    }

}
