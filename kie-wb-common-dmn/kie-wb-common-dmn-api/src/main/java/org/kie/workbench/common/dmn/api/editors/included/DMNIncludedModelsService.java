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

package org.kie.workbench.common.dmn.api.editors.included;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;

/**
 * This service handles calls related to included DMN models.
 */
@Remote
public interface DMNIncludedModelsService {

    /**
     * This method loads all DMN models from a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all {@link DMNIncludedModel}s from a given project.
     */
    List<DMNIncludedModel> loadModels(final WorkspaceProject workspaceProject);

    /**
     * This method loads all nodes from an included model.
     * @param workspaceProject represents the project that will be scanned.
     * @param includedModels represents all imports that provide the list of nodes.
     * @return a list of {@link DMNIncludedNode}s.
     */
    List<DMNIncludedNode> loadNodesFromImports(final WorkspaceProject workspaceProject,
                                               final List<DMNIncludedModel> includedModels);

    /**
     * This method finds the list of {@link ItemDefinition}s for a given <code>namespace</code>.
     * @param workspaceProject represents the project that will be scanned.
     * @param modelName is the value used as the prefix for imported {@link ItemDefinition}s.
     * @param namespace is the namespace of the model that provides the list of {@link ItemDefinition}s.
     * @return a list of {@link ItemDefinition}s.
     */
    List<ItemDefinition> loadItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                        final String modelName,
                                                        final String namespace);
}
