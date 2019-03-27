/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.docks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchFocusEvent;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.resources.images.ImagesResources;
import org.kie.workbench.common.screens.datamodeller.security.DataModelerFeatures;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

@Dependent
public class DataModellerDocksHandler extends AbstractWorkbenchDocksHandler {

    protected Constants constants = Constants.INSTANCE;

    protected SessionInfo sessionInfo;

    protected AuthorizationManager authorizationManager;

    protected DataModelerWorkbenchContext dataModelerWBContext;

    protected DataModelerContext lastActiveContext;

    protected DataModelerContext.EditionMode lastEditionMode;

    protected boolean dataModelerIsHidden;

    @Inject
    public DataModellerDocksHandler(SessionInfo sessionInfo,
                                    AuthorizationManager authorizationManager,
                                    DataModelerWorkbenchContext dataModelerWBContext) {
        this.sessionInfo = sessionInfo;
        this.authorizationManager = authorizationManager;
        this.dataModelerWBContext = dataModelerWBContext;
    }

    public boolean isGraphicMode(DataModelerContext context) {
        return context != null && context.getEditionMode().equals(DataModelerContext.EditionMode.GRAPHICAL_MODE);
    }

    @Override
    public Collection<UberfireDock> provideDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();

        if (lastActiveContext == null) {
            lastActiveContext = dataModelerWBContext.getActiveContext();
        }

        if (isGraphicMode(lastActiveContext)) {
            result.add(new UberfireDock(UberfireDockPosition.EAST,
                                        "RANDOM",
                                        new DefaultPlaceRequest("DroolsDomainScreen"),
                                        perspectiveIdentifier).withSize(450).withLabel(constants.DocksDroolsJBPMTitle()));

            result.add(new UberfireDock(UberfireDockPosition.EAST,
                                        "BRIEFCASE",
                                        new DefaultPlaceRequest("JPADomainScreen"),
                                        perspectiveIdentifier).withSize(450).withLabel(constants.DocksPersistenceTitle()));

            result.add(new UberfireDock(UberfireDockPosition.EAST,
                                        "COG",
                                        new DefaultPlaceRequest("AdvancedDomainScreen"),
                                        perspectiveIdentifier).withSize(450).withLabel(constants.DocksAdvancedTitle()));

            if (authorizationManager.authorize(DataModelerFeatures.PLANNER_AVAILABLE,
                                               sessionInfo.getIdentity())) {
                result.add(new UberfireDock(UberfireDockPosition.EAST,
                                            ImagesResources.INSTANCE.optaPlannerDisabledIcon(),
                                            ImagesResources.INSTANCE.optaPlannerEnabledIcon(),
                                            new DefaultPlaceRequest("PlannerDomainScreen"),
                                            perspectiveIdentifier)
                                   .withSize(450).withLabel(constants.DocksOptaPlannerTitle()));
            }
        }
        return result;
    }

    public void onContextChange(@Observes DataModelerWorkbenchContextChangeEvent contextEvent) {
        DataModelerContext newContext = dataModelerWBContext.getActiveContext();

        /*
        Check if we should refresh the docks. The cases are:
        - if any context (lastActiveContext or newContext) are null we should refresh the docks and hide them
        - if there's a context change or there's a edition mode change we should refresh docks and show them.
        - if none of these cases we don't refresh the docks
        */
        boolean doRefresh;
        if (newContext == null) {
            doRefresh = true;
            dataModelerIsHidden = true;
        } else if (!newContext.equals(lastActiveContext) || (newContext.equals(lastActiveContext) && !newContext.getEditionMode().equals(lastEditionMode))) {
            doRefresh = true;
            dataModelerIsHidden = !isGraphicMode(newContext);
            lastEditionMode = newContext.getEditionMode();
        } else {
            doRefresh = false;
        }

        lastActiveContext = newContext;

        refreshDocks(doRefresh,
                     dataModelerIsHidden);
    }

    public void onDataModelerWorkbenchFocusEvent(@Observes DataModelerWorkbenchFocusEvent event) {

        //If there's a focus event we must refresh the docks and show / hide depending on the event.
        dataModelerIsHidden = !event.isFocused();

        refreshDocks(true,
                     dataModelerIsHidden);
    }
}
