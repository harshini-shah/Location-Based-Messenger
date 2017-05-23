/*
 * TODO : Add some field which can be used to connect to the user later. (while he is still logged on) 
 */
public class User {
	private Location currLocation;
	protected String userEmail;

	public User(String userEmail) {
		this.userEmail = userEmail;
	}

	protected Location getCurrLocation() {
		return this.currLocation;
	}

	protected void setCurrLocation(Location currLocation) {
		this.currLocation = currLocation;
	}
}
