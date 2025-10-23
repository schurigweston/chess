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

    public void createGame(){

    }

    public void joinGame(){

    }

}
