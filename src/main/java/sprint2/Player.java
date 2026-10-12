package sprint2;

public class Player {
    private String fName;
    private String lName;
    private int jerseyNum;
    private Team team;

    public int numFreeThrows;
    public int attemptedFreeThrows;
    public double percentFreeThrows;

    public int numTwoPointers;
    public int attemptedTwoPointers;
    public double percentTwoPointers;

    public int numThreePointers;
    public int attemptedThreePointers;
    public double percentThreePointers;

    public int totalPoints;
    public int numAssists;
    public int numFouls;

    public Player(String fName, String lName, int jerseyNum, Team team) {
        this.fName = fName;
        this.lName = lName;
        this.jerseyNum = jerseyNum;
        this.team = team;
    }

    public String getLastName() { return lName; }
    public String getFirstName() { return fName; }
    public Team getTeam() { return this.team; }
    public int getJerseyNumber() { return jerseyNum; }

    public void setJerseyNum() {
        if (team == null) throw new IllegalStateException("Team must be set before assigning jersey");
        jerseyNum = team.newJerseyNum();
    }

    public void setTeam(Team teamName) { this.team = teamName; }

    public void setFreeThrows(int attempted, int made) {
        numFreeThrows = made;
        attemptedFreeThrows = attempted;
        percentFreeThrows = attempted == 0 ? 0.0 : ((double) numFreeThrows / attemptedFreeThrows) * 100.0;
        setTotalPoints();
    }

    public void setTwoPointers(int attempted, int made) {
        numTwoPointers = made;
        attemptedTwoPointers = attempted;
        percentTwoPointers = attempted == 0 ? 0.0 : ((double) numTwoPointers / attemptedTwoPointers) * 100.0;
        setTotalPoints();
    }

    public void setThreePointers(int attempted, int made) {
        numThreePointers = made;
        attemptedThreePointers = attempted;
        percentThreePointers = attempted == 0 ? 0.0 : ((double) numThreePointers / attemptedThreePointers) * 100.0;
        setTotalPoints();
    }

    public void setAssists(int assists) { numAssists = assists; }
    public void setFouls(int fouls) { numFouls = fouls; }

    public int getTotalPoints() { return totalPoints; }

    public void setTotalPoints() {
        totalPoints = numFreeThrows + (2 * numTwoPointers) + (3 * numThreePointers);
    }
}

