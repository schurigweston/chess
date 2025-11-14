package ui;

import java.util.Arrays;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;


import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;


public class TerminalClient {
    private final ServerFacade serverFacade;

    public TerminalClient() {
        serverFacade = new ServerFacade();
    }

//    public void run() {
//        System.out.println("Hello, we just called 'run'.");
//
//        Scanner scanner = new Scanner(System.in);
//        String userWrote = "";
//        while (!userWrote.equals("quit")) {
//            System.out.println("Next line: ");
//            userWrote = scanner.nextLine();
//
//
//        }
//    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started. ");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(">>>");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String quit() {
        return "quit"; //Could just be a lambda function, but not now.
    }

    private String help() {
        return "register <USERNAME> <PASSWORD> <EMAIL>\nLOGIN <USERNAME> <PASSWORD>\nquit\nhelp\n";
    }

    private String register(String[] params) throws ResponseException {
        //Parameters should be Username, password, and email.
        if(params.length == 3){

            serverFacade.register(params);
            return "yey";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "expected 3 arguments and got" + params.length);
    }

    private String login(String[] params) {
        return null;
    }
}
