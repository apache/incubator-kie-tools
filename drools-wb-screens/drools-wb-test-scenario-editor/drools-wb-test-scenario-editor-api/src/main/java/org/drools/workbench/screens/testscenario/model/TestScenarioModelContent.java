package org.drools.workbench.screens.testscenario.model;


import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TestScenarioModelContent {

    private Scenario scenario;

    private PackageDataModelOracle oracle;
    private String packageName;


    public TestScenarioModelContent() {

    }

    public TestScenarioModelContent(Scenario scenario, PackageDataModelOracle oracle, String packageName) {
        this.scenario = scenario;
        this.oracle = oracle;
        this.packageName = packageName;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public PackageDataModelOracle getOracle() {
        return oracle;
    }

    public String getPackageName() {
        return packageName;
    }
}
