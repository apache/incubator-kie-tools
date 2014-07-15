package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.KieProjectService;

public class MockProjectServiceCaller
        implements Caller<KieProjectService> {

    @Override
    public KieProjectService call() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public KieProjectService call( RemoteCallback<?> remoteCallback ) {
        return null;  //TODO: -Rikkola-
    }

    @Override
    public KieProjectService call( RemoteCallback<?> remoteCallback,
                                   ErrorCallback<?> errorCallback ) {
        return null;  //TODO: -Rikkola-
    }
}
