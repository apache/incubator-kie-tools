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

    Collection<ServerRef> listServers();

    void registerServer( final String endpoint,
                         final String name,
                         final String version) throws ServerAlreadyRegisteredException;

    void deleteOp( final Collection<String> servers,
                   final Map<String, List<String>> containers );

    void startContainers( final Map<String, List<String>> containers );

    void stopContainers( final Map<String, List<String>> containers );

    void createContainer( final String serverId,
                          final String containerId,
                          final GAV gav );

    void refresh();

    Container getContainerInfo( final String serverId,
                                final String container );

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
