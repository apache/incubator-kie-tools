/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.events;

import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.UberFireEvent;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * CDI event fired by the framework just after the current perspective has changed.
 */
public class PerspectiveChange implements UberFireEvent {

    private final PerspectiveDefinition perspectiveDefinition;
    private final Menus menus;
    private final String identifier;
    private final PlaceRequest placeRequest;

    public PerspectiveChange( final PlaceRequest placeRequest,
                              final PerspectiveDefinition perspectiveDefinition,
                              final Menus menus,
                              final String identifier ) {
        this.placeRequest = placeRequest;
        this.perspectiveDefinition = perspectiveDefinition;
        this.menus = menus;
        this.identifier = identifier;
    }

    public PerspectiveDefinition getPerspectiveDefinition() {
        return perspectiveDefinition;
    }

    public Menus getMenus() {
        return menus;
    }

    public String getIdentifier() {
        return identifier;
    }

    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    @Override
    public String toString() {
        return "PerspectiveChange{" +
                "perspectiveDefinition=" + perspectiveDefinition +
                ", menus=" + menus +
                ", identifier='" + identifier + '\'' +
                ", placeRequest=" + placeRequest +
                '}';
    }
}
