import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ReceiptMaker {
    public static void printReceipt(HashMap Chips, int gamesPlayed) throws FileNotFoundException {
        // Parse HashMap for chip delta
        double current_chips = Double.valueOf((int) Chips.get("CURRENT_CHIPS"));
        double start_chips = Double.valueOf((int)Chips.get("START_CHIPS"));
        // In file output
        try {
            // Create instance of required classes
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedNow = now.format(formatter);
            // Interact with OS for file creation
            String filePath = System.getProperty("user.home") + "\\Desktop\\receipt.txt";
            System.out.println("Outputting receipt to your Desktop ("+filePath+"):\n" +
                    "You have " + String.format("%.0f",current_chips) + " chips left out of " + String.format("%.0f",start_chips) +
                    "\nAttempting to open receipt for you!" +
                    "\nThank you for using our services!");
            PrintWriter writer = new PrintWriter(filePath);
            writer.println("Receipt (" + formattedNow + "):");
            writer.println("-".repeat(20));
            writer.println("GAMES PLAYED      : " + gamesPlayed);
            writer.println("STARTING CHIPS    : " + String.format("%.0f",start_chips));
            writer.println("CURRENT CHIPS     : " + String.format("%.0f",current_chips));
            double percentageChange = ((current_chips / start_chips) * 100) - 100;
            String percentageChangeString = null;
            if (percentageChange > 0) {
                percentageChangeString = "+" + String.format("%.2f", percentageChange) + "%";
            } else{
                percentageChangeString = String.format("%.2f", percentageChange) + "%";
            }
            writer.println("PERCENTAGE CHANGE : " + percentageChangeString);
            writer.println("-".repeat(20));
            writer.println("\nThank you for using our services!");
            writer.close();
            outputToScreen(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while writing your receipt!");
            e.printStackTrace();
        }
    }
    public static void outputToScreen(String filePath) {
        try {
            File file = new File(filePath);
            if(file.exists()) {
                if(Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    System.out.println("Attempted to open receipt but failed!");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
