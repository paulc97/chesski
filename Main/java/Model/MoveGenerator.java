package Model;

import Model.Pieces.King;
import Model.Pieces.Knights;
import Model.Pieces.Pawns;
import Model.Pieces.SlidingPieces;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.Arrays;
import static Model.Mask.*;


public class MoveGenerator {


    //TODO: why eigtl. non static?


        Pawns pawns = new Pawns();
        SlidingPieces slidingPieces = new SlidingPieces();
        Knights knights = new Knights();
        King king = new King();


        public String ownPossibleMoves(Board board) {
            String list = "";
            long startTime = System.currentTimeMillis();
            long maxtime = 10000L; //TODO: Zeitmanagement in validMoves Methode?
            while (true) {

                list += pawns.moves(board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += knights.moves(board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += king.moves(board) + "-"; //includes castling
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += slidingPieces.rookMoves(board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;


                list += slidingPieces.bishopMoves(board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += slidingPieces.queenMoves(board);
                break;
            }

            //TODO: Remove king in check/check mate situations from possible move list


            //format: rank-old file-old rank-new file-new
            //rank wird nach oben größer, file nach links
            //6050 wäre move von (Bitboard Layout 1) 48 auf 40

            return list;
        }
        //TODO: possible moves der gegenseite?


    //gibt Bitboard mit allen Positionen auf denen White Landen könnte (inkl. eigener weißer Figuren)
    // wenn dann mit BlackKing-Bitboard verundet (&) eine 1 ergibt, könnte BlackKing geschlagen
    public long unsafeForBlack(Board b) {
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
            possibility=slidingPieces.DAndAntiDMoves(iLocation,b.getAllPieces());
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
            possibility=slidingPieces.HAndVMoves(iLocation,b.getAllPieces());
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
    public long unsafeForWhite(Board b) {
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
            possibility=slidingPieces.DAndAntiDMoves(iLocation,b.getAllPieces());
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
            possibility=slidingPieces.HAndVMoves(iLocation,b.getAllPieces());
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


    //TODO: static or non-static?
    //ändert für 1 Bitboard ("long board") die Positionen, nachdem "String move" ausgeführt wurde
    public static long makeMove(long board, String move, char type) {
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

    public static long makeMoveCastle(long rookBoard, long kingBoard, String move, char type) {
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
    public static long makeMoveEP(long board,String move) {
        if (Character.isDigit(move.charAt(3))) {
            int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
            if ((Math.abs(move.charAt(0)-move.charAt(2))==2)&&(((board>>>start)&1)==1)) {//pawn double push
                //board, to be sure that type is a pawn


                return FileMasks8[move.charAt(1)-'0']; //return file on which that pawn push occured on
            }
        }
        return 0; //-> no en passant allowed
    }

    public static String makeMoveEPString(long board,String move) {
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
    }




    //"valid" i.e. eigener King ist danach nicht (mehr) im Schach
    //public static void perft(long WP,long WN,long WB,long WR,long WQ,long WK,long BP,long BN,long BB,long BR,long BQ,long BK,long EP,boolean CWK,boolean CWQ,boolean CBK,boolean CBQ,boolean WhiteToMove,int depth)
    public String validMoves(Board b)
    {
        String validMoves = "";

        String moves = ownPossibleMoves(b).replace("-","");

        for (int i=0;i<moves.length();i+=4) {

            Board tempB = new Board(b.getFen());

            tempB.setWhitePawns(makeMove(b.getWhitePawns(), moves.substring(i,i+4), 'P'));
            tempB.setWhiteKnights(makeMove(b.getWhiteKnights(), moves.substring(i,i+4), 'N'));
            tempB.setWhiteBishops(makeMove(b.getWhiteBishops(), moves.substring(i,i+4), 'B'));
            tempB.setWhiteRooks(makeMove(b.getWhiteRooks(), moves.substring(i,i+4), 'R'));
            tempB.setWhiteQueen(makeMove(b.getWhiteQueen(), moves.substring(i,i+4), 'Q'));
            tempB.setWhiteKing(makeMove(b.getWhiteKing(), moves.substring(i,i+4), 'K'));
            tempB.setBlackPawns(makeMove(b.getBlackPawns(), moves.substring(i,i+4), 'p'));
            tempB.setBlackKnights(makeMove(b.getBlackKnights(), moves.substring(i,i+4), 'n'));
            tempB.setBlackBishops(makeMove(b.getBlackBishops(), moves.substring(i,i+4), 'b'));
            tempB.setBlackRooks(makeMove(b.getBlackRooks(), moves.substring(i,i+4), 'r'));
            tempB.setBlackQueen(makeMove(b.getBlackQueen(), moves.substring(i,i+4), 'q'));
            tempB.setBlackKing(makeMove(b.getBlackKing(), moves.substring(i,i+4), 'k'));
            tempB.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));
            tempB.setEnPassants(makeMoveEPString(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));

            tempB.setWhiteRooks(makeMoveCastle(tempB.getWhiteRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'R'));
            tempB.setBlackRooks(makeMoveCastle(tempB.getBlackRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'r'));

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
                if (((tempB.getWhiteKing()&unsafeForWhite(tempB))==0 && b.isCurrentPlayerIsWhite()) ||
                        ((tempB.getBlackKing()&unsafeForBlack(tempB))==0 && !b.isCurrentPlayerIsWhite())) {
                    //add current move to validMoves if king is not in danger
                    validMoves += moves.substring(i,i+4);

                }
            }

        return validMoves;

    }

    public void selectAndMakeMove(Board b, String validMoves){
        // String move = validMoves.substring(0,4); //-> ersten Move nehmen


        //zufällige Zugauswahl
        //Min + (int)(Math.random() * ((Max - Min) + 1))
        int randomEndIndex = (1 + (int)(Math.random() * ((validMoves.length()/4 - 1) + 1)))*4; //TODO: dürfen wir Math.random benutzen?
        String move = validMoves.substring(randomEndIndex-4,randomEndIndex);

        //TODO: Error abfangen, wenn keine Moves mehr möglich? Spiel zuende

        //Debugging:
        System.out.println("I made this move: "+move+ " which is: "+convertMoveDigitsToField(move.charAt(0),move.charAt(1))+"->"+convertMoveDigitsToField(move.charAt(2),move.charAt(3)));


            String oldFEN = b.getFen().split(" ")[0]; //für 50-Zug-Remis-Regel
        long oldWhitePawns = b.getWhitePawns();
        long oldBlackPawns = b.getBlackPawns();

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
        b.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),move));
        b.setEnPassants(makeMoveEPString(b.getWhitePawns()|b.getBlackPawns(),move));

        //Castling auch vor Änderung der King Position??
        b.setWhiteRooks(makeMoveCastle(b.getWhiteRooks(), b.getWhiteKing()|b.getBlackKing(), move, 'R'));
        b.setBlackRooks(makeMoveCastle(b.getBlackRooks(), b.getWhiteKing()|b.getBlackKing(), move, 'r'));

        b.setWhitePawns(makeMove(b.getWhitePawns(), move, 'P'));
        b.setWhiteKnights(makeMove(b.getWhiteKnights(), move, 'N'));
        b.setWhiteBishops(makeMove(b.getWhiteBishops(), move, 'B'));
        b.setWhiteRooks(makeMove(b.getWhiteRooks(), move, 'R'));
        b.setWhiteQueen(makeMove(b.getWhiteQueen(), move, 'Q'));
        b.setWhiteKing(makeMove(b.getWhiteKing(), move, 'K'));
        b.setBlackPawns(makeMove(b.getBlackPawns(), move, 'p'));
        b.setBlackKnights(makeMove(b.getBlackKnights(), move, 'n'));
        b.setBlackBishops(makeMove(b.getBlackBishops(), move, 'b'));
        b.setBlackRooks(makeMove(b.getBlackRooks(), move, 'r'));
        b.setBlackQueen(makeMove(b.getBlackQueen(), move, 'q'));
        b.setBlackKing(makeMove(b.getBlackKing(), move, 'k'));




        b.setCurrentPlayerIsWhite(!b.isCurrentPlayerIsWhite());

        if(b.getWhitePawns()!=oldWhitePawns||b.getBlackPawns()!=oldBlackPawns){ //wurde ein Bauer bewegt?(/geschlagen)
            b.setHalfMoveCount(-1);
        } else {  //wurde eine Figur geschlagen?
            String newFEN = b.getFen().split(" ")[0]; //für 50-Zug-Remis-Regel;

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

    }


        public int getMoveCount(String list){

            return (list.replace("-","").length()/4);

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

                Board tempB = new Board(b.getFen());

                tempB.setWhitePawns(makeMove(b.getWhitePawns(), moves.substring(i,i+4), 'P'));
                tempB.setWhiteKnights(makeMove(b.getWhiteKnights(), moves.substring(i,i+4), 'N'));
                tempB.setWhiteBishops(makeMove(b.getWhiteBishops(), moves.substring(i,i+4), 'B'));
                tempB.setWhiteRooks(makeMove(b.getWhiteRooks(), moves.substring(i,i+4), 'R'));
                tempB.setWhiteQueen(makeMove(b.getWhiteQueen(), moves.substring(i,i+4), 'Q'));
                tempB.setWhiteKing(makeMove(b.getWhiteKing(), moves.substring(i,i+4), 'K'));
                tempB.setBlackPawns(makeMove(b.getBlackPawns(), moves.substring(i,i+4), 'p'));
                tempB.setBlackKnights(makeMove(b.getBlackKnights(), moves.substring(i,i+4), 'n'));
                tempB.setBlackBishops(makeMove(b.getBlackBishops(), moves.substring(i,i+4), 'b'));
                tempB.setBlackRooks(makeMove(b.getBlackRooks(), moves.substring(i,i+4), 'r'));
                tempB.setBlackQueen(makeMove(b.getBlackQueen(), moves.substring(i,i+4), 'q'));
                tempB.setBlackKing(makeMove(b.getBlackKing(), moves.substring(i,i+4), 'k'));
                tempB.setEnPassantBitboardFile(makeMoveEP(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));
                tempB.setEnPassants(makeMoveEPString(b.getWhitePawns()|b.getBlackPawns(),moves.substring(i,i+4)));

                tempB.setWhiteRooks(makeMoveCastle(tempB.getWhiteRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'R'));
                tempB.setBlackRooks(makeMoveCastle(tempB.getBlackRooks(), b.getWhiteKing()|b.getBlackKing(), moves.substring(i,i+4), 'r'));

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
                if (((tempB.getWhiteKing()&unsafeForWhite(tempB))==0 && b.isCurrentPlayerIsWhite()) ||
                        ((tempB.getBlackKing()&unsafeForBlack(tempB))==0 && !b.isCurrentPlayerIsWhite())) {
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
