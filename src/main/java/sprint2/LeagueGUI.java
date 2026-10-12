package sprint2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * GUI Login Screen — unchanged except:
 * After login → opens LeagueDashboardA
 */
public class LeagueGUI extends Application {

    private static LeagueManager leagueManager;

    public static void setLeagueManager(LeagueManager lm) {
        leagueManager = lm;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("League Management System");
        
     // If no LeagueManager was set externally, create one
        if (leagueManager == null) {
            leagueManager = new LeagueManager(new League("Default League"), new GameManager());
        }
        
        // If you want default test users
        seedTestUsers();

        if (leagueManager.getLeague().getTeams().isEmpty()) {
            leagueManager.initializeDummyLeague();
        }

        stage.setScene(createLoginScene(stage));
        stage.show();
    }

    // ---------------------------------------------------------
    // LOGIN SCREEN
    // ---------------------------------------------------------
    private Scene createLoginScene(Stage stage) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginBtn = new Button("Login");

        Label status = new Label();

        loginBtn.setOnAction(e -> {
            Users user = leagueManager.login(username.getText(), password.getText());

            if (user == null) {
                status.setText("Invalid username or password.");
            } else {
                // 🔥 OPEN DASHBOARD A
                new LeagueDashboard(stage, user, leagueManager).show();
            }
        });

        root.getChildren().addAll(
                new Label("Login"),
                username,
                password,
                loginBtn,
                status
        );

        return new Scene(root, 300, 250);
    }

    // ---------------------------------------------------------
    // Optional: default test users so login works immediately
    // ---------------------------------------------------------
    private void seedTestUsers() {
        try {
            Users sys = new Users("System", "Admin", "sys", "Sys@123", Role.LeagueA);
            LeagueManager.addUser(sys, "Admin", "User", "admin", "Admin@123", Role.LeagueA);
            LeagueManager.addUser(sys, "League", "Official", "off", "Off@123", Role.LeagueO);
            LeagueManager.addUser(sys, "Team", "Official", "team", "Team@123", Role.TeamO);
        } catch (Exception ignored) {}
    }
}
