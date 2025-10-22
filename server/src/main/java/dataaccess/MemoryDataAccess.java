package dataaccess;

import datamodel.*;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    Map<String, UserData> users = new HashMap<>();
    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }
}
