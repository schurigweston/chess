package service;

import model.*;
import dataaccess.*;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final DataAccess db;

    public GameService(DataAccess db) {
        this.db = db;
    }

    public void clear() {
        db.clear();
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        if(authToken == null || db.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return db.listGames();
    }
    public ListResult listGameSummaries(ListRequest request) throws DataAccessException {

        if(request.authToken() == null || db.getAuth(request.authToken()) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<GameData> games = db.listGames();
        Collection<GameSummary> summaries = new ArrayList<>();

        for (GameData game : games) {
            GameSummary summary = new GameSummary(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            );
            summaries.add(summary);
        }

        return new ListResult(summaries);
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException {
        //Comment
        if (request.authToken() == null || db.getAuth(request.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        int gameID = db.createGame(request.gameName());
        CreateResult result = new CreateResult(gameID);
        return result;
    }

    public void joinGame(JoinRequest request) throws DataAccessException {
        if (request.authToken() == null || db.getAuth(request.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (request.playerColor() == null || !(request.playerColor().equals("WHITE") || request.playerColor().equals("BLACK"))) {
            throw new DataAccessException("Error: bad request");
        }
        GameData game = db.getGame(request.gameID());
        if(game == null){ //game doesn't exist
            throw new DataAccessException("Error: bad request");
        }
        if(game.blackUsername() != null && request.playerColor().equals("BLACK")){ //If someone is already black
            throw new DataAccessException("Error: already taken");
        }
        if(game.whiteUsername() != null && request.playerColor().equals("WHITE")){ //If someone is already white
            throw new DataAccessException("Error: already taken");
        }

        GameData updatedGame;
        if (request.playerColor().equals("WHITE")) {
            updatedGame = new GameData(game.gameID(), db.getAuth(request.authToken()).username(),
                    game.blackUsername(), game.gameName(), game.game());
        } else { // BLACK
            updatedGame = new GameData(game.gameID(), game.whiteUsername(),
                    db.getAuth(request.authToken()).username(), game.gameName(), game.game());
        }

        db.updateGame(updatedGame);
    }

}
