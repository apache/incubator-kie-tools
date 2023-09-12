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

package org.dashbuilder.displayer.client;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.workbench.events.PerspectiveChange;

/**
 * It holds the set of Displayer instances being displayed on the current
 * perspective.
 * <p>
 * It also makes sure those instances are properly synced to reflect the data
 * set manipulation requests issued by any Displayer on the dashboard.
 * </p>
 */
@ApplicationScoped
public class PerspectiveCoordinator {

    /**
     * The real coordinator.
     */
    private DisplayerCoordinator displayerCoordinator;

    public PerspectiveCoordinator() {}

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
    public List<Displayer> getDisplayerList() {
        return displayerCoordinator.getDisplayerList();
    }

    /**
     * Reset the coordinator every time the perspective is changed.
     */
    private void onPerspectiveChanged(@Observes final PerspectiveChange event) {
        displayerCoordinator.clear();
    }

    public void closeDisplayer(String uuid) {
        if (uuid != null) {
            displayerCoordinator.getDisplayerList()
                    .stream()
                    .filter(d -> uuid.equals(d.getDisplayerSettings().getUUID()))
                    .findFirst()
                    .ifPresent(d -> {
                        d.close();
                        displayerCoordinator.removeDisplayer(d);
                    });
        }
    }
}
