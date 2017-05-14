import java.io.Serializable;

//TODO: send client identification on log-in
/*Message object to be used, make this serialized*/
public class Message implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 42L;
    int what;
    String field1, field2, field3;
    Location location;

    Message(int w, String f1, String f2, String f3, Location loc) {
        what = w;
        field1 = f1;
        field2 = f2;
        field3 = f3;
        location = loc;
    }
}
