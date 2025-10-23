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

        RegisterResult result = new RegisterResult(registerRequest.username(), authToken);

        return result;
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        if(loginRequest.username() == null || loginRequest.password() == null) { //if there is no username or password provided...
            throw new DataAccessException("Error: bad request");
        }
        if(db.getUser(loginRequest.username()) == null || !db.getUser(loginRequest.username()).password().equals(loginRequest.password())){ //If the username doesn't exist or the password doesn't match for that username...
            throw new DataAccessException("Error: Username or Password does not exist");
        }

        String authToken = UUID.randomUUID().toString();

        db.createAuth(new AuthData(authToken, loginRequest.username()));

        LoginResult result = new LoginResult(loginRequest.username(), authToken);

        return result;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        //if username and authtoken are correct, then... remove authtoken from database?
        if(logoutRequest.authToken() == null) { //if there is no authToken provided...
            throw new DataAccessException("Error: bad request");
        }
        String authToken = logoutRequest.authToken();
        if(db.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        db.deleteAuth(db.getAuth(authToken));
    }

    public Collection<UserData> listUsers()throws DataAccessException {
        return db.listUsers();
    }

    public void clear() {
        db.clear();
    }
}
