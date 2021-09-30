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
package org.uberfire.workbench.model;

import jsinterop.annotations.JsType;
import org.uberfire.mvp.PlaceRequest;

/**
 * Describes the assignment of a {@link PlaceRequest} to a tab/card/item in a {@link PanelDefinition}. Given this
 * information, you can find out (or dictate) which panel within the current perspective will contain the GUI element
 * for the given PlaceRequest. However, the {@link PartDefinition} does not contain any information about the GUI
 * itself, presumably because PartDefinition objects can be sent to the server. The UI (Widget) information is contained
 * with UIPart. The mapping of PartDefinitions to UIParts is maintained by a PanelManager.
 */
@JsType
public interface PartDefinition {

    PlaceRequest getPlace();

    void setPlace(final PlaceRequest place);

    PanelDefinition getParentPanel();

    void setParentPanel(final PanelDefinition parentPanel);

    ContextDefinition getContextDefinition();

    void setContextDefinition(final ContextDefinition contextDefinition);

    ContextDisplayMode getContextDisplayMode();

    void setContextDisplayMode(final ContextDisplayMode contextDisplayMode);

    /**
     * Invokes {@link #toString()} but exported to JavaScript so it can be invoked from different scripts.
     */
    default String asString() {
        return this.toString();
    }

    boolean isSelectable();

    void setSelectable(boolean selectable);
}
