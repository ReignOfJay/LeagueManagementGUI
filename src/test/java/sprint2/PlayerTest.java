package sprint2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private Team tigers;

    @BeforeEach
    void setUp() {
        tigers = new Team("Tigers");
        player = new Player("John", "Doe", 42, tigers);
    }

    @Test
    void constructor_setsNames() {
        assertEquals("John", player.getFirstName());
        assertEquals("Doe", player.getLastName());
    }

    @Test
    void setTeam_and_setJerseyNum_assignJerseyFromTeam() {
        player.setTeam(tigers);
        player.setJerseyNum();   // uses team.newJerseyNum()

        int jersey = player.getJerseyNumber();
        assertTrue(jersey > 0, "Jersey number should be assigned");
    }

    @Test
    void getTeam_returnsTeamName() {
        player.setTeam(tigers);
        assertEquals("Tigers", player.getTeam());
    }

    @Test
    void totalPoints_computesFromFields() {
        // Assuming sprint2.Player has fields like numFreeThrows, etc.
        player.numFreeThrows = 3;       // 3 * 1
        player.numTwoPointers = 4;      // 4 * 2
        player.numThreePointers = 2;    // 2 * 3

        int total = player.getTotalPoints();
        assertEquals(3 * 1 + 4 * 2 + 2 * 3, total);
    }
}
