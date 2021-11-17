/*
* This class was contributed by: Antonin Komenda, Alexander Shleyfman and Carmel Domshlak
*/

package tests;

import ai.core.AI;
import ai.PassiveAI;
import ai.UtilitySystemAI;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

 public class RunExperimentTest {
    public static void main(String args[]) throws Exception {
        String scenarioFileName = "maps/16x16/basesWorkers16x16.xml";
        UnitTypeTable utt = new UnitTypeTable(); // original game (NOOP length = move length)
        int MAX_GAME_CYCLES = 3000; // game time
        int MAX_INACTIVE_CYCLES = 3000;

        AI ai1 = new UtilitySystemAI(utt);
        AI ai2 = new UtilitySystemAI(utt);
        AI ai3 = new PassiveAI();

        PhysicalGameState pgs = PhysicalGameState.load(scenarioFileName, utt);
        GameState gs = runExperiment(ai1, ai2, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES);

        GameState gs2 = runUntilAtResourceCount(ai1, ai3, pgs, utt, MAX_GAME_CYCLES, MAX_INACTIVE_CYCLES, 10);

        System.out.println("GAMEOVER");
        System.out.println("Winner: " + gs.winner());
        System.out.println("Time: " + gs.getTime());
        
        System.out.println("GAMEOVER");
        System.out.println("Winner: " + gs2.winner());
        System.out.println("Time: " + gs2.getTime());
    }

    // Run a single iteration of the game
    public static GameState runExperiment(AI ai1, AI ai2, PhysicalGameState map, UnitTypeTable utt, int max_cycles, int max_inactive_cycles) throws Exception {
        boolean GC_EACH_FRAME = true;
        long lastTimeActionIssued = 0;
        ai1.reset();
        ai2.reset();
        GameState gs = new GameState(map.clone(),utt);
        boolean gameover = false;

        do {
            if (GC_EACH_FRAME) System.gc();
            PlayerAction pa1 = null, pa2 = null;
            pa1 = ai1.getAction(0, gs);
            pa2 = ai2.getAction(1, gs);
            if (gs.issueSafe(pa1)) lastTimeActionIssued = gs.getTime();
            if (gs.issueSafe(pa2)) lastTimeActionIssued = gs.getTime();
            gameover = gs.cycle();
        } while (!gameover && 
                    (gs.getTime() < max_cycles) && 
                    (gs.getTime() - lastTimeActionIssued < max_inactive_cycles));
        ai1.gameOver(gs.winner());
        ai2.gameOver(gs.winner());
        return gs;
    }

    public static GameState runUntilAtResourceCount(AI ai1, AI ai2, PhysicalGameState map, UnitTypeTable utt, int max_cycles, int max_inactive_cycles, int resourceGoal) throws Exception {
        boolean GC_EACH_FRAME = false;
        long lastTimeActionIssued = 0;
        ai1.reset();
        ai2.reset();
        GameState gs = new GameState(map.clone(),utt);
        boolean gameover = false;
        int resourceCount = 0;

        do {
            if (GC_EACH_FRAME) System.gc();
            PlayerAction pa1 = null, pa2 = null;
            pa1 = ai1.getAction(0, gs);
            pa2 = ai2.getAction(1, gs);
            if (gs.issueSafe(pa1)) lastTimeActionIssued = gs.getTime();
            if (gs.issueSafe(pa2)) lastTimeActionIssued = gs.getTime();
            gameover = gs.cycle();
            resourceCount = gs.getPhysicalGameState().getPlayer(0).getResources();
        } while (!gameover && 
                    (gs.getTime() < max_cycles) && 
                    (gs.getTime() - lastTimeActionIssued < max_inactive_cycles) &&
                    resourceCount < resourceGoal);
        ai1.gameOver(gs.winner());
        ai2.gameOver(gs.winner());
        return gs;
    }

}
