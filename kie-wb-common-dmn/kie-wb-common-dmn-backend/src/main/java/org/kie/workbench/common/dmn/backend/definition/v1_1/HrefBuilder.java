/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Map;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;

public class HrefBuilder {

    public static String getHref(final DRGElement drgElement) {
        if (!drgElement.getId().getValue().contains(":")) {
            return "#" + drgElement.getId().getValue();
        }

        // If it have ":" it is an imported element
        final DMNModelInstrumentedBase parent = drgElement.getParent();
        final Definitions definitions;
        if (parent instanceof DMNDiagram) {
            final DMNDiagram diagram = (DMNDiagram) parent;
            definitions = diagram.getDefinitions();
        } else {
            definitions = (Definitions) parent;
        }

        final String[] split = drgElement.getId().getValue().split(":");
        final String namespace = getNamespaceForImport(split[0], definitions.getNsContext());
        return namespace + "#" + split[1];
    }

    static String getNamespaceForImport(final String importName, final Map<String, String> nsContext) {
        return nsContext.get(importName);
    }
}
