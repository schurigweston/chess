package dataaccess;

import chess.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class MyDatabaseTests {

    private static DataAccess db;
    private static UserData user1 = new UserData("john", "pass", "NRG");
    private static UserData user2 = new UserData("Greak", "pass", "Fnatic");
    private static UserData user3 = new UserData("Sarlot", "pass", "SEN");

    @BeforeAll
    public static void startServer() throws DataAccessException {
        db = new MySQLDataAccess();
    }

    @AfterAll
    public static void stopServer() throws DataAccessException {
        db.clear();
    }

    @BeforeEach
    public void clearDB() throws DataAccessException{
        db.clear();
    }

    @AfterEach
    public void clearDBEnd() throws DataAccessException{
        db.clear();
    }

    @Test
    void testClear() throws DataAccessException{ //relies on createUser and getUser. Make sure those also work.
        db.createUser(user1);
        db.createGame("A Game");
        db.clear();
        assertNull(db.getUser(user1.username()));
        assertNull(db.getGame(1));
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

    @Test
    void testGetUser() throws DataAccessException{
        db.createUser(user1);

        assertEquals(user1.username(), db.getUser(user1.username()).username());
        assertEquals(user1.email(), db.getUser(user1.username()).email());
    }

    @Test
    void testGetNullUser() throws DataAccessException{
        db.createUser(user1);

        assertNull(db.getUser("Not a user"));
    }

    @Test
    void testListUsers() throws DataAccessException{ //Doesn't require bad test becuase no parameters are passed.
        assertEquals(0, db.listUsers().size());
        db.createUser(user1);
        assertEquals(1, db.listUsers().size());
        db.createUser(user2);
        assertEquals(2, db.listUsers().size());
        db.createUser(user3);
        assertEquals(3, db.listUsers().size());

    }

    @Test
    void testCreateGame() throws DataAccessException{
        try {
            db.createGame("Game woooo");
        } catch (Exception e) {
            Assertions.fail("createGame threw an exception: " + e.getMessage());
        }
    }

    @Test //Test didn't pass, but maybe that's fine.
    void testCreateNullGame() throws DataAccessException{
        boolean excepted = false;
        try {
            db.createGame(null); //try to create null named game.
        } catch (Exception e) {
            excepted = true;
        }
        if(!excepted){
            Assertions.fail("Expected DataAccessException on creating null game");
        }
    }




    @Test
    void testGetGame() throws DataAccessException{
        int gameID = db.createGame("woo GAme");
        int secondGameID = db.createGame("2nd GAYme");
        GameData game = db.getGame(secondGameID);
        assertNotNull(game);
        assertEquals(secondGameID, game.gameID());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals(new ChessGame(), game.game());
        assertEquals("2nd GAYme", game.gameName());

    }

    @Test
    void testGetWrongGame() throws  DataAccessException{
        int gameID = db.createGame("woo GAme");
        GameData game = db.getGame(0);
        assertNull(game);
    }

    @Test
    void testGetGames() throws DataAccessException{
        db.createGame("woo GAme");
        db.createGame("2nd GAYme");
        assertEquals(2, db.listGames().size());
    }

    @Test
    void testUpdateGame() throws InvalidMoveException, DataAccessException {
        ChessGame game = new ChessGame();
        game.makeMove(new ChessMove(new ChessPosition(2,2), new ChessPosition(3,2), null));
        int gameID = db.createGame("zeGame");
        GameData updatedGame = new GameData(gameID, null, null, null, game);
        db.updateGame(updatedGame);
        GameData retrievedGame = db.getGame(gameID);

        assertNotNull(retrievedGame);
        assertEquals(gameID, retrievedGame.gameID());
        assertEquals(game, retrievedGame.game()); // Requires ChessGame.equals implemented
        assertNull(retrievedGame.whiteUsername());
        assertNull(retrievedGame.blackUsername());
        assertEquals("zeGame", retrievedGame.gameName());
    }

    @Test
    void testCreateAuth() throws DataAccessException {
        db.createUser(user1);
        AuthData auth = new AuthData("token123", "john");
        Assertions.assertDoesNotThrow(() -> db.createAuth(auth));

        AuthData fetched = db.getAuth("token123");
        assertNotNull(fetched);
        assertEquals("john", fetched.username());
        assertEquals("token123", fetched.authToken());
    }

    @Test
    void testCreateDuplicateAuth() throws DataAccessException {
        db.createUser(user1);
        AuthData auth = new AuthData("tokenDuplicate", "john");
        Assertions.assertDoesNotThrow(() -> db.createAuth(auth));

        Exception exception = Assertions.assertThrows(DataAccessException.class, () -> {
            db.createAuth(auth); // same token again
        });

        String message = exception.getMessage();
        assertTrue(message.contains("Duplicate") || message.contains("unable to update"));
    }

    @Test
    void testGetAuth() throws DataAccessException {
        db.createUser(user1);
        AuthData auth = new AuthData("tokenGet", "john");
        db.createAuth(auth);

        AuthData fetched = db.getAuth("tokenGet");
        assertNotNull(fetched);
        assertEquals("john", fetched.username());
    }

    @Test
    void testGetAuthNonExistent() throws DataAccessException {
        AuthData fetched = db.getAuth("nonexistentToken");
        assertNull(fetched);
    }

    @Test
    void testDeleteAuth() throws DataAccessException {
        db.createUser(user1);
        AuthData auth = new AuthData("tokenDelete", "john");
        db.createAuth(auth);

        db.deleteAuth(auth);
        assertNull(db.getAuth("tokenDelete"));
    }

    @Test
    void testDeleteAuthNonExistent() throws DataAccessException {
        db.createUser(user1);
        AuthData auth = new AuthData("nonexistentToken", "john");
        Assertions.assertDoesNotThrow(() -> db.deleteAuth(auth));
    }
}