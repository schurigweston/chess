package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public interface DataAccess {

    void clear();

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username);

    Collection<UserData> listUsers();

    void createGame(GameData game);

    ChessGame getGame(String gameID);

    Collection<GameData> listGames();

    void updateGame(); // needs more

    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(AuthData authData);
}
