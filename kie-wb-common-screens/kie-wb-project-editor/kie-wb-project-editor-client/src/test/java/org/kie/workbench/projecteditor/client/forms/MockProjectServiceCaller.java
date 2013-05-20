package org.kie.workbench.projecteditor.client.forms;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.services.project.service.ProjectService;

public class MockProjectServiceCaller
        implements Caller<ProjectService> {
    @Override
    public ProjectService call(RemoteCallback<?> remoteCallback) {
        return null;  //TODO: -Rikkola-
    }

    @Override
    public ProjectService call(RemoteCallback<?> remoteCallback, ErrorCallback errorCallback) {
        return null;  //TODO: -Rikkola-
    }
}
