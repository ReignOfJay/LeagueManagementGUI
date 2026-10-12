package sprint2;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Version A — Single-window unified dashboard
 * - Thin horizontal league list across top
 * - Teams list left, Players list right
 * - Selecting league/team auto-updates views
 * - Buttons for Add / Remove / Edit / View remain available
 */
public class LeagueDashboard {

    private final Stage primaryStage;
    private final Users currentUser;
    private final LeagueManager leagueManager;

    private League league;
    private ListView<Team> teamsListView;
    private ListView<Player> playersListView;
    private Label currentLeagueLabel;

    public LeagueDashboard(Stage stage, Users user, LeagueManager lm) {
        this.primaryStage = stage;
        this.currentUser = user;
        this.leagueManager = lm;
        this.league = lm.getLeague();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();

        // League/Teams/Players Tab (Version A)
        tabPane.getTabs().add(createLeagueTab());

        // Game Tab
        tabPane.getTabs().add(createGameTab());

        // Bracket Tab
        tabPane.getTabs().add(createBracketTab());

        // Users Tab (only for League Admins)
        if (currentUser.getRoles() == Role.LeagueA) {
            tabPane.getTabs().add(createUserTab());
        }

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 900, 600);

        // ⭐️ ADD THIS — Load your dark theme
        scene.getStylesheets().add(
                LeagueGUI.class.getResource("/darktheme.css").toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ---------------------------------------------
    // LEAGUE / TEAMS / PLAYERS TAB (Version A)
    // ---------------------------------------------

    private Tab createLeagueTab() {
        Tab leagueTab = new Tab("League");

        // =======================================
        // CENTER: left (teams/players) + right (standings)
        // =======================================
        HBox center = new HBox(20);
        center.setPadding(new Insets(15));

        // ---------------------------------------------------------
        // LEFT SIDE (Teams + Players + buttons)
        // ---------------------------------------------------------
        VBox leftSide = new VBox(10);

        currentLeagueLabel = new Label("Current League: " + leagueManager.getLeague().getLeagueName());

        Role role = currentUser.getRoles();
        boolean isLeagueAdmin = (role == Role.LeagueA);
        boolean isLeagueOfficial = (role == Role.LeagueO);
        boolean isTeamOfficial = (role == Role.TeamO);

        boolean canLoadDeleteStats = isLeagueAdmin || isLeagueOfficial;                  // Load / Delete
        boolean canViewStats = isLeagueAdmin || isLeagueOfficial || isTeamOfficial;      // All “view” stats
        boolean canArchive = isLeagueAdmin || isLeagueOfficial;                         // Archive / Load Archive
        boolean canLoadTeams = (role == Role.LeagueA || role == Role.LeagueO);

        // Create league controls
        HBox createLeagueBox = new HBox(10);
        TextField newLeagueField = new TextField();
        newLeagueField.setPromptText("Enter new league name");

        Button createLeagueBtn = new Button("Create League");
        if (!currentUser.canCreateLeague()) {
            createLeagueBtn.setDisable(true);
        }

        createLeagueBtn.setOnAction(e -> {
            String name = newLeagueField.getText().trim();
            if (name.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "League name cannot be empty.").show();
                return;
            }

            League newLeague = new League(name);
            try {
                Field leagueField = LeagueManager.class.getDeclaredField("league");
                leagueField.setAccessible(true);
                leagueField.set(leagueManager, newLeague);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            currentLeagueLabel.setText("Current League: " + name);
            teamsListView.setItems(FXCollections.observableArrayList(newLeague.getTeams()));
            playersListView.setItems(FXCollections.observableArrayList());

            new Alert(Alert.AlertType.INFORMATION,
                    "New league \"" + name + "\" created successfully!").show();

            newLeagueField.clear();
        });

        createLeagueBox.getChildren().addAll(newLeagueField, createLeagueBtn);

        // Teams
        Label teamsLabel = new Label("Teams:");
        teamsListView = new ListView<>();
        teamsListView.getStyleClass().add("lavender-list");
        teamsListView.setItems(FXCollections.observableArrayList(
                leagueManager.getLeague().getTeams()
        ));
        teamsListView.getStyleClass().add("roster-list");
        
        VBox.setVgrow(teamsListView, Priority.ALWAYS);

        Button addTeamBtn = new Button("Add Team");
        if (!currentUser.canAddTeams()) {
            addTeamBtn.setDisable(true);
        }

        addTeamBtn.setOnAction(ev -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter new team name:");
            dialog.showAndWait().ifPresent(teamName -> {
                ArrayList<Team> leagueTeams = leagueManager.getLeague().getTeams();
                for (Team t : leagueTeams) {
                    if (teamName.equals(t.getName())) {
                        new Alert(Alert.AlertType.WARNING,
                                "Team already exists, please enter a new team name.").show();
                        return;
                    }
                }

                Team t = new Team(teamName);
                leagueManager.getLeague().getTeams().add(t);
                teamsListView.setItems(FXCollections.observableArrayList(
                        leagueManager.getLeague().getTeams()
                ));
            });
        });

        // Players
        Label playersLabel = new Label("Players:");
        playersListView = new ListView<>();
        playersListView.getStyleClass().add("lavender-list");
        playersListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getFirstName() + " " + p.getLastName());
            }
        });
        playersListView.getStyleClass().add("roster-list");

        teamsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTeam, newTeam) -> {
            if (newTeam != null) {
                playersListView.setItems(
                        FXCollections.observableArrayList(newTeam.getPlayers())
                );
            }
        });

        VBox.setVgrow(playersListView, Priority.ALWAYS);

        Button addPlayerBtn = new Button("Add Player");
        if (!currentUser.canAddPlayers()) {
            addPlayerBtn.setDisable(true);
        }

        addPlayerBtn.setOnAction(ev -> {
            Team selectedTeam = teamsListView.getSelectionModel().getSelectedItem();
            if (selectedTeam == null) {
                new Alert(Alert.AlertType.WARNING, "Select a team first.").show();
                return;
            }

            TextInputDialog firstDialog = new TextInputDialog();
            firstDialog.setHeaderText("Enter player's first name");
            firstDialog.showAndWait().ifPresent(first -> {
                TextInputDialog lastDialog = new TextInputDialog();
                lastDialog.setHeaderText("Enter player's last name");
                lastDialog.showAndWait().ifPresent(last -> {
                    Users admin = leagueManager.getUserList().get(0);
                    TeamManager tm = new TeamManager(selectedTeam);
                    tm.addPlayer(admin, first, last);
                    playersListView.setItems(FXCollections.observableArrayList(
                            selectedTeam.getPlayers()
                    ));
                });
            });
        });

        // Player stats
        Button viewPlayerStatsBtn = new Button("View Player Stats");
        viewPlayerStatsBtn.setDisable(!canViewStats);
        viewPlayerStatsBtn.setOnAction(e -> {
            Player p = playersListView.getSelectionModel().getSelectedItem();
            if (p == null) {
                new Alert(Alert.AlertType.WARNING, "Select a player first.").show();
                return;
            }

            GameManager.PlayerTotals t = leagueManager.getGameManager().getPlayerTotals(p);

            String msg =
                    "Player: " + p.getFirstName() + " " + p.getLastName() + "\n" +
                    "Games Played: " + t.gamesPlayed + "\n\n" +
                    "Total Points: " + t.totalPoints() + "\n" +
                    "Avg Points: " + String.format("%.2f", t.avg(t.totalPoints())) + "\n\n" +
                    "FT: " + t.ftMade + "/" + t.ftAtt + " Avg: " +
                    String.format("%.2f", t.avg(t.ftMade)) + "/" +
                    String.format("%.2f", t.avg(t.ftAtt)) + "\n" +
                    "2P: " + t.twoMade + "/" + t.twoAtt + "\n" +
                    "3P: " + t.threeMade + "/" + t.threeAtt + "\n" +
                    "Assists: " + t.assists + " Avg: " +
                    String.format("%.2f", t.avg(t.assists)) + "\n" +
                    "Fouls: " + t.fouls + " Avg: " +
                    String.format("%.2f", t.avg(t.fouls));

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Player Stats");
            a.setHeaderText("Totals & Averages for " + p.getFirstName());
            a.setContentText(msg);
            a.getDialogPane().setMinWidth(500);
            a.showAndWait();
        });

        // Team stats
        Button viewTeamStatsBtn = new Button("View Team Stats");
        viewTeamStatsBtn.setDisable(!canViewStats);
        viewTeamStatsBtn.setOnAction(e -> {
            Team selected = teamsListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Select a team first.").show();
                return;
            }

            String report = leagueManager.getGameManager().getTeamStats(selected);

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Team Stats");
            a.setHeaderText("Stats for " + selected.getName());
            a.getDialogPane().setMinWidth(400);
            a.setContentText(report);
            a.showAndWait();
        });

        // Standings
        Button viewStandingsBtn = new Button("View League Standings");
        viewStandingsBtn.setDisable(!canViewStats);
        viewStandingsBtn.setOnAction(e -> {
            String report = leagueManager.getLeagueStandingsReport();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("League Standings");
            alert.setHeaderText("Current League Standings");
            alert.setContentText(report);
            alert.getDialogPane().setMinWidth(420);
            alert.getDialogPane().setPrefHeight(400);
            alert.showAndWait();
        });

        // League teams' stats
        Button viewLeagueTeamsStatsBtn = new Button("View League Teams' Stats");
        viewLeagueTeamsStatsBtn.setDisable(!canViewStats);
        viewLeagueTeamsStatsBtn.setOnAction(e -> {
            String report = leagueManager.getGameManager()
                    .getLeagueTeamsStats(leagueManager.getLeague());

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("League Teams' Stats");
            a.setHeaderText("Team Averages Across Entire League");
            a.getDialogPane().setMinWidth(500);
            a.setContentText(report);
            a.showAndWait();
        });

        // All teams' stats (PPG)
        Button viewAllTeamsStatsBtn = new Button("View ALL Teams' Stats");
        viewAllTeamsStatsBtn.setDisable(!canViewStats);
        viewAllTeamsStatsBtn.setOnAction(e -> {
            GameManager gm = leagueManager.getGameManager();
            List<Team> teams = leagueManager.getLeague().getTeams();
            StringBuilder sb = new StringBuilder();

            for (Team t : teams) {
                int games = 0;
                int totalPts = 0;

                for (Game g : gm.getScheduleList()) {
                    for (Player p : t.getPlayers()) {
                        Game.PlayerStats ps = g.getStats(p);
                        if (ps != null) {
                            games++;
                            totalPts += ps.getPoints();
                        }
                    }
                }

                double avg = games == 0 ? 0 : (double) totalPts / games;
                sb.append(t.getName())
                  .append(" — Avg PPG: ")
                  .append(String.format("%.2f", avg))
                  .append("\n");
            }

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("All Teams Stats");
            a.setHeaderText("Average Team Points Per Game");
            a.getDialogPane().setMinWidth(400);
            a.setContentText(sb.toString());
            a.showAndWait();
        });

        // Add everything to the left side
        leftSide.getChildren().addAll(
                currentLeagueLabel,
                createLeagueBox,
                teamsLabel,
                teamsListView,
                addTeamBtn,
                playersLabel,
                playersListView,
                addPlayerBtn,
                viewPlayerStatsBtn,
                viewTeamStatsBtn,
                viewLeagueTeamsStatsBtn,
                viewAllTeamsStatsBtn,
                viewStandingsBtn
        );

        // ---------------------------------------------------------
        // RIGHT SIDE — Standings Panel
        // ---------------------------------------------------------
        VBox rightSide = new VBox(10);
        Label standingsLabel = new Label("League Standings:");
        TextArea standingsArea = new TextArea();
        standingsArea.setEditable(false);
        standingsArea.getStyleClass().add("standings-box");
        standingsArea.setPrefWidth(300);
        standingsArea.setPrefHeight(400);
        standingsArea.getStyleClass().add("standings-box");


        Button refreshStandingsBtn = new Button("Refresh Standings");
        refreshStandingsBtn.setOnAction(e -> {
            List<Team> teams = leagueManager.getLeague().getTeams();
            teams.sort(Comparator.comparingInt(Team::getWins)
                    .reversed()
                    .thenComparing(Team::getName));

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-15s %-5s %-5s %-7s%n", "Team", "W", "L", "Win%"));
            sb.append("=================================\n");

            for (Team t : teams) {
                double pct = (t.getWins() + t.getLosses()) == 0
                        ? 0
                        : (double) t.getWins() / (t.getWins() + t.getLosses()) * 100;
                sb.append(String.format("%-15s %-5d %-5d %-6.2f%n",
                        t.getName(), t.getWins(), t.getLosses(), pct));
            }

            standingsArea.setText(sb.toString());
        });

        rightSide.getChildren().addAll(standingsLabel, standingsArea, refreshStandingsBtn);

        // center layout
        center.getChildren().addAll(leftSide, rightSide);
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        // =======================================
        // BOTTOM: Save / Restore / Archive buttons centered
        // =======================================
        Button saveButton = new Button("Save State");
        saveButton.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            League toSave = leagueManager.getLeague();
            if (toSave == null) {
                showAlert("Error", "No league to save.");
                return;
            }

            leagueManager.saveFile(toSave);
            showAlert("Success", "System state saved.");
        });

        Button loadButton = new Button("Restore State");
        loadButton.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            LeagueManager.SavedData data = leagueManager.loadChosenState(primaryStage);
            if (data != null) {
                leagueManager.setUserList(data.users);
                this.league = data.league;
                leagueManager.setLeague(data.league);

                // 🔁 rebuild Player.team backrefs after Gson deserialization
                leagueManager.relinkPlayersToTeams();

                GameManager gm = leagueManager.getGameManager();
                gm.loadSchedule(data.schedule, this.league); // use the overload that rebinds teams
                gm.seedStats(this.league);

                refreshLeague();
                refreshUsers();
                showAlert("Success", "State restored!");
            }
        });


        Button archiveButton = new Button("Archive League");
        archiveButton.setDisable(!canArchive);
        archiveButton.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                          "Cannot archive while viewing an archive.").show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter archive name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String name = result.get().trim();
                if (name.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Name cannot be empty").show();
                    return;
                }

                String msg = leagueManager.archiveCurrentSystem(name);
                new Alert(Alert.AlertType.INFORMATION, msg).show();
                // ⬆️ nothing here changes archiveMode or the label
            }
        });


        Button loadArchiveButton = new Button("Load Archive");
        loadArchiveButton.setDisable(!canArchive);
        loadArchiveButton.setOnAction(e -> {
            List<String> archives = leagueManager.listArchives();
            if (archives.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No archives available.").show();
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(archives.get(0), archives);
            dialog.setHeaderText("Select an archive to load:");
            Optional<String> choice = dialog.showAndWait();

            if (choice.isPresent()) {
                String name = choice.get();
                LeagueManager.SavedData data = leagueManager.loadArchive(name);
                if (data != null) {
                    // go into archive mode
                    leagueManager.enterArchiveMode();

                    // restore users and league
                    leagueManager.setUserList(data.users);
                    this.league = data.league;

                    // restore schedule + stats into GameManager
                    GameManager gm = leagueManager.getGameManager();
                    gm.loadSchedule(data.schedule);
                    gm.seedStats(this.league);

                    // refresh UI
                    refreshLeague();
                    refreshUsers();
                    currentLeagueLabel.setText(
                            "Current League: " + league.getLeagueName() + " (ARCHIVE MODE)"
                    );
                }
            }
        });

        Button exitArchiveButton = new Button("Exit Archive Mode");
        exitArchiveButton.setOnAction(e -> {
            if (!leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Not currently in archive mode.").show();
                return;
            }

            leagueManager.exitArchiveMode();
            this.league = leagueManager.getLeague();
            refreshLeague();
            refreshUsers();
            currentLeagueLabel.setText("Current League: " + league.getLeagueName());
        });

        Button loadTeamsAndPlayersBtn = new Button("Load Teams & Players");
        loadTeamsAndPlayersBtn.setDisable(!canLoadTeams);
        loadTeamsAndPlayersBtn.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            League current = leagueManager.getLeague();
            if (current == null) {
                new Alert(Alert.AlertType.ERROR,
                        "No active league. Please create a league first.").show();
                return;
            }

            // Check for existing data (teams or players)
            boolean hasTeamsOrPlayers =
                    !current.getTeams().isEmpty() &&
                    current.getTeams().stream().anyMatch(t -> !t.getPlayers().isEmpty());

            if (hasTeamsOrPlayers) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("Existing Data");
                warn.setHeaderText("This league already has teams/players.");
                warn.setContentText(
                        "Per the user story, Load Teams & Players is only used on an empty league.\n\n" +
                        "Please:\n" +
                        " 1. Archive the current data,\n" +
                        " 2. Create a new league,\n" +
                        " 3. Then run Load Teams & Players again."
                );
                warn.showAndWait();
                return;
            }

            // Choose file
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Teams & Players File");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.text", "*.*")
            );

            File file = chooser.showOpenDialog(primaryStage);
            if (file == null) {
                return; // user cancelled
            }

            try (Scanner scanner = new Scanner(file)) {
                // Example format: Team: Tigers Player: Sam Smith
                Pattern pattern = Pattern.compile("Team:\\s*(.+?)\\s+Player:\\s*(\\w+)\\s+(\\w+)");
                ArrayList<Team> teams = current.getTeams();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    Matcher m = pattern.matcher(line);
                    if (!m.matches()) continue;

                    String teamName = m.group(1).trim();
                    String first = m.group(2);
                    String last = m.group(3);

                    // Find or create the team
                    Team team = teams.stream()
                            .filter(t -> t.getName().equalsIgnoreCase(teamName))
                            .findFirst()
                            .orElseGet(() -> {
                                Team nt = new Team(teamName);
                                teams.add(nt);
                                return nt;
                            });

                    // Add player via TeamManager (respects jersey logic, etc.)
                    TeamManager tm = new TeamManager(team);
                    tm.addPlayer(currentUser, first, last);
                }

                // Refresh lists
                teamsListView.setItems(FXCollections.observableArrayList(current.getTeams()));
                playersListView.getItems().clear();

                new Alert(Alert.AlertType.INFORMATION,
                        "Teams and players loaded successfully.").show();

            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Could not read file:\n" + ex.getMessage()).show();
            }
        });

        HBox bottomButtons = new HBox(10);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setPadding(new Insets(5, 10, 10, 10));
        bottomButtons.getChildren().addAll(
                saveButton,
                loadButton,
                archiveButton,
                loadArchiveButton,
                exitArchiveButton,
                loadTeamsAndPlayersBtn
        );

        // =======================================
        // COMBINE CENTER + BOTTOM
        // =======================================
        VBox root = new VBox(10);
        root.getChildren().addAll(center, bottomButtons);
        VBox.setVgrow(center, Priority.ALWAYS);

        leagueTab.setContent(root);
        return leagueTab;
    }

    private void refreshLeague() {
        this.league = leagueManager.getLeague();

        if (leagueManager.isArchiveMode()) {
            currentLeagueLabel.setText(
                    "Current League: " + league.getLeagueName() + " (ARCHIVE MODE)"
            );
        } else {
            currentLeagueLabel.setText(
                    "Current League: " + league.getLeagueName()
            );
        }

        teamsListView.setItems(
                FXCollections.observableArrayList(league.getTeams())
        );
        playersListView.getItems().clear();
    }

    // ---------------------------------------------
    // USERS TAB (League Admin only)
    // ---------------------------------------------

    private Tab createUserTab() {
        Tab userTab = new Tab("Users");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label greeting = new Label("Hello, " + currentUser.getFirstName() + "!");

        TableView<Users> userTable = new TableView<>();

        TableColumn<Users, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getFirstName()));

        TableColumn<Users, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getLastName()));

        TableColumn<Users, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getID()));

        TableColumn<Users, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getRoles().toString()));

        userTable.getColumns().addAll(lastCol, firstCol, idCol, roleCol);

        // populate
        userTable.setItems(FXCollections.observableArrayList(
                leagueManager.getUserList().stream()
                        .sorted((u1, u2) -> {
                            int cmp = u1.getLastName().compareToIgnoreCase(u2.getLastName());
                            if (cmp != 0) return cmp;
                            cmp = u1.getFirstName().compareToIgnoreCase(u2.getFirstName());
                            if (cmp != 0) return cmp;
                            return u1.getID().compareToIgnoreCase(u2.getID());
                        })
                        .toList()
        ));

        HBox buttons = new HBox(10);

        Button addUserBtn = new Button("Add User");
        addUserBtn.setOnAction(e -> {
            Dialog<Users> dialog = new Dialog<>();
            dialog.setTitle("Add User");

            VBox dialogVBox = new VBox(5);
            TextField fName = new TextField();
            fName.setPromptText("First Name");

            TextField lName = new TextField();
            lName.setPromptText("Last Name");

            TextField id = new TextField();
            id.setPromptText("User ID");

            PasswordField pass = new PasswordField();
            pass.setPromptText("Password");

            ComboBox<Role> roleBox =
                    new ComboBox<>(FXCollections.observableArrayList(Role.values()));
            roleBox.setValue(Role.TeamO);

            ButtonType submitBtn = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(submitBtn, ButtonType.CANCEL);

            dialogVBox.getChildren().addAll(fName, lName, id, pass, roleBox);
            dialog.getDialogPane().setContent(dialogVBox);

            dialog.setResultConverter(btnType -> {
                if (btnType == submitBtn) {
                    boolean success = LeagueManager.addUser(
                            currentUser,
                            fName.getText(),
                            lName.getText(),
                            id.getText(),
                            pass.getText(),
                            roleBox.getValue()
                    );
                    if (success) {
                        userTable.setItems(FXCollections.observableArrayList(
                                leagueManager.getUserList()
                        ));
                    }
                }
                return null;
            });

            dialog.showAndWait();
        });

        Button changePassBtn = new Button("Change Password");
        changePassBtn.setOnAction(e -> {
            Users selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog passDialog = new TextInputDialog();
                passDialog.setTitle("Change Password");
                passDialog.setHeaderText("Change password for: " + selected.getID());
                passDialog.setContentText("New Password:");
                passDialog.showAndWait().ifPresent(newPass -> {
                    LeagueManager.changeUserPassword(currentUser, selected.getID(), newPass);
                    userTable.refresh();
                });
            }
        });

        buttons.getChildren().addAll(addUserBtn, changePassBtn);
        root.getChildren().addAll(greeting, userTable, buttons);

        userTab.setContent(root);
        return userTab;
    }

    // ---------------------------------------------
    // GAME TAB
    // ---------------------------------------------

    private Tab createGameTab() {
        Tab gameTab = new Tab("Games");
        gameTab.setClosable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        GameManager gm = leagueManager.getGameManager();

        Role role = currentUser.getRoles();
        boolean isLeagueAdmin = (role == Role.LeagueA);
        boolean isLeagueOfficial = (role == Role.LeagueO);
        boolean isTeamOfficial = (role == Role.TeamO);

        boolean canLoadDeleteStats = isLeagueAdmin || isLeagueOfficial;                  // Load / Delete
        boolean canViewStats = isLeagueAdmin || isLeagueOfficial || isTeamOfficial;      // All “view” stats

        // ========================================================
        // SCHEDULE LIST
        // ========================================================
        ListView<Game> gameListView = new ListView<>();
        gameListView.setPrefHeight(200);

        // Show ID + date + teams so users know what Game ID to use
        gameListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Game g, boolean empty) {
                super.updateItem(g, empty);
                if (empty || g == null) {
                    setText(null);
                } else {
                    String dateText = (g.getDate() == null || g.getDate().isBlank())
                            ? "Date TBD"
                            : g.getDate();

                    setText("#" + g.getId() + " — " + dateText
                            + " | " + g.getHome().getName()
                            + " vs " + g.getAway().getName());
                }
            }
        });

        Label scheduleLabel = new Label("Scheduled Games:");
        Label scheduleHelp = new Label(
                "All league games that have been scheduled.\n" +
                "Format: #ID — date | Home vs Away.\n" +
                "Select a game to see its ID and date."
        );
		scheduleHelp.getStyleClass().add("help-text");
		scheduleHelp.setWrapText(true);
		scheduleHelp.setMaxWidth(350);		
		
        scheduleHelp.getStyleClass().add("help-text");

        Button createScheduleBtn = new Button("Schedule Games");
        createScheduleBtn.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            ArrayList<Game> schedule = gm.getScheduleList();
            gameListView.setItems(FXCollections.observableArrayList(schedule));

            new Alert(Alert.AlertType.INFORMATION,
                    "Game schedule created. Select a game to set the date.").show();

            gm.seedStats(leagueManager.getLeague());

            Game g = gameListView.getSelectionModel().getSelectedItem();
            if (g == null) {
                new Alert(Alert.AlertType.WARNING, "Select a game first.").show();
                return;
            }

            TextInputDialog d = new TextInputDialog();
            d.setHeaderText("Enter: date\nFormat: MM-DD-YY HH:MM");
            d.showAndWait().ifPresent(input -> {
                try {
                    String date = input;
                    String team1 = g.getHome().getName();
                    String team2 = g.getAway().getName();

                    for (Game game : schedule) {
                        String t1 = game.getHome().getName();
                        String t2 = game.getAway().getName();

                        if (date.equals(game.getDate())) {
                            if (team1.equals(t1) || team1.equals(t2)) {
                                new Alert(Alert.AlertType.WARNING,
                                        "Team cannot be scheduled for two games on the same date. " +
                                                "Please select a new date.").show();
                                return;
                            }
                            if (team2.equals(t2) || team2.equals(t1)) {
                                new Alert(Alert.AlertType.WARNING,
                                        "Team cannot be scheduled for two games on the same date. " +
                                                "Please select a new date.").show();
                                return;
                            }
                        }
                    }

                    g.setDate(date);
                    gameListView.setItems(FXCollections.observableArrayList(
                            gm.getScheduleList()
                    ));

                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid format.").show();
                }
            });
        });

        Button addGameBtn = new Button("Add Game");
        addGameBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setHeaderText("Enter: date, home team, away team\nFormat: MM-DD-YY HH:MM,Team1,Team2");
            d.showAndWait().ifPresent(input -> {
                try {
                    String[] parts = input.split(",");
                    String date = parts[0].trim();
                    String homeName = parts[1].trim();
                    String awayName = parts[2].trim();

                    Team home = league.getTeams().stream()
                            .filter(t -> t.getName().equalsIgnoreCase(homeName))
                            .findFirst().orElse(null);

                    Team away = league.getTeams().stream()
                            .filter(t -> t.getName().equalsIgnoreCase(awayName))
                            .findFirst().orElse(null);

                    if (home == null || away == null) {
                        new Alert(Alert.AlertType.ERROR, "Team names invalid").show();
                        return;
                    }

                    gm.scheduleGame(date, home, away);
                    gameListView.setItems(FXCollections.observableArrayList(
                            gm.getScheduleList()
                    ));

                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid format.").show();
                }
            });
        });

        VBox scheduleBox = new VBox(5);
        scheduleBox.getChildren().addAll(
                scheduleLabel,
                scheduleHelp,
                gameListView
        );
        scheduleBox.setPadding(new Insets(8));
        scheduleBox.setStyle("-fx-border-color: #c38aff; -fx-border-radius: 5;");

        // ========================================================
        // UPLOAD / DELETE / VIEW GAME STATS
        // ========================================================
        TextField gameIdField = new TextField();
        gameIdField.setPromptText("Game ID");

        Label gameIdHelp = new Label("Enter the ID of a scheduled game.\nUse the list above to find IDs.");
        gameIdHelp.getStyleClass().add("help-text");

        HBox gameIdRow = new HBox(10, gameIdField, gameIdHelp);

        TextField filePathField = new TextField();
        filePathField.setPromptText("Stats file path");

        Label filePathHelp = new Label(
                "Enter or browse to a .txt or .csv file\ncontaining player stats for the game."
        );
        filePathHelp.getStyleClass().add("help-text");
        
        gameIdHelp.setWrapText(true);
        filePathHelp.setWrapText(true);

        // give them some width so they don't get squished
        gameIdHelp.setMaxWidth(280);
        filePathHelp.setMaxWidth(280);

        HBox.setHgrow(gameIdHelp, Priority.ALWAYS);
        HBox.setHgrow(filePathHelp, Priority.ALWAYS);


        HBox filePathRow = new HBox(10, filePathField, filePathHelp);

        // Auto-fill Game ID when a game is selected in the list
        gameListView.getSelectionModel().selectedItemProperty().addListener((obs, oldGame, newGame) -> {
            if (newGame != null) {
                gameIdField.setText(String.valueOf(newGame.getId()));
            }
        });
        gameListView.setPrefHeight(200);
        gameListView.getStyleClass().add("games-list");

        Button uploadStatsBtn = new Button("Upload Stats");
        uploadStatsBtn.setDisable(!canLoadDeleteStats);
        uploadStatsBtn.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            try {
                int id = Integer.parseInt(gameIdField.getText().trim());
                String file = filePathField.getText().trim();
                gm.readGameStats(file, id);

                new Alert(Alert.AlertType.INFORMATION,
                        "Stats uploaded for Game " + id).show();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid ID or file path.").show();
            }
        });

        Button deleteStatsBtn = new Button("Delete Game Stats");
        deleteStatsBtn.setDisable(!canLoadDeleteStats);
        deleteStatsBtn.setOnAction(e -> {
            if (leagueManager.isArchiveMode()) {
                new Alert(Alert.AlertType.ERROR,
                        "Editing disabled — archived league is read-only.").show();
                return;
            }

            Game g = gameListView.getSelectionModel().getSelectedItem();
            if (g == null) {
                new Alert(Alert.AlertType.WARNING, "Select a game first.").show();
                return;
            }

            gm.deleteStats(g.getId());
            new Alert(Alert.AlertType.INFORMATION,
                    "Stats deleted for Game " + g.getId()).show();
        });

        Button viewStatsBtn = new Button("View Selected Game Stats");
        viewStatsBtn.setDisable(!canViewStats);
        viewStatsBtn.setOnAction(e -> {
            Game g = gameListView.getSelectionModel().getSelectedItem();
            if (g == null) {
                new Alert(Alert.AlertType.WARNING, "Select a game first.").show();
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Game #").append(g.getId()).append("\n");
            sb.append("Date: ").append(g.getDate()).append("\n\n");

            sb.append("=== ").append(g.getHome().getName()).append(" ===\n");
            g.getHome().getPlayers().forEach(p -> appendPlayerStats(sb, g, p));

            sb.append("\n=== ").append(g.getAway().getName()).append(" ===\n");
            g.getAway().getPlayers().forEach(p -> appendPlayerStats(sb, g, p));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Stats");
            alert.setHeaderText("Stats for Game " + g.getId());
            alert.getDialogPane().setMinWidth(550);
            alert.setContentText(sb.toString());
            alert.showAndWait();
        });

        VBox statsBox = new VBox(10);
        statsBox.getChildren().addAll(
                new Label("Upload / Manage Game Stats:"),
                gameIdRow,
                filePathRow,
                uploadStatsBtn,
                deleteStatsBtn,
                viewStatsBtn
        );
        statsBox.setPadding(new Insets(8));
        statsBox.setStyle("-fx-border-color: #c38aff; -fx-border-radius: 5;");

        // ========================================================
        // GAME SUMMARY SECTION
        // ========================================================
        Label summaryLabel = new Label("Game Summary:");
        ListView<String> summaryList = new ListView<>();
        summaryList.setPrefHeight(200);
        summaryList.getStyleClass().add("summary-list");

        Button viewAllGamesBtn = new Button("View All Games");
        viewAllGamesBtn.setDisable(!canViewStats);
        viewAllGamesBtn.setOnAction(e -> {
            ArrayList<Game> games = gm.getScheduleList();
            games.sort(Comparator.comparing(Game::getDate));
            ArrayList<String> formatted = new ArrayList<>();

            for (Game g : games) {
                formatted.add(gameSummaryLine(g));
            }

            summaryList.setItems(FXCollections.observableArrayList(formatted));
        });

        TextField startDateField = new TextField();
        startDateField.setPromptText("Start Date (MM-DD-YY)");

        TextField endDateField = new TextField();
        endDateField.setPromptText("End Date (MM-DD-YY)");

        Button filterBtn = new Button("Filter");
        filterBtn.setOnAction(e -> {
            String start = startDateField.getText().trim();
            String end = endDateField.getText().trim();

            ArrayList<Game> games = gm.getScheduleList();
            games.sort(Comparator.comparing(Game::getDate));

            ArrayList<String> list = new ArrayList<>();
            for (Game g : games) {
                String d = g.getDate();
                if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0) {
                    list.add(gameSummaryLine(g));
                }
            }

            summaryList.setItems(FXCollections.observableArrayList(list));
        });

        HBox dateFilterBox = new HBox(10, startDateField, endDateField, filterBtn);

        VBox summaryBox = new VBox(8);
        summaryBox.getChildren().addAll(
                summaryLabel,
                viewAllGamesBtn,
                dateFilterBox,
                summaryList
        );
        summaryBox.setPadding(new Insets(8));
        summaryBox.setStyle("-fx-border-color: #c38aff; -fx-border-radius: 5;");

        // ========================================================
        // FINAL LAYOUT
        // ========================================================
        root.getChildren().addAll(
                addGameBtn,
                createScheduleBtn,
                scheduleBox,
                statsBox,
                summaryBox
        );

        gameTab.setContent(root);
        return gameTab;
    }


    /*
     * Bracket related US
     */

    
    private Tab createBracketTab() {
        Tab bracketTab = new Tab("Brackets");
        bracketTab.setClosable(false);

        GameManager gm = leagueManager.getGameManager();
        League lg = leagueManager.getLeague();

        // ====== MAIN GRID FOR BRACKET ======
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(40);   // space between rounds
        grid.setVgap(20);
        grid.setPadding(new Insets(30, 40, 40, 40));
        grid.getStyleClass().add("bracket-grid");

        // Get current bracket games
        List<Game> bracketGames = gm.getScheduleList().stream()
                .filter(Game::isBracketGame)
                .collect(Collectors.toList());

        // If no bracket yet, seed a small demo bracket so something shows
        if (bracketGames.isEmpty() && lg != null && !lg.getTeams().isEmpty()) {
            gm.scheduleBracket(lg, "Championship Bracket");  // see GameManager method below
            bracketGames = gm.getScheduleList().stream()
                    .filter(Game::isBracketGame)
                    .collect(Collectors.toList());
        }

        if (!bracketGames.isEmpty()) {
            buildBracketGrid(grid, bracketGames);   // NBA-style layout below
        } else {
            // Fallback message if literally no teams
            Label none = new Label("No teams available for a bracket.");
            grid.add(none, 0, 0);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // ====== BUTTONS (Schedule Brackets + View Results) ======
        Role role = currentUser.getRoles();
        boolean isLeagueAdmin = (role == Role.LeagueA);
        boolean isLeagueOfficial = (role == Role.LeagueO);
        boolean isTeamOfficial = (role == Role.TeamO);

        boolean canScheduleBracket = isLeagueAdmin || isLeagueOfficial;
        boolean canViewBracketResults = isLeagueAdmin || isLeagueOfficial || isTeamOfficial;

        Button scheduleBracketButton = new Button("Schedule Bracket");
        scheduleBracketButton.setDisable(!canScheduleBracket);
        scheduleBracketButton.setOnAction(e -> {
        	if (leagueManager.isArchiveMode()) {
                showAlert("Bracket", "Cannot schedule bracket while in archive mode.");
                return;
            }

            // Check if all regular-season (non-bracket) games are finished
            boolean allLeagueGamesFinished = gm.getScheduleList().stream()
                    .filter(g -> !g.isBracketGame())
                    .allMatch(Game::isFinished);

            if (!allLeagueGamesFinished) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Schedule Bracket");
                confirm.setHeaderText("Not all regular-season games are finished.");
                confirm.setContentText("Do you want to schedule the bracket anyway?");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    // User cancelled or closed the dialog → do nothing
                    return;
                }
                // If OK: fall through and schedule anyway
            }

            gm.scheduleBracket(leagueManager.getLeague(), "Championship Bracket");

            List<Game> updated = gm.getScheduleList().stream()
                    .filter(Game::isBracketGame)
                    .collect(Collectors.toList());

            grid.getChildren().clear();
            buildBracketGrid(grid, updated);
        });

        Button resultsBtn = new Button("View Bracket Results");
        resultsBtn.setDisable(!canViewBracketResults);
        resultsBtn.setOnAction(e -> showBracketResults());

        HBox buttons = new HBox(10, scheduleBracketButton, resultsBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        VBox root = new VBox(10, scroll, buttons);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10));

        bracketTab.setContent(root);
        return bracketTab;
    }



 // Place games in a tree-style bracket layout (NBA-style).
 // Assumes: game.isBracketGame() == true and game has bracketRound (1,2,3,...) and bracketIndex 0,1,...
 private void buildBracketGrid(GridPane grid, List<Game> bracketGames) {
     if (bracketGames == null || bracketGames.isEmpty()) return;

     // Group by round number
     Map<Integer, List<Game>> byRound = bracketGames.stream()
             .collect(Collectors.groupingBy(Game::getBracketRound));

     int maxRound = byRound.keySet().stream()
             .max(Integer::compareTo)
             .orElse(1);

     // For a full single-elim bracket, number of "slots" = 2^maxRound
     int slots = 1 << maxRound;  // e.g., 4 teams -> maxRound=2 -> slots=4

     for (int round = 1; round <= maxRound; round++) {
         List<Game> roundGames = byRound
                 .getOrDefault(round, Collections.emptyList())
                 .stream()
                 .sorted(Comparator.comparingInt(Game::getBracketIndex))
                 .collect(Collectors.toList());

         int col = round - 1; // left-to-right

         // Round label at top of column
         Label title = new Label("Round " + round);
         title.setStyle("-fx-font-weight: bold; -fx-underline: true;");
         title.setPadding(new Insets(0, 0, 15, 0));
         grid.add(title, col, 0);

         int blockHeight = 1 << (round - 1);  // 1,2,4,...

         for (Game g : roundGames) {
             int idx = g.getBracketIndex(); // 0,1,... in this round

             // Vertical position spacing
             int centerRow = (2 * idx + 1) * blockHeight;

             HBox box = createGameBracket(g);
             grid.add(box, col, centerRow);
         }
     }
 }

    
 private List<Game> createDummyBracketGames() {
	    List<Game> demo = new ArrayList<>();

	    // Round 1 – 2 games (indices 0,1)
	    for (int i = 0; i < 2; i++) {
	        Game g = new Game("Round 1");
	        g.setBracketGame(true);
	        g.setBracketRound(1);
	        g.setBracketIndex(i);   // 0 or 1
	        demo.add(g);
	    }

	    // Round 2 – 1 game (final, index 0)
	    Game finalG = new Game("Round 2");
	    finalG.setBracketGame(true);
	    finalG.setBracketRound(2);
	    finalG.setBracketIndex(0);
	    demo.add(finalG);

	    return demo;
	}




 // helper method for creating one bracket "box" for a Game
//helper method for creating one bracket "box" for a Game
private HBox createGameBracket(Game g) {
  HBox root = new HBox(10);
  root.setPadding(new Insets(10));
  root.setAlignment(Pos.CENTER);

  VBox round = new VBox(10);
  round.setAlignment(Pos.CENTER_LEFT);

  // Date label (or TBD)
  String date = (g.getDate() == null || g.getDate().trim().isEmpty())
          ? "Date: TBD"
          : g.getDate();
  Label dateLabel = new Label(date);

  // Team names (guard against null to avoid NPE)
  String name1 = (g.getHome() != null) ? g.getHome().getName() : "TBD";
  String name2 = (g.getAway() != null) ? g.getAway().getName() : "TBD";

  // Append scores if game is finished
  if (g.isFinished()) {
      if (g.getHome() != null && g.getHomeScore() != null) {
          name1 += " (" + g.getHomeScore() + ")";
      }
      if (g.getAway() != null && g.getAwayScore() != null) {
          name2 += " (" + g.getAwayScore() + ")";
      }
  }

  StackPane team1 = createRect(name1);
  StackPane team2 = createRect(name2);

  round.getChildren().addAll(dateLabel, team1, team2);
  root.getChildren().add(round);

  // Winner bubble on the right, NBA-style
  if (g.isFinished() && g.getHome() != null && g.getAway() != null) {
      VBox winnerRound = new VBox(10);
      winnerRound.setAlignment(Pos.CENTER);

      String winnerName;
      if (g.getHomeScore() > g.getAwayScore()) {
          winnerName = g.getHome().getName();
      } else if (g.getHomeScore() < g.getAwayScore()) {
          winnerName = g.getAway().getName();
      } else {
          winnerName = "Tie";
      }

      StackPane winner = createRect(winnerName);

      VBox lines = new VBox(40);
      lines.setAlignment(Pos.CENTER);
      Line line1 = new Line(0, 45, 20, 45);
      Line line2 = new Line(0, 65, 20, 65);
      lines.getChildren().addAll(line1, line2);

      winnerRound.getChildren().add(winner);
      root.getChildren().addAll(lines, winnerRound);
  }

  return root;
}

//helper method for creating rectangles
private StackPane createRect(String name) {
  Rectangle team = new Rectangle(130, 45);  // nice NBA-ish box size
  team.setArcWidth(14);
  team.setArcHeight(14);
  team.setStroke(Color.web("#d6b0ff"));   // matches your lavender theme
  team.setFill(Color.TRANSPARENT);

  Label teamName = new Label(name);
  teamName.setTextFill(Color.WHITE);

  StackPane stack = new StackPane(team, teamName);
  stack.setPadding(new Insets(5));
  stack.getStyleClass().add("bracket-box"); // optional CSS

  return stack;
}



    private void appendPlayerStats(StringBuilder sb, Game g, Player p) {
        Game.PlayerStats ps = g.getStats(p);
        if (ps == null) return;

        sb.append(p.getFirstName()).append(" ").append(p.getLastName())
          .append(" (#").append(p.getJerseyNumber()).append(")\n");

        sb.append(" FT: ").append(ps.ftMade).append("/").append(ps.ftAtt).append("\n");
        sb.append(" 2P: ").append(ps.twoMade).append("/").append(ps.twoAtt).append("\n");
        sb.append(" 3P: ").append(ps.threeMade).append("/").append(ps.threeAtt).append("\n");
        sb.append(" Assists: ").append(ps.assists)
          .append(" Fouls: ").append(ps.fouls)
          .append(" Points: ").append(ps.getPoints()).append("\n\n");
    }

    private void showBracketResults() {
        GameManager gm = leagueManager.getGameManager();

        // 1) Collect all bracket games
        List<Game> bracketGames = gm.getScheduleList().stream()
                .filter(Game::isBracketGame)
                .collect(Collectors.toList());

        if (bracketGames.isEmpty()) {
            showAlert("Bracket Results",
                      "No bracket has been scheduled yet.");
            return;
        }

        // 2) Ensure ALL bracket games are finished
        boolean allFinished = bracketGames.stream()
                .allMatch(Game::isFinished);

        if (!allFinished) {
            showAlert("Bracket Results",
                      "All bracket games must be completed before viewing final results.");
            return;
        }

        // 3) Find the final round number
        int maxRound = bracketGames.stream()
                .mapToInt(Game::getBracketRound)
                .max()
                .orElse(1);

        // Prefer the game with bracketIndex == 0 in the last round (championship),
        // but if that's not there, just take any game in that round.
        Game finalGame = bracketGames.stream()
                .filter(g -> g.getBracketRound() == maxRound)
                .filter(g -> g.getBracketIndex() == 0)
                .findFirst()
                .orElseGet(() ->
                        bracketGames.stream()
                                .filter(g -> g.getBracketRound() == maxRound)
                                .findFirst()
                                .orElse(null)
                );

        if (finalGame == null) {
            showAlert("Bracket Results",
                      "Could not determine the championship game.");
            return;
        }

        Team champion = finalGame.getWinner();
        Team home = finalGame.getHome();
        Team away = finalGame.getAway();

        if (champion == null || home == null || away == null) {
            showAlert("Bracket Results",
                      "Cannot determine the champion and runner-up from the final game.");
            return;
        }

        Team runnerUp = (champion == home) ? away : home;

        // 4) Build the display string: champion + players, runner-up + players
        StringBuilder sb = new StringBuilder();

        sb.append("Champion: ").append(champion.getName()).append("\n");
        for (Player p : champion.getPlayers()) {
            sb.append("  - ")
              .append(p.getFirstName()).append(" ")
              .append(p.getLastName())
              .append("\n");
        }

        sb.append("\nRunner-Up: ").append(runnerUp.getName()).append("\n");
        for (Player p : runnerUp.getPlayers()) {
            sb.append("  - ")
              .append(p.getFirstName()).append(" ")
              .append(p.getLastName())
              .append("\n");
        }

        showInfo("Bracket Results", sb.toString());
    }

    private void refreshUsers() {
        // Placeholder: you don't currently keep a reference to the Users TableView
        // so you just log to console.
        System.out.println("Users refreshed; current users:");
        for (Users u : leagueManager.getUserList()) {
            System.out.println(
                    u.getID() + " | " + u.getFirstName() + " " + u.getLastName() +
                    " | " + u.getRoles()
            );
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private String formatPlayerStats(Player p) {
        return String.format(
                "%s %s ( #%d )\n" +
                " FT: %d/%d 2P: %d/%d 3P: %d/%d\n" +
                " Assists: %d Fouls: %d Points: %d\n\n",
                p.getFirstName(),
                p.getLastName(),
                p.getJerseyNumber(),
                p.numFreeThrows,
                p.attemptedFreeThrows,
                p.numTwoPointers,
                p.attemptedTwoPointers,
                p.numThreePointers,
                p.attemptedThreePointers,
                p.numAssists,
                p.numFouls,
                p.getTotalPoints()
        );
    }

    private String gameSummaryLine(Game g) {
        if (!g.isFinished()) {
            return g.getDate() + " — " + g.getHome().getName() + " vs " +
                    g.getAway().getName() + " (Not Played)";
        }

        String winner;
        String loser;
        int wScore;
        int lScore;

        if (g.getHomeScore() > g.getAwayScore()) {
            winner = g.getHome().getName();
            loser = g.getAway().getName();
            wScore = g.getHomeScore();
            lScore = g.getAwayScore();
        } else {
            winner = g.getAway().getName();
            loser = g.getHome().getName();
            wScore = g.getAwayScore();
            lScore = g.getHomeScore();
        }

        return g.getDate() + " — " + winner + " (" + wScore + ") defeated " +
                loser + " (" + lScore + ")";
    }
}

