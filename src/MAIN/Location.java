package MAIN;
/*
 * This is the location that can be specified by the user. For now, the user can only specify a string
 * which will represent the room number of Donald Bren. Later, this class can be modified by giving the
 * user the flexibility to specify a string (name of place), an integer (room number) or the co-ordinates
 * on a map. The queue of requests remaining that the server checks, consists of the message ID and an 
 * instance of this class that can be used to check if the user is indeed at the specified location. 
 */
public class Location {
	private String roomNum;
	
	public enum Distance {VERY_NEAR,
	    NEAR,
	    FAR,
	    VERY_FAR,
	    };
	/* Have added this for future Scalability */
	private boolean isGPS = false;
	public Distance distance;

	Location (String roomNum) {
		/*To be used for DBH*/
		this.roomNum = roomNum;
		this.distance = Distance.VERY_FAR;
	}
	
	Location () {
		/*To be used for GPS*/
		isGPS = true;
	}

	public String getRoomNum() {
		return roomNum;
	}

	/* This is to check if two given locations are same or not. */
	@Override
	public boolean equals(Object obj) {
		return isEqualImpl((Location) obj);
	}

	public boolean isEqualImpl(Location loc) {
		if (isGPS)
			return false;
		return loc.roomNum.equals(roomNum);
	}
	
	/*
	 * Implement the logic for comparison of two Location objects,
	 * and depending on the result, return the right value of 'Distance'
	 */
	public Distance getDistance(Location location) {
	    return Distance.VERY_NEAR;
	}
}
