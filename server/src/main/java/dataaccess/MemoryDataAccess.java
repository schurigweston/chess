package dataaccess;

import datamodel.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    Map<String, UserData> users = new HashMap<>();
    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    public Collection<UserData> listUsers(){
        return users.values();
    }

    public void clear(){
        users = new HashMap<String, UserData>();
    }
}
