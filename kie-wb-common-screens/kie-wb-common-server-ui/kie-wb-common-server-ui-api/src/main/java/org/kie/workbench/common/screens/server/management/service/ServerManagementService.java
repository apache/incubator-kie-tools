package org.kie.workbench.common.screens.server.management.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;

@Remote
public interface ServerManagementService {

    public Collection<ServerRef> listServers();

    public void registerServer( final String endpoint,
                                final String name,
                                final String username,
                                final String password,
                                final String controllerUrl) throws ServerAlreadyRegisteredException;

    void deleteOp( final Collection<String> servers,
                   final Map<String, List<String>> containers );

    void startContainers( final Map<String, List<String>> containers );

    void stopContainers( Map<String, List<String>> containers );

    void createContainer( final String serverId,
                          final String containerId,
                          final GAV gav );

    void refresh();

    Container getContainerInfo( String serverId,
                                String container );

    ScannerOperationResult scanNow( final String serverId,
                                    final String containerId );

    ScannerOperationResult startScanner( final String serverId,
                                         final String containerId,
                                         long interval );

    ScannerOperationResult stopScanner( final String serverId,
                                        final String containerId );

    void upgradeContainer( final String serverId,
                           final String containerId,
                           final GAV releaseId );
}
