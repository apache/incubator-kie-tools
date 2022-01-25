/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;

public class DRGDiagramUtils {

    public static String DRG = "DRG";

    public static boolean isDRG(final DMNDiagramElement dmnDiagramElement) {
        return DRG.equals(dmnDiagramElement.getName().getValue());
    }

    public static boolean isDRG(final JSIDMNDiagram dmnDiagramElement) {
        return DRG.equals(dmnDiagramElement.getName());
    }

    public static DMNDiagramElement newDRGInstance() {
        return new DMNDiagramElement(new Id(), new Name(DRG));
    }

    public static JSIDMNDiagram newJSIDRGInstance() {
        final JSIDMNDiagram diagram = JSIDMNDiagram.newInstance();
        diagram.setId(new Id().getValue());
        diagram.setName(DRG);
        return diagram;
    }
}
