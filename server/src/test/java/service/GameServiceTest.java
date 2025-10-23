package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    static final DataAccess db = new MemoryDataAccess();
    static final GameService gameService = new GameService(db);
    @BeforeEach
    void clear() {
        gameService.clear();
    }

    @Test
    void listGamesHappy() throws DataAccessException {

        UserData user = new UserData("johndoe", "pass", "j@d");
        db.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        db.createAuth(auth);

        Collection<GameData> games = gameService.listGames(auth.authToken());

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }
    @Test
    void listGamesSad() {

        try {
            gameService.listGames("badtoken");
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("unauthorized"));
        }
    }
    @Test
    void listGameSummariesHappy() throws DataAccessException {

        UserData user = new UserData("johndoe", "pass", "j@d");
        db.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        db.createAuth(auth);

        ListResult games = gameService.listGameSummaries(new ListRequest(auth.authToken()));

        assertNotNull(games);
        assertTrue(games.games().isEmpty());
    }
    @Test
    void listGameSummariesSad() {
        ListRequest request = new ListRequest("invalid-token");

        try {
            gameService.listGameSummaries(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void createGameHappy() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        db.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        db.createAuth(auth);

        CreateRequest request = new CreateRequest(auth.authToken(), "TestGame");
        CreateResult result = gameService.createGame(request);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
        assertEquals("TestGame", db.getGame(result.gameID()).gameName());
    }

    @Test
    void createGameSadUnauthorized() {
        CreateRequest request = new CreateRequest("badtoken", "TestGame");
        try {
            gameService.createGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void createGameSadBadRequest() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        db.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        db.createAuth(auth);

        CreateRequest request = new CreateRequest(auth.authToken(), null);
        try {
            gameService.createGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    void joinGameHappy() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        db.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        db.createAuth(auth);

        int gameID = db.createGame("TestGame");

        JoinRequest request = new JoinRequest(auth.authToken(), "WHITE", gameID);
        gameService.joinGame(request);

        GameData game = db.getGame(gameID);
        assertEquals(user.username(), game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameSadUnauthorized() {
        JoinRequest request = new JoinRequest("badtoken", "WHITE", 1);
        try {
            gameService.joinGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    void joinGameSadBadRequest() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");
        db.createUser(user);
        AuthData auth = new AuthData("token", user.username());
        db.createAuth(auth);

        JoinRequest request = new JoinRequest(auth.authToken(), "RED", 999);
        try {
            gameService.joinGame(request);
            fail("Expected a DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: bad request", e.getMessage());
        }
    }



}
