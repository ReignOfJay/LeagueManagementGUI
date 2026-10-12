package sprint2;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeagueDashboardTest {

    @Test
    void gameSummaryLine_forFinishedGameShowsWinnerAndScores() {
        League league = new League("Test League");
        GameManager gm = new GameManager();
        LeagueManager lm = new LeagueManager(league, gm);

        Team home = new Team("Tigers");
        Team away = new Team("Sharks");
        league.getTeams().add(home);
        league.getTeams().add(away);

        Game g = gm.scheduleGame("01-01-25 19:00", home, away);
        gm.recordResult(g.getId(), 100, 90);

        // dummy stage, we won't actually show UI
        Stage stage = new Stage();
        Users admin = new Users("Default", "Admin", "admin", "Admin@123", Role.LeagueA);

        LeagueDashboard dash = new LeagueDashboard(stage, admin, lm);
        String summary = dash.gameSummaryLine(g);

        assertTrue(summary.contains("Tigers"));
        assertTrue(summary.contains("100"));
        assertTrue(summary.contains("Sharks"));
        assertTrue(summary.contains("90"));
    }

    @Test
    void gameSummaryLine_forUnplayedGameMarksNotPlayed() {
        League league = new League("Test League");
        GameManager gm = new GameManager();
        LeagueManager lm = new LeagueManager(league, gm);

        Team home = new Team("Tigers");
        Team away = new Team("Sharks");
        league.getTeams().add(home);
        league.getTeams().add(away);

        Game g = gm.scheduleGame("01-01-25 19:00", home, away);
        // do NOT record result

        Stage stage = new Stage();
        Users admin = new Users("Default", "Admin", "admin", "Admin@123", Role.LeagueA);

        LeagueDashboard dash = new LeagueDashboard(stage, admin, lm);
        String summary = dash.gameSummaryLine(g);

        assertTrue(summary.contains("Not Played"));
    }
}
