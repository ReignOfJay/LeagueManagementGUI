package sprint2;

import java.util.HashMap;
import java.util.Map;


public class Game {
    private static int NEXT_ID = 1;

    private int id;
    private String date;   // e.g., "2025-11-05 19:00"
    private Team home;
    private Team away;

    private Integer homeScore;   // null until recorded
    private Integer awayScore;   // null until recorded
    private boolean finished = false;
    
    private boolean bracketGame;   // true if this is part of the championship bracket
    private int bracketRound;      // 1 = first round, 2 = semifinal, 3 = final, ...
    private int bracketIndex;

    
    public Game() {
    	
    }
    
    public Game(String dateOnly) {
        this.id = NEXT_ID++;
        this.date = dateOnly;
        // home and away intentionally left null for bracket placeholders
    }

    
    public Game(String date, Team home, Team away) {
        if (home == null || away == null || home == away) {
            throw new IllegalArgumentException("Invalid teams for game");
        }
        this.id = NEXT_ID++;
        this.date = date;
        this.home = home;
        this.away = away;
    }

    public int getId() { 
    	return id; 
    }
    public String getDate() {
    	return date; 
    }
    public Team getHome() {
    	return home; 
   	}
    public Team getAway() { 
    	return away; 
   	}
    public boolean isFinished() {
    	return finished; 
   	}
    public Integer getHomeScore() { 
    	return homeScore; 
   	}
    public Integer getAwayScore() { 
    	return awayScore; 
    }
    
    public boolean isBracketGame() {
        return bracketGame;
    }

    public void setBracketGame(boolean bracketGame) {
        this.bracketGame = bracketGame;
    }

    public int getBracketRound() {
        return bracketRound;
    }

    public void setBracketRound(int bracketRound) {
        this.bracketRound = bracketRound;
    }

    public int getBracketIndex() {
        return bracketIndex;
    }

    public void setBracketIndex(int bracketIndex) {
        this.bracketIndex = bracketIndex;
    }

    public void recordScore(int homePts, int awayPts) {
        if (isFinished()) {
            throw new IllegalStateException("Score already recorded");
        }

        if (homePts < 0 || awayPts < 0) {
            throw new IllegalArgumentException("Negative scores not allowed");
        }

        this.homeScore = homePts;
        this.awayScore = awayPts;
        setFinished(true);

        // Update team standings
        if (homePts > awayPts) {
            home.recordWin();
            away.recordLoss();
        }
        else if (awayPts > homePts) {
            away.recordWin();
            home.recordLoss();
        }
        else {
            home.recordTie();
            away.recordTie();
        }
    }

    
    public String summaryLine() {
        // Placeholder bracket game with TBD teams
        if (home == null || away == null) {
            return String.format(
                    "#%d  %s  (Bracket game TBD)",
                    id, date
            );
        }

        if (!isFinished()) {
            return String.format(
                    "#%d  %s  %s vs %s  (Scheduled)",
                    id, date, home.getName(), away.getName()
            );
        }

        return String.format(
                "#%d  %s  %s %d – %d %s",
                id, date,
                home.getName(), homeScore,
                awayScore, away.getName()
        );
    }

    
    public void setDate(String gameDate){
    	this.date = gameDate;
    }
    
    public void setHome(Team home) {
    	this.home = home; 
    }
    
    public void setAway(Team away) {
    	this.away = away;
    }
    
    public void setHomeScore(Integer homeScore) {
    	this.homeScore = homeScore;
    }
    public void setAwayScore(Integer awayScore) {
    	this.awayScore = awayScore;
    }
    public void setFinished(boolean finished) {
    	this.finished = finished;
    }
    
    public Team getWinner() {
        if (!finished || homeScore == null || awayScore == null) {
            return null;
        }
        if (homeScore > awayScore) {
            return home;
        } else if (awayScore > homeScore) {
            return away;
        }
        // tie – for bracket games, you probably shouldn't allow ties; this makes
        // the bracket not advance until resolved
        return null;
    }

    
 // ---------- STATS FIELD ----------
 // instead of: private transient HashMap<Player, PlayerStats> stats;
    private HashMap<String, PlayerStats> stats = new HashMap<>();

    private String keyFor(Player p) {
        return p.getTeam().getName() + "#" + p.getJerseyNumber();
    }

    public void setStats(Player p, int ftAtt, int ftMade,
                         int twoAtt, int twoMade,
                         int threeAtt, int threeMade,
                         int assists, int fouls) {

        if (stats == null) {
            stats = new HashMap<>();
        }

        PlayerStats ps = new PlayerStats();
        ps.ftAtt = ftAtt;
        ps.ftMade = ftMade;
        ps.twoAtt = twoAtt;
        ps.twoMade = twoMade;
        ps.threeAtt = threeAtt;
        ps.threeMade = threeMade;
        ps.assists = assists;
        ps.fouls = fouls;

        stats.put(keyFor(p), ps);
    }
  
    public PlayerStats getStats(Player p) {
        if (stats == null) return null;
        return stats.get(keyFor(p));
    }

 
    // Tiny inner class - stores ONE game's stats for ONE player
    public static class PlayerStats {
        public int ftAtt, ftMade;
        public int twoAtt, twoMade;
        public int threeAtt, threeMade;
        public int assists;
        public int fouls;

        public int getPoints() {
            return ftMade + twoMade * 2 + threeMade * 3;
        }
    }
    
    @Override
    public String toString() {
        if (!finished) {
            return String.format("%s — %s vs %s (Not Played)",
                    date, home.getName(), away.getName());
        } else {
            String winner, loser;
            int wScore, lScore;

            if (homeScore > awayScore) {
                winner = home.getName();
                loser = away.getName();
                wScore = homeScore;
                lScore = awayScore;
            } else {
                winner = away.getName();
                loser = home.getName();
                wScore = awayScore;
                lScore = homeScore;
            }

            return String.format("%s — %s (%d) defeated %s (%d)",
                    date, winner, wScore, loser, lScore);
        }
    }

	public void setId(int nextId) {
		this.id = id;
		
	}

}
