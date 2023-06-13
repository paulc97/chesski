package Model.Pieces;

import Model.Board;

import static Model.Mask.*;



public class SlidingPieces  implements Piece{

    public String rookMoves(Board b) {

        long allPieces = b.getAllPieces();
        String rookMoves="";
        if (b.isCurrentPlayerIsWhite()){
            long whiteRooks = b.getWhiteRooks();
            long counter=whiteRooks&~(whiteRooks-1);
            long option;

            while(counter != 0)
            {
                int location=Long.numberOfTrailingZeros(counter);
                option=horizonralAndVerticalMoves(location,allPieces)&~(b.getWhitePieces()|b.getBlackKing());
                long i=option&~(option-1);
                while (i != 0)
                {
                    int index=Long.numberOfTrailingZeros(i);
                    rookMoves+=""+(location/8)+(location%8)+(index/8)+(index%8);
                    option&=~i;
                    i=option&~(option-1);
                }
                whiteRooks&=~counter;
                counter=whiteRooks&~(whiteRooks-1);
            }
        }else{
            long blackRooks = b.getBlackRooks();
            long i=blackRooks&~(blackRooks-1);
            long option;

            while(i != 0)
            {
                int location=Long.numberOfTrailingZeros(i);
                option=horizonralAndVerticalMoves(location,allPieces)&~(b.getBlackPieces()|b.getWhiteKing());
                long j=option&~(option-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    rookMoves+=""+(location/8)+(location%8)+(index/8)+(index%8);
                    option&=~j;
                    j=option&~(option-1);
                }
                blackRooks&=~i;
                i=blackRooks&~(blackRooks-1);
            }

        }
        return rookMoves;
    }

    public String bishopMoves(Board b) {

        long OCCUPIED = b.getAllPieces();
        String moves="";
        if (b.isCurrentPlayerIsWhite()){
            long whiteBishop = b.getWhiteBishops();
            long i=whiteBishop&~(whiteBishop-1);
            long option;

            while(i != 0)
            {
                int location=Long.numberOfTrailingZeros(i);
                option=DaigonalMoves(location,OCCUPIED)&~(b.getWhitePieces()|b.getBlackKing());
                long j=option&~(option-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    moves+=""+(location/8)+(location%8)+(index/8)+(index%8);
                    option&=~j;
                    j=option&~(option-1);
                }
                whiteBishop&=~i;
                i=whiteBishop&~(whiteBishop-1);
            }
        }else{
            long blackBishop = b.getBlackBishops();
            long i=blackBishop&~(blackBishop-1);
            long option;

            while(i != 0)
            {
                int location=Long.numberOfTrailingZeros(i);
                option=DaigonalMoves(location,OCCUPIED)&~(b.getBlackPieces()|b.getWhiteKing());
                long j=option&~(option-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    moves+=""+(location/8)+(location%8)+(index/8)+(index%8);
                    option&=~j;
                    j=option&~(option-1);
                }
                blackBishop&=~i;
                i=blackBishop&~(blackBishop-1);
            }

        }
        return moves;

    }

    public String queenMoves(Board b) {

        long OCCUPIED = b.getAllPieces();
        String moves="";
        if (b.isCurrentPlayerIsWhite()){
            long whiteQueen = b.getWhiteQueen();
            long i=whiteQueen&~(whiteQueen-1);
            long option;

            while(i != 0)
            {
                int location=Long.numberOfTrailingZeros(i);
                option=(horizonralAndVerticalMoves(location,OCCUPIED)|DaigonalMoves(location,OCCUPIED))&~(b.getWhitePieces()|b.getBlackKing());
                long j=option&~(option-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    moves+=""+(location/8)+(location%8)+(index/8)+(index%8);
                    option&=~j;
                    j=option&~(option-1);
                }
                whiteQueen&=~i;
                i=whiteQueen&~(whiteQueen-1);
            }
        }else{
            long blackQueen = b.getBlackQueen();
            long i=blackQueen&~(blackQueen-1);
            long option;

            while(i != 0)
            {
                int location=Long.numberOfTrailingZeros(i);
                option=(horizonralAndVerticalMoves(location,OCCUPIED)|DaigonalMoves(location,OCCUPIED))&~(b.getBlackPieces()|b.getWhiteKing());
                long j=option&~(option-1);
                while (j != 0)
                {
                    int index=Long.numberOfTrailingZeros(j);
                    moves+=""+(location/8)+(location%8)+(index/8)+(index%8);
                    option&=~j;
                    j=option&~(option-1);
                }
                blackQueen&=~i;
                i=blackQueen&~(blackQueen-1);
            }

        }
        return moves;

    }


    public static long DaigonalMoves(int location,long OCCUPIED)
    {
        long binaryS=1L<<location;
        long diagonal = ((OCCUPIED&DiagonalMasks8[(location / 8) + (location % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(OCCUPIED&DiagonalMasks8[(location / 8) + (location % 8)]) - (2 * Long.reverse(binaryS)));
        long antidiagonal = ((OCCUPIED&AntiDiagonalMasks8[(location / 8) + 7 - (location % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(OCCUPIED&AntiDiagonalMasks8[(location / 8) + 7 - (location % 8)]) - (2 * Long.reverse(binaryS)));
        return (diagonal&DiagonalMasks8[(location / 8) + (location % 8)]) | (antidiagonal&AntiDiagonalMasks8[(location / 8) + 7 - (location % 8)]);
    }

    public static long horizonralAndVerticalMoves(int location,long OCCUPIED)
    {
        long binaryS=1L<<location;
        long horizontal = (OCCUPIED - 2 * binaryS) ^ Long.reverse(Long.reverse(OCCUPIED) - 2 * Long.reverse(binaryS));
        long vertical = ((OCCUPIED& ColumnMasks8[location % 8]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(OCCUPIED& ColumnMasks8[location % 8]) - (2 * Long.reverse(binaryS)));
        return (horizontal& RowMasks8[location / 8]) | (vertical& ColumnMasks8[location % 8]);

    }


    @Override
    public String moves(Board board) {
        return null;
    }
}
