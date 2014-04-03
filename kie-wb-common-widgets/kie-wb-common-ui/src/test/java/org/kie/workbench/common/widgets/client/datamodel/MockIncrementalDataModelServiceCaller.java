package org.kie.workbench.common.widgets.client.datamodel;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;

import static org.mockito.Mockito.*;

public class MockIncrementalDataModelServiceCaller implements Caller<IncrementalDataModelService> {

    private IncrementalDataModelService service = mock( IncrementalDataModelService.class );

    @Override
    public IncrementalDataModelService call() {
        return service;
    }

    @Override
    public IncrementalDataModelService call( final RemoteCallback<?> remoteCallback ) {
        remoteCallback.callback( null );
        return service;
    }

    @Override
    public IncrementalDataModelService call( final RemoteCallback<?> remoteCallback,
                                             final ErrorCallback<?> errorCallback ) {
        remoteCallback.callback( null );
        return service;
    }

}
