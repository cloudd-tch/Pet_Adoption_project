package org.example;
import java.util.HashMap;
import java.util.Scanner;

import static org.example.PetActions.*;
import static org.example.PetShelterGUI.db;


public class Main {
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
        DatabaseHandler db = new DatabaseHandler();
        db.initialize();
        try { db.registerUser("Dariia", "Nana_Blast", "ADMIN"); } catch (Exception e) {}
        try { db.registerUser("Ayana", "Hachi_strawberry<3", "ADMIN"); } catch (Exception e) {}
        try { db.registerUser("Armin", "Dariia's_fvr", "ADMIN"); } catch (Exception e) {}

        Scanner sc = new Scanner(System.in);
        HashMap<String, String> aCommands = new HashMap<>();
        aCommands.put("help", " -- to see available commands");
        // C
        aCommands.put("add", " -- to add new pets");
        // R
        aCommands.put("show all / dogs / cats", " -- to see pets");
        // U
        aCommands.put("update", " -- to update pet's information");
        // D
        aCommands.put("delete", " -- to remove an existing pet");
        //others
        aCommands.put("exit", "-- to exit the program");

        HashMap<String, String> uCommands = new HashMap<>();
        uCommands.put("help", " -- to see available commands");
        uCommands.put("show all / dogs / cats", " -- to see pets");
        uCommands.put("exit", "-- to exit the program");

        User currentUser = null;
        boolean registered = false;
        while (!registered) {
            System.out.println("| - - - - - - - - - -");
            System.out.println("| To Enter Please Choose:");
            System.out.println("| * Register (Create an account)");
            System.out.println("|               or");
            System.out.println("| * Log in (Already have an account)");
            System.out.println("| - - - - - - - - - -");
            String c = sc.nextLine().toLowerCase().trim();
            if(c.equals("login") || c.equals("log in") || c.equals("l")) {
                System.out.println("Please enter the username:");
                String username = sc.nextLine().trim();
                System.out.println("Please enter the password:");
                String password = sc.nextLine().trim();
                currentUser = db.loginUser(username, password);
                if (currentUser != null) {
                    registered = true;
                    System.out.println("Logged in successfully ^^");
                } else {
                    System.out.println("Invalid username or password!");
                }
            } else if(c.equals("register") || c.equals("reg")) {
                System.out.println("Please enter a username:");
                String u = sc.nextLine().trim();
                if (db.isValueExists("users", "username", u)) {
                    System.out.println("User already exists!");
                    return;
                }
                System.out.println("Please set a password:");
                String p = sc.nextLine().trim();
                try {
                    Validator.validateCredentials(u, "Username");
                    Validator.validateCredentials(p, "Password");
                    db.registerUser(u, p, "USER");
                    currentUser = new User(u, p, "USER");
                    registered = true;
                    System.out.println("Registration successful ^^");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error: username is already taken!");
                    System.out.println("Please try again.");
                    registered = false;
                }
            } else {
                System.out.println("You have entered an incorrect format");
            }
        }

        boolean exit = false;
        if (currentUser == null) return;
        System.out.println("\nEnter a command ('help' to see available commands) >>");
        while(!exit) {
            System.out.println("\n>>");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.isEmpty()) continue;

            String[] parts = input.toLowerCase().split("\\s+");
            String command = parts[0];
            try {
                switch (command) {
                    case "add":
                    case "update":
                    case "delete":
                        if (!currentUser.getRole().equals("ADMIN")) {
                        System.out.println("Access denied: Admins only :(");
                    } else {
                        if(command.equals("add"))
                            add(parts, db);
                        else if(command.equals("update"))
                            update(parts, db);
                        else
                            delete(parts, db);
                    }
                        break;
                    case "show":
                        if (parts.length > 1 && parts[1].equals("dogs")) {
                            typeForShow = "dogs";
                            showDogs(db);
                        } else if (parts.length > 1 && parts[1].equals("cats")) {
                            typeForShow = "cats";
                            showCats(db);
                        } else if (parts.length > 1 && parts[1].equals("parrots")) {
                            typeForShow = "parrots";
                            showParrots(db);
                        } else if (parts.length >1 && parts[1].equals("all")) {
                            showAll(db);
                        }
                        else
                            System.out.println("Usage: show all/ dogs/ cats/ parrots");
                        break;
                    case "help":
                        if(currentUser.getRole().equals("ADMIN")) {
                            System.out.println("--- Available Commands ---");
                            aCommands.forEach((key, value) -> {
                                System.out.printf("%-25s %s%n", key, value);
                            });
                            System.out.println("--------------------------");
                        } else {
                            System.out.println("--- Available Commands ---");
                            uCommands.forEach((key, value) -> {
                                System.out.printf("%-25s %s%n", key, value);
                            });
                            System.out.println("--------------------------");
                        }
                        break;
                    case "exit":
                        System.out.println("Exiting...");
                        System.out.println("Goodbye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Unknown command.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input Error: You must enter a number (integer)!");
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
                System.out.println("Please try again.");
            }
            catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
    }
}