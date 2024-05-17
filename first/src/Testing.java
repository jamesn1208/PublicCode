import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class Testing {
    public static void main(String[] args) throws SQLException, IOException {
        Database database = new Database();
        AccountManagement accountManagement = new AccountManagement();
        database.initialiseDatabase();
        // Testing login
        try {
            System.out.println("Testing Login");
            String username = "james.nash";
            if (database.doesUserExist(username)) {
                System.out.println("Username doesn't exist!");
            } else {
                System.out.println("Username already exists!");
            }
            System.out.println("Login test passed!");
        } catch (Exception e) {
            System.out.println("Login test failed! " + e);
        }
        System.out.println("-".repeat(20));
        System.out.println("\n");
        // Testing register user
        try {
            System.out.println("Testing Register User");
            HashMap<String, String> userInfo = new HashMap<>();
            userInfo.put("Username", "james.nash1");
            userInfo.put("FirstName", "James");
            userInfo.put("LastName", "Nash");
            String hashedPassword = accountManagement.hashInput("password");
            userInfo.put("Password", hashedPassword);
            if (!database.registerUser(userInfo)) {
                System.out.println("User registered!");
            } else {
                System.out.println("user failed to register!");
            }
            System.out.println("Register User test passed!");
        } catch (Exception e) {
            System.out.println("Register User test failed! " + e);
        }
        System.out.println("-".repeat(20));
        System.out.println("\n");
    }
}
