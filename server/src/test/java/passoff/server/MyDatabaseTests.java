package passoff.server;

import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;
import server.Server;

public class MyDatabaseTests {

    private static Server server;
    private static DatabaseManager dbManager;
    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

}