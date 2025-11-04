package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.json.JavalinGson;
import model.*;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.*;

import java.util.Map;

public class Server{

    private final Javalin javalin;
    private final DataAccess db;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        DataAccess tempDB; //create Database with mySQL if it works, otherwise use memoryDataAccess
        try{
            tempDB = new MySQLDataAccess();
        } catch (DataAccessException e){
            System.out.println("USING MEMORYDATAACCESS");
            tempDB = new MemoryDataAccess();
        }
        this.db = tempDB;
        userService = new UserService(db);
        gameService = new GameService(db);
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });
        javalin.delete("/db", ctx -> clearDatabase(ctx));
        javalin.post("/user", ctx -> addUser(ctx));
        javalin.post("/session", ctx -> loginUser(ctx));
        javalin.delete("/session", ctx -> logoutUser(ctx));
        javalin.get("/game", ctx -> listGames(ctx));
        javalin.post("/game", ctx -> createGame(ctx));
        javalin.put("/game", ctx -> joinGame(ctx));


        // Register your endpoints and exception handlers here.

    }

    private void clearDatabase(@NotNull Context ctx) {
        try {
            userService.clear();
            gameService.clear();
            ctx.status(200).json(Map.of("message", "Database cleared"));
        } catch (DataAccessException e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private void addUser(@NotNull Context ctx) {
        try {
            RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg.contains("already taken")) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            } else if (msg.contains("bad request")) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + msg));

            }
        }
    }

    private void loginUser(@NotNull Context ctx) {
        try {
            LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg.contains("does not exist")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else if (msg.contains("bad request")) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + msg));
            }
        }
    }

    private void logoutUser(@NotNull Context ctx) {
        try {
            String authToken = ctx.header("authorization"); // get authToken from header
            LogoutRequest request = new LogoutRequest(authToken);
            userService.logout(request);
            ctx.status(200).json(Map.of()); // empty I guess
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg.contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else if (msg.contains("bad request")) {
                ctx.status(400).json(Map.of("message", "Error: " + msg));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + msg));

            }
        }
    }

    private void listGames(@NotNull Context ctx) {
        try {
            ListRequest request = new ListRequest(ctx.header("authorization"));
            //String authToken = ctx.header("authorization");
            var games = gameService.listGameSummaries(request);
            ctx.status(200).json(games);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
            }
        }
    }


    private void createGame(@NotNull Context ctx) {
        try {
            CreateRequest body = new Gson().fromJson(ctx.body(), CreateRequest.class);
            CreateRequest request = new CreateRequest(ctx.header("authorization"), body.gameName());

            CreateResult result = gameService.createGame(request);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg.contains("bad request")) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            }
            else if (msg.contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500).json(Map.of("message", "Error: " + msg));
            }
        }
    }

    private void joinGame(@NotNull Context ctx) {
        try {
            JoinRequest body = new Gson().fromJson(ctx.body(), JoinRequest.class);
            JoinRequest request = new JoinRequest(ctx.header("authorization"), body.playerColor(), body.gameID());

            gameService.joinGame(request);
            ctx.status(200);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg.contains("bad request")) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            }
            else if (msg.contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            }
            else if (msg.contains("already taken")) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            }
            else {
                ctx.status(500).json(Map.of("message", "Error: " + msg));
            }
        }
    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
