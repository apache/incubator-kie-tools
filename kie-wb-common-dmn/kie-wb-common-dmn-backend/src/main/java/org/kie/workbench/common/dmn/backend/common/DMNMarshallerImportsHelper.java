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

package org.kie.workbench.common.dmn.backend.common;

import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.backend.DMNMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * This helper provides methods to handle imports into the {@link DMNMarshaller}.
 */
public interface DMNMarshallerImportsHelper {

    /**
     * This method loads all imported DMN definitions from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link Definitions} indexed by {@link Import}s.
     */
    Map<Import, Definitions> getImportDefinitions(final Metadata metadata,
                                                  final List<Import> imports);

    /**
     * This method loads all imported PMML documents from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link PMMLDocumentMetadata} indexed by {@link Import}s.
     */
    Map<Import, PMMLDocumentMetadata> getPMMLDocuments(final Metadata metadata,
                                                       final List<Import> imports);

    /**
     * This method loads {@link String} of all imported XML files from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link String} indexed by {@link Import}s.
     */
    Map<Import, String> getImportXML(final Metadata metadata,
                                     final List<Import> imports);

    /**
     * This method extract a list of {@link DRGElement}s from the <code>importDefinitions</code> map.
     * @param importDefinitions is a map of {@link Definitions} indexed by {@link Import}.
     * @return a list of imported {@link DRGElement}s.
     */
    List<DRGElement> getImportedDRGElements(final Map<Import, Definitions> importDefinitions);

    /**
     * This method extract a list of {@link ItemDefinition} from the <code>importDefinitions</code> map.
     * @param importDefinitions is a map of {@link Definitions} indexed by {@link Import}.
     * @return a list of imported {@link ItemDefinition}s.
     */
    List<ItemDefinition> getImportedItemDefinitions(final Map<Import, Definitions> importDefinitions);

    /**
     * This method finds the list of {@link ItemDefinition}s for a given <code>namespace</code>.
     * @param workspaceProject represents the project that will be scanned.
     * @param modelName is the value used as the prefix for imported {@link ItemDefinition}s.
     * @param namespace is the namespace of the model that provides the list of {@link ItemDefinition}s.
     * @return a list of imported {@link ItemDefinition}s.
     */
    List<ItemDefinition> getImportedItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                               final String modelName,
                                                               final String namespace);
}
