package org.kie.workbench.common.screens.server.management.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;

@Portable
public class ServerImpl extends ServerRefImpl implements Server {

    private Collection<Container> containers = new ArrayList<Container>();

    public ServerImpl() {
    }

    public ServerImpl( final String id,
                       final String url,
                       final String name,
                       final String username,
                       final String password,
                       final ContainerStatus status,
                       final ConnectionType connectionType,
                       final Collection<Container> containers,
                       final Map<String, String> properties,
                       final Collection<ContainerRef> containersConfig ) {
        super( id, url, name, username, password, status, connectionType, properties, containersConfig );
        if ( containers != null && !containers.isEmpty() ) {
            this.containers.addAll( containers );
            for ( final Container container : containers ) {
                containersRef.remove( container.getId() );
                containersRef.put( container.getId(), container );
            }
        }
    }

    @Override
    public Collection<Container> containers() {
        return containers;
    }

    @Override
    public void deleteContainer( final String containerId ) {
        super.deleteContainer( containerId );
        Container obj = null;
        for ( final Container container : containers ) {
            if ( container.getId().equals( containerId ) ) {
                obj = container;
                break;
            }
        }
        if ( obj != null ) {
            containers.remove( obj );
        }
    }

}
