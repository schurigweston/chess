package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static final GameService gameService = new GameService(new MemoryDataAccess());

    @BeforeEach
    void clear() {
        gameService.clear();
    }

}
