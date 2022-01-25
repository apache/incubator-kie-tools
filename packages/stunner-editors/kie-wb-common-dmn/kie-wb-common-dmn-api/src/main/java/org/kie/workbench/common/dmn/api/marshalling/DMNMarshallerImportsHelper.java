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

package org.kie.workbench.common.dmn.api.marshalling;

import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * This helper provides methods to handle imports into the DMNMarshaller.
 */
public interface DMNMarshallerImportsHelper<IMPORT, DEFINITIONS, DRGELEMENT, ITEMDEFINITION> {

    /**
     * This method loads {@link String} of all imported XML files from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link String} indexed by {@link IMPORT}s.
     */
    Map<IMPORT, String> getImportXML(final Metadata metadata,
                                     final List<IMPORT> imports);

    /**
     * This method extract a list of {@link DRGELEMENT}s from the <code>importDefinitions</code> map.
     * @param importDefinitions is a map of {@link DEFINITIONS} indexed by {@link IMPORT}.
     * @return a list of imported {@link DRGELEMENT}s.
     */
    List<DRGELEMENT> getImportedDRGElements(final Map<IMPORT, DEFINITIONS> importDefinitions);

    /**
     * This method extract a list of {@link ITEMDEFINITION} from the <code>importDefinitions</code> map.
     * @param importDefinitions is a map of {@link DEFINITIONS} indexed by {@link IMPORT}.
     * @return a list of imported {@link ITEMDEFINITION}s.
     */
    List<ITEMDEFINITION> getImportedItemDefinitions(final Map<IMPORT, DEFINITIONS> importDefinitions);
}
