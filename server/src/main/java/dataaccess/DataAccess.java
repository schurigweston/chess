package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public interface DataAccess {

    void clear() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID)  throws DataAccessException;

    Collection<GameData> listGames();

    void updateGame(GameData updatedGame);

    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(AuthData authData);
}
