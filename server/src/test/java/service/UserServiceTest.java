package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserService USER_SERVICE;

    static {
        USER_SERVICE = new UserService(new MemoryDataAccess());
    }

    @BeforeEach
    void clear() throws DataAccessException{
        USER_SERVICE.clear();
    }

    @Test
    void clearUsers() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");

        RegisterResult result = USER_SERVICE.register(new RegisterRequest(user.username(), user.password(), user.email()));

        USER_SERVICE.clear();
        Collection<UserData> users = USER_SERVICE.listUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void addUser() throws DataAccessException {
        UserData user = new UserData("johndoe", "pass", "j@d");

        RegisterResult result = USER_SERVICE.register(new RegisterRequest(user.username(), user.password(), user.email()));

        Collection<UserData> users = USER_SERVICE.listUsers();
        UserData storedUser = users.iterator().next();
        assertEquals(user.username(), storedUser.username());
        assertEquals(user.email(), storedUser.email());
        assertNotEquals(user.password(), storedUser.password());
    }

    @Test
    void addDuplicate() throws DataAccessException{
        UserData john = new UserData("johndoe", "pass", "j@d");


        USER_SERVICE.register(new RegisterRequest(john.username(), john.password(), john.email()));
        assertThrows(DataAccessException.class, () -> USER_SERVICE.register(new RegisterRequest(john.username(), john.password(), john.email())));

    }

    @Test
    void loginUser() throws DataAccessException{
        UserData user = new UserData("johndoe", "pass", "j@d");

        USER_SERVICE.register(new RegisterRequest(user.username(), user.password(), user.email()));

        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());

        LoginResult result = USER_SERVICE.login(loginRequest);

        assertNotNull(result);
        assertEquals(user.username(), result.username());
        assertNotNull(result.authToken());
        System.out.println(result);
    }
    @Test
    void loginBadUser() throws DataAccessException{
        LoginRequest badUser = new LoginRequest("nonexistent", "pass");


        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            USER_SERVICE.login(badUser);
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
