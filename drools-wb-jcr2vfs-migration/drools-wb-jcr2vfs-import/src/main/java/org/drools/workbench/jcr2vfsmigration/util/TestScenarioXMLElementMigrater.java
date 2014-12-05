package org.drools.workbench.jcr2vfsmigration.util;

import java.util.HashMap;
import java.util.Map;

public class TestScenarioXMLElementMigrater {

    //ScenarioXMLScenarioPersistence has already a the following package alias
    //xt.aliasPackage("org.drools.guvnor.client", "org.drools.ide.common.client");
    //so the expected package for Scenario elements is
    //org.drools.guvnor.client.modeldriven.testing instead of
    //org.drools.ide.common.client.modeldriven.testing   (this is the actual package name used in version 5.6.0)

    private static final String ORIGIN_PACKAGE = "org.drools.guvnor.client.modeldriven.testing";

    private static final String TARGET_PACKAGE = "org.drools.workbench.models.testscenarios.shared";

    private static final Map<String, String> refactoredPackages = new HashMap<String, String>();

    static {

        refactoredPackages.put( ORIGIN_PACKAGE + ".ActivateRuleFlowGroup", TARGET_PACKAGE + ".ActivateRuleFlowGroup" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".CallFieldValue", TARGET_PACKAGE + ".CallFieldValue" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".CallFixtureMap", TARGET_PACKAGE + ".CallFixtureMap" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".CallMethod", TARGET_PACKAGE + ".CallMethod" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".CollectionFieldData", TARGET_PACKAGE + ".CollectionFieldData" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".ExecutionTrace", TARGET_PACKAGE + ".ExecutionTrace" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".Expectation", TARGET_PACKAGE + ".Expectation" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".Fact", TARGET_PACKAGE + ".Fact" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".FactAssignmentField", TARGET_PACKAGE + ".FactAssignmentField" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".FactData", TARGET_PACKAGE + ".FactData" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".Field", TARGET_PACKAGE + ".Field" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".FieldData", TARGET_PACKAGE + ".FieldData" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".FieldPlaceHolder", TARGET_PACKAGE + ".FieldPlaceHolder" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".Fixture", TARGET_PACKAGE + ".Fixture" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".FixtureList", TARGET_PACKAGE + ".FixtureList" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".FixturesMap", TARGET_PACKAGE + ".FixturesMap" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".RetractFact", TARGET_PACKAGE + ".RetractFact" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".Scenario", TARGET_PACKAGE + ".Scenario" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".VerifyFact", TARGET_PACKAGE + ".VerifyFact" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".VerifyField", TARGET_PACKAGE + ".VerifyField" );
        refactoredPackages.put( ORIGIN_PACKAGE + ".VerifyRuleFired", TARGET_PACKAGE + ".VerifyRuleFired" );
    }

    public static String migrate( String source ) {
        String result = source;
        String oldStartTag;
        String oldEndTag;
        String newStartTag;
        String newEndTag;

        for ( Map.Entry<String, String> entry : refactoredPackages.entrySet() ) {
            oldStartTag = "<" + entry.getKey() + ">";
            newStartTag = "<" + entry.getValue() + ">";

            oldEndTag = "</" + entry.getKey() + ">";
            newEndTag = "</" + entry.getValue() + ">";

            result = result.replaceAll( oldStartTag, newStartTag );
            result = result.replaceAll( oldEndTag, newEndTag );
        }
        return result;
    }

}
