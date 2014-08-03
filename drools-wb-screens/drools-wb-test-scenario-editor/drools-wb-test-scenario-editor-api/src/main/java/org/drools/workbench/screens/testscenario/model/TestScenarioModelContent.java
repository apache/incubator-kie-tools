package org.drools.workbench.screens.testscenario.model;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class TestScenarioModelContent {

    private Scenario scenario;
    private String packageName;
    private PackageDataModelOracleBaselinePayload dataModel;
    private Overview overview;

    public TestScenarioModelContent() {
    }

    public TestScenarioModelContent(
            final Scenario scenario,
            final Overview overview,
            final String packageName,
            final PackageDataModelOracleBaselinePayload dataModel) {
        this.overview = PortablePreconditions.checkNotNull("overview",
                overview);
        this.scenario = PortablePreconditions.checkNotNull("scenario",
                scenario);
        this.packageName = PortablePreconditions.checkNotNull("packageName",
                packageName);
        this.dataModel = PortablePreconditions.checkNotNull("dataModel",
                dataModel);
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

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }
}
