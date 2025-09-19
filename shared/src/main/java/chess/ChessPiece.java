package chess;

import java.util.ArrayList;
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
        List<ChessMove> moves = new ArrayList<>();

        switch(board.getPiece(myPosition).getPieceType()){
            case KING:
                moves.addAll(kingMoves(board, myPosition));
                break;
            case QUEEN:
                moves.addAll(queenMoves(board, myPosition));
                break;
            case BISHOP:
                moves.addAll(bishopMoves(board, myPosition));
                break;
            case ROOK:
                moves.addAll(rookMoves(board, myPosition));
                break;
            case KNIGHT:
                moves.addAll(knightMoves(board, myPosition));
                break;
            case PAWN:
                moves.addAll(pawnMoves(board, myPosition));
                break;
            default:
                throw new IllegalArgumentException("Piece is... not a piece?");
        }

        return moves;
    }
    private boolean isValidMovePosition(ChessBoard board, ChessPosition potentialPosition, ChessGame.TeamColor color){

        if(potentialPosition.getRow()>=1&&potentialPosition.getRow()<=8&&potentialPosition.getColumn()>=1&&potentialPosition.getColumn()<=8){//Both row and column are between 1 and 8
            ChessPiece potentialPositionPiece = board.getPiece(potentialPosition);
            if(potentialPositionPiece==null||!color.equals(potentialPositionPiece.getTeamColor())){//If the space is empty or occupied by a piece not of the same color.
                return true;
            }
        }
        return false;
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;
        ChessPiece potentialPositionPiece;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                potentialPosition = new ChessPosition(myPosition.getRow()+i,myPosition.getColumn()+j);//Position to consider

                if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                    moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;


        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int distance = 1;
                while(true) {
                    potentialPosition = new ChessPosition(myPosition.getRow()+i*distance,myPosition.getColumn()+j*distance);//Position to consider

                    if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                        moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                    }else{
                        break;
                    }
                    distance++;
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                int distance = 1;
                while(true) {
                    potentialPosition = new ChessPosition(myPosition.getRow()+i*distance,myPosition.getColumn()+j*distance);//Position to consider

                    if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                        moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                    }else{
                        break;
                    }
                    distance++;
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;
        for (int i = -1; i <= 1; i+=2) {
                int distance = 1;
                while(true) {
                    potentialPosition = new ChessPosition(myPosition.getRow()+i*distance,myPosition.getColumn()+0*distance);//Position to consider

                    if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                        moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                    }else{
                        break;
                    }
                    distance++;
                }

        }
        for (int j = -1; j <= 1; j+=2) {
            int distance = 1;
            while(true) {
                potentialPosition = new ChessPosition(myPosition.getRow()+0*distance,myPosition.getColumn()+j*distance);//Position to consider

                if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                    moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                }else{
                    break;
                }
                distance++;
            }

        }
        return moves;
    }
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                int distance = 1;
                while(true) {
                    potentialPosition = new ChessPosition(myPosition.getRow()+i*distance,myPosition.getColumn()+j*distance);//Position to consider

                    if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                        moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                    }else{
                        break;
                    }
                    distance++;
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                int distance = 1;
                while(true) {
                    potentialPosition = new ChessPosition(myPosition.getRow()+i*distance,myPosition.getColumn()+j*distance);//Position to consider

                    if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                        moves.add(new ChessMove(myPosition, potentialPosition, PieceType.KNIGHT));
                    }else{
                        break;
                    }
                    distance++;
                }
            }
        }
        return moves;
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
