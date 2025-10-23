package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    DataAccess db = new MemoryDataAccess();
    UserService userService = new UserService(db);
    GameService gameService = new GameService(db);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
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
        userService.clear();
        gameService.clear();

    }

    private void addUser(@NotNull Context ctx) throws DataAccessException {

        try{
            RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200).json(result);
        }
        catch(DataAccessException e){
            String errormsg = e.getMessage();
            if(errormsg.contains("already exists")){ ctx.status(403);}
            else if(errormsg.contains("bad request")){ ctx.status(400);}
            else{ctx.json(errormsg);}
        }
    }

    private void loginUser(@NotNull Context ctx) throws DataAccessException {
        try{
            LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200).json(result);
        }
        catch(DataAccessException e){
            String errormsg = e.getMessage();
            if(errormsg.contains("does not exist")){ ctx.status(401);}
            else if(errormsg.contains("bad request")){ ctx.status(400);}
            else{ctx.json(errormsg);}
        }
    }

    private void logoutUser(@NotNull Context ctx) {
        try{
            LogoutRequest request = new Gson().fromJson(ctx.body(), LogoutRequest.class);
            userService.logout(request);
            ctx.status(200);
        }
        catch(DataAccessException e){
            String errormsg = e.getMessage();
            if(errormsg.contains("unauthorized")){ ctx.status(401);}
            else if(errormsg.contains("bad request")){ ctx.status(400);}
            else{ctx.json(errormsg);}
        }
    }

    private void listGames(@NotNull Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            var games = gameService.listGames(authToken);
            ctx.status(200).json(Map.of("games", games));;
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500).json(Map.of("message", e.getMessage()));
            }
        }
    }


    private void createGame(@NotNull Context ctx) {

    }

    private void joinGame(@NotNull Context ctx) {

    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
