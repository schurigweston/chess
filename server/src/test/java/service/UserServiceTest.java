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

    @Test
    void loginUser() throws DataAccessException{
        UserData user = new UserData("johndoe", "pass", "j@d");

        userService.register(new RegisterRequest(user.username(), user.password(), user.email()));

        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());

        LoginResult result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals(user.username(), result.username());
        assertNotNull(result.authToken());
        System.out.println(result);
    }
    @Test
    void loginBadUser() throws DataAccessException{
        LoginRequest badUser = new LoginRequest("nonexistent", "pass");


        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            userService.login(badUser);
        });

        assertTrue(thrown.getMessage().contains("Username or Password does not exist"));

    }
    @Test
    void logoutUser() throws  DataAccessException{

    }
    @Test
    void logoutBadUser() throws  DataAccessException{

    }

}
