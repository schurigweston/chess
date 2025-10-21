package service;

import datamodel.*;

public class UserService {
    public static RegisterResult register(RegisterRequest registerRequest) {
        RegisterResult result = new RegisterResult(registerRequest.username(), "Auther");
        return result;
    }
    public static LoginResult login(LoginRequest loginRequest) {
        return null; //BAD
    }
    public static void logout(LogoutRequest logoutRequest) {}
}
