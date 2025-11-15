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



}