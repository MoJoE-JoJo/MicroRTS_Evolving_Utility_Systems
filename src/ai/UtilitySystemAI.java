/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import ai.core.AI;
import ai.core.ParameterSpecification;
import java.util.ArrayList;
import java.util.List;
import rts.*;
import rts.units.UnitTypeTable;
import util.*;

/**
 *
 */
public class UtilitySystemAI extends AI {
    private UtilitySystem us; // can we just pass it as param in constructor?
    
    public UtilitySystemAI(UnitTypeTable utt) {
    }
    

    public UtilitySystemAI() {
    }
    
    
    @Override
    public void reset() {
    }

    
    @Override
    public AI clone() {
        return new UtilitySystemAI(); // copy utility system aswell
    }
   
    
    @Override
    public PlayerAction getAction(int player, GameState gs) {
        try {
            return us.getAction(gs);
        }catch(Exception e) {
            return new PlayerAction();
        }
    }
    
    
    @Override
    public List<ParameterSpecification> getParameters()
    {
        return new ArrayList<>();
    }
    
}
