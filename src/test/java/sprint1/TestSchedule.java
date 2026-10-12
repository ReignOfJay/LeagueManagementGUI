package sprint1;

import sprint2.Game;
import sprint2.Team;
import sprint2.ScheduleManager;

public class TestSchedule {
    public static void main(String[] args) {
        // Teams
        Team eagles  = new Team("Eagles");
        Team falcons = new Team("Falcons");
        Team bears   = new Team("Bears");

        // Scheduler
        ScheduleManager sm = new ScheduleManager();

        // Schedule games
        Game g1 = sm.scheduleGame("2025-11-02 18:00", eagles,  falcons);
        Game g2 = sm.scheduleGame("2025-11-05 19:00", falcons, bears);
        Game g3 = sm.scheduleGame("2025-11-08 17:30", eagles,  bears);

        // View upcoming schedule
        sm.viewSchedule();
        System.out.println();

        // Record results
        sm.recordResult(g1.getId(), 72, 68); // Eagles beat Falcons
        sm.recordResult(g2.getId(), 55, 55); // tie (if allowed)
        sm.viewSchedule();
        System.out.println();

        // Standings
        sm.viewStandings(java.util.List.of(eagles, falcons, bears));
        System.out.println();

        // Team-specific view
        sm.viewScheduleForTeam(eagles);
    }
}

