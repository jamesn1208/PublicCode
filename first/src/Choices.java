import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Scanner;

public class Choices {

    public static void wordsOfEncouragement(String FName) {
        System.out.println("Okay " + FName + ", you selected to see some words of encouragement;\n" +
                "100% of people who quit gambling will never win the jackpot again! Keep going!");
    }

    public static HashMap choiceMenu(HashMap userData, boolean Online) throws IOException {
        AccountManagement accountManagement = new AccountManagement();
        Database database = new Database();
        LocalDatabase localDatabase = new LocalDatabase();
        FruitMachine fruitMachine = new FruitMachine();
        RouletteTable rouletteTable = new RouletteTable();
        // Initialise classes, create static vars
        Scanner userInput = new Scanner(System.in);
        String FName = (String) userData.get("FirstName");
        boolean endLoop = false;
        boolean validChoice = false;
        int gamesPlayed = 0;
        // Forcing user to enter valid input to continue
        while ((!endLoop) || (!validChoice)) {
            System.out.println("""
                    \n
                    Option 1: Manage your account
                    Option 2: Spin the fruit machine!
                    Option 3: Spin the roulette wheel!
                    Option 4: Hear some personalised words of encouragement
                    Option 5: Exit the script with receipt
                    Please select (1-5)""");
            // Take input as int
            int Choice = userInput.nextInt();
            userInput.nextLine();
            switch (Choice) {
                case 1:
                    accountManagement.manageAccount(userData, userInput);
                    validChoice = true;
                    choiceMenu(userData, Online);
                    break;  // Have to put 'break;' or else it will continue through switch statement
                case 2:
                    // Will spin fruit machine & allow the user to play with their chips
                    boolean replayFruit = false;
                    do {
                        String currentChips = (String) userData.get("Chips");
                        int leftOverChips = fruitMachine.playMachine(Integer.parseInt(currentChips));
                        gamesPlayed += 1;
                        userData.put("Chips", String.valueOf(leftOverChips));
                        if (Online) {
                            database.updateChips(leftOverChips, (String) userData.get("UserID"));
                        } else {
                            localDatabase.updateChips(leftOverChips, (String) userData.get("Username"));
                        }
                        boolean invalidChoice = true;
                        while (invalidChoice) {
                            if (leftOverChips != 0) {
                                System.out.println("\nWould you like to spin again? (Y/N)");
                                String endChoice = userInput.nextLine();
                                if (endChoice.equalsIgnoreCase("N")) {
                                    replayFruit = false;
                                    invalidChoice = false;
                                    System.out.println("Exiting to main menu, you still have " + leftOverChips + " chips left!");
                                } else if (endChoice.equalsIgnoreCase("Y")) {
                                    replayFruit = true;
                                    invalidChoice = false;
                                }
                            } else {
                                replayFruit = false;
                                invalidChoice = false;
                            }
                        }
                    } while (replayFruit);
                    break;
                case 3:
                    boolean replayRoulette = false;
                    do {
                        String currentChips = (String) userData.get("Chips");
                        int leftOverChips = rouletteTable.playMachine(Integer.parseInt(currentChips));
                        gamesPlayed += 1;
                        userData.put("Chips", String.valueOf(leftOverChips));
                        if (Online) {
                            database.updateChips(leftOverChips, (String) userData.get("UserID"));
                        } else {
                            localDatabase.updateChips(leftOverChips, (String) userData.get("Username"));
                        }
                        boolean invalidChoice = true;
                        while (invalidChoice) {
                            if (leftOverChips != 0) {
                                System.out.println("\nWould you like to spin again? (Y/N)");
                                String endChoice = userInput.nextLine();
                                if (endChoice.equalsIgnoreCase("N")) {
                                    replayRoulette = false;
                                    invalidChoice = false;
                                    System.out.println("Exiting to main menu, you still have " + leftOverChips + " chips left!");
                                } else if (endChoice.equalsIgnoreCase("Y")) {
                                    replayRoulette = true;
                                    invalidChoice = false;
                                }
                            } else {
                                replayRoulette = false;
                                invalidChoice = false;
                            }
                        }

                    } while (replayRoulette);
                    break;
                case 4:
                    wordsOfEncouragement(FName);
                    validChoice = true;
                    break;
                case 5:
                    ReceiptMaker receiptMaker = new ReceiptMaker();
                    HashMap<String, Integer> chipDelta = new HashMap<>();
                    chipDelta.put("START_CHIPS", Integer.parseInt((String) userData.get("START_CHIPS")));
                    chipDelta.put("CURRENT_CHIPS", Integer.parseInt((String) userData.get("Chips")));
                    try {
                        receiptMaker.printReceipt(chipDelta, gamesPlayed);
                    } catch (FileNotFoundException e) {
                        System.out.println("Failed to create receipt!");
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Incorrect menu choice, please select again!");
                    validChoice = false;
                    break;
            }
        }
        return userData;
    }
}
