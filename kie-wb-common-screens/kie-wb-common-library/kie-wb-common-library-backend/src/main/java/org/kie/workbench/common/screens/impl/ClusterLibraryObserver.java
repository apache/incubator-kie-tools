/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.backend.config.OrgUnit;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.kie.workbench.common.screens.library.api.sync.ClusterLibraryEvent;
import org.kie.workbench.common.screens.library.api.sync.SpacesUpdated;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class ClusterLibraryObserver {

    private ClusterService clusterService;

    private Event<ClusterLibraryEvent> clusterLibraryEvent;

    public ClusterLibraryObserver() {

    }

    @Inject
    public ClusterLibraryObserver(ClusterService clusterService,
                                  @SpacesUpdated Event<ClusterLibraryEvent> clusterLibraryEvent) {
        this.clusterService = clusterService;
        this.clusterLibraryEvent = clusterLibraryEvent;
    }

    public void onSystemRepositoryChangedEvent(@Observes @OrgUnit final SystemRepositoryChangedEvent systemRepositoryChangedEvent) {
        if (clusterService.isAppFormerClustered()) {
            clusterLibraryEvent.fire(new ClusterLibraryEvent());
        }
    }

    public void onAfterDeleteOrganizationalUnitEvent(@Observes AfterDeleteOrganizationalUnitEvent afterDeleteOrganizationalUnitEvent) {
        // This empty method is to make a Client side event available within cluster.
    }

}
