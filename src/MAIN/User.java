package MAIN;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/*
 * TODO : Add some field which can be used to connect to the user later. (while he is still logged on) 
 */
public class User {
    private Location currLocation;
    protected String userEmail;
    protected InetAddress ipAddress;
    int port;

    public User(String userEmail, InetAddress ipAddress, int port) {
        this.userEmail = userEmail;
        this.ipAddress = ipAddress;
    }

    protected Location getCurrLocation() {
        return this.currLocation;
    }

    protected void setCurrLocation(Location currLocation) {
        this.currLocation = currLocation;
    }
    
    protected InetAddress getIPAddress() {
        return this.ipAddress;
    }
}
