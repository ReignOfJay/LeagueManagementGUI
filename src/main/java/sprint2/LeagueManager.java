package sprint2;

import java.util.ArrayList;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LeagueManager {
    private static ArrayList<Users> userList = new ArrayList<>();
    private static final String SAVE_DIR  = "saves";
    private static final String FILE_NAME = "SystemData.json";
    private static final String FILE_PATH = SAVE_DIR + File.separator + FILE_NAME;


    private League league;
    private final GameManager gameManager;

    public LeagueManager(League league, GameManager gameManager) {
        this.league = league;
        this.gameManager = gameManager;
    }

    public League getLeague() {
        return league;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ArrayList<Users> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<Users> users) {
        LeagueManager.userList = users;
    }

    // ChatGPT method Implemented
    public static Users login(String id, String password) {
        for (Users u : userList) {
            if (u.getID().equals(id) && u.getPass().equals(password)) {
                return u;
            }
        }
        return null; // login failed
    }

    public static boolean addUser(Users currentUser, String fName, String lName, String id, String pass, Role role) {
        if (currentUser.getRoles() != Role.LeagueA) {
            System.out.println("Access denied: Only League Admins can add users.");
            return false;
        }
        if (!isValidPassword(pass)) {
            System.out.println("Invalid password: Must be at least 6 characters and include uppercase, lowercase, digit, and special character.");
            return false;
        }
        for (Users u : userList) {
            if (u.getID().equals(id)) {
                System.out.println("User ID already exists.");
                return false;
            }
        }
        Users newUser = new Users(fName, lName, id, pass, role);
        userList.add(newUser);
        System.out.println("User added successfully.");
        return true;
    }

    public static boolean changeOwnPassword(Users user, String newPass) {
        if (!isValidPassword(newPass)) {
            System.out.println("Invalid password format.");
            return false;
        }
        user.setPass(newPass);
        System.out.println("Password updated.");
        return true;
    }

    public static boolean changeUserPassword(Users admin, String targetID, String newPass) {
        if (admin.getRoles() != Role.LeagueA) {
            System.out.println("Access denied: Only League Admins can change other users' passwords.");
            return false;
        }
        if (!isValidPassword(newPass)) {
            System.out.println("Invalid password format.");
            return false;
        }
        for (Users u : userList) {
            if (u.getID().equals(targetID)) {
                u.setPass(newPass);
                System.out.println("Password updated for user: " + targetID);
                return true;
            }
        }
        System.out.println("User not found.");
        return false;
    }

    private static boolean isValidPassword(String pass) {
        if (pass == null || pass.length() < 6) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : pass.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true; // any non-alphanumeric counts as special
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public SavedData loadFile() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type dataType = new TypeToken<SavedData>() {}.getType();
            SavedData data = new Gson().fromJson(reader, dataType);
            if (data != null) {
                // restore users
                this.userList = data.users;
                System.out.println("Loaded users + league.");
                return data; // return both pieces
            }
        } catch (FileNotFoundException e) {
            System.out.println("No saved data found. Creating default users.");
            // Create default users for FIRST RUN ONLY
            this.userList = new ArrayList<>();
            this.userList.add(new Users("Default", "Admin", "admin", "Admin@123", Role.LeagueA));
            this.userList.add(new Users("League", "League Official", "league", "Lo@123", Role.LeagueO));
            this.userList.add(new Users("Team", "Team Official", "team", "Team@123", Role.TeamO));
            // Return default saved data
            return new SavedData(this.userList, null, null);
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
        return null;
    }

    private static void addDefaultUserIfMissing(String id, String fName, String lName, String pass, Role role) {
        boolean exists = userList.stream().anyMatch(u -> u.getID().equals(id));
        if (!exists) {
            userList.add(new Users(fName, lName, id, pass, role));
        }
    }

    private boolean archiveMode = false;

    public void archiveLeague(Stage stage) {
        // 1) Check if all games finished
        if (!allGamesFinished()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.initOwner(stage);
            confirm.setTitle("Games Not Finished");
            confirm.setHeaderText("Some games are not marked as finished.");
            confirm.setContentText(
                    "Archiving now will lock the league in its current state,\n" +
                    "even though some games are still unfinished.\n\n" +
                    "Do you want to archive anyway?"
            );

            ButtonType archiveAnyway = new ButtonType("Archive Anyway", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(archiveAnyway, cancel);

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != archiveAnyway) {
                // User chose Cancel or closed the dialog → abort archiving
                return;
            }
        }

        // 2) At this point either:
        //    - allGamesFinished() was true, OR
        //    - user explicitly chose "Archive Anyway"

        if (league == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText("No league to archive.");
            a.show();
            return;
        }

        // Save the league using your safe JSON logic
        saveFile(league);

        // Enter archive mode so editing is disabled
        archiveMode = true;

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Archive Created");
        a.setContentText("The current league has been archived and is now read-only.");
        a.show();
    }


    private boolean allGamesFinished() {
        for (Game g : gameManager.getScheduleList()) {
            if (!g.isFinished()) return false;
        }
        return true;
    }

    public boolean isArchiveMode() {
        return archiveMode;
    }

    public void saveFile(League toSave) {
        if (toSave == null) {
            System.out.println("No league to save.");
            return;
        }

        // Build the SavedData wrapper
        SavedData data = new SavedData();
        data.league   = toSave;
        data.schedule = gameManager.getScheduleList();
        data.users    = userList;

        // 1) Temporarily break Player -> Team back-references
        List<PlayerTeamLink> backups = new ArrayList<>();
        for (Team t : data.league.getTeams()) {
            for (Player p : t.getPlayers()) {
                backups.add(new PlayerTeamLink(p, p.getTeam()));
                p.setTeam(null); // break the cycle for JSON
            }
        }

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            // Ensure "saves" directory exists
            java.nio.file.Path dir = java.nio.file.Paths.get(SAVE_DIR);
            java.nio.file.Files.createDirectories(dir);

            // Write to saves/SystemData.json
            java.nio.file.Path path = java.nio.file.Paths.get(FILE_PATH);
            try (java.io.Writer writer = java.nio.file.Files.newBufferedWriter(path)) {
                gson.toJson(data, writer);
            }

            System.out.println("System saved to: " + path.toAbsolutePath());

        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("Error saving system data: " + e.getMessage());
        } finally {
            // 3) Restore Player.team back-references so runtime stays intact
            for (PlayerTeamLink link : backups) {
                link.player.setTeam(link.team);
            }
        }
    }


    public SavedData loadChosenState(Stage parent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a saved state");

        // 🔒 Only set initial directory if it actually exists and is a directory
        File savesDir = new File("saves").getAbsoluteFile();
        if (savesDir.exists() && savesDir.isDirectory()) {
            chooser.setInitialDirectory(savesDir);
        }
        
      
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        // else: let JavaFX use its default location (no COM error)

        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File file = chooser.showOpenDialog(parent);
        if (file == null) return null;   // user cancelled

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<SavedData>() {}.getType();
            SavedData data = new Gson().fromJson(reader, type);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    
    // Inner class to save users + league together
    public static class SavedData {
        public League league;
        public ArrayList<Users> users;
        public ArrayList<Game> schedule;
        
        public SavedData() {
        }

        public SavedData(ArrayList<Users> users, League league, ArrayList<Game> schedule) {
            this.users = users;
            this.league = league;
            this.schedule = schedule;
        }
    }

    public void addTeam(Users user, String name) {
        if (!user.canAddTeams()) {
            throw new SecurityException("You do not have permission to add teams.");
        }
        // Create team
        Team newTeam = new Team(name);
        // Add it directly to the league's internal list (assumes League has a public getter for teams)
        league.getTeams().add(newTeam);
    }

    /**
     * Creates an archive file containing the entire system state.
     * Saved as: /archives/<archiveName>.json
     */
    public String archiveCurrentSystem(String archiveName) {
        try {
            // 1) First save the current system safely using your existing logic
            if (league == null) {
                return "Archive failed: no active league to save.";
            }

            saveFile(league);  // writes saves/SystemData.json using cycle-safe serialization

            // 2) Now copy that file into /archives/<name>.json
            Path source = Paths.get(FILE_PATH);           // saves/SystemData.json
            if (!Files.exists(source)) {
                return "Archive failed: main save file not found at " + source.toString();
            }

            Path archiveDir = Paths.get("archives");
            Files.createDirectories(archiveDir);

            String safeName = archiveName.replaceAll("[^a-zA-Z0-9_-]", "_");
            Path archiveFile = archiveDir.resolve(safeName + ".json");

            Files.copy(source, archiveFile, StandardCopyOption.REPLACE_EXISTING);

            return "Archive saved as: " + archiveFile.toAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "Archive failed: " + e.getMessage();
        }
    }

    /**
     * Loads an archived league state by name.
     * Does NOT overwrite files — GUI will swap the objects.
     */
    public SavedData loadArchive(String archiveName) {
        try {
            Path file = Paths.get("archives", archiveName + ".json");
            if (!Files.exists(file)) {
                System.out.println("Archive not found: " + file.toString());
                return null;
            }
            String json = Files.readString(file);
            Gson gson = new Gson();
            SavedData data = gson.fromJson(json, SavedData.class);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> listArchives() {
        try {
            Path archiveDir = Paths.get("archives");
            Files.createDirectories(archiveDir);
            return Files.list(archiveDir)
                .filter(p -> p.toString().endsWith(".json"))
                .map(p -> p.getFileName().toString().replace(".json", ""))
                .sorted()
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public void relinkPlayersToTeams() {
        League lg = getLeague();
        if (lg == null) return;

        for (Team t : lg.getTeams()) {
            for (Player p : t.getPlayers()) {
                p.setTeam(t);
            }
        }
    }

    public void enterArchiveMode() {
        archiveMode = true;
    }

    public void exitArchiveMode() {
        archiveMode = false;
    }

    public void initializeDummyLeague() {
        // -------------------- TEAMS --------------------
        Team tigers = new Team("Tigers");
        Team sharks = new Team("Sharks");
        Team wolves = new Team("Wolves");
        Team bulls = new Team("Bulls");

        league.getTeams().add(tigers);
        league.getTeams().add(sharks);
        league.getTeams().add(wolves);
        league.getTeams().add(bulls);

        // -------------------- TEAM MANAGERS --------------------
        TeamManager tmTigers = new TeamManager(tigers);
        TeamManager tmSharks = new TeamManager(sharks);
        TeamManager tmWolves = new TeamManager(wolves);
        TeamManager tmBulls = new TeamManager(bulls);

        // Find a user with permission to add players
        Users admin = userList.stream().filter(Users::canAddPlayers).findFirst().orElse(userList.get(0));

        // -------------------- ADD PLAYERS --------------------
        tmTigers.addPlayer(admin, "John", "Doe");
        tmTigers.addPlayer(admin, "Jane", "Smith");
        tmTigers.addPlayer(admin, "Kevin", "Hart");
        tmTigers.addPlayer(admin, "Lisa", "Ray");
        tmTigers.addPlayer(admin, "Tom", "White");

        tmSharks.addPlayer(admin, "Mike", "Brown");
        tmSharks.addPlayer(admin, "Alice", "Johnson");
        tmSharks.addPlayer(admin, "Chris", "Green");
        tmSharks.addPlayer(admin, "Sara", "Blue");
        tmSharks.addPlayer(admin, "Robert", "Black");

        tmWolves.addPlayer(admin, "Emily", "Stone");
        tmWolves.addPlayer(admin, "Jack", "Hill");
        tmWolves.addPlayer(admin, "Olivia", "King");
        tmWolves.addPlayer(admin, "Paul", "Adams");
        tmWolves.addPlayer(admin, "Nancy", "Drew");

        tmBulls.addPlayer(admin, "Mark", "Fox");
        tmBulls.addPlayer(admin, "Laura", "Moon");
        tmBulls.addPlayer(admin, "Tim", "Lee");
        tmBulls.addPlayer(admin, "Rachel", "Gray");
        tmBulls.addPlayer(admin, "Sam", "Cole");

        // -------------------- SCHEDULE GAMES --------------------
        gameManager.scheduleGame("2025-11-05 19:00", tigers, sharks);
        gameManager.recordResult(1, 98, 89); // Tigers win

        gameManager.scheduleGame("2025-11-06 20:00", wolves, tigers);
        gameManager.recordResult(2, 76, 80); // Tigers win

        gameManager.scheduleGame("2025-11-07 18:00", sharks, wolves);
        gameManager.recordResult(3, 91, 87); // Sharks win

        gameManager.scheduleGame("2025-12-01 19:00", bulls, tigers); // upcoming game

        gameManager.seedStats(league);
    }
    
    

    public String getLeagueStandingsReport() {
        ArrayList<Team> teams = league.getTeams();
        teams.sort((t1, t2) -> {
            if (t1.getWins() != t2.getWins()) {
                return Integer.compare(t2.getWins(), t1.getWins());
            }
            return t1.getName().compareToIgnoreCase(t2.getName());
        });

        StringBuilder sb = new StringBuilder();
        sb.append("Team               W  L  Win%\n");
        sb.append("-------------------------------------\n");
        for (Team t : teams) {
            int wins = t.getWins();
            int losses = t.getLosses();
            double pct = (wins + losses == 0) ? 0.0 : (double) wins / (wins + losses) * 100.0;
            sb.append(String.format("%-18s %2d %2d %.2f\n", t.getName(), wins, losses, pct));
        }
        return sb.toString();
    }

    public void setLeague(League league) {
        this.league = league;
    }
    
    private static class PlayerTeamLink {
        final Player player;
        final Team team;
        PlayerTeamLink(Player p, Team t) {
            this.player = p;
            this.team = t;
        }
    }
}
