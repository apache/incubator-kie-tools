package org.kie.workbench.projecteditor.client.forms;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.services.metadata.MetadataService;

public class MockMetadataServiceCaller
        implements Caller<MetadataService> {
    @Override
    public MetadataService call(RemoteCallback<?> remoteCallback) {
        return null;  //TODO -Rikkola-
    }

    @Override
    public MetadataService call(RemoteCallback<?> remoteCallback, ErrorCallback errorCallback) {
        return null;  //TODO -Rikkola-
    }
}
