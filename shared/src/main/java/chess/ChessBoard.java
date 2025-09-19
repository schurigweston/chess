package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board;
    public static void main(String[] args) {
        ChessBoard board1 = new ChessBoard();
        ChessBoard board2 = new ChessBoard();

        // They should be equal initially
        System.out.println("Empty boards equal? " + board1.equals(board2)); // true

        // Add a piece to board1
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board1.addPiece(new ChessPosition(2, 2), whitePawn);

        // Now they should NOT be equal
        System.out.println("After adding a pawn to board1, boards equal? " + board1.equals(board2)); // false

        // Add the same piece to board2 in the same position
        ChessPiece whitePawn2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board2.addPiece(new ChessPosition(2, 2), whitePawn2);

        // Now they should be equal again
        System.out.println("After adding the same pawn to board2, boards equal? " + board1.equals(board2)); // true

        // Add a different piece to board2
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board2.addPiece(new ChessPosition(3, 3), blackKnight);

        // Boards should no longer be equal
        System.out.println("After adding a black knight to board2, boards equal? " + board1.equals(board2)); // false

        // Print the boards for visual verification
        System.out.println("\nBoard 1:");
        System.out.println(board1.toStringBoard());
        System.out.println("\nBoard 2:");
        System.out.println(board2.toStringBoard());
    }
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece; //Subtract 1 because of 0 index, but rows and columns are 1-8, not 0-7.
        //Maybe add bounds checking and error handling later.
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        this.addPiece(new ChessPosition(8,1),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,8),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,2),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,7),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,3),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,6),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,4),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8,5),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

        this.addPiece(new ChessPosition(1,1),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,8),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,2),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,7),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,3),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,6),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,4),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1,5),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        for (int i = 1; i <= 8 ; i++) {
            this.addPiece(new ChessPosition(7,i),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(2,i),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        if (this == o) return true;
        //System.out.println("this board:\n" + this.toStringBoard());
        //System.out.println("that board:\n" + that.toStringBoard());
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece thisPiece = this.getPiece(new ChessPosition(i,j));
                ChessPiece thatPiece = that.getPiece(new ChessPosition(i,j));

                if (!Objects.equals(thisPiece, thatPiece)) {
                    return false;
                }

            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        //System.out.println(this.toStringBoard());
        return Arrays.deepHashCode(board);
    }

    public String toStringBoard() {
        StringBuilder boardString = new StringBuilder(); //♙♘♗♖♕♔♚♛♜♝♞♟░█
        for (int i = 8; i >=1; i--) {
            for (int j = 1; j <= 8; j++) {

                ChessPiece thisPiece = this.getPiece(new ChessPosition(i,j));
                if(thisPiece==null){
                    if((i+j)%2==1){//white square. odd = white, even = black.
                        boardString.append("░");
                    }else{
                        boardString.append("█");}
                }else {
                    boardString.append(thisPiece.getCharacter());
                }
            }
            boardString.append("\n");

        }
        return boardString.toString();
    }
}
