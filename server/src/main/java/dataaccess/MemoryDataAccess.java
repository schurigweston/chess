package dataaccess;

import chess.ChessGame;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    Map<String, UserData> users = new HashMap<>();
    Map<String, AuthData> auths = new HashMap<>();
    Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        users.put(user.username(), new UserData(user.username(), hashedPassword, user.email()));
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    public Collection<UserData> listUsers(){
        return users.values();
    }

    @Override
    public int createGame(String gameName) {
        int gameID = nextGameID++;
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        GameData gameData = games.get(gameID);
        return gameData;
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData updatedGame) {
        games.put(updatedGame.gameID(), updatedGame);
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
