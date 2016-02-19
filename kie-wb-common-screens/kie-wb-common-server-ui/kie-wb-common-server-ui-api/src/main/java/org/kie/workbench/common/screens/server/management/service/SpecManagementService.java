/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

@Remote
public interface SpecManagementService {

    void saveContainerSpec( final String serverTemplateId,
                            final ContainerSpec containerSpec );

    void saveServerTemplate( final ServerTemplate serverTemplate );

    ServerTemplate getServerTemplate( final String serverTemplateId );

    void deleteServerInstance( final ServerInstanceKey serverInstanceKey );

    Collection<ServerTemplateKey> listServerTemplateKeys();

    Collection<ServerTemplate> listServerTemplates();

    Collection<ContainerSpec> listContainerSpec( final String serverTemplateId );

    void deleteContainerSpec( final String serverTemplateId,
                              final String containerSpecId );

    void deleteServerTemplate( final String serverTemplateId );

    void copyServerTemplate( final String serverTemplateId,
                             final String newServerTemplateId,
                             final String newServerTemplateName );

    void updateContainerConfig( final String serverTemplateId,
                                final String containerSpecId,
                                final Capability capability,
                                final ContainerConfig containerConfig );

    void updateServerTemplateConfig( final String serverTemplateId,
                                     final Capability capability,
                                     final ServerConfig serverTemplateConfig );

    void startContainer( final ContainerSpecKey containerSpecKey );

    void stopContainer( final ContainerSpecKey containerSpecKey );

    boolean isContainerIdValid( final String serverTemplateId,
                                final String containerId );

    boolean isNewServerTemplateIdValid( final String serverTemplateId );
}
