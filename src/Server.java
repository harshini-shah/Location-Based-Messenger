import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Starts the server on the specified port number. It listens on the port for connection requests (log in) from 
 * clients, and accepts them. It then prints the client address with whom it is connected. Next, it asks for the 
 * username and password, verifies that username is in the database, and that the username and passwords match.
 * It then spawns a thread (instance of the ClientThread class) to handle it. 
 */
public class Server {
    public Server(int port) throws IOException{
        ServerSocket server = new ServerSocket(port);   // Creates a listening port
        while (true) {
            Socket client = server.accept();    // Accepts connection request from client
            System.out.println("Accepted connection request from " + client.getInetAddress());  // Prints client address
            
            // Get the input and output streams for the socket
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            
            // The server asks the client the email id
            String requestUsername = "Please enter your email id connected with the TIPPERs system";
            out.writeObject(requestUsername);
            String userEmail = "";
            String password = "";
            
            try {
                // Reads the userEmail from the socket
                userEmail = (String)in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            // TODO : Should check from the database that the user email is a registered one 
         
            // Asks client for password
            String requestPassword = "Please enter your password";
            out.writeObject(requestPassword);
            
            try {
                // Reads the Password from the socket
                password = (String)in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            // TODO : Check that the user id and passwords match (Database)
            
            //  Control should reach here only if all conditions satisfied
            
            // Spawn a thread to check for pending requests
            ClientThread clientThread = new ClientThread(client, userEmail);
            
            // TODO : figure out when to shut the server
        }
    }
}
