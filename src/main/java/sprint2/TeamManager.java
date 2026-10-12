package sprint2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TeamManager {

	private final Team team;

    public TeamManager(Team team) {
        this.team = team;
    }
    
    public void addPlayer(Users user, String first, String last) {
    	if(!user.canAddPlayers()) {
    		throw new SecurityException("You do not have permission to add teams.");
    	}
    	
    	int jersey = team.newJerseyNum();
        Player p = new Player(first, last, 0, team);
        p.setTeam(team);
        p.setJerseyNum();     
        team.getPlayers().add(p);
    }

    public ArrayList<Player> getSortedPlayers() {
        return team.getPlayers().stream()
            .sorted(Comparator.comparing(Player::getLastName).thenComparing(Player::getFirstName).thenComparing(Player::getJerseyNumber))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
