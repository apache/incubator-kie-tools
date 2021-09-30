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
package org.uberfire.client.workbench.panels.impl;

import java.util.Collection;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

public class SinglePartPanelHelper {

    private Collection<PartDefinition> parts;
    private PlaceManager placeManager;

    public SinglePartPanelHelper(Collection<PartDefinition> parts,
                                 PlaceManager placeManager) {
        this.parts = parts;
        this.placeManager = placeManager;
    }

    public boolean hasNoParts() {
        return parts.isEmpty();
    }

    public void closeFirstPartAndAddNewOne(Command command) {
        placeManager.tryClosePlace(getPlaceFromFirstPart(),
                                   command);
    }

    PlaceRequest getPlaceFromFirstPart() {
        if (parts.iterator().hasNext()) {
            PartDefinition part = this.parts.iterator().next();
            if (part != null) {
                return part.getPlace();
            }
        }
        return null;
    }
}
