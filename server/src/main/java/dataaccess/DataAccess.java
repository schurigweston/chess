package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public interface DataAccess {

    void clear();

    void createUser(UserData user);

    UserData getUser(String username);

    Collection<UserData> listUsers();

    int createGame(String gameName);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(GameData updatedGame);

    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(AuthData authData);
}
