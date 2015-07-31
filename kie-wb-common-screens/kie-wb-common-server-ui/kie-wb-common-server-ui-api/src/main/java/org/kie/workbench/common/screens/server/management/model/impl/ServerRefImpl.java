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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.service.ContainerAlreadyRegisteredException;

@Portable
public class ServerRefImpl implements ServerRef {

    private String id;
    private String url;
    private String name;
    private String username;
    private String password;
    private ContainerStatus status;
    private ConnectionType connectionType;
    private final Map<String, String> properties = new HashMap<String, String>();
    protected final Map<String, ContainerRef> containersRef = new HashMap<String, ContainerRef>();
    private List<ServerInstanceRef> managedServers = new ArrayList<ServerInstanceRef>();

    public ServerRefImpl() {
    }

    public ServerRefImpl(
            final String id,
            final String url,
            final String name,
            final String username,
            final String password,
            final ContainerStatus status,
            final ConnectionType connectionType,
            final Map<String, String> properties,
            final Collection<ContainerRef> containerRefs ) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.username = username;
        this.password = password;
        this.connectionType = connectionType;
        this.status = status;
        if ( properties != null ) {
            this.properties.putAll( properties );
        }
        if ( containerRefs != null && !containerRefs.isEmpty() ) {
            for ( final ContainerRef containerConfig : containerRefs ) {
                containerConfig.setStatus( ContainerStatus.STOPPED );
                this.containersRef.put( containerConfig.getId(), containerConfig );
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUrl() {
        if ( url == null ) {
            return id;
        }
        return url;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public ContainerStatus getStatus() {
        return status;
    }

    @Override
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public Collection<ContainerRef> getContainersRef() {
        return containersRef.values();
    }

    public boolean hasContainerRef( final String id ) {
        return containersRef.containsKey( id );
    }

    @Override
    public ContainerRef getContainerRef( String containerId ) {
        return containersRef.get( containerId );
    }

    @Override
    public void addContainerRef( final ContainerRef containerRef ) {
        if ( containersRef.containsKey( containerRef.getId() ) ) {
            throw new ContainerAlreadyRegisteredException( containerRef.getId() );
        }
        containersRef.put( containerRef.getId(), containerRef );
    }

    @Override
    public void deleteContainer( final String containerId ) {
        containersRef.remove( containerId );
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
        if ( !( o instanceof ServerRefImpl ) ) {
            return false;
        }

        final ServerRefImpl serverRef = (ServerRefImpl) o;

        return id.equals( serverRef.id );
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}