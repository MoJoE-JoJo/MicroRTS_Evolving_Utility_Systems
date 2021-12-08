package AnjiIntegration;

import ai.utilitySystem.StaticUtilitySystems;

public class createBaselineXML {

    public static void main(String[] args)
    {
        anjiConverter conv = new anjiConverter();
        String baselinexml = conv.toXMLStringFromUtilitySystem(StaticUtilitySystems.getBaselineUtilitySystem());

        System.out.print(baselinexml);

    }
}
