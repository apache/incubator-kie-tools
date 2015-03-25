package org.kie.workbench.common.screens.server.management.model;

import java.util.Collection;
import java.util.Map;

public interface ServerRef {

    String getId();

    String getUrl();

    String getName();

    String getUsername();

    String getPassword();

    ContainerStatus getStatus();

    ConnectionType getConnectionType();

    Map<String, String> getProperties();

    Collection<ContainerRef> getContainersRef();

    boolean hasContainerRef( final String containerId );

    ContainerRef getContainerRef( final String containerId );

    void addContainerRef( final ContainerRef containerConfig );

    void deleteContainer( String containerId );
}
