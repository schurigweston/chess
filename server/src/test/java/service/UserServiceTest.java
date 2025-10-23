package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserService userService = new UserService(new MemoryDataAccess());

    @BeforeEach
    void clear() {
        userService.clear();
    }

    @Test
    void clearUsers() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");

        RegisterResult result = userService.register(new RegisterRequest(user.username(), user.password(), user.email()));

        userService.clear();
        Collection<UserData> users = userService.listUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void addUser() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");

        RegisterResult result = userService.register(new RegisterRequest(user.username(), user.password(), user.email()));

        Collection<UserData> users = userService.listUsers();
        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

    @Test
    void addDuplicate() throws DataAccessException{
        UserData john = new UserData("johndoe", "pass", "j@d");


        userService.register(new RegisterRequest(john.username(), john.password(), john.email()));
        assertThrows(DataAccessException.class, () -> userService.register(new RegisterRequest(john.username(), john.password(), john.email())));

    }

}
