package Test;

import MAIN.DBUtils;
import MAIN.Message;

/**
 * Checks the following functions of DBUtils:
 * 
 * 1. Create database (createDatabase())
 * 2. Delete Database (cleanup())
 * 3. Create User table (input is CSV file) (populateUsersTable)
 * 4. Authenticates user name and password  (checkUser and CheckPassword)
 * 5. Creating the Transaction Table
 * 6. Adding 
 * 
 * @author harshini
 *
 */
public class DBTests {
  
    public static Message createFirstDummyMessage() {
        Message m = new Message();
        m.field1 = "Harshini";
        m.field2 = "Vignesh";
        m.field3 = "Hello from Harshini to Vignesh";
        m.field4 = "DBH 2085";
        return m;
    }
    
    public static Message createSecondDummyMessage() {
        Message m = new Message();
        m.field1 = "Madhur";
        m.field2 = "Harshini";
        m.field3 = "Hello from Madhur to Harshini";
        m.field4 = "DBH 2085";
        return m;
    }
    
    public static Message createThirdDummyMessage() {
        Message m = new Message();
        m.field1 = "Vignesh";
        m.field2 = "Madhur";
        m.field3 = "Hello from Vignesh to Madhur";
        m.field4 = "DBH 2085";
        return m;
    }
    
    public static void main(String[] args) {
        DBUtils.createDatabase();
        DBUtils.populateDummyUsersTable("C:/Users/harshini/Downloads/Dummy_users.csv");
//        System.out.println(DBUtils.checkUser("Harshini"));
//        System.out.println(DBUtils.checkPassword("Harshini", "harshi"));
        
        Message m1 = createFirstDummyMessage();
        Message m2 = createSecondDummyMessage();
        Message m3 = createThirdDummyMessage();
        
        DBUtils.createTransactionsTable();
        
        System.out.println(DBUtils.addTransaction(m1));
        System.out.println(DBUtils.addTransaction(m2));
        System.out.println(DBUtils.addTransaction(m3));

        System.out.println(DBUtils.getMessage(1, m1.field2));
        
        DBUtils.cleanup();
    }
}
