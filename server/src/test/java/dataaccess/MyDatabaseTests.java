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

    @Test
    void testCreateUser() throws DataAccessException{
        try {
            db.createUser(user1);
        } catch (Exception e) {
            Assertions.fail("createUser threw an exception: " + e.getMessage());
        }

    }

    @Test
    void testCreateDuplicateUser() throws DataAccessException{
        boolean excepted = false;
        try {
            db.createUser(user1);
            db.createUser(user1);
        } catch (Exception e) {
            excepted = true;
        }

        if(!excepted){
            Assertions.fail("Expected DataAccessException on creating Duplicate user");
        }


    }


    void testGetUser() throws DataAccessException{

    }

    void testGetNullUser() throws DataAccessException{

    }

}