package sprint2;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TeamTest {

	@Test
	@DisplayName("getPlayers")
	void testGetPlayers() {
		boolean isSame = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		Team test = new Team("Dummy Team");
		TeamManager manager = new TeamManager(test);
		
		manager.addPlayer(admin, "James", "Bond");
		manager.addPlayer(admin, "Mickey", "Mouse");
		manager.addPlayer(admin, "Wyatt", "Earp");
		
		ArrayList <Player> playersActual = test.getPlayers();
		
		ArrayList <Player> playersExpected = new ArrayList <>();
		Player james = new Player("James", "Bond", 1, test);
		Player mickey = new Player("Mickey", "Mouse", 2, test);
		Player wyatt = new Player("Wyatt", "Earp", 3, test);
		
		playersExpected.add(james);
		playersExpected.add(mickey);
		playersExpected.add(wyatt);
		
		if(playersExpected.size() != playersActual.size()) {
			isSame = false;
		}
		if(!playersExpected.equals(playersActual)) {
			isSame = false;
		}
		
		Assertions.assertTrue(isSame);
		
	}
	
	@Test
	@DisplayName("getName")
	void testGetName() {
		String expectedName = "Tester";
		Team t = new Team("Tester");
		String actualName = t.getName();
		
		assertEquals(expectedName, actualName);
	}
	
	@Test
	@DisplayName("getWins")
	void testGetWins() {
		int expectedWins = 5;
		Team t = new Team("Tester");
		t.recordWin();
		t.recordWin();
		t.recordWin();
		t.recordWin();
		t.recordWin();
		
		int actualWins = t.getWins();
		
		assertEquals(expectedWins, actualWins);
	}
	
	@Test
	@DisplayName("getLosses")
	void testGetLosses() {
		int expectedLosses = 2;
		Team t = new Team("Tester");
		t.recordLoss();
		t.recordLoss();
		
		int actualLoss = t.getLosses();
		
		assertEquals(expectedLosses, actualLoss);
	}
	
	@Test
	@DisplayName("getTies")
	void testGetTies() {
		int expectedTies = 1;
		Team t = new Team("Tester");
		t.recordTie();
		
		int actualTies = t.getTies();
		
		assertEquals(expectedTies, actualTies);
	}
	
	@Test
	@DisplayName("newJerseyNum")
	void testNewJerseyNum() {
		int expectedNum = 1;
		Team t = new Team("Tester");
		int actualNum = t.newJerseyNum();
		
		assertEquals(expectedNum, actualNum);
	}
	
	@Test
	@DisplayName("recordWin")
	void testRecordWin() {
		int expectedWins = 2;
		Team t = new Team("Tester");
		t.recordWin();
		t.recordWin();
		
		int actualWins = t.getWins();
		
		assertEquals(expectedWins, actualWins);
	}
	
	@Test
	@DisplayName("recordLoss")
	void testRecordLoss() {
		int expectedLosses = 2;
		Team t = new Team("Tester");
		t.recordLoss();
		t.recordLoss();
		
		int actualLoss = t.getLosses();
		
		assertEquals(expectedLosses, actualLoss);
	}
	
	@Test
	@DisplayName("recordTie")
	void testRecordTie() {
		int expectedTies = 2;
		Team t = new Team("Tester");
		t.recordTie();
		t.recordTie();
		
		int actualTies = t.getTies();
		
		assertEquals(expectedTies, actualTies);
	}
	
	@Test
	@DisplayName("undoWin")
	void testUndoWin() {
		int expectedWins = 2;
		Team t = new Team("Tester");
		t.recordWin();
		t.recordWin();
		t.recordWin();
		t.undoWin();
		
		int actualWins = t.getWins();
		
		assertEquals(expectedWins, actualWins);
	}
	
	@Test
	@DisplayName("undoLoss")
	void testUndoLoss() {
		int expectedLoss = 1;
		Team t = new Team("Tester");
		t.recordLoss();
		t.recordLoss();
		t.undoLoss();
		
		int actualLoss = t.getLosses();
		
		assertEquals(expectedLoss, actualLoss);
				
	}
	
	@Test
	@DisplayName("undoTie")
	void testUndoTie() {
		int expectedTie = 1;
		Team t = new Team("Tester");
		t.recordTie();
		t.recordTie();
		t.undoTie();
		
		int actualTie = t.getTies();
		
		assertEquals(expectedTie, actualTie);
	}

}
