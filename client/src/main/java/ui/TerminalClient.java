package ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;
import model.*;

import static ui.EscapeSequences.*;


public class TerminalClient {
    private final ServerFacade serverFacade;
    private String authToken;
    private String username;
    private HashMap<Integer, GameSummary> gameSummaryMap;
    public TerminalClient(String url) {
        serverFacade = new ServerFacade(url);
        gameSummaryMap = new HashMap<>();
    }

    public TerminalClient() {
        serverFacade = new ServerFacade("http://localhost:8080");
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
            String[] tokens = input.trim().toLowerCase().split("\\s+");
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

    private String logout() throws ResponseException {
        serverFacade.logout(authToken);
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
            boolean white = true;
            if(gameSummaryMap.isEmpty()){
                list();
            }
            try{
                gameID = Integer.parseInt(params[0]);
                gameSummaryMap.get(gameID).gameID();
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid gameID");
            }

            if(params[1].equalsIgnoreCase("BLACK")){
                joinRequest = new JoinRequest(authToken, "BLACK", gameSummaryMap.get(gameID).gameID());
                white = false;
            }else if(params[1].equalsIgnoreCase("WHITE")){
                joinRequest = new JoinRequest(authToken, "WHITE", gameSummaryMap.get(gameID).gameID());
                white = true;
            }else{
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid Color");
            }
            JoinResult result = serverFacade.join(joinRequest); //returns a register result, which has username and authtoken

            drawBoard(white);
            return "Joined " + gameSummaryMap.get(gameID).gameName() + " as " + params[1];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 2 arguments and got " + params.length);
    }

    private String observe(String[] params) throws ResponseException {
        if(params.length == 1){
            int gameID;

            if(gameSummaryMap.isEmpty()){
                list();
            }
            try{
                gameID = Integer.parseInt(params[0]);
                gameSummaryMap.get(gameID).gameID();
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Invalid gameID");
            }

            drawBoard(true);
            return "Observing " + gameSummaryMap.get(gameID).gameName();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 1 arguments and got " + params.length);
    }

    private String list() throws ResponseException {
        ListResult result = serverFacade.listGames(authToken);

        int k = 1;
        StringBuilder returnString = new StringBuilder();
        gameSummaryMap.clear();
        for(GameSummary gameSummary : result.games()){
            gameSummaryMap.put(k, gameSummary);
            returnString.append(String.format("GameID: %d, %s, %s, %s%n",
                    k, gameSummary.gameName(), gameSummary.whiteUsername(), gameSummary.blackUsername()));
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
        if(params.length == 3 && !params[1].isEmpty()){

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

    public void drawBoard(boolean whitePerspective) {


        String[][] board = {
                {BLACK_ROOK,  BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
                {BLACK_PAWN,  BLACK_PAWN,   BLACK_PAWN,   BLACK_PAWN,  BLACK_PAWN, BLACK_PAWN,   BLACK_PAWN,   BLACK_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {WHITE_PAWN,  WHITE_PAWN,   WHITE_PAWN,   WHITE_PAWN,  WHITE_PAWN, WHITE_PAWN,   WHITE_PAWN,   WHITE_PAWN},
                {WHITE_ROOK,  WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
        };

        class Helper{
            public static void printCell(int i, int j, String[][] board){
                if ((i + j) % 2 == 0) {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY + board[i][j] + RESET_BG_COLOR);
                } else {
                    System.out.print(SET_BG_COLOR_DARK_GREY + board[i][j] + RESET_BG_COLOR);
                }
            }
        }

        if (!whitePerspective) {
            for (int i = 7; i >= 0; i--) {
                System.out.print((8 - i) + " ");
                for (int j = 7; j >= 0; j--) {
                    Helper.printCell(i, j, board);
                }
                System.out.println();
            }
            System.out.printf(
                    "%sh%sg%sf%se%sd%sc%sb%sa%n",
                    SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY
            );
        } else {
            for (int i = 0; i < board.length; i++) {
                System.out.print((8-i) + " ");
                for (int j = 0; j < board[i].length; j++) {
                    Helper.printCell(i,j,board);
                }
                System.out.println();
            }
            System.out.printf(
                    "%sa%sb%sc%sd%se%sf%sg%sh%n",
                    SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY, SMALLER_EMPTY
            );
        }
    }
}
