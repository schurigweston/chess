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
    private boolean isInBounds(ChessPosition potentialPosition){
        //Both row and column are between 1 and 8
        return potentialPosition.getRow() >= 1 && potentialPosition.getRow() <= 8 && potentialPosition.getColumn() >= 1 && potentialPosition.getColumn() <= 8;
    }
    private boolean isValidMovePosition(ChessBoard board, ChessPosition potentialPosition, ChessGame.TeamColor color){

        if(isInBounds(potentialPosition)){//Both row and column are between 1 and 8
            ChessPiece potentialPositionPiece = board.getPiece(potentialPosition);
            //If the space is empty or occupied by a piece not of the same color.
            return potentialPositionPiece == null || !color.equals(potentialPositionPiece.getTeamColor());
        }
        return false;
    }

    private Collection<ChessMove> getSlideMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean recurse){
        List<ChessMove> moves = new ArrayList<>();

        for(int[] direction : directions){
            int rowDir = direction[0];
            int colDir = direction[1];
            int distance = 1;
            ChessPosition potentialPosition = myPosition;
            do{
                potentialPosition = new ChessPosition(myPosition.getRow() + rowDir * distance, myPosition.getColumn() + colDir * distance);

                if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){//If it's a valid position, add piece, but stop if the position is on a piece.
                    moves.add(new ChessMove(myPosition, potentialPosition, null));

                    if(board.getPiece(potentialPosition) != null) {
                        break;
                    }
                } else {
                    break;
                }

                distance++;

            } while(recurse);
        }
        return moves;
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,0},{0,1},{1,-1},{1,0},{1,1}};
        return getSlideMoves(board,myPosition, directions,false);
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,0},{0,1},{1,-1},{1,0},{1,1}};
        return getSlideMoves(board,myPosition, directions,true);
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
                        moves.add(new ChessMove(myPosition, potentialPosition, null));
                        if (board.getPiece(potentialPosition)!=null){
                            break;
                        }
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

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if(!((i==0)==(j==0))){ //xor i and j. if
                    int distance = 1;
                    while(true) {
                        potentialPosition = new ChessPosition(myPosition.getRow()+i*distance,myPosition.getColumn()+j*distance);//Position to consider

                        if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                            moves.add(new ChessMove(myPosition, potentialPosition, null));
                            if (board.getPiece(potentialPosition)!=null){
                                break;
                            }
                        }else{
                            break;
                        }
                        distance++;
                    }
                }

            }
        }
        return moves;



    }
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;

        int[][] knightMoves = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for(int[] move : knightMoves){
            potentialPosition = new ChessPosition(myPosition.getRow()+move[0],myPosition.getColumn()+move[1]);//Position to consider

            if(isValidMovePosition(board, potentialPosition, board.getPiece(myPosition).getTeamColor())){
                moves.add(new ChessMove(myPosition, potentialPosition, null));
            }
        }
        return moves;
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        ChessPosition potentialPosition;

        int direction = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startingRow = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotableRow = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 7 : 2;

        potentialPosition = new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn());//Consider the spot right it front
        if(board.getPiece(potentialPosition) == null){//the spot in front is empty
            moves.add(new ChessMove(myPosition, potentialPosition, null));//add move
            potentialPosition = new ChessPosition(myPosition.getRow()+2*direction,myPosition.getColumn());
            if(myPosition.getRow()==startingRow&&(board.getPiece(potentialPosition) == null)) {//If it's the pawn's first move and the spot's open,
                moves.add(new ChessMove(myPosition,potentialPosition, null));//add move
            }
        }

        for (int i = -1; i <=1 ; i+=2) {//for the pawn's left and right side
            potentialPosition = new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+i);
            if(isInBounds(potentialPosition)&& board.getPiece(potentialPosition)!=null&& board.getPiece(potentialPosition).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){//If the side corners are in bounds and the piece isn't null and the piece is not the same color...
                moves.add(new ChessMove(myPosition,potentialPosition, null));
            }
        }

        if(myPosition.getRow()==promotableRow) {//adds promotion pieces
            List<ChessMove> promotionMoves = new ArrayList<>();
            for(ChessMove move:moves){
                promotionMoves.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),PieceType.KNIGHT));
                promotionMoves.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),PieceType.ROOK));
                promotionMoves.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),PieceType.BISHOP));
                promotionMoves.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),PieceType.QUEEN));
            }
            moves.clear();
            moves.addAll(promotionMoves);
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
