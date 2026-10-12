package sprint2;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

class UsersTest {

	@Test
	@DisplayName("Constructor: empty first name")
	void testConstructor_empty_first() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {new Users("", "Tester", "tester", "Tester@123", Role.LeagueA);});
		Assertions.assertTrue(ex.getMessage().contains("First name cannot be blank"));
	}
	
	@Test
	@DisplayName("Constructor: empty last name")
	void testConstructor_empty_last() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {new Users("User", "", "tester", "Tester@123", Role.LeagueA);});
		Assertions.assertTrue(ex.getMessage().contains("Last name cannot be blank"));
	}
	
	@Test
	@DisplayName("canCreateLeague when user is a League Admin")
	void testCanCreateLeague_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canCreateLeague();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canCreateLeague when user is a League Official")
	void testCanCreateLeague_league_official() {
		boolean expected = false;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canCreateLeague();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canCreateLeague when user is a Team Official")
	void testCanCreateLeague_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canCreateLeague();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canScheduleGames when user is a League Admin")
	void testCanScheduleGames_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canScheduleGames();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canScheduleGames when user is a League Official")
	void testCanScheduleGames_league_official() {
		boolean expected = false;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canScheduleGames();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canScheduleGames when user is a Team Official")
	void testCanScheduleGames_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canScheduleGames();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canAddTeams when user is a League Admin")
	void testCanAddTeams_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canAddTeams();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canAddTeams when user is a League Official")
	void testCanAddTeams_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canAddTeams();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canAddTeams when user is a Team Official")
	void testCanAddTeams_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canAddTeams();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canAddPlayers when user is a League Admin")
	void testCanAddPlayers_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canAddPlayers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canAddPlayers when user is a League Official")
	void testCanAddPlayers_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canAddPlayers();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canAddPlayers when user is a Team Official")
	void testCanAddPlayers_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canAddPlayers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canAddUsers when user is a League Admin")
	void testCanAddUsers_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canAddUsers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canAddUsers when user is a League Official")
	void testCanAddUsers_league_official() {
		boolean expected = false;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canAddUsers();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canAddUsers when user is a Team Official")
	void testCanAddUsers_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canAddUsers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canViewUsers when user is a League Admin")
	void testCanViewUsers_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canViewUsers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canViewUsers when user is a League Official")
	void testCanViewUsers_league_official() {
		boolean expected = false;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canViewUsers();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canViewUsers when user is a Team Official")
	void testCanViewUsers_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canAddUsers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canLoadGameStats when user is a League Admin")
	void testCanLoadGameStats_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canLoadGameStats();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canLoadGameStats when user is a League Official")
	void testCanLoadGameStats_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canLoadGameStats();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canLoadGameStats when user is a Team Official")
	void testCanLoadGameStats_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canLoadGameStats();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canDeleteStats when user is a League Admin")
	void testCanDeleteStats_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canDeleteStats();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canDeleteStats when user is a League Official")
	void testCanDeleteStats_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canDeleteStats();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canDeleteStats when user is a Team Official")
	void testCanDeleteStats_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canDeleteStats();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canArchive when user is a League Admin")
	void testCanArchive_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canArchive();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canArchive when user is a League Official")
	void testCanArchive_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canArchive();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canArchive when user is a Team Official")
	void testCanArchive_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canArchive();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canLoadArchive when user is a League Admin")
	void testCanLoadArchive_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canLoadArchive();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canLoadArchive when user is a League Official")
	void testCanLoadArchive_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canLoadArchive();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canLoadArchive when user is a Team Official")
	void testCanLoadArchive_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canLoadArchive();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canLoadTeamsAndPlayers when user is a League Admin")
	void testCanLoadTeamsAndPlayers_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canLoadTeamsAndPlayers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canLoadTeamsAndPlayers when user is a League Official")
	void testCanLoadTeamsAndPlayers_league_official(){
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canLoadTeamsAndPlayers();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canLoadTeamsAndPlayers when user is a Team Official")
	void testCanLoadTeamsAndPlayers_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canLoadTeamsAndPlayers();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canScheduleBrackets when user is a League Admin")
	void testCanScheduleBrackets_league_admin() {
		boolean expected = true;
		Users admin = new Users("League", "Admin", "admin", "Admin@123", Role.LeagueA);
		boolean actual = admin.canScheduleBrackets();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("canScheduleBrackets when user is a League Official")
	void testCanScheduleBrackets_league_official() {
		boolean expected = true;
		Users lOfficial = new Users("League", "Official", "official", "LOfficial@123", Role.LeagueO);
		boolean actual = lOfficial.canScheduleBrackets();
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("canScheduleBrackets when user is a Team Official")
	void testCanScheduleBrackets_team_official() {
		boolean expected = false;
		Users tOfficial = new Users("Team", "Official", "tOfficial", "TOfficial@123", Role.TeamO);
		boolean actual = tOfficial.canScheduleBrackets();
		
		assertEquals(expected,actual);
	}
	
	@Test
	@DisplayName("createPass: password under 6 characters")
	void testCreatePass_under_6() {
		IllegaalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {new Users("User", "Tester", "tester", "T@123", Role.LeagueA);});
		Assertions.assertTrue(ex.getMessage().contains("Password must be at least 6 characters"));
	}
	
	@Test
	@DisplayName("createPass: password with no uppercase")
	void testCreatePass_no_upper() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {new Users("User", "Tester", "tester", "tester@123", Role.LeagueA);});
		Assertions.assertTrue(ex.getMessage().contains("Password must have at least 1 upper case, 1 number, and 1 special character (!@#$%^&*)"));
	}
	
	@Test
	@DisplayName("createPass: password with no numbers")
	void testCreatePass_no_num() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {new Users("User", "Tester", "tester", "Tester@", Role.LeagueA);});
		Assertions.assertTrue(ex.getMessage().contains("Password must have at least 1 upper case, 1 number, and 1 special character (!@#$%^&*)"));
	}
	
	@Test
	@DisplayName("createPass: password with no special character")
	void testCreatePass_no_special() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {new Users("User", "Tester", "tester", "Tester123", Role.LeagueA);});
		Assertions.assertTrue(ex.getMessage().contains("Password must have at least 1 upper case, 1 number, and 1 special character (!@#$%^&*)"));
	}
	
	@Test
	@DisplayName("getFirstName")
	void testGetFirstName() {
		String expectedName = "Test";
		Users user = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		String actualName = user.getFirstName();
		
		assertEquals(expectedName, actualName);
	}
	
	@Test
	@DisplayName("getLastName")
	void testGetLastName() {
		String expectedName = "User";
		Users user = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		String actualName = user.getLastName();
		
		assertEquals(expectedName, actualName);
	}
	
	@Test
	@DisplayName("getID")
	void testGetID() {
		String expectedID = "tester";
		Users user = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		String actualID = user.getID();
		
		assertEquals(expectedID, actualID);
	}
	
	@Test
	@DisplayName("getPass")
	void testGetPass() {
		String expectedPass = "Tester@123";
		Users user = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		String actualPass = user.getPass();
		
		assertEquals(expectedPass, actualPass);
	}
	
	@Test
	@DisplayName("setPass: less than 6 characters")
	void testSetPass_less_than_6() {
		Users tester = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tester.setPass("T@123"));
		Assertions.assertTrue(ex.getMessage().contains("Password must be at least 6 characters"));
	}
	
	@Test
	@DisplayName("setPass: no uppercase")
	void testSetPass_no_upper() {
		Users tester = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tester.setPass("tester@123"));
		Assertions.assertTrue(ex.getMessage().contains("Password must have at least 1 upper case, 1 number, and 1 special character (!@#$%^&*)"));
	}
	
	@Test
	@DisplayName("setPass: no number")
	void testSetPass_no_num() {
		Users tester = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tester.setPass("Tester@"));
		Assertions.assertTrue(ex.getMessage().contains("Password must have at least 1 upper case, 1 number, and 1 special character (!@#$%^&*)"));
	}
	
	@Test
	@DisplayName("setPass: no special character")
	void testSetPass_no_special() {
		Users tester = new Users("Test", "User", "tester", "Tester@123", Role.LeagueA);
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tester.setPass("NewTest123"));
		Assertions.assertTrue(ex.getMessage().contains("Password must have at least 1 upper case, 1 number, and 1 special character (!@#$%^&*)"));
	}
	
	@Test
	@DisplayName("getRoles")
	void testGetRoles() {
		Role expectedRole = Role.LeagueA;
		Users tester = new Users("User", "Test", "tester", "Tester@123", Role.LeagueA);
		Role actualRole = tester.getRoles();
		assertEquals(expectedRole, actualRole);
	}

}
