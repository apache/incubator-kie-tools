/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.kogito.client.docks;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.docks.WorkbenchDocksHandler;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@ApplicationScoped
public class KogitoEditorDock implements AuthoringEditorDock {

    protected UberfireDocks uberfireDocks;
    protected ManagedInstance<WorkbenchDocksHandler> installedHandlers;
    protected String authoringPerspectiveIdentifier = null;
    protected WorkbenchDocksHandler activeHandler = null;
    protected UberfireDock[] activeDocks;

    @Inject
    public KogitoEditorDock(final UberfireDocks uberfireDocks,
                            final ManagedInstance<WorkbenchDocksHandler> installedHandlers) {
        this.uberfireDocks = uberfireDocks;
        this.installedHandlers = installedHandlers;
    }

    @PostConstruct
    public void initialize() {
        // Initializing the handlers
        installedHandlers.iterator().forEachRemaining(handler -> {
            Command initCommand = () -> setActiveHandler(handler);
            handler.init(initCommand);
        });
    }

    @Override
    public boolean isSetup() {
        return authoringPerspectiveIdentifier != null;
    }

    @Override
    public void setup(String authoringPerspectiveIdentifier, PlaceRequest defaultPlaceRequest) {
        this.authoringPerspectiveIdentifier = authoringPerspectiveIdentifier;
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void expandAuthoringDock(UberfireDock dockToOpen) {
        uberfireDocks.show(UberfireDockPosition.EAST);
        if (dockToOpen != null) {
            uberfireDocks.open(dockToOpen);
        }
    }

    protected void setActiveHandler(WorkbenchDocksHandler handler) {
        // If there's an active handler let's check if it should refresh docks
        if (Objects.equals(activeHandler, handler) && !activeHandler.shouldRefreshDocks()) {
            return;
        }

        // setting the new handler as active
        activeHandler = handler;

        if (activeHandler.shouldDisableDocks()) {
            // disable docks
            if (activeDocks != null) {
                uberfireDocks.remove(activeDocks);
            }
        } else {
            // first remove the existing docks
            if (activeDocks != null) {
                uberfireDocks.remove(activeDocks);
            }

            // getting docks from the handler and  refreshing
            Collection<UberfireDock> docks = activeHandler.provideDocks(authoringPerspectiveIdentifier);
            activeDocks = docks.toArray(new UberfireDock[docks.size()]);
            uberfireDocks.add(activeDocks);
            uberfireDocks.show(UberfireDockPosition.EAST);
        }
    }
}
