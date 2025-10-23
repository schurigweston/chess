package service;

import model.*;
import dataaccess.*;

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

}
