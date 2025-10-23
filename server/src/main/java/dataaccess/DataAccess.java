package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {

    void clear();

    void createUser(UserData user) throws DataAccessException;

    Collection<UserData> listUsers();

    UserData getUser(String username);

    void createAuth(AuthData authData);
}
