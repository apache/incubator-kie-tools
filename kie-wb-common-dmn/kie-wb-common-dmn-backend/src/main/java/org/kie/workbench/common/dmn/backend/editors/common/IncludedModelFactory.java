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

package org.kie.workbench.common.dmn.backend.editors.common;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.marshalling.DMNImportTypesHelper;
import org.kie.workbench.common.dmn.api.marshalling.DMNPathsHelper;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;

@Dependent
public class IncludedModelFactory {

    private static final String DEFAULT_PACKAGE_NAME = "";

    private final DMNPathsHelper pathsHelper;

    private final DMNDiagramHelper diagramHelper;

    private final DMNImportTypesHelper importTypesHelper;

    private final PMMLIncludedDocumentFactory pmmlDocumentFactory;

    private final KieModuleService moduleService;

    @Inject
    public IncludedModelFactory(final DMNDiagramHelper diagramHelper,
                                final DMNPathsHelper pathsHelper,
                                final DMNImportTypesHelper importTypesHelper,
                                final PMMLIncludedDocumentFactory pmmlDocumentFactory,
                                final KieModuleService moduleService) {
        this.diagramHelper = diagramHelper;
        this.pathsHelper = pathsHelper;
        this.importTypesHelper = importTypesHelper;
        this.pmmlDocumentFactory = pmmlDocumentFactory;
        this.moduleService = moduleService;
    }

    public IncludedModel create(final Path dmnModelPath,
                                final Path includedModelPath) throws DMNIncludeModelCouldNotBeCreatedException {
        try {

            if (importTypesHelper.isDMN(includedModelPath)) {
                return makeDMNIncludedModel(dmnModelPath, includedModelPath);
            } else if (importTypesHelper.isPMML(includedModelPath)) {
                return makePMMLIncludedModel(dmnModelPath, includedModelPath);
            } else {
                throw new IllegalArgumentException("Unsupported external model type.");
            }
        } catch (final Exception e) {
            throw new DMNIncludeModelCouldNotBeCreatedException();
        }
    }

    private IncludedModel makeDMNIncludedModel(final Path dmnModelPath,
                                               final Path includedModelPath) {
        final String fileName = includedModelPath.getFileName();
        final String modelPackage = getPackage(includedModelPath);
        final String relativeURI = pathsHelper.getRelativeURI(dmnModelPath, includedModelPath);

        final Diagram<Graph, Metadata> diagram = diagramHelper.getDiagramByPath(includedModelPath);
        final String namespace = diagramHelper.getNamespace(diagram);
        final String importType = DMNImportTypes.DMN.getDefaultNamespace();
        final int drgElementCount = diagramHelper.getNodes(diagram).size();
        final int itemDefinitionCount = diagramHelper.getItemDefinitions(diagram).size();

        return new DMNIncludedModel(fileName,
                                    modelPackage,
                                    relativeURI,
                                    namespace,
                                    importType,
                                    drgElementCount,
                                    itemDefinitionCount);
    }

    private IncludedModel makePMMLIncludedModel(final Path dmnModelPath,
                                                final Path includedModelPath) {
        final String fileName = includedModelPath.getFileName();
        final String modelPackage = getPackage(includedModelPath);
        final String relativeURI = pathsHelper.getRelativeURI(dmnModelPath, includedModelPath);

        final PMMLDocumentMetadata document = pmmlDocumentFactory.getDocumentByPath(includedModelPath);
        final String importType = document.getImportType();
        final int modelCount = document.getModels().size();

        return new PMMLIncludedModel(fileName,
                                     modelPackage,
                                     relativeURI,
                                     importType,
                                     modelCount);
    }

    private String getPackage(final Path path) {
        return Optional
                .ofNullable(moduleService.resolvePackage(path))
                .map(Package::getPackageName)
                .orElse(DEFAULT_PACKAGE_NAME);
    }
}
