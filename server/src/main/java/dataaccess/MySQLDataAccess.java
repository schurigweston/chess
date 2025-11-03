package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySQLDataAccess implements DataAccess{


    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) Not Null,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auths (
                `id` int NOT NULL AUTO_INCREMENT,
                `authToken` varchar(256) NOT NULL UNIQUE,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`id`),
                FOREIGN KEY (`username`) REFERENCES users(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
                `id` int NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(256) NOT NULL,
                `blackUsername` varchar(256) NOT NULL,
                `gameName` varchar(256) NOT NULL,
                `game` TEXT NOT NULL,
                PRIMARY KEY (`id`),
                FOREIGN KEY (`whiteUsername`) REFERENCES users(username),
                FOREIGN KEY (`blackUsername`) REFERENCES users(username)
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE from auths").execute();
            conn.prepareStatement("DELETE from games").execute();
            conn.prepareStatement("DELETE from users").execute();
        } catch (Exception ex) {
            throw new DataAccessException("Unable to clear database", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException{ //make sure that user is unique. I didn't do that with memory database.
        //conn.prepareStatement("INSERT INTO users values (1, " + user.username() + "," + user.password() + "," + user.email() + ")"); //Bad, unsafe
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(query, user.username(), user.password(), user.email()); //This line returns an integer, the ID of the new user, if I ever need to use it.
    }

    @Override
    public UserData getUser(String username) {

        return null;
    }

    @Override
    public Collection<UserData> listUsers() {
        return List.of();
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData updatedGame) {

    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException { //Lol yoinked from pet shop.
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);

                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
