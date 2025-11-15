package ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;
import model.*;



import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;


public class TerminalClient {
    private final ServerFacade serverFacade;
    private String authToken;
    private String username;
    private HashMap<Integer, GameSummary> gameSummaryMap;
    public TerminalClient() {
        serverFacade = new ServerFacade();
        gameSummaryMap = new HashMap<>();
    }


    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started. ");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(">>>");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if(!result.isEmpty()){
                    System.out.println(SET_TEXT_COLOR_BLUE + result);
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            //This makes cmd be the first typed, and params be everything after.
            if(authToken == null){
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                default -> help();
            };}
            else{ //authToken is not null, so they must be logged in, right?
                return switch (cmd) {
                    case "create" -> create(params);
                    case "list" -> list();
                    case "join" -> join(params);
                    case "observe" -> observe(params);
                    case "logout" -> logout();
                    case "help" -> innerHelp();
                    default -> innerHelp();
                };

            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        authToken = null;
        return "logged out";
    }

    private String create(String[] params) throws ResponseException {
        if(params.length == 1){

            CreateResult result = serverFacade.create(params, authToken); //returns a register result, which has username and authtoken

            return "Created game " + params[0];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 1 arguments and got " + params.length);
    }

    private String join(String[] params) throws ResponseException {
        if(params.length == 2){
            int gameID;
            JoinRequest joinRequest;
            if(gameSummaryMap.isEmpty()){
                list();
            }
            try{
                gameID = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid gameID");
            }

            if(params[1].equalsIgnoreCase("BLACK")){
                joinRequest = new JoinRequest(authToken, "BLACK", gameSummaryMap.get(gameID).gameID());
            }else if(params[1].equalsIgnoreCase("WHITE")){
                joinRequest = new JoinRequest(authToken, "WHITE", gameSummaryMap.get(gameID).gameID());
            }else{
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid Color");
            }
            JoinResult result = serverFacade.join(joinRequest); //returns a register result, which has username and authtoken

            return "Joined " + gameSummaryMap.get(gameID).gameName() + " as " + params[1];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 2 arguments and got " + params.length);
    }

    private String observe(String[] params) {
        return "maybe looked at a game";
    }

    private String list() throws ResponseException {
        ListResult result = serverFacade.listGames(authToken);

        int k = 1;
        StringBuilder returnString = new StringBuilder();
        gameSummaryMap.clear();
        for(GameSummary gameSummary : result.games()){
            gameSummaryMap.put(k, gameSummary);
            returnString.append(String.format("GameID: %d, %s, %s, %s%n", k, gameSummary.gameName(), gameSummary.whiteUsername(), gameSummary.blackUsername()));
            k++;
        }
        if (!returnString.isEmpty()){
            returnString.deleteCharAt(returnString.length()-1);
        }

        return returnString.toString();
    }

    private String innerHelp() {
        return "create <NAME>\n" +
                "list - games\n" +
                "join<ID> [WHITE|BLACK|\n" +
                "observe <ID>\n" +
                "logout\n" +
                "help";
    }

    private String quit() {
        return "quit"; //Could just be a lambda function, but not now.
    }


    private String help() {
        return "register <USERNAME> <PASSWORD> <EMAIL>\nLOGIN <USERNAME> <PASSWORD>\nquit\nhelp";
    }

    private String register(String[] params) throws ResponseException {
        //Parameters should be Username, password, and email.
        if(params.length == 3){

            RegisterResult result = serverFacade.register(params); //returns a register result, which has username and authtoken
            authToken = result.authToken();
            username = result.username();
            System.out.println("Registered " + result.username());
            loggedIn();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 3 arguments and got " + params.length);
    }

    private String login(String[] params) throws ResponseException{
        if(params.length == 2){

            LoginResult result = serverFacade.login(params); //returns a register result, which has username and authtoken
            authToken = result.authToken();
            username = result.username();
            //System.out.println("Welcome, " + result.username());
            loggedIn();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 2 arguments and got " + params.length);
    }

    private void loggedIn(){
        System.out.println("Logged in");
        System.out.println(innerHelp());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (authToken != null) {//second part should be unnecessary.
            System.out.print(">>>");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println(help());

    }
}
