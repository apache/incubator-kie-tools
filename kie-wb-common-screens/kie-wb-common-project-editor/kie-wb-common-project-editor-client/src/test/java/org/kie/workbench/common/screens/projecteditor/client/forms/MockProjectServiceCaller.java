package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class MockProjectServiceCaller
        implements Caller<ProjectService> {

    @Override
    public ProjectService call() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProjectService call(RemoteCallback<?> remoteCallback) {
        return null;  //TODO: -Rikkola-
    }

    @Override
    public ProjectService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
        return null;  //TODO: -Rikkola-
    }
}
