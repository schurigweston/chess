package dataaccess;

import datamodel.*;
import org.eclipse.jetty.server.Authentication;

public interface DataAccess {

    void createUser(UserData user) throws DataAccessException;
}
