package service;

import model.*;
import dataaccess.*;

public class GameService {
    private final DataAccess db;

    public GameService(DataAccess db) {
        this.db = db;
    }

    public void clear() {
        db.clear();
    }
}
