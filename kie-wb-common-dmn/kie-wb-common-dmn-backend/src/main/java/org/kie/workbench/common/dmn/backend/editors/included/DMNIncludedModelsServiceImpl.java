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

package org.kie.workbench.common.dmn.backend.editors.included;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModelsService;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelper;
import org.kie.workbench.common.dmn.backend.definition.v1_1.ImportedItemDefinitionConverter;
import org.kie.workbench.common.dmn.backend.editors.common.DMNIncludedNodesFilter;
import org.kie.workbench.common.dmn.backend.editors.common.IncludedModelFactory;
import org.kie.workbench.common.dmn.backend.editors.common.PMMLIncludedDocumentsFilter;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.uberfire.backend.vfs.Path;

@Service
public class DMNIncludedModelsServiceImpl implements DMNIncludedModelsService {

    private static Logger LOGGER = Logger.getLogger(DMNIncludedModelsServiceImpl.class.getName());

    private final DMNPathsHelper pathsHelper;

    private final IncludedModelFactory includedModelFactory;

    private final DMNIncludedNodesFilter includedNodesFilter;

    private final PMMLIncludedDocumentsFilter includedDocumentsFilter;

    private final DMNMarshallerImportsHelper importsHelper;

    @Inject
    public DMNIncludedModelsServiceImpl(final DMNPathsHelper pathsHelper,
                                        final IncludedModelFactory includedModelFactory,
                                        final DMNIncludedNodesFilter includedNodesFilter,
                                        final PMMLIncludedDocumentsFilter includedDocumentsFilter,
                                        final DMNMarshallerImportsHelper importsHelper) {
        this.pathsHelper = pathsHelper;
        this.includedModelFactory = includedModelFactory;
        this.includedNodesFilter = includedNodesFilter;
        this.includedDocumentsFilter = includedDocumentsFilter;
        this.importsHelper = importsHelper;
    }

    @Override
    public List<IncludedModel> loadModels(final Path path,
                                          final WorkspaceProject workspaceProject) {
        return getModelsPaths(workspaceProject)
                .stream()
                .map(includedModelPath -> getPathToIncludeModelBiFunction().apply(path,
                                                                                  includedModelPath))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<DMNIncludedNode> loadNodesFromImports(final WorkspaceProject workspaceProject,
                                                      final List<DMNIncludedModel> includedModels) {
        return getDMNModelsPaths(workspaceProject)
                .stream()
                .map(path -> includedNodesFilter.getNodesFromImports(path,
                                                                     includedModels))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<PMMLDocumentMetadata> loadPMMLDocumentsFromImports(final Path path,
                                                                   final WorkspaceProject workspaceProject,
                                                                   final List<PMMLIncludedModel> includedModels) {
        return getPMMLModelsPaths(workspaceProject)
                .stream()
                .map(includedModelPath -> includedDocumentsFilter.getDocumentFromImports(path,
                                                                                         includedModelPath,
                                                                                         includedModels))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDefinition> loadItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                               final String modelName,
                                                               final String namespace) {
        return importsHelper
                .getImportedItemDefinitionsByNamespace(workspaceProject,
                                                       modelName,
                                                       namespace)
                .stream()
                .map(itemDefinition -> wbFromDMN(itemDefinition, modelName))
                .collect(Collectors.toList());
    }

    private BiFunction<Path, Path, IncludedModel> getPathToIncludeModelBiFunction() {
        return (dmnModelPath, includedModelPath) -> {
            try {
                return includedModelFactory.create(dmnModelPath,
                                                   includedModelPath);
            } catch (final DMNIncludeModelCouldNotBeCreatedException e) {
                LOGGER.warning("The 'IncludedModel' could not be created for " + includedModelPath.toURI());
                return null;
            }
        };
    }

    private List<Path> getModelsPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getModelsPaths(workspaceProject);
    }

    private List<Path> getDMNModelsPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getDMNModelsPaths(workspaceProject);
    }

    private List<Path> getPMMLModelsPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getPMMLModelsPaths(workspaceProject);
    }

    ItemDefinition wbFromDMN(final org.kie.dmn.model.api.ItemDefinition itemDefinition,
                             final String modelName) {
        return ImportedItemDefinitionConverter.wbFromDMN(itemDefinition, modelName);
    }
}
