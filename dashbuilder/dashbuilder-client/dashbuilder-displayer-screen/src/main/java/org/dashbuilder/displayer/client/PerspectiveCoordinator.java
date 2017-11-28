/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.dashbuilder.displayer.DisplayerSettings;
import org.uberfire.client.workbench.events.PerspectiveChange;

/**
 * It holds the set of Displayer instances being displayed on the current perspective.
 * <p>It also makes sure those instances are properly synced to reflect the data set manipulation requests
 * issued by any Displayer on the dashboard.</p>
 */
@ApplicationScoped
public class PerspectiveCoordinator {

    /**
     * The real coordinator.
     */
    private DisplayerCoordinator displayerCoordinator;

    /**
     * Flag indicating if the perspective is on edit mode.
     */
    boolean editOn = false;

    public PerspectiveCoordinator() {
    }

    @Inject
    public PerspectiveCoordinator(DisplayerCoordinator coordinator) {
        this.displayerCoordinator = coordinator;
    }

    /**
     * Adds a Displayer instance to the current perspective context.
     */
    public void addDisplayer(Displayer displayer) {
        displayerCoordinator.addDisplayer(displayer);
    }

    /**
     * Removes a Displayer instance from the current perspective context.
     */
    public boolean removeDisplayer(Displayer displayer) {
        return displayerCoordinator.removeDisplayer(displayer);
    }

    /**
     *
     * @return the current list of displayers
     */
    public List<Displayer> getDisplayerList(){
        return displayerCoordinator.getDisplayerList();
    }

    /**
     * Turn on the edition of the perspective
     */
    public void editOn() {
        editOn = true;

        // Turns off the automatic refresh of all the displayers.
        for (Displayer displayer : displayerCoordinator.getDisplayerList()) {
            displayer.setRefreshOn(false);
        }
    }

    /**
     * Turn off the edition of the perspective
     */
    public void editOff() {
        editOn = false;

        // Resumes the automatic refresh on all the displayers.
        for (Displayer displayer : displayerCoordinator.getDisplayerList()) {
            displayer.setRefreshOn(true);
        }
    }

    /**
     * Reset the coordinator every time the perspective is changed.
     */
    private void onPerspectiveChanged(@Observes final PerspectiveChange event) {
        displayerCoordinator.clear();
    }

    /**
     * Listen to modifications on any of the data set being used in this perspective.
     */
    private void onDataSetModifiedEvent(@Observes DataSetModifiedEvent event) {
        if (!editOn) {

            String targetUUID = event.getDataSetDef().getUUID();
            for (Displayer displayer : displayerCoordinator.getDisplayerList()) {
                DisplayerSettings settings = displayer.getDisplayerSettings();

                // Do nothing if the displayer:
                // - Is not drawn
                // - Is handling the refresh by itself
                // - Is not configured to be updated on stale data
                if (!displayer.isDrawn() || displayer.isRefreshOn() || !settings.isRefreshStaleData()) {
                    continue;
                }

                String uuid = null;
                DataSet dataSet = settings.getDataSet();
                if (dataSet != null) {
                    uuid = dataSet.getUUID();
                }
                DataSetLookup dataSetLookup = settings.getDataSetLookup();
                if (uuid == null && dataSetLookup != null) {
                    uuid = dataSetLookup.getDataSetUUID();
                }
                if (uuid != null && targetUUID.equals(uuid)) {
                    displayer.redraw();
                }
            }
        }
    }
}
