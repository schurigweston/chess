package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import  java.util.ArrayList;

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
                `whiteUsername` varchar(256) DEFAULT NULL,
                `blackUsername` varchar(256) DEFAULT NULL,
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
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(query, user.username(), hashedPassword, user.email()); //This line returns an integer, the ID of the new user, if I ever need to use it.

    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data for: %s", username), e);
        }
        return null;
    }

    @Override
    public Collection<UserData> listUsers() throws DataAccessException {
        ArrayList<UserData> userList = new ArrayList<UserData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM users";
            try (PreparedStatement ps = conn.prepareStatement(query)) {

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        userList.add(
                        new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        ));
                    }

                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data", e);
        }
        return userList;
    }


    @Override
    public int createGame(String gameName) throws DataAccessException{

        String chessGameJson = new Gson().toJson(new ChessGame());

        String query = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        int gameID = executeUpdate(query, gameName, chessGameJson);

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("chessGame"), ChessGame.class)
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data for gameID: %d", gameID), e);
        }
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
