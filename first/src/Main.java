import com.mysql.cj.log.Log;

import java.io.Console;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException, IOException {
        HashMap<String, String> userInfo = new HashMap<>();
        Database database = new Database();
        AccountManagement accountManagement = new AccountManagement();
        Scanner userInput = new Scanner(System.in);
        Choices choices = new Choices();
        boolean online = database.initialiseDatabase();
        accountManagement.initialiseAccountManagement(online);
        System.out.println("\nHello and welcome to the programme. Before we start, do you have an account?\n" +
                "1) Yes, go to login page!\n" +
                "2) No, create me an account!\n" +
                "3) Exit script!");
        int menuChoice = userInput.nextInt();
        userInput.nextLine();

        HashMap userData = null;
        boolean choiceSuccess = false;
        while (!choiceSuccess) {
            switch (menuChoice) {
                case 1:
                    boolean validAccount = false;
                    while (!validAccount) {
                        userData = accountManagement.Login();
                        if (userData.get("Valid") == "YES") {
                            validAccount = true;
                            choiceSuccess = true;
                        }
                    }
                    break;
                case 2:
                    System.out.println("Please enter a username:");
                    String enteredUsername = userInput.nextLine();
                    System.out.println("Please enter your first name:");
                    String userFirstName = userInput.nextLine();
                    System.out.println("Please enter your second name:");
                    String userSurname = userInput.nextLine();
                    System.out.println("Please enter a password:");
                    String userPassword = userInput.nextLine();
                    userInfo.put("Username", enteredUsername);
                    userInfo.put("FirstName", userFirstName);
                    userInfo.put("LastName", userSurname);
                    userInfo.put("Password", accountManagement.hashInput(userPassword));
                    boolean databaseActionComplete = false;
                    if (online) {
                        databaseActionComplete = database.registerUser(userInfo);
                    } else {
                        databaseActionComplete = LocalDatabase.registerUser(userInfo);
                    }
                    if (databaseActionComplete) {
                        System.out.println("Account created successfully!\n");
                        System.out.println("Please login to continue\n");
                        boolean validAccount1 = false;
                        while (!validAccount1) {
                            userData = accountManagement.Login();
                            if (userData.get("Valid") == "YES") {
                                validAccount1 = true;
                                choiceSuccess = true;
                            }
                        }
                    } else {
                        System.out.println("Account creation failed!");
                    }
                    break;
                case 3:
                    System.exit(0);
                    choiceSuccess = true;
                    break;
                default:
                    System.out.println("Invalid choice! Exiting...");
                    System.exit(1);
                    break;
            }
        }

        // Output details
        String FName = (String) userData.get("FirstName");
        System.out.println("Credentials verified, welcome " + FName + "!");
        System.out.println("Welcome to the programme");
        System.out.println("Now, let’s begin…");
        System.out.println("In this programme you have 5 choices:");
        userData.put("START_CHIPS", userData.get("Chips"));
        userData = choices.choiceMenu(userData, online);

        System.out.println("The programme will exit now. Goodbye!");
    }
}