import org.apache.commons.codec.digest.DigestUtils;

import java.io.Console;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class AccountManagement {
    public static Scanner userInput = new Scanner(System.in);
    public static Database database = new Database();
    public static LocalDatabase localDatabase = new LocalDatabase();
    private static boolean online;
    private HashMap<String, String> userData = new HashMap<>();
    public static String hashInput(String input) {
        return DigestUtils.sha256Hex(input);
    }
    public void initialiseAccountManagement(boolean Online) {
        this.online = Online;
    }
    public HashMap Login() throws SQLException, IOException {
        Console console = System.console();

        System.out.println("Please enter your username:");
        String enteredUsername = userInput.nextLine();
        String enteredPassword = "";

        // Code will mask input if ran using java -jar XXXXXX.jar but not if ran using an IDE
        if (console != null) {
            char[] enteredPasswordArray = console.readPassword("Please enter your password: (input is masked)\n");
            for (char c : enteredPasswordArray) {
                enteredPassword += c;
            }
        } else {
            System.out.println("Please enter your password:");
            enteredPassword = userInput.nextLine();
        }
        String hash = hashInput(enteredPassword);
        ResultSet userDatabase = null;
        HashMap userLocalDatabase = null;
        if (online) {
            userDatabase = database.getUser(enteredUsername, hash);
        } else {
            userLocalDatabase = localDatabase.getUser(enteredUsername, hash);
        }
        if (userDatabase == null && userLocalDatabase == null) {
            System.out.println("Invalid username or password! \n");
            this.userData.put("Valid", "NO");
        }
        else {
            String userPassword = null;
            this.userData.put("Valid", "YES");
            if (online) {
                while (userDatabase.next()) {
                    userPassword = userDatabase.getString("UserPassword");
                    this.userData.put("FirstName", userDatabase.getString("FirstName"));
                    this.userData.put("LastName", userDatabase.getString("LastName"));
                    this.userData.put("AccountCreated", userDatabase.getString("LastAccessed"));
                    this.userData.put("Username", userDatabase.getString("Username"));
                    this.userData.put("Chips", userDatabase.getString("Chips"));
                    this.userData.put("UserID", userDatabase.getInt("Users.UserID") + "");
                }
            } else {
                this.userData = userLocalDatabase;
                this.userData.put("Valid", "YES");
            }
        }
        return userData;
    }

    public static void manageAccount(HashMap userData, Scanner userInput) throws IOException {
        // Get variables
        String FName = (String) userData.get("FirstName");
        String SName = (String) userData.get("LastName");
        String Chips = (String) userData.get("Chips");
        String AccountCreated = (String) userData.get("AccountCreated");
        String Username = (String) userData.get("Username");
        // Output data
        System.out.println("-= Account Details =-\n" +
                "User full name: " + FName + " " + SName +
                "\nAccount last modified (date & time): " + AccountCreated +
                "\nUsername: " + Username +
                "\nChips: " + Chips);
        System.out.println("\n-= Account Management =-\n" +
                "Would you like to edit your account details? (Y/N)");
        String userChoice = userInput.nextLine();
        if (userChoice.equalsIgnoreCase("y")) {
            System.out.println("What would you like to edit?\n" +
                    "1) Change forename\n" +
                    "2) Change surname\n" +
                    "3) Change password\n" +
                    "4) Nothing");
            int userChoice2 = userInput.nextInt();
            boolean validChoice = false;
            while (!validChoice) {
                switch (userChoice2) {
                    case 1:
                        if (userInput.hasNextLine()) {
                            userInput.nextLine();
                        }
                        System.out.println("Please enter your new forename:");
                        if (online) {
                            if (database.updateRow("Users", "FirstName", userInput.nextLine(), (String) userData.get("UserID"))) {
                                System.out.println("Forename updated successfully!");
                            } else {
                                System.out.println("Forename failed to update!");
                            }
                        } else {
                            if (localDatabase.updateRow("user", "firstname", userInput.nextLine(), (String) userData.get("Username"))) {
                                System.out.println("Forename updated successfully!");
                            } else {
                                System.out.println("Forename failed to update!");
                            }
                        }
                        validChoice = true;
                        break;
                    case 2:
                        if (userInput.hasNextLine()) {
                            userInput.nextLine();
                        }
                        System.out.println("Please enter your new surname:");
                        if (online) {
                            if (database.updateRow("Users", "LastName", userInput.nextLine(), (String) userData.get("UserID"))) {
                                System.out.println("Surname updated successfully!");
                            } else {
                                System.out.println("Surname failed to update!");
                            }
                        } else {
                            if (localDatabase.updateRow("user", "surname", userInput.nextLine(), (String) userData.get("Username"))) {
                                System.out.println("Surname updated successfully!");
                            } else {
                                System.out.println("Surname failed to update!");
                            }
                        }
                        validChoice = true;
                        break;
                    case 3:

                        if (userInput.hasNextLine()) {
                            userInput.nextLine();
                        }
                        String enteredPassword2 = "";
                        String enteredPassword1 = "";
                        String enteredPassword = "";
                        boolean validPassword = false;
                        Console console = System.console();
                        while (!validPassword) {
                            if (console != null) {
                                char[] enteredPasswordArray1 = console.readPassword("Please enter your new password: (input is masked)\n");
                                for (char c : enteredPasswordArray1) {
                                    enteredPassword1 += c;
                                }
                                char[] enteredPasswordArray2 = console.readPassword("Please re-enter your new password: (input is masked)\n");
                                for (char c : enteredPasswordArray2) {
                                    enteredPassword2 += c;
                                }
                                if (enteredPassword1.equals(enteredPassword2)) {
                                    validPassword = true;
                                    enteredPassword = enteredPassword1;
                                } else {
                                    System.out.println("Passwords do not match, please try again!\n");
                                }
                            } else {
                                System.out.println("Please enter your new password:");
                                enteredPassword1 = userInput.nextLine();
                                System.out.println("Please re-enter your new password:");
                                enteredPassword2 = userInput.nextLine();
                                if (enteredPassword1.equals(enteredPassword2)) {
                                    validPassword = true;
                                    enteredPassword = enteredPassword1;
                                } else {
                                    System.out.println("Passwords do not match, please try again!\n");
                                }
                            }
                        }
                        if (online) {
                            if (database.updateRow("Users", "UserPassword", hashInput(enteredPassword), (String) userData.get("UserID"))) {
                                System.out.println("Password updated successfully, returning to main menu!");
                            } else {
                                System.out.println("Password failed to update, returning to main menu!");
                            }
                        } else {
                            if (localDatabase.updateRow("user", "passwordhash", hashInput(enteredPassword), (String) userData.get("Username"))) {
                                System.out.println("Password updated successfully, returning to main menu!");
                            } else {
                                System.out.println("Password failed to update, returning to main menu!");
                            }
                        }

                        validChoice = true;
                        break;
                    case 4:
                        validChoice = true;
                        System.out.println("Returning to main menu");
                        break;
                    default:
                        System.out.println("Invalid menu choice, please select again!");
                        System.out.println("What would you like to edit?\n" +
                                "1) Change first name\n" +
                                "2) Change second name\n" +
                                "3) Change password\n" +
                                "4) Nothing, go back to main menu");
                        userChoice2 = userInput.nextInt();
                        break;
                }

            }
        } else if (userChoice.equalsIgnoreCase("n")) {
            System.out.println("Ok, returning to main menu");
        } else {
            System.out.println("Invalid choice, returning to main menu");
        }
    }
}
