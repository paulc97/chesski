package Model;

import Model.Pieces.*;

import java.util.HashMap;

import static Model.Mask.*;


public class MoveGenerator {

    private static boolean outOfTime = false; //used for Iterative Deepening
    //https://github.com/nealyoung/CS171/blob/master/AI.java


        static Pawns pawns = new Pawns();
        static SlidingPieces slidingPieces = new SlidingPieces();
        static Knights knights = new Knights();
        static King king = new King();
        public static HashMap<Long,Integer> assesedBoards = new HashMap<Long,Integer>();
        public static final Zobrist zobrist = new Zobrist();

        static int assessedLeaves = 0;
        static int quiescenceSearchIterations = 0;
        static int cutoffs = 0;

        static int averageNumberOfMoves = 30;
        static private long[] timeDistribution = new long[averageNumberOfMoves];
        static double gameTimeLimit = 120000;
        static double panicModeTimeBuffer = 10000;
        static long totalTime;

        public MoveGenerator(){
            double expectationValue = averageNumberOfMoves/2;
            double variance = 20;
            zobrist.zobristFillArray();
            System.out.println("MG Created");
            for (int i = 0; i < this.timeDistribution.length; i++){
                double k = (double)i+1;
                //if(k<expectationValue){k=k*(-1);}
                double standardDistribution = (1/Math.sqrt(2*Math.PI*(variance)))*Math.exp(-1*((((k)-expectationValue)*((k)-expectationValue))/(2*(variance))));
                this.timeDistribution[i] = (long)Math.abs((standardDistribution)*(gameTimeLimit-panicModeTimeBuffer));
            }
        }

        public static String ownPossibleMoves(Board board) {
            String list = "";
                list += pawns.moves(board) + "-";
                list += knights.moves(board) + "-";
                list += king.moves(board) + "-"; //includes castling
                list += slidingPieces.rookMoves(board) + "-";
                list += slidingPieces.bishopMoves(board) + "-";
                list += slidingPieces.queenMoves(board);

            //TODO: Remove king in check/check mate situations from possible move list
            //format: rank-old file-old rank-new file-new
            //rank wird nach oben größer, file nach links
            //6050 wäre move von (Bitboard Layout 1) 48 auf 40

            return list;
        }
        //TODO: possible moves der gegenseite?


    //gibt Bitboard mit allen Positionen auf denen White Landen könnte (inkl. eigener weißer Figuren)
    // wenn dann mit BlackKing-Bitboard verundet (&) eine 1 ergibt, könnte BlackKing geschlagen
    public static long fieldsAttackedByWhite(Board b) {
        long unsafe;
       // OCCUPIED=WP|WN|WB|WR|WQ|WK|BP|BN|BB|BR|BQ|BK;
        //pawn
        unsafe=((b.getWhitePawns()>>>7)&~FILE_A);//pawn capture right
        unsafe|=((b.getWhitePawns()>>>9)&~FILE_H);//pawn capture left
        long possibility;
        //knight
        long WN = b.getWhiteKnights();
        long i=WN&~(WN-1);
        while(i != 0)
        {
            int iLocation=Long.numberOfTrailingZeros(i);
            if (iLocation>18)
            {
                possibility=KNIGHT_C6<<(iLocation-18);
            }
            else {
                possibility=KNIGHT_C6>>(18-iLocation);
            }
            if (iLocation%8<4)
            {
                possibility &=~FILE_GH;
            }
            else {
                possibility &=~FILE_AB;
            }
            unsafe |= possibility;
            WN&=~i;
            i=WN&~(WN-1);
        }
        //bishop/queen
        long QB=b.getWhiteQueen()|b.getWhiteBishops();
        i=QB&~(QB-1);
        while(i != 0)
        {
            int iLocation=Long.numberOfTrailingZeros(i);
            possibility= SlidingPieces.DAndAntiDMoves(iLocation,b.getAllPieces());
            unsafe |= possibility;
            QB&=~i;
            i=QB&~(QB-1);
        }
        //rook/queen
        long QR=b.getWhiteQueen()|b.getWhiteRooks();
        i=QR&~(QR-1);
        while(i != 0)
        {
            int iLocation=Long.numberOfTrailingZeros(i);
            possibility= SlidingPieces.HAndVMoves(iLocation,b.getAllPieces());
            unsafe |= possibility;
            QR&=~i;
            i=QR&~(QR-1);
        }
        //king
        int iLocation=Long.numberOfTrailingZeros(b.getWhiteKing());
        if (iLocation>9)
        {
            possibility=KING_B7<<(iLocation-9);
        }
        else {
            possibility=KING_B7>>(9-iLocation);
        }
        if (iLocation%8<4)
        {
            possibility &=~FILE_GH;
        }
        else {
            possibility &=~FILE_AB;
        }
        unsafe |= possibility;
        return unsafe;
    }
    public static long fieldsAttackedByBlack(Board b) {
        long unsafe;
        //OCCUPIED=WP|WN|WB|WR|WQ|WK|BP|BN|BB|BR|BQ|BK;
        //pawn
        unsafe=((b.getBlackPawns()<<7)&~FILE_H);//pawn capture right
        unsafe|=((b.getBlackPawns()<<9)&~FILE_A);//pawn capture left
        long possibility;
        //knight
        long BN = b.getBlackKnights();
        long i=BN&~(BN-1);
        while(i != 0)
        {
            int iLocation=Long.numberOfTrailingZeros(i);
            if (iLocation>18)
            {
                possibility=KNIGHT_C6<<(iLocation-18);
            }
            else {
                possibility=KNIGHT_C6>>(18-iLocation);
            }
            if (iLocation%8<4)
            {
                possibility &=~FILE_GH;
            }
            else {
                possibility &=~FILE_AB;
            }
            unsafe |= possibility;
            BN&=~i;
            i=BN&~(BN-1);
        }
        //bishop/queen
        long QB=b.getBlackQueen()|b.getBlackBishops();
        i=QB&~(QB-1);
        while(i != 0)
        {
            int iLocation=Long.numberOfTrailingZeros(i);
            possibility= SlidingPieces.DAndAntiDMoves(iLocation,b.getAllPieces());
            unsafe |= possibility;
            QB&=~i;
            i=QB&~(QB-1);
        }
        //rook/queen
        long QR=b.getBlackQueen()|b.getBlackRooks();
        i=QR&~(QR-1);
        while(i != 0)
        {
            int iLocation=Long.numberOfTrailingZeros(i);
            possibility= SlidingPieces.HAndVMoves(iLocation,b.getAllPieces());
            unsafe |= possibility;
            QR&=~i;
            i=QR&~(QR-1);
        }
        //king
        int iLocation=Long.numberOfTrailingZeros(b.getBlackKing());
        if (iLocation>9)
        {
            possibility=KING_B7<<(iLocation-9);
        }
        else {
            possibility=KING_B7>>(9-iLocation);
        }
        if (iLocation%8<4)
        {
            possibility &=~FILE_GH;
        }
        else {
            possibility &=~FILE_AB;
        }
        unsafe |= possibility;
        return unsafe;
    }


    //ändert für 1 Bitboard ("long board") die Positionen, nachdem "String move" ausgeführt wurde
    public static long executeMoveforOneBitboard(long board, String move, char type) {
        if (Character.isDigit(move.charAt(3))) {//'regular' move
            int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
            int end=(Character.getNumericValue(move.charAt(2))*8)+(Character.getNumericValue(move.charAt(3)));
            if (((board>>>start)&1)==1)

            //falls "passendes" Bitboard (d.h. auf Startposition war 1)-> Bit von Start auf Endposition verschieben
            {board&=~(1L<<start); board|=(1L<<end);}

            //bei allen anderen Bitboards Endposition auf 0 setzen (falls auf Ende gecaptured wurde)
            else {board&=~(1L<<end);}
        } else if (move.charAt(3)=='P') {//pawn promotion
            int start, end;
            if (Character.isUpperCase(move.charAt(2))) {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[1]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[0]);
            } else {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[6]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[7]);
            }
            if (type==move.charAt(2)) {board|=(1L<<end);} else {board&=~(1L<<start); board&=~(1L<<end);}
        } else if (move.charAt(3)=='E') {//en passant
            int start, end;
            if (move.charAt(2)=='W') {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[3]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[2]);
                board&=~(FileMasks8[move.charAt(1)-'0']&RankMasks8[3]);
            } else {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[4]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[5]);
                board&=~(FileMasks8[move.charAt(1)-'0']&RankMasks8[4]);
            }
            if (((board>>>start)&1)==1) {board&=~(1L<<start); board|=(1L<<end);}
        } else {
            System.out.print("ERROR: Invalid move type");
        }
        return board;
    }

    public static long executeCastling(long rookBoard, long kingBoard, String move, char type) {
        int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
        if ((((kingBoard>>>start)&1)==1)&&(("0402".equals(move))||("0406".equals(move))||("7472".equals(move))||("7476".equals(move)))) {//'regular' move
            if (type=='R') {
                switch (move) {
                    case "7472": rookBoard&=~(1L<<56L); rookBoard|=(1L<<(56L+3));
                        break;
                    case "7476": rookBoard&=~(1L<<63L); rookBoard|=(1L<<(63L-2));
                        break;
                }
            } else {
                switch (move) {
                    case "0402": rookBoard&=~(1L<<0L); rookBoard|=(1L<<(0L+3));
                        break;
                    case "0406": rookBoard&=~(1L<<7L); rookBoard|=(1L<<(7L-2));
                        break;
                }
            }
        }
        return rookBoard;
    }

    //when pawn moves forward 2 spaces, then on that file an en passant can happen
    /*public static long makeMoveEP(long board,String move) {
        if (Character.isDigit(move.charAt(3))) {
            int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
            if ((Math.abs(move.charAt(0)-move.charAt(2))==2)&&(((board>>>start)&1)==1)) {//pawn double push
                //board, to be sure that type is a pawn


                return FileMasks8[move.charAt(1)-'0']; //return file on which that pawn push occured on
            }
        }
        return 0; //-> no en passant allowed
    }*/

    public static String executeEnPassant(long board, String move) {
        if (Character.isDigit(move.charAt(3))) {
            int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
            if ((Math.abs(move.charAt(0)-move.charAt(2))==2)&&(((board>>>start)&1)==1)) {//pawn double push
                //board, to be sure that type is a pawn


                char rank;
                if (move.charAt(0)>move.charAt(2)){ //white pawn double push e.g. 6040
                    rank = (char) (move.charAt(0)-1);
                } else { //black pawn double push
                    rank = (char) (move.charAt(0)+1);
                }
                char file = move.charAt(1);

                return convertMoveDigitsToField(rank,file); //return field on which that pawn push occured on
            }
        }
        return "-"; //-> no en passant allowed
    }

    //zeileAlt spalteAlt zeileNeu spalteNeu
    // 0         1         2        3
    //

    public static String convertMoveDigitsToField (char rank, char file){
        char f =(char)(97+(file-48));
        int r = (Math.abs(rank-48-8));
        return ""+f+r;
    }  //TODO: fix for Queenpromotion/Ep Notation

    public static String convert4digitMoveToField (String move){
        return convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + convertMoveDigitsToField(move.charAt(2),move.charAt(3));
    }

    public static void print4digitMoveToField (String move){
        System.out.println(convertMoveDigitsToField(move.charAt(0),move.charAt(1)) + "->" + convertMoveDigitsToField(move.charAt(2),move.charAt(3)));
    }


    public static String convertInternalMoveToGameserverMove(String moveBitboardPosition, Board b){

        String move = "";

        //check for promotion
        if (moveBitboardPosition.charAt(3) == 'P') {
            if (b.isCurrentPlayerIsWhite()){
                move += Character.toString((char)((97+(moveBitboardPosition.charAt(0)-48))));
                move += "7";
                move += Character.toString((char)(97+(moveBitboardPosition.charAt(1)-48)));
                move += "8";
                move += Character.toString(moveBitboardPosition.charAt(2));
                return move;
            } else {
                move += Character.toString((char)((97+(moveBitboardPosition.charAt(0)-48))));
                move += "2";
                move += Character.toString((char)(97+(moveBitboardPosition.charAt(1)-48)));
                move += "1";
                move += Character.toString(moveBitboardPosition.charAt(2));
                return move;
            }

        }

        if (moveBitboardPosition.charAt(3) == 'E') {
            //TODO: Add case handling for enpassant
            return move;
        }

        move += (char)(97+(moveBitboardPosition.charAt(1)-48));
        move += (Math.abs(moveBitboardPosition.charAt(0)-48-8));
        move += (char)(97+(moveBitboardPosition.charAt(3)-48));
        move += (Math.abs(moveBitboardPosition.charAt(2)-48-8));
        return move;

    }



    //"valid" i.e. eigener King ist danach nicht (mehr) im Schach
    //public static void perft(long WP,long WN,long WB,long WR,long WQ,long WK,long BP,long BN,long BB,long BR,long BQ,long BK,long EP,boolean CWK,boolean CWQ,boolean CBK,boolean CBQ,boolean WhiteToMove,int depth)
    public static String validMoves(Board b)
    {
        String validMoves = "";

        String moves = ownPossibleMoves(b).replace("-","");

        for (int i=0;i<moves.length();i+=4) {

            Board tempB = new Board(b);

            tempB.setWhitePawns(executeMoveforOneBitboard(b.getWhitePawns(), moves.substring(i,i+4), 'P'));
            tempB.setWhiteKnights(executeMoveforOneBitboard(b.getWhiteKnights(), moves.substring(i,i+4), 'N'));
            tempB.setWhiteBishops(executeMoveforOneBitboard(b.getWhiteBishops(), moves.substring(i,i+4), 'B'));
            tempB.setWhiteRooks(executeMoveforOneBitboard(b.getWhiteRooks(), moves.substring(i,i+4), 'R'));
            tempB.setWhiteQueen(executeMoveforOneBitboard(b.getWhiteQueen(), moves.substring(i,i+4), 'Q'));
            tempB.setWhiteKing(executeMoveforOneBitboard(b.getWhiteKing(), moves.substring(i,i+4), 'K'));
            tempB.setBlackPawns(executeMoveforOneBitboard(b.getBlackPawns(), moves.substring(i,i+4), 'p'));
            tempB.setBlackKnights(executeMoveforOneBitboard(b.getBlackKnights(), moves.substring(i,i+4), 'n'));
            tempB.setBlackBishops(executeMoveforOneBitboard(b.getBlackBishops(), moves.substring(i,i+4), 'b'));
            tempB.setBlackRooks(executeMoveforOneBitboard(b.getBlackRooks(), moves.substring(i,i+4), 'r'));
            tempB.setBlackQueen(executeMoveforOneBitboard(b.getBlackQueen(), moves.substring(i,i+4), 'q'));
            tempB.setBlackKing(executeMoveforOneBitboard(b.getBlackKing(), moves.substring(i,i+4), 'k'));
            //tempB.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));
            tempB.setEnPassants(executeEnPassant(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));

            tempB.setWhiteRooks(executeCastling(tempB.getWhiteRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'R'));
            tempB.setBlackRooks(executeCastling(tempB.getBlackRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'r'));

                tempB.setWhiteToCastleKingside(b.isWhiteToCastleKingside());
                tempB.setWhiteToCastleQueenside(b.isWhiteToCastleQueenside());
                tempB.setBlackToCastleKingside(b.isBlackToCastleKingside());
                tempB.setBlackToCastleQueenside(b.isBlackToCastleQueenside());
                if (Character.isDigit(moves.charAt(3))) {//'regular' move
                    int start=(Character.getNumericValue(moves.charAt(i))*8)+(Character.getNumericValue(moves.charAt(i+1)));
                    if (((1L<<start)&b.getWhiteKing())!=0) {tempB.setWhiteToCastleKingside(false); tempB.setWhiteToCastleQueenside(false);}
                    if (((1L<<start)&b.getBlackKing())!=0) {tempB.setBlackToCastleKingside(false); tempB.setBlackToCastleQueenside(false);}
                    if (((1L<<start)&b.getWhiteRooks()&(1L<<63))!=0) {tempB.setWhiteToCastleKingside(false);}
                    if (((1L<<start)&b.getWhiteRooks()&(1L<<56))!=0) {tempB.setWhiteToCastleQueenside(false);}
                    if (((1L<<start)&b.getBlackRooks()&(1L<<7))!=0) {tempB.setBlackToCastleKingside(false);}
                    if (((1L<<start)&b.getBlackRooks()&1L)!=0) {tempB.setBlackToCastleQueenside(false);}
                }

                tempB.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());


                //check if own King is NOT in danger after move
                if (((tempB.getWhiteKing()& fieldsAttackedByBlack(tempB))==0 && b.isCurrentPlayerIsWhite()) ||
                        ((tempB.getBlackKing()& fieldsAttackedByWhite(tempB))==0 && !b.isCurrentPlayerIsWhite())) {
                    //add current move to validMoves if king is not in danger
                    validMoves += moves.substring(i,i+4);

                }
            }

        return validMoves;

    }

    public static void makeMove(Board b, String move){

        if (move == null || move.equals("")){

            //wird angegriffen?
            if(b.isCurrentPlayerIsWhite()){



                if((fieldsAttackedByBlack(b)&b.getWhiteKing())!=0){
                    //Weißer König wird angegriffen
                    b.setGameOver(true);
                    b.setWhiteWon(false);
                    return;
                }

            }else { //Schwarz am Zug
                if((fieldsAttackedByWhite(b)&b.getBlackKing())!=0){
                    b.setGameOver(true);
                    b.setWhiteWon(true);
                    return;
                }

            }

            //wird nicht angegriffen -> Remis
            b.setGameOver(true);
            b.setRemis(true);
            return;
        }

        //Debugging:
        String player ="";
        if(b.isCurrentPlayerIsWhite()){
            player = "White";
        }else{
            player = "Black";
        }
        //System.out.println(player+" played: "+convertMoveDigitsToField(move.charAt(0),move.charAt(1))+"->"+convertMoveDigitsToField(move.charAt(2),move.charAt(3)));


            String oldFEN = b.bitboardsToFenParser().split(" ")[0]; //für 50-Zug-Remis-Regel
        long oldWhitePawns = b.getWhitePawns();
        long oldBlackPawns = b.getBlackPawns();

        long oldWhiteKing = b.getWhiteKing();
        long oldBlackKing = b.getBlackKing();

        //Castling (muss ->vor<- Änderung auf Bitboard durchgeführt werden)
        if (Character.isDigit(move.charAt(3))) {//'regular' move
            int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(0+1)));
            if (((1L<<start)&b.getWhiteKing())!=0) { b.setWhiteToCastleKingside(false); b.setWhiteToCastleQueenside(false);}
            if (((1L<<start)&b.getBlackKing())!=0) {b.setBlackToCastleKingside(false); b.setBlackToCastleQueenside(false);}
            if (((1L<<start)&b.getWhiteRooks()&(1L<<63))!=0) {b.setWhiteToCastleKingside(false);}
            if (((1L<<start)&b.getWhiteRooks()&(1L<<56))!=0) {b.setWhiteToCastleQueenside(false);}
            if (((1L<<start)&b.getBlackRooks()&(1L<<7))!=0) {b.setBlackToCastleKingside(false);}
            if (((1L<<start)&b.getBlackRooks()&1L)!=0) {b.setBlackToCastleQueenside(false);}
        }

        //En Passant (muss ->vor<- Änderung auf Bitboard durchgeführt werden)
        //b.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),move));
        b.setEnPassants(executeEnPassant(b.getWhitePawns()|b.getBlackPawns(),move));



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

        //Castling auch vor Änderung der King Position??
        b.setWhiteRooks(executeCastling(b.getWhiteRooks(), oldWhiteKing|oldBlackKing, move, 'R'));
        b.setBlackRooks(executeCastling(b.getBlackRooks(), oldWhiteKing|oldBlackKing, move, 'r'));




        b.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());

        if(b.getWhitePawns()!=oldWhitePawns||b.getBlackPawns()!=oldBlackPawns){ //wurde ein Bauer bewegt?(/geschlagen)
            b.setHalfMoveCount(-1);
        } else {  //wurde eine Figur geschlagen?
            String newFEN = b.bitboardsToFenParser().split(" ")[0]; //für 50-Zug-Remis-Regel;

            int oldFigureCount =0;
            for (int i =0; i<oldFEN.length();i++){
                if(oldFEN.charAt(i) == 'r'||oldFEN.charAt(i) == 'n'||oldFEN.charAt(i) == 'b'||oldFEN.charAt(i) == 'k'||oldFEN.charAt(i) == 'q'||oldFEN.charAt(i) == 'p'||oldFEN.charAt(i) == 'P'||oldFEN.charAt(i) == 'R'||oldFEN.charAt(i) == 'N'||oldFEN.charAt(i) == 'B'||oldFEN.charAt(i) == 'K'||oldFEN.charAt(i) == 'Q'){
                    oldFigureCount++;
                }
            }
            int newFigureCount =0;
            for (int i =0; i<newFEN.length();i++){
                if(newFEN.charAt(i) == 'r'||newFEN.charAt(i) == 'n'||newFEN.charAt(i) == 'b'||newFEN.charAt(i) == 'k'||newFEN.charAt(i) == 'q'||newFEN.charAt(i) == 'p'||newFEN.charAt(i) == 'P'||newFEN.charAt(i) == 'R'||newFEN.charAt(i) == 'N'||newFEN.charAt(i) == 'B'||newFEN.charAt(i) == 'K'||newFEN.charAt(i) == 'Q'){
                    newFigureCount++;
                }
            }
            if (oldFigureCount!=newFigureCount) b.setHalfMoveCount(-1);
        }




        if(b.isCurrentPlayerIsWhite()){
            b.setHalfMoveCount(b.getHalfMoveCount()+1);

        }else{
            b.setHalfMoveCount(b.getHalfMoveCount()+1);
            b.setNextMoveCount(b.getNextMoveCount()+1); //"full" move after black

        }


        //Check if King is in CENTRE


        if((CENTRE&b.getWhiteKing())!=0){
            //Weißer König wird angegriffen
            b.setGameOver(true);
            b.setWhiteWon(true);
            return;
        }
        if((CENTRE&b.getBlackKing())!=0){
            b.setGameOver(true);
            b.setWhiteWon(false);
            return;
        }

        //50-Züge-Remis-Regel
        if(b.getHalfMoveCount()>=100){
            b.setGameOver(true);
            b.setRemis(true);
        }

    }

    public static String moveSelector(Board b, String validMoves, long usedTimeInMs) {


        //defines the number of moves to use deepening search
        int firstMoveThresholdMoves = 10;

        if (validMoves == null || validMoves.equals("")) {
            return validMoves;
        }

        if ((gameTimeLimit - usedTimeInMs) < panicModeTimeBuffer) {
            System.out.println("Entered panic mode");
            int depth = 1;
            return alphaBeta(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true).substring(0, 4);
        }

        //TODO: Das hier nicht löschen!! Ist Variante ohne PVS nur mit IDS!


/*            System.out.println("Using iterative deepening search for move generation");
            //Iterative Deepening Search (ohne Zugsortierung)
            long timeLimit = 100;
            if (Math.floor(b.getNextMoveCount()/2)<timeDistribution.length) {
            timeLimit += timeDistribution[b.getNextMoveCount()];}
            return iterativeDeepeningSearch(b, timeLimit).substring(0, 4);*/


        System.out.println("Using principal variation search for move generation");
        //Iterative Deepening Search + PVS (mit PV Zugsortierung)
        long timeLimit = 100;
        if (Math.floor(b.getNextMoveCount()/2)<timeDistribution.length) {
            timeLimit += timeDistribution[b.getNextMoveCount()];}
        return PrincipalVariationSearch.moiterativeDeepeningPVSWithTimeLimitNoWindow(b, timeLimit).substring(0, 4);



        //int suchtiefe = depthCalculator(b);
        //System.out.println("Using alpha beta search for move generation");
        //return alphaBeta(b, suchtiefe, Integer.MIN_VALUE, Integer.MAX_VALUE, true).substring(0, 4);

        //String bestMoveFromMinMax = minMax(b, suchtiefe, true).substring(0,4);

    }


/*    public int depthCalculator(Board b){
        if (b.getNextMoveCount()/2 < 10){
            return 1;
        }
        if (b.getNextMoveCount()/2 < 40){
            return 3;
        }
        return 5;

    }*/



    //TODO: generell mal überlegen, ob wir auf einem einzigen Board (static) arbeiten wollen und dann mit make/undo moves
    //oder immer viele einzelne Boards generieren wollen?
    //ersteres wahrscheinlich aufwandärmer hinsichtlich rechenzeit etc.?

    //TODO: sollte intial nicht mit suchtiefe 0 aufgerufen werden, sonst (ergibt ja auch keinen sinn) fehler wegen getCreatedByMove...

    /**
     *
     * @param b
     * @param depth
     * @param alpha
     * @param beta
     * @param isMaxPlayer
     * @return String in this format: XXXXY.. where XXXX is representation of move and Y.. value of Bewertungsfunktion
     */
    public static String alphaBeta (Board b, int depth, int alpha, int beta, boolean isMaxPlayer){
        //System.out.println("Is KI playing white: "+ b.isKIPlayingWhite());
        //System.out.println("Current player is white: "+ b.isCurrentPlayerIsWhite());
        //System.out.println("Is max player: "+isMaxPlayer);


        if(depth == 0){
            assessedLeaves++;
            //TODO: "gameOver" wird momentan am Anfang von makeMove gesetzt, wenn eigener Player Schachmatt ist
            //wenn am Ende von Make move noch "gameOver" gesetzt würde, wenn anderer Player Schachmatt ist (oder gleich am Ende von validMoves, wenn das leer ist
            //dann könnte man hier die moveList auch erst nach dem check dass nicht gameOver ist generieren und so evtl. Zeit sparen
            //momentan könnte moveList jedoch noch "" sein ohne dass gameOver schon gesetzt wurde
            //TODO: muss dan bei der Bewertungsfunktion überprüft werden, ob gerade verloren ist/König im Schachmatt steht?
            String score = String.valueOf(b.assessBoardFromOwnPerspective());

            //System.out.println(b.getCreatedByMove() + score);
            return b.getCreatedByMove() + score; //TODO: wann wird created by move gesetzt?
        }
        String moveList = validMoves(b);
        //System.out.println("move list:" + moveList);
        //System.out.println("in depth:" + depth);

        if(b.isGameOver()||moveList.equals("")){
            assessedLeaves++;
            //TODO: "gameOver" wird momentan am Anfang von makeMove gesetzt, wenn eigener Player Schachmatt ist
            //wenn am Ende von Make move noch "gameOver" gesetzt würde, wenn anderer Player Schachmatt ist (oder gleich am Ende von validMoves, wenn das leer ist
            //dann könnte man hier die moveList auch erst nach dem check dass nicht gameOver ist generieren und so evtl. Zeit sparen
            //momentan könnte moveList jedoch noch "" sein ohne dass gameOver schon gesetzt wurde
            //TODO: muss dan bei der Bewertungsfunktion überprüft werden, ob gerade verloren ist/König im Schachmatt steht?
            String score = String.valueOf(b.assessBoardFromOwnPerspective());

            //System.out.println(b.getCreatedByMove() + score);
            return b.getCreatedByMove() + score; //TODO: wann wird created by move gesetzt?
        }



        if(isMaxPlayer){
            String bestMove = "9999";
            for(int i = 0; i<moveList.length(); i+=4){

                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                int currentEval = Integer.parseInt(alphaBeta(newBoard, depth-1, alpha, beta, false).substring(4));
                //System.out.println("alpha was:" + alpha);
                //System.out.println("beta was:" + beta);
                if(currentEval > alpha){
                    bestMove = move; //equiv. zu b.getCreatedByMove();
                    alpha = currentEval;
                }
                //System.out.println("alpha is:" + alpha);
                //alpha = Math.max(alpha,currentEval); wird redundant, siehe 2 Zeilen vorher

                if (beta<=alpha){
                    //System.out.println("Beta cutoff!");
                    break; //beta-cutoff
                }
            }
            return bestMove + alpha;
        } else {
            String bestMove= "9999";
            for(int i = 0; i<moveList.length(); i+=4){

                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                int currentEval = Integer.parseInt(alphaBeta(newBoard, depth-1, alpha, beta, true).substring(4));
                //System.out.println("beta was:" + beta);
                //System.out.println("alpha was:" + alpha);
                if (currentEval < beta){
                    bestMove = move;
                    beta = currentEval;
                }
                //System.out.println("beta is:" + beta);
                //beta = Math.min(beta, currentEval);

                if (beta <= alpha){
                    //System.out.println("Alpha cutoff!");
                    break; //alpha-cutoff
                }

            }
            return bestMove + beta;
        }
    }


    //das gleiche wie alphaBeta nur ohne cutoffs
    public static String minMax (Board b, int depth, boolean isMaxPlayer){

        if (depth == 0){
            assessedLeaves++;
            //TODO: "gameOver" wird momentan am Anfang von makeMove gesetzt, wenn eigener Player Schachmatt ist
            //wenn am Ende von Make move noch "gameOver" gesetzt würde, wenn anderer Player Schachmatt ist (oder gleich am Ende von validMoves, wenn das leer ist
            //dann könnte man hier die moveList auch erst nach dem check dass nicht gameOver ist generieren und so evtl. Zeit sparen
            //momentan könnte moveList jedoch noch "" sein ohne dass gameOver schon gesetzt wurde
            //TODO: muss dan bei der Bewertungsfunktion überprüft werden, ob gerade verloren ist/König im Schachmatt steht?

            String score = String.valueOf(b.assessBoardFromOwnPerspective());

            //System.out.println(b.getCreatedByMove() + score);
            return b.getCreatedByMove() + score; //TODO: wann wird created by move gesetzt?
        }

        String moveList = validMoves(b);
        //System.out.println("Valid Move List: " + moveList);

        if(b.isGameOver()||moveList.equals("")){
            assessedLeaves++;
            //TODO: "gameOver" wird momentan am Anfang von makeMove gesetzt, wenn eigener Player Schachmatt ist
            //wenn am Ende von Make move noch "gameOver" gesetzt würde, wenn anderer Player Schachmatt ist (oder gleich am Ende von validMoves, wenn das leer ist
            //dann könnte man hier die moveList auch erst nach dem check dass nicht gameOver ist generieren und so evtl. Zeit sparen
            //momentan könnte moveList jedoch noch "" sein ohne dass gameOver schon gesetzt wurde
            //TODO: muss dan bei der Bewertungsfunktion überprüft werden, ob gerade verloren ist/König im Schachmatt steht?

            String score = String.valueOf(b.assessBoardFromOwnPerspective());

            //System.out.println(b.getCreatedByMove() + score);
            return b.getCreatedByMove() + score; //TODO: wann wird created by move gesetzt?
        }



        if(isMaxPlayer){
            //System.out.println("This is max player");
            String bestMove = "";
            int max = -Integer.MAX_VALUE;
            for(int i = 0; i<moveList.length(); i+=4){

                String move = moveList.substring(i,i+4);

                //System.out.println("Iteration: " + i + " with move: " + move);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String zwischenergebnis = minMax(newBoard, depth-1, false);
                //System.out.println("zwischenergebnis: " + zwischenergebnis);
                int currentEval = Integer.parseInt(zwischenergebnis.substring(4));
                //System.out.println("currentEval: "+ currentEval + "in iteration (max): " + i + "of move: " + move);
                if(currentEval > max){
                    bestMove = move; //equiv. zu b.getCreatedByMove();
                    max = currentEval;
                    //System.out.println("was greater!");
                }



            }
            //System.out.println("best move(max): " + bestMove + "with score: " + max);
            return bestMove + max;
        } else {
            //System.out.println("This is min player");
            String bestMove= "";
            int min = Integer.MAX_VALUE;
            for(int i = 0; i<moveList.length(); i+=4){

                String move = moveList.substring(i,i+4);
                //System.out.println("Iteration: " + i + " with move: " + move);


                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                int currentEval = Integer.parseInt(minMax(newBoard, depth-1, true).substring(4));
                //System.out.println("currentEval: "+ currentEval + "in iteration (min): " + i + "of move: " + move);
                if (currentEval < min){
                    bestMove = move;
                    min = currentEval;
                    //System.out.println("was lower!");
                }

            }
            //System.out.println("best move(min): " + bestMove + "with score: " + min);
            return bestMove + min;
        }
    }

    //IDS
    public static String iterativeDeepeningSearch(Board b, long timeLimit){
        System.out.println("Starting iterative deepening search with time limit: "+timeLimit);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        int depth = 1;
        String bestMoveSoFar ="";
        outOfTime = false;

        while(true){
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime){
                break;
            }
            long newTimeLimit = endTime-currentTime;
            String result = alphaBetaTimeLimit(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, currentTime, newTimeLimit); //TODO

            if(!outOfTime){ //ohne den check könnte bestMoveSoFar mit move aus unkomplettierter Suche überschrieben werden, der könnte (momentan noch) schlechter sein
                bestMoveSoFar = result;
                System.out.println("depth was: " + depth);
            }
            depth++;
            //System.out.println("Increased depth, current:" + depth);
        }
        return bestMoveSoFar;
    }

    //Alpha-Beta with Time-Limit for IDS
    //TODO: aktualisieren, falls alphaBeta Methode verändert wird
    public static String alphaBetaTimeLimit (Board b, int depth, int alpha, int beta, boolean isMaxPlayer, long startTime, long timeLimit){
        //System.out.println("Is KI playing white: "+ b.isKIPlayingWhite());
        //System.out.println("Current player is white: "+ b.isCurrentPlayerIsWhite());
        //System.out.println("Is max player: "+isMaxPlayer);

        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime);
        if(elapsedTime >= timeLimit){
            outOfTime = true;
        }


/*       if (depth == 0) {
           //System.out.println("Calling qs with move" +b.getCreatedByMove());
            return quiescenceSearchv1(b, alpha, beta, !isMaxPlayer);
        }*/

        if(depth == 0||outOfTime){
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if(outOfTime){
                System.out.println("Out of Time!");
            }
            //System.out.println("b currently assessed, was created by move" + b.getCreatedByMove());
            //System.out.println(b.getCreatedByMove() + score);
            return b.getCreatedByMove() + score;
        }




        String moveList = validMoves(b);

        if(outOfTime || b.isGameOver()||moveList.equals("")){
            assessedLeaves++;
            String score = String.valueOf(b.assessBoardFromOwnPerspective());
            if(outOfTime){
                System.out.println("Out of Time! (after Move Generation)");
            }
            //System.out.println("b currently assessed, was created by move" + b.getCreatedByMove());
            //System.out.println(b.getCreatedByMove() + score);
            return b.getCreatedByMove() + score;
        }

        //https://stackoverflow.com/questions/15447580/java-minimax-alpha-beta-pruning-recursion-return

        if(isMaxPlayer){
            String bestMove = "";
            for(int i = 0; i<moveList.length(); i+=4){
                if(outOfTime) break; //TODO: richtig? -> out of time wird vor der for Schleife berechnet und evaluiert und kann hier meiner Meinung nach nie true sein... Die nächste Überprüfung dürfte beim nächsten Selbsaufruf erfolgen...
                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                //System.out.println("max" + "board was created by move:" + move);

                //System.out.println("max: wird mit alpha" + alpha + "und beta:" + beta +"aufgerufen");
                String zwischenergebnis = alphaBetaTimeLimit(newBoard, depth-1, alpha, beta, false, startTime, timeLimit);
                //System.out.println("move:" + move + "ze(max): "+zwischenergebnis);
                int currentEval = Integer.parseInt(zwischenergebnis.substring(4));
                //System.out.println("currentEval is:" + currentEval);
                //System.out.println("alpha was:" + alpha);
                //System.out.println("beta was:" + beta);
                if(currentEval > alpha){
                    bestMove = move; //equiv. zu b.getCreatedByMove();

                    alpha = currentEval;
                }
                //System.out.println("best Move is currently" + bestMove);
                if(bestMove.equals("")){
                    //System.out.println("nothing!");
                    bestMove = "9999";
                }
                //System.out.println("alpha is:" + alpha);
                //alpha = Math.max(alpha,currentEval); wird redundant, siehe 2 Zeilen vorher

                if (beta<=alpha){
                    //System.out.println("Beta cutoff!");
                    cutoffs++;
                    break; //beta-cutoff
                }
            }
            //System.out.println("max out of for:" + bestMove + alpha);
            return bestMove + alpha;
        } else {
            String bestMove= "";
            for(int i = 0; i<moveList.length(); i+=4){
                if(outOfTime) break; //TODO: richtig?
                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);
                //System.out.println("min" + "board was created by move:" + move);

                //System.out.println("min: wird mit alpha" + alpha + "und beta:" + beta +"aufgerufen");
                String zwischenergebnis = alphaBetaTimeLimit(newBoard, depth-1, alpha, beta, true, startTime, timeLimit);
                //System.out.println("move:" + move + "ze: "+ zwischenergebnis);
                int currentEval = Integer.parseInt(zwischenergebnis.substring(4));
                //System.out.println("currentEval is:" + currentEval);
                //System.out.println("beta was:" + beta);
                //System.out.println("alpha was:" + alpha);
                if (currentEval < beta){
                    bestMove = move;

                    beta = currentEval;
                }
                //System.out.println("best move is currently:" + bestMove);
                if(bestMove.equals("")){
                    //System.out.println("nothing!");
                    bestMove = "9999";
                }
                //System.out.println("beta is:" + beta );
                //beta = Math.min(beta, currentEval);

                if (beta <= alpha){
                    //System.out.println("Alpha cutoff!");
                    cutoffs++;
                    break; //alpha-cutoff
                }

            }
            //System.out.println("min out of for:" + bestMove + beta);
            return bestMove + beta;
        }
    }

    //IDS ohne Zeitlimit (für Benchmarkvergleich mit IDS+PVS)
    public static String iterativeDeepeningSearchNoTimeLimit(Board b, int depth, boolean isMaxPlayer){
        //System.out.println("Starting iterative deepening search with depth: "+depth);

        String result ="";

        for(int i = 1; i <= depth; i++){
            //System.out.println("current depth: " + i);

            result = alphaBeta(b, i, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);

        }
        return result;

    }

/*    public static String quiescenceSearchv2(Board b, int alpha, int beta, boolean isMaxPlayer){
        String moveList = validMoves(b);

            String bestMove = "";

            //recursion anchor
            if (b.getAssessmentValue() >= beta || moveList.equals("")){
                System.out.println("Finished quiescence search");
                return b.getCreatedByMove() + beta;
            }

            //System.out.println("Using quiescence search (evalValue:"+b.getAssessmentValue()+"(>=) beta: "+beta+")");
            alpha = Math.max(alpha, b.getAssessmentValue());

            for(int i = 0; i<moveList.length(); i+=4){

                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String intermediateResult = quiescenceSearchv2(newBoard, (-1*beta), (-1*alpha), false);
                int currentEval = -1*Integer.parseInt(intermediateResult.substring(4));

                if(currentEval >= beta){
                    bestMove = move;
                    alpha = currentEval;
                }

                if(bestMove.equals("")){
                    bestMove = "9999";
                }

                alpha = Math.max(alpha,currentEval);
            }

            return bestMove + alpha;

    }*/

    public static String quiescenceSearchv1(Board b, int alpha, int beta, boolean isMaxPlayer){
        String moveList = validMoves(b);

        if(isMaxPlayer){
            String bestMove = "";

            //recursion anchor
            if (b.assessBoardFromOwnPerspective() >= beta || moveList.equals("")){
                //System.out.println("Finished quiescence search with evalValue: "+ b.assessBoardFromOwnPerspective() +" >= alpha: "+alpha);
                assessedLeaves++;
                //System.out.println("returning move "+b.getCreatedByMove()+" from qs");
                return b.getCreatedByMove() + beta;
            }
            quiescenceSearchIterations++;
            //System.out.println("Using quiescence search (evalValue:"+b.assessBoardFromOwnPerspective()+"(>=) alpha: "+alpha+")");
            alpha = Math.max(alpha, b.assessBoardFromOwnPerspective());

            for(int i = 0; i<moveList.length(); i+=4){

                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String intermediateResult = quiescenceSearchv1(newBoard, alpha, beta, false);
                int currentEval = Integer.parseInt(intermediateResult.substring(4));

                if(currentEval >= alpha){
                    bestMove = move;
                    alpha = currentEval;
                }

                if(bestMove.equals("")){
                    bestMove = "9999";
                }

                alpha = Math.max(alpha,currentEval);
                //System.out.println("new alpha: "+alpha);

            }

            return bestMove + alpha;

        } else {

            //recursion anchor
            if (b.assessBoardFromOwnPerspective() <= alpha || moveList.equals("")){
                //System.out.println("Finished quiescence search with evalValue: "+ b.assessBoardFromOwnPerspective() +" <= beta: "+beta);
                assessedLeaves++;
                return b.getCreatedByMove() + alpha;
            }
            quiescenceSearchIterations++;
            //System.out.println("Using quiescence search (evalValue:"+b.assessBoardFromOwnPerspective()+"(<=) beta: "+beta+")");
            beta = Math.min(beta, b.assessBoardFromOwnPerspective());

            String bestMove= "";
            for(int i = 0; i<moveList.length(); i+=4){
                String move = moveList.substring(i,i+4);
                Board newBoard = b.createBoardFromMove(move);
                newBoard.setCreatedByMove(move);

                String intermediateResult = quiescenceSearchv1(newBoard, alpha, beta, true);

                int currentEval = Integer.parseInt(intermediateResult.substring(4));

                if (currentEval <= beta){
                    bestMove = move;
                    beta = currentEval;
                }

                if(bestMove.equals("")){
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

    public static int getMoveCount(String list){

            return (list.replace("-","").length()/4);

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

    public String checkBishopMoves(Board board){

            return slidingPieces.bishopMoves(board);

        }

        public String checkRookMoves(Board board){

            return slidingPieces.rookMoves(board);
        }

        public String checkQueenMoves(Board board){

            return slidingPieces.queenMoves(board);
        }

        public String checkpawnMoves(Board board){

            return pawns.moves(board);
        }

        public String checkKingMove(Board board){

            return king.moves(board);
        }

        public String checkValidKingMoves(Board b){

            String validMoves = "";

            String moves = checkKingMove(b);


            for (int i=0;i<moves.length();i+=4) {

                Board tempB = new Board(b);

                tempB.setWhitePawns(executeMoveforOneBitboard(b.getWhitePawns(), moves.substring(i,i+4), 'P'));
                tempB.setWhiteKnights(executeMoveforOneBitboard(b.getWhiteKnights(), moves.substring(i,i+4), 'N'));
                tempB.setWhiteBishops(executeMoveforOneBitboard(b.getWhiteBishops(), moves.substring(i,i+4), 'B'));
                tempB.setWhiteRooks(executeMoveforOneBitboard(b.getWhiteRooks(), moves.substring(i,i+4), 'R'));
                tempB.setWhiteQueen(executeMoveforOneBitboard(b.getWhiteQueen(), moves.substring(i,i+4), 'Q'));
                tempB.setWhiteKing(executeMoveforOneBitboard(b.getWhiteKing(), moves.substring(i,i+4), 'K'));
                tempB.setBlackPawns(executeMoveforOneBitboard(b.getBlackPawns(), moves.substring(i,i+4), 'p'));
                tempB.setBlackKnights(executeMoveforOneBitboard(b.getBlackKnights(), moves.substring(i,i+4), 'n'));
                tempB.setBlackBishops(executeMoveforOneBitboard(b.getBlackBishops(), moves.substring(i,i+4), 'b'));
                tempB.setBlackRooks(executeMoveforOneBitboard(b.getBlackRooks(), moves.substring(i,i+4), 'r'));
                tempB.setBlackQueen(executeMoveforOneBitboard(b.getBlackQueen(), moves.substring(i,i+4), 'q'));
                tempB.setBlackKing(executeMoveforOneBitboard(b.getBlackKing(), moves.substring(i,i+4), 'k'));
                //tempB.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));
                tempB.setEnPassants(executeEnPassant(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));

                tempB.setWhiteRooks(executeCastling(tempB.getWhiteRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'R'));
                tempB.setBlackRooks(executeCastling(tempB.getBlackRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'r'));

                tempB.setWhiteToCastleKingside(b.isWhiteToCastleKingside());
                tempB.setWhiteToCastleQueenside(b.isWhiteToCastleQueenside());
                tempB.setBlackToCastleKingside(b.isBlackToCastleKingside());
                tempB.setBlackToCastleQueenside(b.isBlackToCastleQueenside());
                if (Character.isDigit(moves.charAt(3))) {//'regular' move
                    int start=(Character.getNumericValue(moves.charAt(i))*8)+(Character.getNumericValue(moves.charAt(i+1)));
                    if (((1L<<start)&b.getWhiteKing())!=0) {tempB.setWhiteToCastleKingside(false); tempB.setWhiteToCastleQueenside(false);}
                    if (((1L<<start)&b.getBlackKing())!=0) {tempB.setBlackToCastleKingside(false); tempB.setBlackToCastleQueenside(false);}
                    if (((1L<<start)&b.getWhiteRooks()&(1L<<63))!=0) {tempB.setWhiteToCastleKingside(false);}
                    if (((1L<<start)&b.getWhiteRooks()&(1L<<56))!=0) {tempB.setWhiteToCastleQueenside(false);}
                    if (((1L<<start)&b.getBlackRooks()&(1L<<7))!=0) {tempB.setBlackToCastleKingside(false);}
                    if (((1L<<start)&b.getBlackRooks()&1L)!=0) {tempB.setBlackToCastleQueenside(false);}
                }

                tempB.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());


                //check if own King is NOT in danger after move
                if (((tempB.getWhiteKing()& fieldsAttackedByBlack(tempB))==0 && b.isCurrentPlayerIsWhite()) ||
                        ((tempB.getBlackKing()& fieldsAttackedByWhite(tempB))==0 && !b.isCurrentPlayerIsWhite())) {
                    //add current move to validMoves if king is not in danger
                    validMoves += moves.substring(i,i+4);

                }
            }

            return validMoves;

        }



/*
        public static void drawBitboard(long bitBoard) {
            String chessBoard[][]=new String[8][8];
            for (int i=0;i<64;i++) {
                chessBoard[i/8][i%8]="";
            }
            for (int i=0;i<64;i++) {
                if (((bitBoard>>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
                if ("".equals(chessBoard[i/8][i%8])) {chessBoard[i/8][i%8]=" ";}
            }
            for (int i=0;i<8;i++) {
                System.out.println(Arrays.toString(chessBoard[i]));
            }
        }

        public static void tEMethodA(int loopLength, Board b) {
            for (int loop=0;loop<loopLength;loop++)
            {
                long PAWN_MOVES=(b.getOwnPawns()>>7) & b.getOppositePieces() & ~RANK_8 & ~FILE_A;//capture right
                String list="";
                for (int i=0;i<64;i++) {
                    if (((PAWN_MOVES>>i)&1)==1) {
                        list+=""+(i/8+1)+(i%8-1)+(i/8)+(i%8);
                    }
                }
            }
        }

        public static void tEMethodB(int loopLength, Board b) {
            for (int loop=0;loop<loopLength;loop++)
            {
                long PAWN_MOVES=(b.getOwnPawns()>>7)&b.getOppositePieces()&~RANK_8&~FILE_A;//capture right
                String list="";
                long possibility=PAWN_MOVES&~(PAWN_MOVES-1);
                while (possibility != 0)
                {
                    int index=Long.numberOfTrailingZeros(possibility);
                    list+=""+(index/8+1)+(index%8-1)+(index/8)+(index%8);
                    PAWN_MOVES&=~(possibility);
                    possibility=PAWN_MOVES&~(PAWN_MOVES-1);
                }
            }
        }
*/


}
