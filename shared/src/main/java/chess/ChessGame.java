package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamColor;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.getPiece(startPosition) == null){ //If that startPosition is empty, return empty valid moves. If that startPosition is en enemy piece, return empty.
            return new ArrayList<ChessMove>();
        }
        TeamColor pieceColor = board.getPiece(startPosition).getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        var potentialMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        ChessGame testGame;
        for(ChessMove move : potentialMoves){ //Make a new board for each move and make sure the move doesn't break any rules.
            testGame = this.copy();
            testGame.board.movePiece(move);

            if(!testGame.isInCheck(pieceColor)){
                //If that pieceColor is not in check after the potential move, then add it to valid moves.
                validMoves.add(move);

            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(!validMoves(move.getStartPosition()).contains(move) || !board.getPiece(move.getStartPosition()).getTeamColor().equals(teamColor)){// if the move isn't a valid move, or the piece is the wrong color...
            throw new InvalidMoveException("Waaaah boo hoo invalid move I'll come and make this a good exception later. ");
        }
        var pieceColor = board.getPiece(move.getStartPosition()).getTeamColor();
        var start = move.getStartPosition();
        var end = move.getEndPosition();
        var piece = board.getPiece(start);
        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(pieceColor, move.getPromotionPiece());
        }
        board.addPiece(end, piece);
        board.addPiece(start, null);

        teamColor = (teamColor.equals(TeamColor.WHITE)) ? TeamColor.BLACK : TeamColor.WHITE;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //for each piece of the opposing team color, get all moves, and check if any moves are on the king's position. If yes, it's in check. If no, then not.

        ChessPosition position;
        Collection<ChessPosition> kingsPositions = board.getPiecePositions(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                position = new ChessPosition(row, col);
                if (board.getPiece(position) != null && !board.getPiece(position).getTeamColor().equals(teamColor)) {//If that piece color is the opposite team...
                    //get the moves for that piece...
                    Collection<ChessMove> movesToCheck = board.getPiece(position).pieceMoves(board, position);
                    for (ChessPosition kingsPosition : kingsPositions) { //For the *probably* one king and his position...
                        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) { // For each possible promotion, besides null...
                            if (movesToCheck.contains(new ChessMove(position, kingsPosition, type))) {// If the movesToCheck contains a move from that position onto the king, including any promotion...
                                return true; //Then we are in check.
                            }
                        }
                        if (movesToCheck.contains(new ChessMove(position, kingsPosition, null))) {// If the movesToCheck contains a move from that position onto the king, not promoting...
                            return true; //Then we are in check.
                        }
                    }
                }
            }

        }
        return false;//We didn't find any moves that land on the king.
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    private boolean hasAnyValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (board.getPiece(position) != null &&
                        board.getPiece(position).getTeamColor().equals(teamColor) &&
                        !validMoves(position).isEmpty()) {
                    return true; // Found at least one valid move
                }
            }
        }
        return false; // No valid moves found
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return !hasAnyValidMoves(teamColor) && isInCheck(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return !hasAnyValidMoves(teamColor) && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board.copy();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamColor == chessGame.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamColor);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamColor=" + teamColor +
                '}';
    }

    public ChessGame copy() {
        ChessGame copy = new ChessGame();
        copy.board = board.copy();
        copy.teamColor = teamColor;
        return copy;
    }
}
