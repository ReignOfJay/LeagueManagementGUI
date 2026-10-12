package sprint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class League{
	public String leagueName;
	public ArrayList<Team> teams = new ArrayList <Team>();
	
	public League(String leagueName){
		this.leagueName = leagueName;
	}
	
	public String getLeagueName() {
		return leagueName;
	}
	
	public ArrayList<Team> getTeams() {
        return teams;
    }
}
