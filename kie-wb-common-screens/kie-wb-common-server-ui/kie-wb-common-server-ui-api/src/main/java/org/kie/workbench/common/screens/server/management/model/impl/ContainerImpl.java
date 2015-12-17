/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
                          final Long pollInterval,
                          final GAV resolvedReleasedId ) {
        super( serverId, id, status, releaseId, scannerStatus, pollInterval );
        this.resolvedReleasedId = resolvedReleasedId;
    }

    @Override
    public GAV getResolvedReleasedId() {
        return resolvedReleasedId;
    }
}
