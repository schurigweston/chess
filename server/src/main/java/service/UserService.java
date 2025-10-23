package service;

import model.*;
import dataaccess.*;

import java.util.Collection;
import java.util.UUID;

public class UserService {
    private final DataAccess db;

    public UserService(DataAccess db) {
        this.db = db;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new DataAccessException("Error: bad request");
        }
        if(db.getUser(registerRequest.username()) != null){
            throw new DataAccessException("Error: already taken");
        }

        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        db.createUser(user);

        String authToken = UUID.randomUUID().toString();

        db.createAuth(new AuthData(authToken, user.username()));

        RegisterResult result = new RegisterResult(registerRequest.username(), "Auther");
        //add logic for if it works or not.
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) {
        LoginResult result = new LoginResult(loginRequest.username(), "Auther");
        //add logic for if it works or not.
        return result;
    }
    public void logout(LogoutRequest logoutRequest) {
        //if username and authtoken are correct, then... remove authtoken from database?
    }

    public Collection<UserData> listUsers() {
        return db.listUsers();
    }

    public void clear() {
        db.clear();
    }
}
