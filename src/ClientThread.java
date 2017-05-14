import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket;
    private String userEmail;
    
    public ClientThread(Socket socket, String userEmail) {
        this.socket = socket;
        this.userEmail = userEmail;
    }
}
