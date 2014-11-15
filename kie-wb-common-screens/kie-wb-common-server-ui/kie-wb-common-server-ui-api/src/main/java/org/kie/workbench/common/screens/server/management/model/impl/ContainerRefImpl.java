package org.kie.workbench.common.screens.server.management.model.impl;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;

@Portable
public class ContainerRefImpl implements ContainerRef {

    private String serverId;
    private String id;
    private ContainerStatus status = ContainerStatus.STOPPED;
    private GAV releaseId;
    private ScannerStatus scannerStatus;
    private Long pollInterval;

    public ContainerRefImpl() {
    }

    public ContainerRefImpl( final String serverId,
                             final String id,
                             final ContainerStatus status,
                             final GAV releaseId,
                             final ScannerStatus scannerStatus,
                             final Long pollInterval ) {
        this.serverId = serverId;
        this.id = id;
        this.status = status;
        this.releaseId = releaseId;
        this.scannerStatus = scannerStatus;
        this.pollInterval = pollInterval;
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ContainerStatus getStatus() {
        return status;
    }

    @Override
    public Long getPollInterval() {
        return pollInterval;
    }

    @Override
    public GAV getReleasedId() {
        return releaseId;
    }

    @Override
    public ScannerStatus getScannerStatus() {
        return scannerStatus;
    }

    public void setStatus( final ContainerStatus status ) {
        this.status = status;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ContainerRefImpl ) ) {
            return false;
        }

        ContainerRefImpl that = (ContainerRefImpl) o;

        if ( !id.equals( that.id ) ) {
            return false;
        }

        return serverId.equals( that.serverId );
    }

    @Override
    public int hashCode() {
        int result = serverId.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
