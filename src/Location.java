/*
 * This is the location that can be specified by the user. For now, the user can only specify a string
 * which will represent the room number of Donald Bren. Later, this class can be modified by giving the
 * user the flexibility to specify a string (name of place), an integer (room number) or the co-ordinates
 * on a map. The queue of requests remaining that the server checks, consists of the message ID and an 
 * instance of this class that can be used to check if the user is indeed at the specified location. 
 */
public class Location {
	private String roomNum;

	/* Have added this for future Scalability */
	private boolean isGPS = false;

	Location (String roomNum) {
		/*To be used for DBH*/
		this.roomNum = roomNum;
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
}
