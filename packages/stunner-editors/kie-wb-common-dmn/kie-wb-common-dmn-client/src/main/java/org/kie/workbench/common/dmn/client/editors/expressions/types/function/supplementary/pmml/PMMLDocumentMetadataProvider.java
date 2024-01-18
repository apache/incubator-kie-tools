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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.events.IncludedPMMLModelUpdate;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;

@ApplicationScoped
public class PMMLDocumentMetadataProvider {

    private DMNGraphUtils graphUtils;
    private DMNClientServicesProxy clientServicesProxy;
    private IncludedModelsPageStateProviderImpl stateProvider;

    private Map<String, PMMLDocumentMetadata> pmmlDocuments = new HashMap<>();

    public PMMLDocumentMetadataProvider() {
        //CDI proxy
    }

    @Inject
    public PMMLDocumentMetadataProvider(final DMNGraphUtils graphUtils,
                                        final DMNClientServicesProxy clientServicesProxy,
                                        final IncludedModelsPageStateProviderImpl stateProvider) {
        this.graphUtils = graphUtils;
        this.clientServicesProxy = clientServicesProxy;
        this.stateProvider = stateProvider;
    }

    @PostConstruct
    public void loadPMMLIncludedDocuments() {

        final Optional<Diagram> diagram = stateProvider.getDiagram();

        if (!diagram.isPresent()) {
            return;
        }

        pmmlDocuments.clear();
        clientServicesProxy.loadPMMLDocumentsFromImports(getDMNModelPath(diagram.get()),
                                                         getPMMLIncludedModels(diagram.get()),
                                                         new ServiceCallback<List<PMMLDocumentMetadata>>() {
                                                             @Override
                                                             public void onSuccess(final List<PMMLDocumentMetadata> documents) {
                                                                 documents.forEach(document -> pmmlDocuments.put(document.getName(),
                                                                                                                 document));
                                                             }

                                                             @Override
                                                             public void onError(final ClientRuntimeError error) {
                                                                 clientServicesProxy.logWarning(error);
                                                             }
                                                         });
    }

    @SuppressWarnings("unused")
    public void onRefreshDecisionComponents(final @Observes @IncludedPMMLModelUpdate RefreshDecisionComponents events) {
        loadPMMLIncludedDocuments();
    }

    private Path getDMNModelPath(final Diagram diagram) {
        return diagram.getMetadata().getPath();
    }

    private List<PMMLIncludedModel> getPMMLIncludedModels(final Diagram diagram) {
        return graphUtils
                .getDefinitions(diagram)
                .getImport()
                .stream()
                .filter(anImport -> Objects.equals(DMNImportTypes.PMML, determineImportType(anImport.getImportType())))
                .map(this::asPMMLIncludedModel)
                .collect(Collectors.toList());
    }

    private PMMLIncludedModel asPMMLIncludedModel(final Import anImport) {
        final String modelName = anImport.getName().getValue();
        final String importType = anImport.getImportType();
        final String path = anImport.getLocationURI().getValue();
        return new PMMLIncludedModel(modelName, "", path, importType, anImport.getNamespace(), 0);
    }

    public List<String> getPMMLDocumentNames() {
        final List<String> pmmlDocumentNames = new ArrayList<>(pmmlDocuments.keySet());
        pmmlDocumentNames.sort(Comparator.naturalOrder());
        return pmmlDocumentNames;
    }

    public List<String> getPMMLDocumentModels(final String pmmlDocumentName) {
        final List<String> pmmlDocumentModelNames = new ArrayList<>();
        if (pmmlDocuments.containsKey(pmmlDocumentName)) {
            final PMMLDocumentMetadata document = pmmlDocuments.get(pmmlDocumentName);
            document.getModels().forEach(pmmlDocumentModel -> pmmlDocumentModelNames.add(pmmlDocumentModel.getName()));
            pmmlDocumentModelNames.sort(Comparator.naturalOrder());
        }
        return pmmlDocumentModelNames;
    }

    public List<String> getPMMLDocumentModelParameterNames(final String pmmlDocumentName,
                                                           final String pmmlDocumentModelName) {
        final List<String> pmmlDocumentModelParameterNames = new ArrayList<>();
        if (pmmlDocuments.containsKey(pmmlDocumentName)) {
            final PMMLDocumentMetadata document = pmmlDocuments.get(pmmlDocumentName);
            document.getModels().stream().filter(model -> Objects.equals(pmmlDocumentModelName, model.getName()))
                    .findFirst()
                    .map(PMMLModelMetadata::getInputParameters)
                    .orElse(Collections.emptySet())
                    .forEach(parameter -> pmmlDocumentModelParameterNames.add(parameter.getName()));
            pmmlDocumentModelParameterNames.sort(Comparator.naturalOrder());
        }
        return pmmlDocumentModelParameterNames;
    }
}
