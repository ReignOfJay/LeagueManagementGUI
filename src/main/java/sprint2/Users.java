package sprint2;


public class Users {

	public String fName;
	public String lName;
	public String id;
	public String pass;
	public Role role;
	
	//Constructor for users LA, LO, and TO
	public Users(String fName, String lName, String id, String pass, Role role){
		this.fName = fName;
		this.lName = lName;
		this.id = id;
		this.pass = pass;
		this.role = role;
	}
	
	public boolean canCreateLeague() {
	    return role == Role.LeagueA;
	}
	public boolean canScheduleGames() {
	    return role == Role.LeagueA;
	}
	public boolean canAddTeams() {
	    return role == Role.LeagueA || role == Role.LeagueA;
	}
	public boolean canAddPlayers() {
	    return role == Role.LeagueA || role == Role.LeagueO;
	}
	public boolean canAddUsers() {
		return role == Role.LeagueA;
	}
	public boolean canViewUsers() {
	    return role == Role.LeagueA;
	}
	
	public String getFirstName() {
		return fName;
	}
	public String getLastName() {
		return lName;
	}
	public String getID() {
		return id;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String password){
		this.pass = password;
	}
	public Role getRoles() {
		return role;
	}
}
