package Model;

import Model.Pieces.King;
import Model.Pieces.Knights;
import Model.Pieces.Pawns;
import Model.Pieces.SlidingPieces;

import java.util.Arrays;
import static Model.Mask.*;


public class MoveGenerator {


    //TODO: why eigtl. non static?


        Pawns pawns = new Pawns();
        SlidingPieces slidingPieces = new SlidingPieces();
        Knights knights = new Knights();
        King king = new King();


        public String ownPossibleMoves(String history, Board board) {
            String list = "";
            long startTime = System.currentTimeMillis();
            long maxtime = 10000L;
            while (true) {

                list += pawns.moves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += knights.moves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += king.moves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += slidingPieces.rookMoves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;


                list += slidingPieces.bishopMoves(history, board) + "-";
                if (System.currentTimeMillis() - startTime > maxtime) break;

                list += slidingPieces.queenMoves(history, board);
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
            possibility=slidingPieces.getDiagonalMoves(iLocation, b);
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
            possibility=slidingPieces.getHorrizontalAndVerticalMoves(iLocation, b);
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
        long BN = b.getBlackKnights()
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
            possibility= slidingPieces.getDiagonalMoves(iLocation, b);
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
            possibility= slidingPieces.getHorrizontalAndVerticalMoves(iLocation, b);
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



    //TODO: Methode, die Move auswählt und tatsächlich ausführt -> "makeAllMoves" aufruft
    /*
    long WPt=Moves.makeMove(WP, moves.substring(i,i+4), 'P'), WNt=Moves.makeMove(WN, moves.substring(i,i+4), 'N'),
    WBt=Moves.makeMove(WB, moves.substring(i,i+4), 'B'), WRt=Moves.makeMove(WR, moves.substring(i,i+4), 'R'),
    WQt=Moves.makeMove(WQ, moves.substring(i,i+4), 'Q'), WKt=Moves.makeMove(WK, moves.substring(i,i+4), 'K'),
    BPt=Moves.makeMove(BP, moves.substring(i,i+4), 'p'), BNt=Moves.makeMove(BN, moves.substring(i,i+4), 'n'),
    BBt=Moves.makeMove(BB, moves.substring(i,i+4), 'b'), BRt=Moves.makeMove(BR, moves.substring(i,i+4), 'r'),
    BQt=Moves.makeMove(BQ, moves.substring(i,i+4), 'q'), BKt=Moves.makeMove(BK, moves.substring(i,i+4), 'k'),
    EPt=Moves.makeMoveEP(moves.substring(i,i+4));
    */

    //TODO: Methode, die alle Bitboards ändert
    //TODO: setterMethoden in Klasse Board
    public void makeAllMoves(Board b, String move){
       // b.setWhitePawns() = makeMove(b.getWhitePawns(), move, 'P');
        //.... (siehe oben)
    }

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
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[6]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[7]);
            } else {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[1]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[0]);
            }
            if (type==move.charAt(2)) {board&=~(1L<<start); board|=(1L<<end);} else {board&=~(1L<<end);}
        } else if (move.charAt(3)=='E') {//en passant
            int start, end;
            if (Character.isUpperCase(move.charAt(2))) {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[4]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[5]);
                board&=~(1L<<(FileMasks8[move.charAt(1)-'0']&RankMasks8[4]));
            } else {
                start=Long.numberOfTrailingZeros(FileMasks8[move.charAt(0)-'0']&RankMasks8[3]);
                end=Long.numberOfTrailingZeros(FileMasks8[move.charAt(1)-'0']&RankMasks8[2]);
                board&=~(1L<<(FileMasks8[move.charAt(1)-'0']&RankMasks8[3]));
            }
            if (((board>>>start)&1)==1) {board&=~(1L<<start); board|=(1L<<end);}
        } else {
            System.out.print("ERROR: Invalid move type");
        }
        return board;
    }
    public static long makeMoveEP(long board,String move) {
        if (Character.isDigit(move.charAt(3))) {
            int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
            if ((Math.abs(move.charAt(0)-move.charAt(2))==2)&&(((board>>>start)&1)==1)) {//pawn double push
                return FileMasks8[move.charAt(1)-'0'];
            }
        }
        return 0;
    }

        public int getMoveCount(String list){
            
            return (list.replace("-","").length()/4);

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
