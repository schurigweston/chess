package dataaccess;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySQLDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class MyDatabaseTests {

    private static DataAccess db;
    private static UserData user1 = new UserData("john", "pass", "nrg");

    @BeforeAll
    public static void startServer() throws DataAccessException {
        db = new MySQLDataAccess();
    }

    @AfterAll
    public static void stopServer() throws DataAccessException {
        db.clear();
    }

    @Test
    void testClear() throws DataAccessException{ //relies on createUser and getUser. Make sure those also work.
        db.createUser(user1);
        db.clear();
        assertNull(db.getUser(user1.username()));
    }


}