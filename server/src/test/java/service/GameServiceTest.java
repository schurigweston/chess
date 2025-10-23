package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static final GameService gameService = new GameService(new MemoryDataAccess());
    static final DataAccess db = new MemoryDataAccess();
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
    void listGamesSadUnauthorized() {
        // Act & Assert: using a null or invalid auth token throws DataAccessException
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.listGames("badtoken"));

        assertTrue(exception.getMessage().contains("unauthorized"));
    }

}
