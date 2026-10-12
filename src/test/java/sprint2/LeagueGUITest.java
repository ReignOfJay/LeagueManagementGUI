package sprint2;

import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class LeagueGUITest {

	private static boolean javafxInitialized = false;

    @BeforeAll
    static void initJavaFx() throws Exception {
        if (!javafxInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
            javafxInitialized = true;
        }
    }

    /**
     * Helper to run code on the JavaFX Application Thread and wait until it finishes.
     */
    private void runOnFxThreadAndWait(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    
    /**
     * Stub LeagueManager for testing login behavior.
     * It overrides login() and league-related methods.
     */
    static class StubLeagueManager extends LeagueManager {

        private League league;
        private final Users loginResult;
        boolean loginCalled = false;

        public StubLeagueManager(League league, Users loginResult) {
            super();           // your LeagueManager has a no-arg constructor
            this.league = league;
            this.loginResult = loginResult;
        }

        @Override
        public Users login(String username, String password) {
            loginCalled = true;
            return loginResult;
        }

        @Override
        public League getLeague() {
            return league;
        }
        
        @Override
        public void setLeague(League newLeague) {
            this.league = newLeague;
        }

        @Override
        public void initializeDummyLeague() {
            if (league == null) {
                league = new League("Test League");
            }
        }
    }

    /**
     * Simple smoke test: starting the LeagueGUI with a stub LeagueManager
     * should not throw and should produce a Scene.
     */
    @Test
    void testStartCreatesScene() throws Exception {
        League testLeague = new League("Test League");
        Users dummyUser = new Users("Test", "User", "test", "Test@123", Role.TeamO);

        StubLeagueManager stubManager = new StubLeagueManager(testLeague, dummyUser);

        runOnFxThreadAndWait(() -> {
            Stage stage = new Stage();
            LeagueGUI.setLeagueManager(stubManager);

            LeagueGUI gui = new LeagueGUI();
            try {
                gui.start(stage);
            } catch (Exception e) {
                fail("start() threw an exception: " + e.getMessage());
            }

            assertNotNull(stage.getScene(), "Stage should have a Scene after start()");
        });
    }
    
    /**
     * Non-JavaFX test: verify that our StubLeagueManager.login()
     * returns the configured user and sets loginCalled.
     */
    @Test
    void testStubLeagueManagerLogin() {
        League testLeague = new League("Test League");
        Users expectedUser = new Users("Admin", "User", "admin", "Admin@123", Role.LeagueA);

        StubLeagueManager stubManager = new StubLeagueManager(testLeague, expectedUser);

        Users result = stubManager.login("whatever", "whatever");
        assertTrue(stubManager.loginCalled, "login should set loginCalled = true");
        assertSame(expectedUser, result, "login should return the stub user");
    }

}
