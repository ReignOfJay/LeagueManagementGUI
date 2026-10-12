package sprint2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * GameManager handles scheduling, recording results, seeding dummy stats,
 * reading stats from files, and producing various reports.
 */
public class GameManager {
    private final List<Game> schedule = new ArrayList<>();

    /** Record score by game ID (controller). */
    public boolean recordResult(int gameId, int homeScore, int awayScore) {
        Game g = findGameById(gameId);
        if (g == null) {
            System.out.println("Game not found: " + gameId);
            return false;
        }

        g.recordScore(homeScore, awayScore);

        // >>> Bracket advancement logic <<<
        if (g.isBracketGame()) {
            updateBracketAdvancement(g);
        }

        return true;
    }
    
    private void updateBracketAdvancement(Game finishedGame) {
        // Only handle our 4-team bracket for now
        int round = finishedGame.getBracketRound();
        int idx   = finishedGame.getBracketIndex();

        // Only semis feed into the final
        if (round != 1) return;

        // Find the final (round 2, index 0)
        Game finalGame = null;
        for (Game g : schedule) {
            if (g.isBracketGame()
                    && g.getBracketRound() == 2
                    && g.getBracketIndex() == 0) {
                finalGame = g;
                break;
            }
        }
        if (finalGame == null) return;

        Team winner = finishedGame.getWinner();
        if (winner == null) return;

        // Semi with index 0 feeds home, index 1 feeds away
        if (idx == 0) {
            finalGame.setHome(winner);
        } else if (idx == 1) {
            finalGame.setAway(winner);
        }
    }
    

    /** Find game by ID. */
    private Game findGameById(int id) {
        for (Game g : schedule) {
            if (g.getId() == id) return g;
        }
        return null;
    }

    /** Create and add a game. */
    public Game scheduleGame(String date, Team home, Team away) {
        Game g = new Game(date, home, away);
        schedule.add(g);
        return g;
    }

    /** Return a defensive copy of the schedule list. */
    public ArrayList<Game> getScheduleList() {
        return new ArrayList<>(schedule);
    }

    /** Player totals across all games. */
    public PlayerTotals getPlayerTotals(Player p) {
        PlayerTotals totals = new PlayerTotals();
        int games = 0;
        for (Game g : schedule) {
            Game.PlayerStats ps = g.getStats(p);
            if (ps != null) {
                games++;
                totals.ftAtt += ps.ftAtt;
                totals.ftMade += ps.ftMade;
                totals.twoAtt += ps.twoAtt;
                totals.twoMade += ps.twoMade;
                totals.threeAtt += ps.threeAtt;
                totals.threeMade += ps.threeMade;
                totals.assists += ps.assists;
                totals.fouls += ps.fouls;
            }
        }
        totals.gamesPlayed = games;
        return totals;
    }

    public static class PlayerTotals {
        public int ftAtt, ftMade;
        public int twoAtt, twoMade;
        public int threeAtt, threeMade;
        public int assists, fouls;
        public int gamesPlayed;

        public int totalPoints() {
            return ftMade + twoMade * 2 + threeMade * 3;
        }

        public double avg(double total) {
            return gamesPlayed == 0 ? 0 : total / gamesPlayed;
        }
    }

    /** Team-level aggregated stats. */
    public String getTeamStats(Team team) {
        int gamesPlayed = 0;
        int ftAtt = 0, ftMade = 0;
        int twoAtt = 0, twoMade = 0;
        int threeAtt = 0, threeMade = 0;
        int assists = 0, fouls = 0;
        int totalPoints = 0;

        for (Game g : schedule) {
            boolean teamInGame = false;
            for (Player p : team.getPlayers()) {
                Game.PlayerStats ps = g.getStats(p);
                if (ps != null) {
                    teamInGame = true;
                    ftAtt += ps.ftAtt;
                    ftMade += ps.ftMade;
                    twoAtt += ps.twoAtt;
                    twoMade += ps.twoMade;
                    threeAtt += ps.threeAtt;
                    threeMade += ps.threeMade;
                    assists += ps.assists;
                    fouls += ps.fouls;
                    totalPoints += ps.getPoints();
                }
            }
            if (teamInGame) gamesPlayed++;
        }

        if (gamesPlayed == 0) return "No stats available for this team.\n";

        double g = gamesPlayed;
        return String.format(
            "Team: %s\nGames Played: %d\n\nAvg Points: %.2f\nAvg FT: %.2f / %.2f\nAvg 2P: %.2f / %.2f\nAvg 3P: %.2f / %.2f\nAvg Assists: %.2f\nAvg Fouls: %.2f\n",
            team.getName(), gamesPlayed, totalPoints / g, ftMade / g, ftAtt / g,
            twoMade / g, twoAtt / g, threeMade / g, threeAtt / g, assists / g, fouls / g
        );
    }

    /** League teams average stats report. */
    public String getLeagueTeamsStats(League league) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== League Teams' Average Stats ===\n\n");

        for (Team team : league.getTeams()) {
            int gamesPlayed = 0;
            int ftAtt = 0, ftMade = 0;
            int twoAtt = 0, twoMade = 0;
            int threeAtt = 0, threeMade = 0;
            int assists = 0, fouls = 0;
            int totalPoints = 0;

            for (Game g : schedule) {
                boolean teamInGame = false;
                for (Player p : team.getPlayers()) {
                    Game.PlayerStats ps = g.getStats(p);
                    if (ps != null) {
                        teamInGame = true;
                        ftAtt += ps.ftAtt;
                        ftMade += ps.ftMade;
                        twoAtt += ps.twoAtt;
                        twoMade += ps.twoMade;
                        threeAtt += ps.threeAtt;
                        threeMade += ps.threeMade;
                        assists += ps.assists;
                        fouls += ps.fouls;
                        totalPoints += ps.getPoints();
                    }
                }
                if (teamInGame) gamesPlayed++;
            }

            sb.append("Team: ").append(team.getName()).append("\n");
            if (gamesPlayed == 0) {
                sb.append(" No data available\n\n");
                continue;
            }

            double g = gamesPlayed;
            sb.append(String.format(
                " Avg Points: %.2f\n Avg FT: %.2f / %.2f\n Avg 2P: %.2f / %.2f\n Avg 3P: %.2f / %.2f\n Avg Assists: %.2f\n Avg Fouls: %.2f\n\n",
                totalPoints / g, ftMade / g, ftAtt / g, twoMade / g, twoAtt / g,
                threeMade / g, threeAtt / g, assists / g, fouls / g
            ));
        }
        return sb.toString();
    }

    /** Delete stats for a game and undo standings changes. */
    public void deleteStats(int gameId) {
        Game g = findGameById(gameId);
        if (g == null) return;

        // Undo standings if game was finished
        if (g.isFinished()) {
            if (g.getHomeScore() > g.getAwayScore()) {
                g.getHome().undoWin();
                g.getAway().undoLoss();
            } else if (g.getAwayScore() > g.getHomeScore()) {
                g.getAway().undoWin();
                g.getHome().undoLoss();
            } else {
                g.getHome().undoTie();
                g.getAway().undoTie();
            }
        }

        // Reset score and finished flag
        g.setHomeScore(0);
        g.setAwayScore(0);
        g.setFinished(false);


    }

    /** Generate a double round-robin schedule (home and away). */
    public void generateSchedule(League league) {
        ArrayList<Team> teams = league.getTeams();
        for (int i = 0; i < teams.size() - 1; i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Team home = teams.get(i);
                Team away = teams.get(j);
                scheduleGame("TBD", home, away);
                scheduleGame("TBD", away, home);
            }
        }
        // Seed stats for newly created schedule if desired
        seedStats(league);
    }

    /** Seeds dummy stats ONE TIME if no stats exist yet. */
    public void seedStats(League league) {
        if (schedule.isEmpty()) return;
        for (Game g : schedule) {
            // Skip games that already have stats
            if (hasStats(g)) continue;

            Team h = g.getHome();
            Team a = g.getAway();

            if (h != null) {
                for (Player p : h.getPlayers()) {
                    Game.PlayerStats ps = dummy();
                    g.setStats(p, ps.ftAtt, ps.ftMade, ps.twoAtt, ps.twoMade, ps.threeAtt, ps.threeMade, ps.assists, ps.fouls);
                }
            }
            if (a != null) {
                for (Player p : a.getPlayers()) {
                    Game.PlayerStats ps = dummy();
                    g.setStats(p, ps.ftAtt, ps.ftMade, ps.twoAtt, ps.twoMade, ps.threeAtt, ps.threeMade, ps.assists, ps.fouls);
                }
            }
        }
    }

    /** True if game already has stats for any player. */
    private boolean hasStats(Game g) {
        Team h = g.getHome();
        Team a = g.getAway();
        if (h != null) {
            for (Player p : h.getPlayers()) {
                if (g.getStats(p) != null) return true;
            }
        }
        if (a != null) {
            for (Player p : a.getPlayers()) {
                if (g.getStats(p) != null) return true;
            }
        }
        return false;
    }

    /** Creates short dummy stats object. */
    private Game.PlayerStats dummy() {
        Game.PlayerStats ps = new Game.PlayerStats();
        ps.ftAtt = rand(0, 6);
        ps.ftMade = rand(0, ps.ftAtt);
        ps.twoAtt = rand(0, 12);
        ps.twoMade = rand(0, ps.twoAtt);
        ps.threeAtt = rand(0, 8);
        ps.threeMade = rand(0, ps.threeAtt);
        ps.assists = rand(0, 6);
        ps.fouls = rand(0, 5);
        return ps;
    }

    private int rand(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /** Compute team averages treating player appearances as units. */
    public GameManager.PlayerTotals getTeamAverages(Team team) {
        GameManager.PlayerTotals totals = new GameManager.PlayerTotals();
        int count = 0;
        for (Game g : schedule) {
            for (Player p : team.getPlayers()) {
                Game.PlayerStats ps = g.getStats(p);
                if (ps != null) {
                    totals.ftAtt += ps.ftAtt;
                    totals.ftMade += ps.ftMade;
                    totals.twoAtt += ps.twoAtt;
                    totals.twoMade += ps.twoMade;
                    totals.threeAtt += ps.threeAtt;
                    totals.threeMade += ps.threeMade;
                    totals.assists += ps.assists;
                    totals.fouls += ps.fouls;
                    count++;
                }
            }
        }
        totals.gamesPlayed = count; // treat player appearances as units for averaging
        return totals;
    }

    /** Helper: find the "real" team in the league by name. */
    private Team findTeamByName(League league, String name) {
        if (league == null || name == null) return null;
        for (Team t : league.getTeams()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    /** Load schedule and rebind teams to canonical league teams. */
    public void loadSchedule(List<Game> savedSchedule, League league) {
        schedule.clear();
        if (savedSchedule == null) return;
        for (Game g : savedSchedule) {
            if (g == null) continue;
            Team savedHome = g.getHome();
            Team savedAway = g.getAway();
            Team canonicalHome = (savedHome == null) ? null : findTeamByName(league, savedHome.getName());
            Team canonicalAway = (savedAway == null) ? null : findTeamByName(league, savedAway.getName());
            if (canonicalHome != null) g.setHome(canonicalHome);
            if (canonicalAway != null) g.setAway(canonicalAway);
            schedule.add(g);
        }
    }

    /** Keep old signature for backward compatibility. */
    public void loadSchedule(List<Game> savedSchedule) {
        loadSchedule(savedSchedule, null);
    }

    /** View entire schedule. */
    public void viewSchedule() {
        System.out.println("=== Schedule ===");
        schedule.stream()
            .sorted(Comparator.comparing(Game::isFinished).thenComparing(Game::getDate))
            .map(Game::summaryLine)
            .forEach(System.out::println);
    }

    /** View schedule for a single team. */
    public void viewScheduleForTeam(Team team) {
        System.out.println("=== Schedule for " + team.getName() + " ===");
        schedule.stream()
            .filter(g -> (g.getHome() == team || g.getAway() == team))
            .sorted(Comparator.comparing(Game::isFinished).thenComparing(Game::getDate))
            .map(Game::summaryLine)
            .forEach(System.out::println);
    }

    /** View standings for a list of teams. */
    public void viewStandings(List<Team> teams) {
        System.out.println("=== Standings (Pts=2*W + 1*T) ===");
        teams.stream()
            .sorted(Comparator.comparingInt(Team::getPoints)
                .reversed()
                .thenComparing(t -> t.getName().toLowerCase(Locale.ROOT)))
            .forEach(t -> System.out.printf("%-12s W:%d L:%d T:%d Pts:%d%n",
                t.getName(), t.getWins(), t.getLosses(), t.getTies(), t.getPoints()));
    }

    /**
     * LOAD GAME STATS FROM FILE
     * Expected line format (example):
     * Player: John Doe FT Attempted: 4 FT Made: 3 2P Attempted: 8 2P Made: 5 3P Attempted: 2 3P Made: 1 Assists: 4 Fouls: 2
     */
    public void readGameStats(String filePath, int id) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            Pattern pattern = Pattern.compile(
                "Player: (\\w+) (\\w+) FT Attempted: (\\d+) FT Made: (\\d+) " +
                "2P Attempted: (\\d+) 2P Made: (\\d+) " +
                "3P Attempted: (\\d+) 3P Made: (\\d+) Assists: (\\d+) Fouls: (\\d+)"
            );

            Game g = findGameById(id);
            if (g == null) {
                System.out.println("ERROR: Game not found.");
                return;
            }

            ArrayList<Player> team1 = g.getHome().getPlayers();
            ArrayList<Player> team2 = g.getAway().getPlayers();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                Matcher matcher = pattern.matcher(line);
                if (!matcher.find()) continue;

                String first = matcher.group(1);
                String last = matcher.group(2);

                Player currentPlayer = null;
                // search home
                for (Player p : team1) {
                    if (p.getFirstName().equals(first) && p.getLastName().equals(last)) {
                        currentPlayer = p;
                        break;
                    }
                }
                // search away
                if (currentPlayer == null) {
                    for (Player p : team2) {
                        if (p.getFirstName().equals(first) && p.getLastName().equals(last)) {
                            currentPlayer = p;
                            break;
                        }
                    }
                }

                if (currentPlayer == null) {
                    System.out.println("WARNING: player not found: " + first + " " + last);
                    continue;
                }

                int ftAtt = Integer.parseInt(matcher.group(3));
                int ftMade = Integer.parseInt(matcher.group(4));
                int twoAtt = Integer.parseInt(matcher.group(5));
                int twoMade = Integer.parseInt(matcher.group(6));
                int threeAtt = Integer.parseInt(matcher.group(7));
                int threeMade = Integer.parseInt(matcher.group(8));
                int assists = Integer.parseInt(matcher.group(9));
                int fouls = Integer.parseInt(matcher.group(10));

                g.setStats(currentPlayer, ftAtt, ftMade, twoAtt, twoMade, threeAtt, threeMade, assists, fouls);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Reset a player's in-memory stats fields. */
    private void resetPlayerStats(Player p) {
        p.numFreeThrows = 0;
        p.attemptedFreeThrows = 0;
        p.percentFreeThrows = 0;
        p.numTwoPointers = 0;
        p.attemptedTwoPointers = 0;
        p.percentTwoPointers = 0;
        p.numThreePointers = 0;
        p.attemptedThreePointers = 0;
        p.percentThreePointers = 0;
        p.numAssists = 0;
        p.numFouls = 0;
        p.setTotalPoints();
    }

    /** Simple scheduler wrapper (keeps old name used elsewhere). */
    public void scheduler(League league) {
        ArrayList<Team> teams = league.getTeams();
        for (int i = 0; i < teams.size() - 1; i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Team home = teams.get(i);
                Team away = teams.get(j);
                scheduleGame("TBD", home, away);
                scheduleGame("TBD", away, home);
            }
        }
    }
    
    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

 // In GameManager
    public void scheduleBracket(League league, String name) {
        if (league == null) return;
        List<Team> teams = new ArrayList<>(league.getTeams());
        if (teams.size() < 4) {
            System.out.println("Not enough teams for a 4-team bracket.");
            return;
        }

        // Sort by wins descending, then name
        teams.sort(Comparator
                .comparingInt(Team::getWins).reversed()
                .thenComparing(Team::getName));

        Team seed1 = teams.get(0);
        Team seed2 = teams.get(1);
        Team seed3 = teams.get(2);
        Team seed4 = teams.get(3);

        // Clear any old bracket games if you want a fresh tournament
        // schedule.removeIf(Game::isBracketGame);

        // Round 1 (semifinals)
        Game semi1 = scheduleGame("Bracket SF1", seed1, seed4);
        semi1.setBracketGame(true);
        semi1.setBracketRound(1);
        semi1.setBracketIndex(0);

        Game semi2 = scheduleGame("Bracket SF2", seed2, seed3);
        semi2.setBracketGame(true);
        semi2.setBracketRound(1);
        semi2.setBracketIndex(1);

        // Round 2 (final) — winner of semi1 vs winner of semi2.
        // For now we *label* it; the UI shows names; you can update teams after semis are played if desired.
        Game finalG = scheduleGame("Bracket Final", seed1, seed2); // or null,null if you prefer TBD
        finalG.setBracketGame(true);
        finalG.setBracketRound(2);
        finalG.setBracketIndex(0);
    }

    
    private void updateBracketAfterGame(Game completed) {
        if (!completed.isBracketGame()) return;

        Team winner = completed.getWinner();
        if (winner == null) {
            // tie or unfinished – bracket doesn't advance
            return;
        }

        int round = completed.getBracketRound();
        int idx   = completed.getBracketIndex();

        int nextRound = round + 1;
        int nextIndex = idx / 2;

        Game nextGame = findBracketGame(nextRound, nextIndex);
        if (nextGame == null) {
            // If the placeholder somehow isn't there, create it
            String label = completed.getDate() + " R" + nextRound + " G" + (nextIndex + 1);
            nextGame = new Game(label);
            nextGame.setBracketGame(true);
            nextGame.setBracketRound(nextRound);
            nextGame.setBracketIndex(nextIndex);
            schedule.add(nextGame);
        }

        if (nextGame.getHome() == null) {
            nextGame.setHome(winner);
        } else if (nextGame.getAway() == null) {
            nextGame.setAway(winner);
        }
    }

    /**
     * Utility to find a bracket game by round + index in that round.
     */
    private Game findBracketGame(int round, int bracketIndex) {
        for (Game g : schedule) {
            if (g.isBracketGame()
                && g.getBracketRound() == round
                && g.getBracketIndex() == bracketIndex) {
                return g;
            }
        }
        return null;
    }
}
