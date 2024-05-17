import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class LocalDatabase {
    static Scanner userInput = new Scanner(System.in);
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedNow = now.format(formatter);
    private static final Path localDictionary = Paths.get("data/dictionary.toml");
    public boolean initialiseLocalDatabase() throws IOException {
        try {
            if (!Files.exists(localDictionary)) {
                System.out.println("Local database files are not present, please ensure you have correctly installed the app!");
                return false;
            }
            String tomlString = new String(Files.readAllBytes(localDictionary));
            Toml toml = new Toml().read(tomlString);
        } catch (Exception e) {
            System.out.println("Failed to read local database, exiting!");
            return false;
        }
        return true;
    }
    public static HashMap getUser(String entered_user, String hashed_pass) throws IOException {
        // Read the dictionary file
        String tomlDictString = new String(Files.readAllBytes(localDictionary));
        Toml dictToml = new Toml().read(tomlDictString);
        // Declare hashmap to store user data
        HashMap<String, String> userData = new HashMap<>();
        // Get the user index key
        String userIndex = dictToml.getString("dictionary." + entered_user);
        // Validation
        if (userIndex == null) {
            return null;
        }
        // Check for file from index
        Path localDataDir = Paths.get("data/" + userIndex + ".toml");
        if (!Files.exists(localDataDir)) {
            return null;
        }
        // Parse data from local database
        String tomlDataString = new String(Files.readAllBytes(localDataDir));
        Toml dataToml = new Toml().read(tomlDataString);
        // Ensure password hash is correct
        if (!hashed_pass.equals(dataToml.getString("user.passwordhash")) || !entered_user.equals(dataToml.getString("user.username"))) {
            return null;
        }
        // Store data in hashmap
        userData.put("UserID", dataToml.getString("user.userid"));
        userData.put("LastName", dataToml.getString("user.surname"));
        userData.put("FirstName", dataToml.getString("user.firstname"));
        userData.put("Username", dataToml.getString("user.username"));
        userData.put("Chips", dataToml.getString("data.chips"));
        userData.put("AccountCreated", dataToml.getString("data.lastaccessed"));
        // Return data
        return userData;
    }
    public static boolean doesUserExist(String enteredUsername) throws IOException {
        String tomlDictString = new String(Files.readAllBytes(localDictionary));
        Toml dictToml = new Toml().read(tomlDictString);
        String userIndex = dictToml.getString("dictionary." + enteredUsername);
        if (userIndex == null) {
            return false;
        } else {
            return true;
        }
    }
    public static boolean isUsernameValid(String enteredUsername) {
        if (enteredUsername.contains(".") || enteredUsername.contains(" ") || enteredUsername.contains("/") || enteredUsername.contains("\\") || enteredUsername.contains("[") || enteredUsername.contains("]")) {
            return false;
        } else {
            return true;
        }
    }
    public boolean updateRow(String table, String element, String newData, String username) throws IOException {
        String tomlDictString = new String(Files.readAllBytes(localDictionary));
        Toml dictToml = new Toml().read(tomlDictString);
        String userIndex = dictToml.getString("dictionary." + username);
        // Validation
        if (userIndex == null) {
            return false;
        }
        // Check for file from index
        Path localDataDir = Paths.get("data/" + userIndex + ".toml");
        if (!Files.exists(localDataDir)) {
            return false;
        }
        // Parse data from local database
        String tomlDataString = new String(Files.readAllBytes(localDataDir));
        Toml dataToml = new Toml().read(tomlDataString);
        Map<String, Object> existingData = dataToml.toMap();
        Map<String, Object> subData = null;
        if (table.equals("data")) {
            subData = (Map<String, Object>) existingData.get(table);
            subData.put("lastaccessed", formattedNow);
        } else if (table.equals("user")) {
            subData = (Map<String, Object>) existingData.get(table);
            Map<String, Object> subData1 = (Map<String, Object>) existingData.get("data");
            subData1.put("lastaccessed", formattedNow);
        }
        subData.put(element, newData);
        TomlWriter tomlWriter = new TomlWriter();
        try {
            tomlWriter.write(existingData, localDataDir.toFile());
        } catch (IOException e) {
            System.out.println("Failed to update "+table+"."+element+" in local database");
            return false;
        }
        return true;
    }
    public boolean updateChips(int newData, String username) throws IOException {
        String tomlDictString = new String(Files.readAllBytes(localDictionary));
        Toml dictToml = new Toml().read(tomlDictString);
        String userIndex = dictToml.getString("dictionary." + username);
        // Validation
        if (userIndex == null) {
            return false;
        }
        // Check for file from index
        Path localDataDir = Paths.get("data/" + userIndex + ".toml");
        if (!Files.exists(localDataDir)) {
            return false;
        }
        // Parse data from local database
        String tomlDataString = new String(Files.readAllBytes(localDataDir));
        Toml dataToml = new Toml().read(tomlDataString);
        Map<String, Object> existingData = dataToml.toMap();
        Map<String, Object> subData = (Map<String, Object>) existingData.get("data");
        subData.put("chips", String.valueOf(newData));
        subData.put("lastaccessed", formattedNow);
        TomlWriter tomlWriter = new TomlWriter();
        try {
            tomlWriter.write(existingData, localDataDir.toFile());
        } catch (IOException e) {
            System.out.println("Failed to update data.chips in local database");
            return false;
        }
        return true;
    }

    public static boolean registerUser(HashMap userInfo) throws IOException{
        boolean invalidUsername = doesUserExist((String) userInfo.get("Username"));
        boolean validUsername = isUsernameValid((String) userInfo.get("Username"));
        if (invalidUsername) {
            System.out.println("Username already taken, please try again!");
            return false;
        }
        if (!validUsername) {
            System.out.println("Username contains invalid characters ('[', ']', ' ', '.', '/', '\\'), please try again!");
            return false;
        }
        String tomlDictString = new String(Files.readAllBytes(localDictionary));
        Toml dictToml = new Toml().read(tomlDictString);
        Path dirPath = Paths.get("data");
        String fileLocation = null;
        long fileCount;
        try {
            fileCount = Files.list(dirPath).count();
            fileLocation = "data/" + fileCount + ".toml";
            Files.createFile(Paths.get(fileLocation));
        } catch (IOException e) {
            System.out.println("Failed to create new .toml entry");
            return false;
        }

        // Write .toml file
        PrintWriter datawriter = new PrintWriter(fileLocation);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        // User section
        datawriter.println("[user]");
        datawriter.println("userid = " + '"'+fileCount+'"');
        datawriter.println("username = " + '"'+userInfo.get("Username")+'"');
        datawriter.println("firstname = " + '"'+userInfo.get("FirstName")+'"');
        datawriter.println("surname = " + '"'+userInfo.get("LastName")+'"');
        datawriter.println("passwordhash = " + '"'+userInfo.get("Password")+'"');
        // Data section
        datawriter.println("[data]");
        datawriter.println("chips = " + '"' + 1000 + '"');
        datawriter.print("lastaccessed = " + '"'+formattedNow+'"');
        datawriter.close();

        PrintWriter dictwriter = new PrintWriter(localDictionary.toFile());
        try {
            dictwriter.println(tomlDictString);
            dictwriter.print(userInfo.get("Username")+" = " + '"' + fileCount + '"');
            dictwriter.close();
        } catch (Exception e) {
            System.out.println("Failed to add user to dictionary in local database");
            return false;
        }
        // report success
        return true;
    }
}
