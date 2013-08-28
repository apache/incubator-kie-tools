package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class MockMetadataServiceCaller
        implements Caller<MetadataService> {

    @Override
    public MetadataService call() {
        return null;
    }

    @Override
    public MetadataService call( RemoteCallback<?> remoteCallback ) {
        return null;  //TODO -Rikkola-
    }

    @Override
    public MetadataService call( RemoteCallback<?> remoteCallback,
                                 ErrorCallback<?> errorCallback ) {
        return null;  //TODO -Rikkola-
    }
}
