/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.client;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class AuthoringWorkbenchDocks {

    @Inject
    private UberfireDocks uberfireDocks;

    @Inject
    protected DataModelerWorkbenchContext dataModelerWBContext;

    private String authoringPerspectiveIdentifier;

    private UberfireDock projectExplorerDock;


    public void perspectiveChangeEvent(@Observes UberfireDockReadyEvent dockReadyEvent) {
        if (authoringPerspectiveIdentifier != null && dockReadyEvent.getCurrentPerspective().equals(authoringPerspectiveIdentifier)) {
            if (projectExplorerDock != null) {
                uberfireDocks.expand(projectExplorerDock);
            }
        }
    }

    public void setup(String authoringPerspectiveIdentifier, PlaceRequest projectExplorerPlaceRequest) {
        this.authoringPerspectiveIdentifier = authoringPerspectiveIdentifier;
        projectExplorerDock = new UberfireDock(UberfireDockPosition.WEST, "ADJUST", projectExplorerPlaceRequest, authoringPerspectiveIdentifier).withSize(400).withLabel("Project Explorer");
        uberfireDocks.add(
                projectExplorerDock,
                new UberfireDock(UberfireDockPosition.EAST, "RANDOM", new DefaultPlaceRequest("DroolsDomainScreen"), authoringPerspectiveIdentifier).withSize(450).withLabel("Drools"),
                new UberfireDock(UberfireDockPosition.EAST, "BRIEFCASE", new DefaultPlaceRequest("JPADomainScreen"), authoringPerspectiveIdentifier).withSize(450).withLabel("JPA"),
                new UberfireDock(UberfireDockPosition.EAST, "COG", new DefaultPlaceRequest("AdvancedDomainScreen"), authoringPerspectiveIdentifier).withSize(450).withLabel("Advanced")

        );
        uberfireDocks.disable(UberfireDockPosition.EAST, authoringPerspectiveIdentifier);
    }

    public void onContextChange(@Observes DataModelerWorkbenchContextChangeEvent contextEvent) {
        DataModelerContext context = dataModelerWBContext.getActiveContext();
        if (shouldDisplayWestDocks(context)) {
            uberfireDocks.enable(UberfireDockPosition.EAST, authoringPerspectiveIdentifier);
        } else {
            uberfireDocks.disable( UberfireDockPosition.EAST, authoringPerspectiveIdentifier);
        }
    }

    private boolean shouldDisplayWestDocks(DataModelerContext context) {
        return context != null && context.getEditionMode() == DataModelerContext.EditionMode.GRAPHICAL_MODE;
    }

}
