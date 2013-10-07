package org.drools.workbench.screens.drltext.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class DrlModelContent {

    private String drl;
    private PackageDataModelOracleBaselinePayload dataModel;

    public DrlModelContent() {
    }

    public DrlModelContent( final String drl,
                            final PackageDataModelOracleBaselinePayload dataModel ) {
        this.drl = PortablePreconditions.checkNotNull( "drl",
                                                       drl );
        this.dataModel = PortablePreconditions.checkNotNull( "dataModel",
                                                             dataModel );
    }

    public String getDrl() {
        return this.drl;
    }

    public PackageDataModelOracleBaselinePayload getDataModel() {
        return this.dataModel;
    }

}
