package dataaccess;

import model.*;

import java.util.Collection;
import java.util.HashMap;
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

    public Collection<UserData> listUsers(){
        return users.values();
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.username(), authData);
    }

}
