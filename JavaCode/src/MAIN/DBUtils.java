package MAIN;
/*
 * This class provides methods to set up the database.
 * TODO : Multicast is NOT supported.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

public class DBUtils {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	static final String USER = "harshini";
	static final String PASS = "password";

	static final String DB_NAME = "MESSENGER";
	static final String TABLES_URL = "jdbc:mysql://localhost/" + DB_NAME;

	static Connection conn = null;
	static Statement stmt = null;

	private static int transactionID = 0;

	public static void createConnection() throws Exception {
		Class.forName(JDBC_DRIVER);
		System.out.println("Connecting to the messenger database...");
		conn = DriverManager.getConnection(TABLES_URL, USER, PASS);
	}

	/*
	 * Creates a database called "MESSENGER".
	 */
	public static void createDatabase() {

		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating database '" + DB_NAME + "'...");
			stmt = conn.createStatement();
			String createDB = "CREATE DATABASE " + DB_NAME;
			stmt.executeUpdate(createDB);
			System.out.println("Database " + DB_NAME + " created successfully...");

			System.out.println("Finished creating the Database");
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
	}

	/*
	 * Takes in a file and writes those users to the database - for now, assumes
	 * it is a CSV file where each line has the username and password separated
	 * by a comma. Creates a "USERS" table in the database and adds this info to
	 * it.
	 */
	public static void populateUsersTable(String csvFile) {
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to the messenger database...");
			conn = DriverManager.getConnection(TABLES_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			System.out.println("Creating users table in given database...");
			stmt = conn.createStatement();

			String createTable = "CREATE TABLE USERS " + "(id INTEGER not NULL, " + " UserEmail VARCHAR(255), "
					+ " Password VARCHAR(255), " + " PRIMARY KEY (id))";

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
				System.out.println("Successfully inserted " + --id + " users");
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
		}
	}

	public static void populateDummyUsersTable(String csvFile) {
		try {
			Class.forName(JDBC_DRIVER);
			// System.out.println("Connecting to the messenger database...");
			conn = DriverManager.getConnection(TABLES_URL, USER, PASS);
			// System.out.println("Connected database successfully...");

			// System.out.println("Creating users table in given database...");
			System.out.println("Creating DUMMY_USERS table in given database...");
			stmt = conn.createStatement();

			String createTable = "CREATE TABLE DUMMY_USERS " + "(UserEmail VARCHAR(255), " + " Location VARCHAR(255))";

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
					String record = "VALUES ('" + user[0] + "', '" + user[1] + "')";
					String insertRecord = "INSERT INTO DUMMY_USERS " + record;
					stmt.executeUpdate(insertRecord);
				}
				System.out.println("Successfully inserted dummy_users with location");
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
		}
	}

	public static void changeLocation(String userEmail, String newLocation) {

		try {
			Class.forName(JDBC_DRIVER);
			// System.out.println("Connecting to the messenger database...");
			conn = DriverManager.getConnection(TABLES_URL, USER, PASS);
			// System.out.println("Connected database successfully...");

			// System.out.println("Creating users table in given database...");
			stmt = conn.createStatement();

			String deleteRecord = "UPDATE Dummy_users " + "SET Location = '" + newLocation + "' WHERE UserEmail = '"
					+ userEmail + "'";

			stmt.executeUpdate(deleteRecord);
			// stmt = conn.createStatement();
			// String record = "VALUES ('" + userEmail + "', '" + newLocation +
			// "')";
			// String insertRecord = "INSERT INTO DUMMY_USERS " + record;
			// stmt.executeUpdate(insertRecord);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Creates a table "TRANSACTIONS" which stores the record of all the pending
	 * messages. For each transaction, it stores the sender, receiver,
	 * messageId, messageText.
	 * 
	 * TODO : For now, just added support for simple textual messages that can
	 * have a maximum of 256 characters. Make this more generic, should be able
	 * to add pictures, and increase the count of characters.
	 * 
	 * TODO : For now, assumes that the location is a string representing a room
	 * number. Later, should be able to directly store an instance of the type
	 * Location in the database.
	 */
	public static void createTransactionsTable() {
		try {
			stmt = conn.createStatement();

			String createTable = "CREATE TABLE TRANSACTIONS " + "(id INTEGER not NULL, " + " SenderEmail VARCHAR(255), "
					+ " ReceiverEmail VARCHAR(255), " + " MessageText VARCHAR(255), " + " RoomNumber VARCHAR(255), "
					+ " PRIMARY KEY (id))";

			stmt.executeUpdate(createTable);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashSet<Location> getCurrentLocationForUser(String userEmail) {
		HashSet<Location> mLocationList = new HashSet<Location>();
		try {
			stmt = conn.createStatement();
			String findLoc = "SELECT Location FROM Dummy_users " + "WHERE UserEmail = '" + userEmail + "'";
			ResultSet rs = stmt.executeQuery(findLoc);
			while (rs.next()) {
				mLocationList.add(new Location(rs.getString("Location")));
				return mLocationList;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mLocationList;
	}

	public static int addTransaction(Message message) {
		try {
			stmt = conn.createStatement();

			String record = "VALUES (" + ++transactionID + ", '" + message.field1 + "', '" + message.field2 + "', '"
					+ message.field3 + "', '" + message.field4 + "')";
			String insertRecord = "INSERT INTO TRANSACTIONS " + record;
			stmt.executeUpdate(insertRecord);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transactionID;
	}

	public static String checkUser(String userEmail) {
		try {
			stmt = conn.createStatement();

			String sql = "SELECT Password FROM USERS " + "WHERE UserEmail = '" + userEmail + "'";
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next()) {
				return null;
			} else {
				return rs.getString("Password");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* TODO: made id arraylist */
	public static Message getMessage(int id, String receiverEmail) {
		/**
		 * CREATE MSG OBJ msgType = NOTIFICATION f1 - recevierEmail
		 * (vignesh@uci.edu) f2 - f3 - "messageText(id1) | messageText(id2) |
		 * messageText(id3)....." f4 - "owner(id1) | owner(id2) | owner(id3)..."
		 */
		Message message = new Message();
		message.field1 = receiverEmail;

		try {
			stmt = conn.createStatement();

			String sql = "SELECT SenderEmail, MessageText FROM TRANSACTIONS " + "WHERE id = " + id;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				message.field4 = rs.getString("SenderEmail");
				message.field3 = rs.getString("MessageText");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	/*
	 * Assumes that all the recipients are the same - No error checking done
	 */
	public static Message getMessagesFromDB(ArrayList<Integer> messageIdList, String userEmail) {

		if (messageIdList == null || messageIdList.isEmpty() || userEmail == null) {
			return null;
		}

		Message message = new Message();

		String field3 = "";
		String field4 = "";

		for (int id : messageIdList) {
			message = getMessage(id, userEmail);
			field3 += message.field3 + " | ";
			field4 += message.field4 + " | ";
		}

		field3 = field3.substring(0, field3.length() - 3);
		field4 = field4.substring(0, field4.length() - 3);

		message.msgType = Message.MsgType.NOTIFICATION;
		message.field3 = field3;
		message.field4 = field4;
		return message;
	}

	/*
	 * Deletes the database and the tables from the system so that when the
	 * program is run again, everything works.
	 */
	public static void cleanup() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

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