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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;

public class HrefBuilder {

    public static String getHref(final DRGElement drgElement) {

        final String drgElementId = drgElement.getId().getValue();

        return getNamespace(drgElement)
                .map(namespace -> namespace + "#" + drgElementId)
                .orElse("#" + drgElementId);
    }

    private static Optional<String> getNamespace(final DRGElement drgElement) {
        final Optional<String> name = Optional.ofNullable(drgElement.getName().getValue());
        return getDefinitions(drgElement)
                .map(definitions -> definitions
                        .getImport()
                        .stream()
                        .filter(anImport -> {
                            final String importName = anImport.getName().getValue();
                            return name.map(n -> n.startsWith(importName + ".")).orElse(false);
                        })
                        .findFirst()
                        .map(Import::getNamespace)
                        .orElse(null));
    }

    private static Optional<Definitions> getDefinitions(final DRGElement drgElement) {

        final DMNModelInstrumentedBase parent = drgElement.getParent();

        if (parent instanceof DMNDiagram) {
            final DMNDiagram diagram = (DMNDiagram) parent;
            return Optional.ofNullable(diagram.getDefinitions());
        }

        if (parent instanceof Definitions) {
            return Optional.of((Definitions) parent);
        }

        return Optional.empty();
    }
}
