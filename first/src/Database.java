import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;
import com.moandjiezana.toml.Toml;

class Database {
    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static Connection conn;
    private static final Path databaseConfig = Paths.get("cfg/database.toml");
    static Scanner userInput = new Scanner(System.in);
    public boolean initialiseDatabase() throws IOException {
        try {
            if (!Files.exists(databaseConfig)) {
                System.out.println("Database config file does not exist, Falling back to local database");
                return false;
            }
            String tomlString = new String(Files.readAllBytes(databaseConfig));
            Toml toml = new Toml().read(tomlString);
            this.DB_DRIVER = toml.getString("database.DRIVER");
            this.DB_URL = toml.getString("database.URL");
            this.DB_USERNAME = toml.getString("database.USERNAME");
            this.DB_PASSWORD = toml.getString("database.PASSWORD");
        } catch (Exception e) {
            System.out.println("Failed to read database config file");
            return false;
        }
        Connection conn = null;
        try{
            //Register the JDBC driver
            Class.forName(DB_DRIVER);

            //Open the connection
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            if(conn != null) {
                System.out.println("Connected to online services\n");
                this.conn = conn;
                return true;
            }
            else{
                System.out.println("Failed to connect to online services... Falling back to local database");
                return false;
            }
        }
        catch(Exception e){
            System.out.println("Failed to connect to online services... Falling back to local database");
            return false;
        }
    }
    public static ResultSet getUser(String entered_user, String hashed_pass) throws SQLException {
        //Initialize the script runner
        PreparedStatement stmt = conn.prepareStatement(
                "select * from Users " +
                "inner join Usernames on Usernames.UserID=Users.UserID " +
                "where Username = ? and UserPassword = ?;");
        stmt.setString(1, entered_user);
        stmt.setString(2, hashed_pass);
        ResultSet queryData = null;
        //Running the script
        try {
            queryData = stmt.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Failed to get user");
            return null;
        }
        if (queryData.isBeforeFirst()) {
            return queryData;
        } else {
            return null;
        }
    }
    public static boolean doesUserExist(String enteredUsername) throws SQLException {
        //Initialize the script runner
        PreparedStatement stmt = conn.prepareStatement("select Users.UserID from Users " +
                "inner join Usernames on Usernames.UserID=Users.UserID " +
                "where Username = ?");
        stmt.setString(1, enteredUsername);
        //Running the script
        ResultSet accountExists = stmt.executeQuery();
        if (accountExists.isBeforeFirst()) {
            return false;
        } else {
            return true;
        }
    }
    public static boolean updateRow(String table, String element, String newData, String userID) {
        //Initialize the script runner
        boolean returned = false;
        PreparedStatement stmt = null;
        try {
            //Running the script
            stmt = conn.prepareStatement("USE uniData;" +
                    "UPDATE " + table + " " +
                    "SET " + element + " = ? " +
                    "WHERE UserID = ?");
            stmt.setString(1, newData);
            stmt.setString(2, userID);
            returned = stmt.execute();
        } catch (Exception e) {
            System.out.println("Failed to update row");
        }
        return !(returned);
    }
    public static boolean updateChips(int newData, String userID) {
        //Initialize the script runner
        boolean returned = false;
        PreparedStatement stmt = null;
        String newChips = Integer.toString(newData);
        try {
            //Running the script
            stmt = conn.prepareStatement("USE uniData;" +
                    "UPDATE Users " +
                    "SET Chips = ? " +
                    "WHERE UserID = ?");
            stmt.setString(1, newChips);
            stmt.setString(2, userID);
            returned = stmt.execute();
        } catch (Exception e) {
            System.out.println("Failed to update row");
        }
        return !(returned);
    }
    public static boolean registerUser(HashMap userInfo) throws SQLException {
        //Initialize the script runner
        boolean returned = false;
        boolean validUsername = false;
        PreparedStatement Insertionstmt = null;
        try {
            // Check it doesn't already exist
            validUsername = doesUserExist((String) userInfo.get("Username"));
            while (!validUsername) {
                System.out.println("\nUsername already taken! Please enter a new username:");
                String newUsername = userInput.nextLine();
                userInfo.put("Username", newUsername);
                validUsername = doesUserExist((String) userInfo.get("Username"));
            }
            // Create account
            Insertionstmt = conn.prepareStatement(
                    "START TRANSACTION; " +
                    "INSERT INTO Usernames (Username) " +
                    "VALUES (?); " +
                    "INSERT INTO Users (UserID, LastName, FirstName, UserPassword, Chips) " +
                    "VALUES (LAST_INSERT_ID(), ?, ?, ?, 1000); " +
                    "COMMIT;");
            Insertionstmt.setString(1, (String) userInfo.get("Username"));
            Insertionstmt.setString(2, (String) userInfo.get("LastName"));
            Insertionstmt.setString(3, (String) userInfo.get("FirstName"));
            Insertionstmt.setString(4, (String) userInfo.get("Password"));
            returned = Insertionstmt.execute();
        } catch (Exception e) {
            //System.out.println(e);
            System.out.println("Failed to register user");
        }
        //Running the script
        return returned;
    }
}
