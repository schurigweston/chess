package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    static final DataAccess DB = new MemoryDataAccess();
    static final GameService GAME_SERVICE = new GameService(DB);
    @BeforeEach
    void clear() {
        GAME_SERVICE.clear();
    }

    @Test
    void listGamesHappy() throws DataAccessException {

        UserData user = new UserData("johndoe", "pass", "j@d");
        DB.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        DB.createAuth(auth);

        Collection<GameData> games = GAME_SERVICE.listGames(auth.authToken());

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }
    @Test
    void listGamesSad() {

        try {
            GAME_SERVICE.listGames("badtoken");
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("unauthorized"));
        }
    }
    @Test
    void listGameSummariesHappy() throws DataAccessException {

        UserData user = new UserData("johndoe", "pass", "j@d");
        DB.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        DB.createAuth(auth);

        ListResult games = GAME_SERVICE.listGameSummaries(new ListRequest(auth.authToken()));

        assertNotNull(games);
        assertTrue(games.games().isEmpty());
    }
    @Test
    void listGameSummariesSad() {
        ListRequest request = new ListRequest("invalid-token");

        try {
            GAME_SERVICE.listGameSummaries(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void createGameHappy() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        DB.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        DB.createAuth(auth);

        CreateRequest request = new CreateRequest(auth.authToken(), "TestGame");
        CreateResult result = GAME_SERVICE.createGame(request);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
        assertEquals("TestGame", DB.getGame(result.gameID()).gameName());
    }

    @Test
    void createGameSadUnauthorized() {
        CreateRequest request = new CreateRequest("badtoken", "TestGame");
        try {
            GAME_SERVICE.createGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void createGameSadBadRequest() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        DB.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        DB.createAuth(auth);

        CreateRequest request = new CreateRequest(auth.authToken(), null);
        try {
            GAME_SERVICE.createGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    void joinGameHappy() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        DB.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        DB.createAuth(auth);

        int gameID = DB.createGame("TestGame");

        JoinRequest request = new JoinRequest(auth.authToken(), "WHITE", gameID);
        GAME_SERVICE.joinGame(request);

        GameData game = DB.getGame(gameID);
        assertEquals(user.username(), game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameSadUnauthorized() {
        JoinRequest request = new JoinRequest("badtoken", "WHITE", 1);
        try {
            GAME_SERVICE.joinGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void joinGameSadBadRequest() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        DB.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        DB.createAuth(auth);

        JoinRequest request = new JoinRequest(auth.authToken(), "RED", 999);
        try {
            GAME_SERVICE.joinGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    void joinGameSadAlreadyTaken() throws DataAccessException {
        UserData user1 = new UserData("user1", "pass", "u1@d");
        DB.createUser(user1);
        AuthData auth1 = new AuthData("token1", user1.username());
        DB.createAuth(auth1);

        UserData user2 = new UserData("user2", "pass", "u2@d");
        DB.createUser(user2);
        AuthData auth2 = new AuthData("token2", user2.username());
        DB.createAuth(auth2);

        int gameID = DB.createGame("TestGame");
        JoinRequest request1 = new JoinRequest(auth1.authToken(), "WHITE", gameID);
        GAME_SERVICE.joinGame(request1);

        JoinRequest request2 = new JoinRequest(auth2.authToken(), "WHITE", gameID);
        try {
            GAME_SERVICE.joinGame(request2);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: already taken", e.getMessage());
        }
    }

}
