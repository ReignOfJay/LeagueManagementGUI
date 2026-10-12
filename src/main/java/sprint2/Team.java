package sprint2;
import java.util.ArrayList;

public class Team {
	String name;
	int numWins;
	int numLosses;
	int jerseyNum = 0;
	ArrayList <Player> players = new ArrayList<>();
	ArrayList <Integer> jerseyNumbers = new ArrayList<>();
	
	public Team() {}
	
	public Team (String name) {
		this.name = name;
		numWins = 0;
		numLosses = 0;
	}
	
	public ArrayList<Player> getPlayers() {
        return players;
    }
	public String getName() {
		return name;
	}
	
	public int getWins()   {
			return numWins; 
			}
    public int getLosses() { 
    	return numLosses; 
    }

    private int numTies = 0;
    
    public int getTies()   { 
    	return numTies; 
    }
    
    public int newJerseyNum() {
    	
        int newNum = ++jerseyNum;
        jerseyNumbers.add(newNum);
        return newNum;
    }
    
 // Record results:
    public void recordWin()  { 
    	numWins++;
    }
    public void recordLoss() { 
    	numLosses++;
    }
    public void recordTie()  { 
    	numTies++;
    }
    
    public void undoWin() {
        if (numWins > 0) numWins--;
    }

    public void undoLoss() {
        if (numLosses > 0) numLosses--;
    }

    public void undoTie() {
        if (numTies > 0) numTies--;
    }

    
 // Points: Win=2, Tie=1, Loss=0 (adjust if your rules differ)
    public int getPoints() { return 2 * numWins + numTies; }
    
    @Override
    public String toString() {
        return name; // display the team name in ListView
    }
}
