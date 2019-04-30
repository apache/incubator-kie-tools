/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.perspective;

import org.uberfire.jsbridge.client.perspective.jsnative.JsNativeContextDisplay;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativePart;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

public class JsWorkbenchPartConverter {

    private final JsNativePart nativePart;

    public JsWorkbenchPartConverter(final JsNativePart nativePart) {
        this.nativePart = nativePart;
    }

    public PartDefinition toPartDefinition() {

        final PlaceRequest placeRequest = new DefaultPlaceRequest(nativePart.placeName(), nativePart.parameters());
        final PartDefinition partDefinition = new PartDefinitionImpl(placeRequest);

        final JsNativeContextDisplay contextDisplay = nativePart.contextDisplay();
        partDefinition.setContextDisplayMode(contextDisplay.mode());
        if (contextDisplay.contextId() != null) {
            partDefinition.setContextDefinition(new ContextDefinitionImpl(new DefaultPlaceRequest(contextDisplay.contextId())));
        }

        return partDefinition;
    }
}
