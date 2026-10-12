package sprint1;

public class TestLMS {
    public static void main(String[] args) {
        // Create League Admin user
        Users admin = new Users("Alice", "Brown", "admin01", "Admin@123", Role.LeagueA);
        LeagueManager.addUser(admin, "Bob", "Clark", "user01", "User@123", Role.TeamO);

        // Create Team and add Players
        Team falcons = new Team("Falcons");
        falcons.addPlayer("Ella", "Davis");
        falcons.addPlayer("Frank", "Edwards");
        falcons.addPlayer("Gina", "Ford");

        // View Team
        falcons.viewSortedTeam();
    }
}
