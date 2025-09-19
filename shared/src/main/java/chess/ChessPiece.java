package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPosition placeHolderPosition = new ChessPosition(1,1);
        ChessPosition placeHolderPosition2 = new ChessPosition(2,2);
        return List.of(new ChessMove(placeHolderPosition,placeHolderPosition2,PieceType.KNIGHT));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    @Override
    public String toString() {
        return "Piece{" + pieceColor + " " + pieceType + '}';
    }
    public String getCharacter(){//♙♘♗♖♕♔♚♛♜♝♞♟
        if(this.pieceType==PieceType.PAWN){
            if(this.pieceColor== ChessGame.TeamColor.WHITE){
                return "♙";
            }else{
                return"♟";
            }
        }else if(this.pieceType==PieceType.KNIGHT){
            if(this.pieceColor== ChessGame.TeamColor.WHITE){
                return "♘";
            }else{
                return"♞";
            }
        }else if(this.pieceType==PieceType.BISHOP){
            if(this.pieceColor== ChessGame.TeamColor.WHITE){
                return "♗";
            }else{
                return"♝";
            }
        }else if(this.pieceType==PieceType.ROOK){
            if(this.pieceColor== ChessGame.TeamColor.WHITE){
                return "♖";
            }else{
                return"♜";
            }
        }else if(this.pieceType==PieceType.QUEEN){
            if(this.pieceColor== ChessGame.TeamColor.WHITE){
                return "♕";
            }else{
                return"♛";
            }
        }else if(this.pieceType==PieceType.KING){
            if(this.pieceColor== ChessGame.TeamColor.WHITE){
                return "♔";
            }else{
                return"♚";
            }
        }else{
            return "❎";
        }
    }
}
