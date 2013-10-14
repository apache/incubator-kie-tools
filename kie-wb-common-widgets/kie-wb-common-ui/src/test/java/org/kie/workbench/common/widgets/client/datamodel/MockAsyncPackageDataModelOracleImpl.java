package org.kie.workbench.common.widgets.client.datamodel;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;

public class MockAsyncPackageDataModelOracleImpl extends AsyncPackageDataModelOracleImpl {

    public void setService( final Caller<IncrementalDataModelService> service ) {
        this.service = service;
    }

}
