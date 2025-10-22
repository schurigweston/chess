package dataaccess;

import datamodel.*;
import org.eclipse.jetty.server.Authentication;

import java.util.Collection;

public interface DataAccess {

    void createUser(UserData user) throws DataAccessException;


    Collection<UserData> listUsers();

    void clear();
}
