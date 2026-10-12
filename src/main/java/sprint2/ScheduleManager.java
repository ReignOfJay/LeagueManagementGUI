package sprint2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ScheduleManager {
    private final List<Game> schedule = new ArrayList<>();

    /** Create and add a game to the schedule. */
    public Game scheduleGame(String date, Team home, Team away) {
        Game g = new Game(date, home, away);
        schedule.add(g);
        return g;
    }

    /** Record a final score for a game by id. */
    public boolean recordResult(int gameId, int homeScore, int awayScore) {
        for (Game g : schedule) {
            if (g.getId() == gameId) {
                g.recordScore(homeScore, awayScore);
                return true;
            }
        }
        System.out.println("Game not found: " + gameId);
        return false;
    }

    /** View entire schedule, upcoming first, then completed. */
    public void viewSchedule() {
        System.out.println("=== Schedule ===");
        schedule.stream()
                .sorted(Comparator.comparing(Game::isFinished).thenComparing(Game::getDate))
                .forEach(g -> System.out.println(g.summaryLine()));
    }

    /** View a specific team’s schedule. */
    public void viewScheduleForTeam(Team team) {
        System.out.println("=== Schedule for " + team.getName() + " ===");
        schedule.stream()
                .filter(g -> g.getHome() == team || g.getAway() == team)
                .sorted(Comparator.comparing(Game::isFinished).thenComparing(Game::getDate))
                .forEach(g -> System.out.println(g.summaryLine()));
    }

    /** Simple standings print (Points: Win=2, Tie=1, Loss=0). */
    public void viewStandings(List<Team> teams) {
        System.out.println("=== Standings (Pts=2*W + 1*T) ===");
        teams.stream()
                .sorted(Comparator
                        .comparingInt(Team::getPoints).reversed()
                        .thenComparing(t -> t.getName().toLowerCase(Locale.ROOT)))
                .forEach(t -> System.out.printf("%-12s  W:%d  L:%d  T:%d  Pts:%d%n",
                        t.getName(), t.getWins(), t.getLosses(), t.getTies(), t.getPoints()));
    }
}