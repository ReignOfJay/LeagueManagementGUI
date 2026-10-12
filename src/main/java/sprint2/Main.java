package sprint2;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {

        // -----------------------------
        // Create your model objects
        // -----------------------------
        League league = new League("Default League");
        GameManager gameManager = new GameManager();
        LeagueManager leagueManager = new LeagueManager(league, gameManager);

        // -----------------------------
        // Pass model into GUI
        // -----------------------------
        LeagueGUI.setLeagueManager(leagueManager);

        // -----------------------------
        // Launch JavaFX
        // -----------------------------
        Application.launch(LeagueGUI.class, args);
    }
}
