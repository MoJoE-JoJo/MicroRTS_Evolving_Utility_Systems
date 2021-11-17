package ai.utilitySystem;

import rts.*;

import java.util.ArrayList;
import java.util.List;

public class USFeature extends USNode {
    private Operation operation;
    private List<USNode> params = new ArrayList<>();
    private USNode param1; // What if we want a constant here instead?
    private USNode param2;

    public enum Operation {
        DIVIDE,
        MULTIPLY,
        SUM,
        SUBTRACT,
        MIN,
        MAX,
        POWER,
        AVGERAGE
    }

    public USFeature(String name, Operation operation) {
        this.name = name;
        this.operation = operation;
        this.params = new ArrayList<>();
    }

    @Override
    protected void calculateValue(GameState gs, int player) throws Exception {
        float val = 0.0f;
        switch (this.operation) {
            case DIVIDE -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        if (nextVal == 0.0f) nextVal = 1f;
                        val /= nextVal;
                    }
                }
                value = val;
            }
            case MULTIPLY -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val *= nextVal;
                    }
                }
                value = val;
            }
            case SUM -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val += nextVal;
                    }
                }
                value = val;
            }
            case SUBTRACT -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val -= nextVal;
                    }
                }
                value = val;
            }
            case MIN -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val = Math.min(val, nextVal);
                    }
                }
                value = val;
            }
            case MAX -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val = Math.max(val, nextVal);
                    }
                }
                value = val;
            }
            case POWER -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val = (float) Math.pow(val, nextVal);
                    }
                }
                value = val;
            }
            case AVGERAGE -> {
                for (int i = 0; i < params.size(); i++) {
                    if (i == 0) val = params.get(i).getValue(gs, player);
                    else {
                        float nextVal = params.get(i).getValue(gs, player);
                        val += nextVal;
                    }
                }
                val /= params.size();
                value = val;
            }
            default -> throw new Exception("Not yet implemented operation: " + this.operation);
        }
    }

    @Override
    public NodeType getType() {
        return NodeType.US_FEATURE;
    }

    @Override
    public String toPlantUML() {
        /*
        String v1 = "V1";
        String v2 = "V2";
        if (this.param1.getClass() == USConstant.class) {
            USConstant constant = (USConstant) this.param1;
            v1 = "" + constant.getConstant();
        }
        if (this.param2.getClass() == USConstant.class) {
            USConstant constant = (USConstant) this.param2;
            v2 = "" + constant.getConstant();
        }
         */
        return "map " + this.name + " {\n" +
                "Func => " + this.operation + "\n" +
                "Value => " + this.value + "\n" +
                "}\n";
    }

    public void addParam(USNode node) {
        params.add(node);
        /*
        if (param1 == null) {
            param1 = node;
        } else if (param2 == null) {
            param2 = node;
        } else {
            //TODO Handle this, unsure how
            System.out.println("YO! trying to set more than 2 param to a feature Node");
        }
         */
    }

    /*
    public USNode getParam2()
    {
        return param2;
    }
     */

    public String relationsToPlantUML() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<params.size(); i++){
            sb.append(params.get(i).getName()).append(" --> ").append(name).append(" : V").append(i).append("\n");
        }
        return sb.toString();
    }
}
