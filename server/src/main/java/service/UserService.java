package service;

import model.*;
import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;

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

        //String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt()); //This would be good to do here, but I've already done it in my databases, which was bad, but is what it is.

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
        UserData user = db.getUser(loginRequest.username());
        if(user == null || !BCrypt.checkpw(loginRequest.password(), user.password())){
            throw new DataAccessException("Error: Username or Password does not exist");
        }

        String authToken = UUID.randomUUID().toString();

        db.createAuth(new AuthData(authToken, loginRequest.username()));

        LoginResult result = new LoginResult(loginRequest.username(), authToken);

        return result;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        //if username and authtoken are correct, then... remove authtoken from database?
        if (logoutRequest == null || logoutRequest.authToken() == null) {
            throw new DataAccessException("Error: bad request");
        }

        String authToken = logoutRequest.authToken();
        if (db.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        db.deleteAuth(db.getAuth(authToken));
    }

    public Collection<UserData> listUsers()throws DataAccessException {
        return db.listUsers();
    }

    public void clear() throws DataAccessException {

        db.clear();
    }
}
