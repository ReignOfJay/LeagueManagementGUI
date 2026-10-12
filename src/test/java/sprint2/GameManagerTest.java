package sprint2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager gm;
    private League league;
    private Team tigers;
    private Team sharks;
    private Player p1;
    private Player p2;

    @BeforeEach
    void setUp() {
        gm = new GameManager();
        league = new League("Test League");

        tigers = new Team("Tigers");
        sharks = new Team("Sharks");
        league.getTeams().add(tigers);
        league.getTeams().add(sharks);

        p1 = new Player("John", "Doe", 15, sharks);
        p1.setTeam(tigers);
        tigers.getPlayers().add(p1);

        p2 = new Player("Jane", "Smith", 24, sharks);
        p2.setTeam(tigers);
        tigers.getPlayers().add(p2);
    }

    @Test
    void scheduleGame_addsGameToSchedule() {
        Game g = gm.scheduleGame("2025-01-01 19:00", tigers, sharks);
        ArrayList<Game> schedule = gm.getScheduleList();

        assertEquals(1, schedule.size(), "Schedule should have one game");
        assertEquals(g, schedule.get(0));
        assertEquals("2025-01-01 19:00", g.getDate());
        assertEquals(tigers, g.getHome());
        assertEquals(sharks, g.getAway());
    }

    @Test
    void recordResult_marksGameFinishedAndSetsScores() {
        Game g = gm.scheduleGame("2025-01-01 19:00", tigers, sharks);
        int id = g.getId();

        boolean ok = gm.recordResult(id, 100, 90);

        assertTrue(ok, "recordResult should return true");
        assertTrue(g.isFinished(), "Game should be marked finished");
        assertEquals(100, g.getHomeScore());
        assertEquals(90, g.getAwayScore());
    }

    @Test
    void getPlayerTotals_aggregatesStatsAcrossGames() {
        Game g1 = gm.scheduleGame("2025-01-01 19:00", tigers, sharks);
        Game g2 = gm.scheduleGame("2025-01-02 19:00", tigers, sharks);

        // Seed some stats manually (assuming Game.PlayerStats has these fields)
        Game.PlayerStats s1 = g1.getStats(p1);
        s1.ftMade = 2;
        s1.ftAtt = 2;
        s1.twoMade = 4;
        s1.twoAtt = 5;
        s1.threeMade = 1;
        s1.threeAtt = 3;
        s1.assists = 3;
        s1.fouls = 2;

        Game.PlayerStats s2 = g2.getStats(p1);
        s2.ftMade = 1;
        s2.ftAtt = 2;
        s2.twoMade = 3;
        s2.twoAtt = 4;
        s2.threeMade = 0;
        s2.threeAtt = 2;
        s2.assists = 2;
        s2.fouls = 3;

        GameManager.PlayerTotals totals = gm.getPlayerTotals(p1);

        assertEquals(2, totals.gamesPlayed);
        assertEquals(3, totals.ftMade);
        assertEquals(4, totals.ftAtt);
        assertEquals(7, totals.twoMade);
        assertEquals(9, totals.twoAtt);
        assertEquals(1, totals.threeMade);
        assertEquals(5, totals.threeAtt);
        assertEquals(5, totals.assists);
        assertEquals(5, totals.fouls);
    }

    @Test
    void getTeamStats_returnsNonEmptyReport() {
        // schedule and finish one game to give the team some stats
        Game g = gm.scheduleGame("2025-01-01 19:00", tigers, sharks);
        Game.PlayerStats ps = g.getStats(p1);
        ps.ftMade = 2;
        ps.ftAtt = 2;

        String report = gm.getTeamStats(tigers);
        assertNotNull(report);
        assertFalse(report.isBlank(), "Team stats report should not be blank");
        assertTrue(report.contains("Tigers") || report.contains("tigers"),
                "Report should mention the team name");
    }
}
