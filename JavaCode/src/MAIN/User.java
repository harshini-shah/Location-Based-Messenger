package MAIN;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;

/*
 * TODO : Add some field which can be used to connect to the user later. (while he is still logged on) 
 */
public class User {
    private static String basic = "http://sensoria.ics.uci.edu:8001/semanticobservation/get?requestor_id=primal@uci.edu&service_id=1&subject_id=";
    private static String startTime = "&type=1&start_timestamp=";
    private HashSet<Location> currLocationList;
    private Date mLastSearchedTime;
    private String mUserEmail;
    private InetAddress mIpAddress;

    public User(String userEmail, InetAddress ipAddress) {
        this.mUserEmail = userEmail;
        this.mIpAddress = ipAddress;
        try {
            mLastSearchedTime = Utils.mDateFormat.parse("2017-06-04 12:22:23");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected HashSet<Location> getCurrLocationList() {
        /* Go to TIPPERS and get the locations */
        currLocationList = Utils.getDBHLocationListForUrl(
                basic + mUserEmail + startTime + (Utils.mDateFormat.format(mLastSearchedTime)).replaceAll(" ", "%20"));
        mLastSearchedTime = new Date();
        return currLocationList;
    }

    protected void updateIP(InetAddress ip) {
        mIpAddress = ip;
    }

    protected InetAddress getIPAddress() {
        return this.mIpAddress;
    }
}