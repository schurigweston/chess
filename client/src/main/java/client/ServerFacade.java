package client;

import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import model.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl = "http://localhost:8080";

//What does a ServerFacade do? It sends requests to the server and receives responses from the server.


    public RegisterResult register(String[] params) throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest(params[0],params[1],params[2]);
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);

    }

    public LoginResult login(String[] params) throws ResponseException {
        LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public CreateResult create(String[] params, String auth) throws ResponseException {
        CreateRequest createRequest = new CreateRequest(auth, params[0]);
        var request = buildRequest("POST", "/game", createRequest, auth);
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public ListResult listGames(String auth) throws ResponseException {
        ListRequest listRequest = new ListRequest(auth);
        var request = buildRequest("GET", "/game", listRequest, auth);
        var response = sendRequest(request);
        return handleResponse(response, ListResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body, status);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}
