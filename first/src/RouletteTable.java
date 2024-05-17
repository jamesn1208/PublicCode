import java.util.*;

public class RouletteTable {
    // ANSI escape codes for colours
    public static final String RESET = "\033[0m";  // text reset
    public static final String WHITE = "\033[37m";  // white foreground
    public static final String RED_BACKGROUND = "\033[41m";  // red background
    public static final String BLACK_BACKGROUND = "\033[40m";  // black background
    public static final String GREEN_BACKGROUND = "\033[42m";  // green background
    public static int playMachine(int Chips) {
        // import classes
        Scanner userInput = new Scanner(System.in);
        Random random = new Random();
        // get player bet
        int Bet = 0;
        do {
            System.out.println("How many chips would you like to bet? (between 1 - " + Chips + " chips)");
            try {
                Bet = userInput.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("You must enter a whole number!\n");
                userInput.nextLine();
                continue;
            }
        }while(Bet > Chips || Bet <= 0);
        // decrease chips by bet amount
        Chips -= Bet;
        // init variables
        String spinResultString = null;
        String colour = null;
        String betChoice = null;
        // declare constants
        ArrayList <String> betChoices = new ArrayList<String>(Arrays.asList("RED", "BLACK", "ODD", "EVEN"));
        ArrayList <Integer> blackNumbers = new ArrayList<Integer>(Arrays.asList(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35));
        ArrayList <Integer> redNumbers = new ArrayList<Integer>(Arrays.asList(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36));
        // get player bet choice
        userInput.nextLine();
        while (true) {
            System.out.println("What would you like to bet on:" +
                    "\nRed (x2)" +
                    "\nBlack (x2)" +
                    "\nGreen (x17)" +
                    "\nOdd (x2)" +
                    "\nEven (x2)" +
                    "\n0 AND 00 - 36 (x36)");
            betChoice = userInput.nextLine();
            try {
                if (betChoices.contains(betChoice.toUpperCase())) {
                    break;
                } else if (betChoice.equals("00")) {
                    betChoice = "37";
                    break;
                    // will run regardless, but must be here to cause error which is handled by the catch
                } try {
                    if (!(Integer.valueOf(betChoice) > 36) && !(Integer.valueOf(betChoice) < 0)) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    }
            System.out.println("Invalid choice, please try again!\n");
            // allows you to enter numbers & strings
            } catch (NumberFormatException e) {
                break;
            }
        };
        System.out.println("You have bet " + Bet + " chips on " + betChoice.toString() +
                "\nSpinning the wheel!");
        // spin the wheel (generate random number between 0 and 37 [will become 00])
        int spinResult = random.nextInt(0, 37);
        // logic for differentiating colours
        if (blackNumbers.contains(spinResult)) {
            spinResultString = BLACK_BACKGROUND + WHITE + String.valueOf(spinResult) + RESET;
            colour = "BLACK";
        } else if (redNumbers.contains(spinResult)) {
            spinResultString = RED_BACKGROUND + WHITE + String.valueOf(spinResult) + RESET;
            colour = "RED";
        } else if (spinResult == 37 || spinResult == 0) {
            spinResultString = GREEN_BACKGROUND + WHITE + String.valueOf(spinResult) + RESET;
            colour = "GREEN";
        // if the random module makes a mistake
        } else {
            spinResultString = String.valueOf(spinResult);
        }
        // output result & change chips accordingly
        System.out.println("The ball has landed on " + spinResultString);
        if (colour.equalsIgnoreCase(betChoice.toString())) {
            if (colour.equalsIgnoreCase("RED") || colour.equalsIgnoreCase("BLACK")) {
                System.out.println("Congratulations! You have won " + Bet * 2 + " chips!");
                Chips += Bet * 2;
            } else if (colour.equalsIgnoreCase("GREEN")) {
                System.out.println("Congratulations! You have won " + Bet * 17 + " chips!");
                Chips += Bet * 36;
            }
        } else if (betChoice.equalsIgnoreCase("ODD") && spinResult % 2 != 0) {
            System.out.println("Congratulations! You have won " + Bet * 2 + " chips!");
            Chips += Bet * 2;
        } else if (betChoice.equalsIgnoreCase("EVEN") && spinResult % 2 == 0) {
            System.out.println("Congratulations! You have won " + Bet * 2 + " chips!");
            Chips += Bet * 2;
        } else if (betChoice.equalsIgnoreCase(String.valueOf(spinResult))) {
            System.out.println("Congratulations! You have won " + Bet * 36 + " chips!");
            Chips += Bet * 36;
        } else {
            System.out.println("Unlucky! You have lost " + Bet + " chips!");
        }
        // return chips to main script
        return Chips;
    }
}
