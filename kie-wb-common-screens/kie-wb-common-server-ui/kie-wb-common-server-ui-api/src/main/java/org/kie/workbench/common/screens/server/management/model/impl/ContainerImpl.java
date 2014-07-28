package org.kie.workbench.common.screens.server.management.model.impl;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;

@Portable
public class ContainerImpl extends ContainerRefImpl implements Container {

    private GAV resolvedReleasedId;

    public ContainerImpl() {
    }

    public ContainerImpl( final String serverId,
                          final String id,
                          final ContainerStatus status,
                          final GAV releaseId,
                          final ScannerStatus scannerStatus,
                          final GAV resolvedReleasedId ) {
        super( serverId, id, status, releaseId, scannerStatus );
        this.resolvedReleasedId = resolvedReleasedId;
    }

    @Override
    public GAV getResolvedReleasedId() {
        return resolvedReleasedId;
    }

}
