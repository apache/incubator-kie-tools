package org.drools.workbench.screens.drltext.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;

@Portable
public class DrlModelContent {

    private String drl;
    private PackageDataModelOracle oracle;

    public DrlModelContent() {
    }

    public DrlModelContent( final String drl,
                            final PackageDataModelOracle oracle ) {
        this.drl = PortablePreconditions.checkNotNull( "drl",
                                                       drl );
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
    }

    public String getDrl() {
        return this.drl;
    }

    public PackageDataModelOracle getDataModel() {
        return this.oracle;
    }

}
