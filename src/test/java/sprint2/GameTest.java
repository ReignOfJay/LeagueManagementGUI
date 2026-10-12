package sprint2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class GameTest {

	private Team homeTeam;
    private Team awayTeam;

    @BeforeEach
    void setUp() {
        // 🔧 Adjust these if your Team constructor is different
        homeTeam = new Team("Home Team");
        awayTeam = new Team("Away Team");
    }

    @Test
    void constructor_ShouldSetFieldsCorrectly() {
        String date = "2025-11-05 19:00";
        Game game = new Game(date, homeTeam, awayTeam);

        assertNotNull(game);
        assertEquals(date, game.getDate());
        assertEquals(homeTeam, game.getHome());
        assertEquals(awayTeam, game.getAway());

        // scores should start as null
        assertNull(game.getHomeScore());
        assertNull(game.getAwayScore());

        // game should not be finished initially
        assertFalse(game.isFinished());
    }
    
    @Test
    void constructor_ShouldAssignIncrementingIds() {
        Game game1 = new Game("2025-11-05 19:00", homeTeam, awayTeam);
        Game game2 = new Game("2025-11-06 19:00", homeTeam, awayTeam);

        int id1 = game1.getId();
        int id2 = game2.getId();

        // We don't assume a starting value, only that it increments by 1
        assertEquals(id1 + 1, id2);
    }

    @Test
    void constructor_ShouldThrowWhenHomeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Game("2025-11-05 19:00", null, awayTeam);
        });
    }

    @Test
    void constructor_ShouldThrowWhenAwayIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Game("2025-11-05 19:00", homeTeam, null);
        });
    }
    
    @Test
    void constructor_ShouldThrowWhenHomeAndAwayAreSameTeam() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Game("2025-11-05 19:00", homeTeam, homeTeam);
        });
    }

    @Test
    void setHomeScore_ShouldUpdateHomeScore() {
        Game game = new Game("2025-11-05 19:00", homeTeam, awayTeam);

        game.setHomeScore(3);

        assertEquals(Integer.valueOf(3), game.getHomeScore());
    }

    @Test
    void setAwayScore_ShouldUpdateAwayScore() {
        Game game = new Game("2025-11-05 19:00", homeTeam, awayTeam);

        game.setAwayScore(2);

        assertEquals(Integer.valueOf(2), game.getAwayScore());
    }
    
    @Test
    void setFinished_ShouldUpdateFinishedFlag() {
        Game game = new Game("2025-11-05 19:00", homeTeam, awayTeam);

        assertFalse(game.isFinished());

        game.setFinished(true);

        assertTrue(game.isFinished());
    }

    @Test
    void canSetScoresAndMarkFinishedTogether() {
        Game game = new Game("2025-11-05 19:00", homeTeam, awayTeam);

        game.setHomeScore(4);
        game.setAwayScore(1);
        game.setFinished(true);

        assertEquals(Integer.valueOf(4), game.getHomeScore());
        assertEquals(Integer.valueOf(1), game.getAwayScore());
        assertTrue(game.isFinished());
    }
}
