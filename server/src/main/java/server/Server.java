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

    private void addUser(Context ctx) throws DataAccessException {

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

    }

    private void logoutUser(@NotNull Context ctx) {
    }

    private void listGames(@NotNull Context ctx) {
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
