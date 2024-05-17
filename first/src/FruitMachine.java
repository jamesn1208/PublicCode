import java.lang.reflect.Array;
import java.util.*;

public class FruitMachine {
    public static int playMachine(int Chips) {
        Scanner userInput = new Scanner(System.in);
        boolean userIsBroke = false;
        int Bet = 0;
        boolean invalidBet = true;
        do {
            System.out.println("You have a total of " + Chips + " chips!\n" +
                    "How many chips would you like to bet? (whole number)");
            try {
                Bet = userInput.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("You must enter a whole number!\n");
                userInput.nextLine();
                continue;
            }
            if ((Bet <= Chips) && (Bet > 0)) {
                Chips -= Bet;
                invalidBet = false;
            } else if (Chips == 0) {
                System.out.println("You have no chips left to bet with! Returning to main menu.\n");
                return 0;
            }
            else {
                System.out.println("You must bet between 1 & " + Chips + " chip(s)!\n");
            }
        } while (invalidBet);
        userInput.nextLine();
        System.out.println("Starting the fruit machine up!");
        ArrayList<Integer> fruitListApple = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6));
        ArrayList<Integer> fruitListOrange = new ArrayList<Integer>(Arrays.asList(7, 8, 9));
        ArrayList<Integer> fruitListBanana = new ArrayList<Integer>(Arrays.asList(10, 11));
        ArrayList<Integer> fruitListWatermelon = new ArrayList<Integer>(Arrays.asList(12));
        // Which values map too which fruit
        HashMap<ArrayList, String> masterFruitDict = new HashMap<ArrayList, String>(Map.ofEntries(
                Map.entry(fruitListApple, "Apple"),
                Map.entry(fruitListOrange, "Orange"),
                Map.entry(fruitListBanana, "Banana"),
                Map.entry(fruitListWatermelon, "Watermelon")));
        // Fruit & their multipliers
        HashMap<String, Integer> fruitMultipier = new HashMap<String, Integer>(Map.ofEntries(
                Map.entry("Apple", 5),
                Map.entry("Orange", 20),
                Map.entry("Banana", 50),
                Map.entry("Watermelon", 100)));

        Random random = new Random();
        ArrayList<Integer> spinnersNum = new ArrayList<Integer>();
        ArrayList<String> spinnersFruit = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            spinnersNum.add(random.nextInt(12) + 1);
        }

        for (int x = 0; x < 3; x++) {
            for (ArrayList i : masterFruitDict.keySet()) {
                if (i.contains(spinnersNum.get(x))) {
                    spinnersFruit.add(masterFruitDict.get(i));
                }
            }
        }
        System.out.println(spinnersFruit);
        if (Objects.equals(spinnersFruit.get(0), spinnersFruit.get(1)) && Objects.equals(spinnersFruit.get(1), spinnersFruit.get(2))) {
            System.out.println("""
                    --------
                    YOU WIN!
                    --------
                    """);
            System.out.println("You have won a total of " + (Bet * fruitMultipier.get(spinnersFruit.getFirst())) + " chips! (multiplier of " + fruitMultipier.get(spinnersFruit.getFirst()) + "x)");
            Chips += (Bet * fruitMultipier.get(spinnersFruit.getFirst()));
        } else {
            System.out.println("""
                    --------
                    YOU LOSE!
                    --------
                    """);
            System.out.println("You have lost " + Bet + " chips!");
        }
        System.out.println("You now have " + Chips + " chips left!");
        return Chips;
    }
}