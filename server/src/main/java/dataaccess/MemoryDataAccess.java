package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    Map<String, UserData> users = new HashMap<>();
    Map<String, AuthData> auths = new HashMap<>();
    Map<Integer, GameData> games = new HashMap<>();

    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    public Collection<UserData> listUsers(){
        return users.values();
    }

    @Override
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public ChessGame getGame(String gameID) {
        GameData gameData = games.get(Integer.valueOf(gameID));
        return gameData != null ? gameData.game() : null;
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame() {

    }



    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        auths.remove(authData.authToken());
    }

}
