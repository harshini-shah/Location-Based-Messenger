package MAIN;

import java.util.HashSet;

/*
 * This is the location that can be specified by the user. For now, the user can only specify a string
 * which will represent the room number of Donald Bren. Later, this class can be modified by giving the
 * user the flexibility to specify a string (name of place), an integer (room number) or the co-ordinates
 * on a map. The queue of requests remaining that the server checks, consists of the message ID and an 
 * instance of this class that can be used to check if the user is indeed at the specified location. 
 */
public class Location {
	private String roomNum;

	public enum Distance {
		VERY_NEAR, NEAR, FAR, VERY_FAR,
	};

	/* Have added this for future Scalability */
	private boolean isGPS = false;
	public Distance distance;

	public Location(String roomNum) {
		/* To be used for DBH */
		this.roomNum = roomNum;
		this.distance = Distance.VERY_FAR;
	}

	Location() {
		/* To be used for GPS */
		isGPS = true;
	}

	public String getRoomNum() {
		return roomNum;
	}

	@Override
	public int hashCode() {
		return roomNum.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (isGPS || !(obj instanceof Location))
			return false;

		Location loc = (Location) obj;
		return roomNum.equals(loc.roomNum);
	}

	@Override
	public String toString() {
		return roomNum;
	}

	public boolean isEqualImpl(HashSet<Location> locationList) {
		if (isGPS)
			return false;

		return locationList.contains(this);
	}

	/*
	 * Implement the logic for comparison of two Location objects, and depending
	 * on the result, return the right value of 'Distance'
	 */
	public Distance getDistance(HashSet<Location> locationlist) {
		return Distance.VERY_NEAR;
	}
}
