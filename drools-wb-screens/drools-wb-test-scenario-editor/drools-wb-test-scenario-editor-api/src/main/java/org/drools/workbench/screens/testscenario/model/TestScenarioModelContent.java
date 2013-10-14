package org.drools.workbench.screens.testscenario.model;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class TestScenarioModelContent {

    private Scenario scenario;
    private String packageName;
    private PackageDataModelOracleBaselinePayload dataModel;

    public TestScenarioModelContent() {
    }

    public TestScenarioModelContent( final Scenario scenario,
                                     final String packageName,
                                     final PackageDataModelOracleBaselinePayload dataModel ) {
        this.scenario = PortablePreconditions.checkNotNull( "scenario",
                                                            scenario );
        this.packageName = PortablePreconditions.checkNotNull( "packageName",
                                                               packageName );
        this.dataModel = PortablePreconditions.checkNotNull( "dataModel",
                                                             dataModel );
    }

    public Scenario getScenario() {
        return scenario;
    }

    public String getPackageName() {
        return packageName;
    }

    public PackageDataModelOracleBaselinePayload getDataModel() {
        return dataModel;
    }

}
