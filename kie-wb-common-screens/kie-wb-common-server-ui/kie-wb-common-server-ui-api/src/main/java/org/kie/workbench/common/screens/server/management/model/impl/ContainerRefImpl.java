/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;

@Portable
public class ContainerRefImpl implements ContainerRef {

    private String serverId;
    private String id;
    private ContainerStatus status = ContainerStatus.STOPPED;
    private GAV releaseId;
    private ScannerStatus scannerStatus;
    private Long pollInterval;

    private List<ServerInstanceRef> managedServers = new ArrayList<ServerInstanceRef>();

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
    public List<ServerInstanceRef> getManagedServers() {
        return managedServers;
    }

    @Override
    public void addManagedServer(ServerInstanceRef managedServers) {
        this.managedServers.add(managedServers);
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

        return id.equals( that.id ) && serverId.equals( that.serverId );
    }

    @Override
    public int hashCode() {
        int result = serverId.hashCode();
        result = ~~result;
        result = 31 * result + id.hashCode();
        result = ~~result;
        return result;
    }
}
