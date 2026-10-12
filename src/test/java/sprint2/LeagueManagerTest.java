package sprint2;

public class LeagueManagerTest {
    public static void main(String[] args) {

        Users admin = new Users("Alice", "Admin", "admin001", "Admin@123", Role.LeagueA);
        LeagueManager.addUser(admin, "Bob", "Brown", "user001", "Bob@1234", Role.TeamO);
        LeagueManager.addUser(admin, "Charlie", "Clark", "user002", "Char#567", Role.LeagueO);

        LeagueManager.addUser(admin, "David", "Dunn", "user003", "weak", Role.TeamO);

        LeagueManager.addUser(admin, "Eve", "Evans", "user001", "Eve@1234", Role.TeamO);

        Users teamOfficial = new Users("Frank", "Foster", "user004", "Frank@123", Role.TeamO);
        LeagueManager.addUser(teamOfficial, "Grace", "Green", "user005", "Grace@123", Role.LeagueO);
        LeagueManager.changeOwnPassword(teamOfficial, "NewPass@123");

        LeagueManager.changeUserPassword(admin, "user002", "NewChar@789");

        LeagueManager.viewSortedUsers();

        simulateRoleSwitch(admin);
        simulateRoleSwitch(teamOfficial);
        simulateRoleSwitch(new Users("Hank", "Hill", "user006", "Hank@123", Role.LeagueO));
    }

    public static void simulateRoleSwitch(Users user) {
        switch (user.getRoles()) {
            case LeagueA:
                System.out.println(user.getFirstName() + " is a League Admin");
                break;
            case LeagueO:
                System.out.println(user.getFirstName() + " is a League Official");
                break;
            case TeamO:
                System.out.println(user.getFirstName() + " is a Team Official");
                break;
            default:
                System.out.println("Invalid role");
        }
    }
}
