package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        // Clear the database before each test
        facade.clear();
    }

    // ========== REGISTER TESTS ==========

    @Test
    @DisplayName("Register - Positive: Successfully register new user")
    public void registerPositive() throws ResponseException {
        String[] params = {"testUser", "password123", "test@email.com"};
        RegisterResult result = facade.register(params);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("testUser", result.username());
    }

    @Test
    @DisplayName("Register - Negative: Register with existing username")
    public void registerNegativeDuplicate() throws ResponseException {
        String[] params = {"testUser", "password123", "test@email.com"};
        facade.register(params);

        // Try to register same username again
        String[] params2 = {"testUser", "different", "different@email.com"};
        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.register(params2)
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    @Test
    @DisplayName("Register - Negative: Register with null username")
    public void registerNegativeNullUsername() {
        String[] params = {null, "password123", "test@email.com"};

        Assertions.assertThrows(
                ResponseException.class,
                () -> facade.register(params)
        );
    }

    // ========== LOGIN TESTS ==========

    @Test
    @DisplayName("Login - Positive: Successfully login existing user")
    public void loginPositive() throws ResponseException {
        // First register a user
        String[] registerParams = {"loginUser", "password123", "login@email.com"};
        facade.register(registerParams);

        // Then login
        String[] loginParams = {"loginUser", "password123"};
        LoginResult result = facade.login(loginParams);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("loginUser", result.username());
    }

    @Test
    @DisplayName("Login - Negative: Login with wrong password")
    public void loginNegativeWrongPassword() throws ResponseException {
        // Register a user
        String[] registerParams = {"loginUser", "password123", "login@email.com"};
        facade.register(registerParams);

        // Try to login with wrong password
        String[] loginParams = {"loginUser", "wrongPassword"};
        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.login(loginParams)
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    @Test
    @DisplayName("Login - Negative: Login non-existent user")
    public void loginNegativeNonExistentUser() {
        String[] loginParams = {"nonExistentUser", "password123"};

        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.login(loginParams)
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    // ========== CREATE GAME TESTS ==========

    @Test
    @DisplayName("Create - Positive: Successfully create a game")
    public void createPositive() throws ResponseException {
        // Register and get auth token
        String[] registerParams = {"createUser", "password123", "create@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        // Create a game
        String[] createParams = {"Test Game"};
        CreateResult result = facade.create(createParams, registerResult.authToken());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create - Negative: Create game with invalid auth token")
    public void createNegativeInvalidAuth() {
        String[] createParams = {"Test Game"};

        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.create(createParams, "invalidAuthToken")
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    @Test
    @DisplayName("Create - Negative: Create game with null game name")
    public void createNegativeNullGameName() throws ResponseException {
        String[] registerParams = {"createUser", "password123", "create@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        String[] createParams = {null};

        Assertions.assertThrows(
                ResponseException.class,
                () -> facade.create(createParams, registerResult.authToken())
        );
    }

    // ========== LIST GAMES TESTS ==========

    @Test
    @DisplayName("List - Positive: Successfully list games")
    public void listPositive() throws ResponseException {
        // Register user
        String[] registerParams = {"listUser", "password123", "list@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        // Create a couple of games
        String[] createParams1 = {"Game One"};
        String[] createParams2 = {"Game Two"};
        facade.create(createParams1, registerResult.authToken());
        facade.create(createParams2, registerResult.authToken());

        // List games
        ListResult result = facade.listGames(registerResult.authToken());

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.games());
        Assertions.assertEquals(2, result.games().size());
    }

    @Test
    @DisplayName("List - Positive: List empty games")
    public void listPositiveEmpty() throws ResponseException {
        String[] registerParams = {"listUser", "password123", "list@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        ListResult result = facade.listGames(registerResult.authToken());

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.games());
        Assertions.assertEquals(0, result.games().size());
    }

    @Test
    @DisplayName("List - Negative: List with invalid auth token")
    public void listNegativeInvalidAuth() {
        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.listGames("invalidAuthToken")
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    // ========== JOIN GAME TESTS ==========




    @Test
    @DisplayName("Join - Negative: Join with invalid auth token")
    public void joinNegativeInvalidAuth() throws ResponseException {
        // Register and create a game
        String[] registerParams = {"joinUser", "password123", "join@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        String[] createParams = {"Join Game"};
        CreateResult createResult = facade.create(createParams, registerResult.authToken());

        // Try to join with invalid auth
        JoinRequest joinRequest = new JoinRequest("invalidAuth", "WHITE", createResult.gameID());

        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.join(joinRequest)
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    @Test
    @DisplayName("Join - Negative: Join non-existent game")
    public void joinNegativeNonExistentGame() throws ResponseException {
        String[] registerParams = {"joinUser", "password123", "join@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        JoinRequest joinRequest = new JoinRequest(registerResult.authToken(), "WHITE", 99999);

        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.join(joinRequest)
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    @Test
    @DisplayName("Join - Negative: Join already taken color")
    public void joinNegativeColorTaken() throws ResponseException {
        // Register two users
        String[] registerParams1 = {"user1", "password123", "user1@email.com"};
        RegisterResult registerResult1 = facade.register(registerParams1);

        String[] registerParams2 = {"user2", "password123", "user2@email.com"};
        RegisterResult registerResult2 = facade.register(registerParams2);

        // Create a game
        String[] createParams = {"Join Game"};
        CreateResult createResult = facade.create(createParams, registerResult1.authToken());

        // First user joins as WHITE
        JoinRequest joinRequest1 = new JoinRequest(registerResult1.authToken(), "WHITE", createResult.gameID());
        facade.join(joinRequest1);

        // Second user tries to join as WHITE
        JoinRequest joinRequest2 = new JoinRequest(registerResult2.authToken(), "WHITE", createResult.gameID());

        ResponseException exception = Assertions.assertThrows(
                ResponseException.class,
                () -> facade.join(joinRequest2)
        );

        Assertions.assertEquals(ResponseException.Code.ClientError, exception.code());
    }

    @Test
    @DisplayName("Join - Negative: Join with invalid color")
    public void joinNegativeInvalidColor() throws ResponseException {
        String[] registerParams = {"joinUser", "password123", "join@email.com"};
        RegisterResult registerResult = facade.register(registerParams);

        String[] createParams = {"Join Game"};
        CreateResult createResult = facade.create(createParams, registerResult.authToken());

        JoinRequest joinRequest = new JoinRequest(registerResult.authToken(), "PURPLE", createResult.gameID());

        Assertions.assertThrows(
                ResponseException.class,
                () -> facade.join(joinRequest)
        );
    }

}