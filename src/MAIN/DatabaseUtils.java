package MAIN;
/*
 * This class provides methods to set up the database.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DatabaseUtils {
    protected static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    protected static final String DB_URL = "jdbc:mysql://localhost/";
    
    protected static final String USER = "harshini";
    protected static final String PASS = "password";
    
    protected static final String DB_NAME = "MESSENGER";
    protected static final String TABLES_URL = "jdbc:mysql://localhost/" + DB_NAME;
    
    /*
     * Creates a database called "MESSENGER". 
     */
    public static void createDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating database '" + DB_NAME + "'...");
            stmt = conn.createStatement();
            String createDB = "CREATE DATABASE " + DB_NAME;
            stmt.executeQuery(createDB);
            System.out.println("Database " + DB_NAME + " created successfully...");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        
        System.out.println("Finished creating the Database");
    }
    
    /*
     * Takes in a file and writes those users to the database - for now, assumes it is a CSV file where 
     * each line has the username and password separated by a comma.
     * Creates a "USERS" table in the database and adds this info to it. 
     */
    public static void populateUsersTable(String csvFile) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to the messenger database...");
            conn = DriverManager.getConnection(TABLES_URL, USER, PASS);
            System.out.println("Connected database successfully...");
            
            System.out.println("Creating users table in given database...");
            stmt = conn.createStatement();
            
            String createTable = "CREATE TABLE USERS " +
                         "(id INTEGER not NULL, " +
                         " UserEmail VARCHAR(255), " + 
                         " Password VARCHAR(255), " + 
                         " PRIMARY KEY (id))"; 

            stmt.executeUpdate(createTable);
            System.out.println("Created table in given database...");
            
            System.out.println("Inserting records into the table...");
            
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";
            
            try {
                br = new BufferedReader(new FileReader(csvFile));
                int id = 1;
                while ((line = br.readLine()) != null) {
                    String[] user = line.split(cvsSplitBy);
                    String record = "VALUES (" + id++ + ", '" + user[0] + "', '" + user[1] + "')";
                    String insertRecord = "INSERT INTO Users " + record;
                    stmt.executeUpdate(insertRecord);
                }
                System.out.println("Successfully inserted " + --id + "users");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
         } catch (SQLException se) {
            se.printStackTrace();
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            try {
               if (stmt != null) {
                   conn.close();
               }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            
            try {
               if (conn != null) {
                   conn.close();
               }
            } catch (SQLException se) {
               se.printStackTrace();
            }
         }
    }
    
    /*
     * Creates a table "TRANSACTIONS" which stores the record of all the pending messages. 
     * For each transaction, it stores the sender, receiver, messageId, messageText.
     * 
     * TODO : For now, just added support for simple textual messages that can have a 
     * maximum of 256 characters. Make this more generic, should be able to add pictures,
     * and increase the count of characters.
     * 
     * TODO : For now, assumes that the location is a string representing a room number. 
     * Later, should be able to directly store an instance of the type Location in the 
     * database.
     */
    public static void createTransactionsTable() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to the messenger database...");
            conn = DriverManager.getConnection(TABLES_URL, USER, PASS);
            System.out.println("Connected database successfully...");
            
            System.out.println("Creating transactions table in given database...");
            stmt = conn.createStatement();
            
            String createTable = "CREATE TABLE TRANSACTIONS " +
                         "(id INTEGER not NULL, " +
                         " SenderEmail VARCHAR(255), " + 
                         " ReceiverEmail VARCHAR(255), " + 
                         " MessageText VARCHAR(255), " +
                         " RoomNumber VARCHAR(255), " +
                         " PRIMARY KEY (id))"; 

            stmt.executeUpdate(createTable);
            System.out.println("Created table in given database...");
         } catch (SQLException se) {
            se.printStackTrace();
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            try {
               if (stmt != null) {
                   conn.close();
               }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            
            try {
               if (conn != null) {
                   conn.close();
               }
            } catch (SQLException se) {
               se.printStackTrace();
            }
         }
    }
    
    /* Deletes the database and the tables from the system so that when the program is run
     * again, everything works.
     */
    public static void cleanup() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
           Class.forName(JDBC_DRIVER);

           System.out.println("Connecting to a selected database...");
           conn = DriverManager.getConnection(DB_URL, USER, PASS);
           System.out.println("Connected database successfully...");
           
           System.out.println("Deleting database...");
           stmt = conn.createStatement();
           
           String sql = "DROP DATABASE MESSENGER";
           stmt.executeUpdate(sql);
           System.out.println("Database deleted successfully...");
        } catch (SQLException se) {
           se.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
           try {
              if (stmt != null) {
                  conn.close();
              }
           } catch (SQLException se) {
               se.printStackTrace();
           }
           
           try {
              if (conn != null) {
                  conn.close();
              }
           } catch (SQLException se) {
              se.printStackTrace();
           }
        }
    }
}