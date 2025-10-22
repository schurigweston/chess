package service;

import datamodel.*;
import dataaccess.*;

import java.util.Collection;

public class UserService {
    private final DataAccess db;

    public UserService(DataAccess db) {
        this.db = db;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        db.createUser(user);

        RegisterResult result = new RegisterResult(registerRequest.username(), "Auther");
        //add logic for if it works or not.
        return result;
    }
    public static LoginResult login(LoginRequest loginRequest) {
        LoginResult result = new LoginResult(loginRequest.username(), "Auther");
        //add logic for if it works or not.
        return result;
    }
    public static void logout(LogoutRequest logoutRequest) {
        //if username and authtoken are correct, then... remove authtoken from database?
    }

    public Collection<UserData> listUsers() {
        return db.listUsers();
    }

    public void clear() {
        db.clear();
    }
}
