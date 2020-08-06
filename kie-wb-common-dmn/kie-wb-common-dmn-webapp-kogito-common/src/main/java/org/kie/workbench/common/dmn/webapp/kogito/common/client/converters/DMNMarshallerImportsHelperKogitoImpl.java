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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.ImportedItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.PMMLMarshallerService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.FileUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.ImportedItemDefinitionPropertyConverter.withNamespace;

@ApplicationScoped
public class DMNMarshallerImportsHelperKogitoImpl implements DMNMarshallerImportsHelperKogito {

    private final KogitoResourceContentService contentService;
    private final DMNClientDiagramServiceImpl diagramService;
    private final Promises promises;
    private final DMNDiagramUtils diagramUtils;
    private final DMNIncludedNodeFactory includedModelFactory;
    private final PMMLMarshallerService pmmlMarshallerService;

    private static final Logger LOGGER = Logger.getLogger(DMNMarshallerImportsHelperKogitoImpl.class.getName());
    private static final String DMN_FILES_PATTERN = "*.dmn";
    static final String PMML_FILES_PATTERN = "*.pmml";
    static final String MODEL_FILES_PATTERN = "*.{dmn,pmml}";

    @Inject
    public DMNMarshallerImportsHelperKogitoImpl(final KogitoResourceContentService contentService,
                                                final DMNClientDiagramServiceImpl diagramService,
                                                final Promises promises,
                                                final DMNDiagramUtils diagramUtils,
                                                final DMNIncludedNodeFactory includedModelFactory,
                                                final PMMLMarshallerService pmmlMarshallerService) {
        this.contentService = contentService;
        this.diagramService = diagramService;
        this.promises = promises;
        this.diagramUtils = diagramUtils;
        this.includedModelFactory = includedModelFactory;
        this.pmmlMarshallerService = pmmlMarshallerService;
    }

    @Override
    public Promise<Map<JSITImport, JSITDefinitions>> getImportDefinitionsAsync(final Metadata metadata,
                                                                               final List<JSITImport> imports) {
        if (!imports.isEmpty()) {
            return loadDMNDefinitions().then(otherDefinitions -> {
                final Map<JSITImport, JSITDefinitions> importDefinitions = new HashMap<>();
                for (final Map.Entry<String, JSITDefinitions> entry : otherDefinitions.entrySet()) {
                    final JSITDefinitions def = Js.uncheckedCast(entry.getValue());
                    findImportByDefinitions(def, imports).ifPresent(anImport -> {
                        final JSITImport foundImported = Js.uncheckedCast(anImport);
                        importDefinitions.put(foundImported, def);
                    });
                }
                return promises.resolve(importDefinitions);
            });
        }
        return promises.resolve(Collections.emptyMap());
    }

    Promise<Map<String, JSITDefinitions>> loadDMNDefinitions() {
        return contentService.getFilteredItems(DMN_FILES_PATTERN, ResourceListOptions.assetFolder())
                .then(list -> {
                    if (list.length == 0) {
                        return promises.resolve(Collections.emptyMap());
                    } else {
                        final Map<String, JSITDefinitions> otherDefinitions = new ConcurrentHashMap<>();
                        return promises.all(Arrays.asList(list),
                                            (String file) -> loadDefinitionFromFile(file, otherDefinitions))
                                .then(v -> promises.resolve(otherDefinitions));
                    }
                });
    }

    @Override
    public void loadNodesFromModels(final List<DMNIncludedModel> includedModels,
                                    final ServiceCallback<List<DMNIncludedNode>> callback) {
        final List<DMNIncludedNode> result = new ArrayList<>();
        if (includedModels.isEmpty()) {
            callback.onSuccess(result);
        } else {
            loadDMNDefinitions()
                    .then(existingDefinitions -> promises.all(includedModels, model -> loadNodes(existingDefinitions, model, result))
                            .then(p ->
                                  {
                                      callback.onSuccess(result);
                                      return promises.resolve();
                                  }));
        }
    }

    private Promise<List<DMNIncludedNode>> loadNodes(final Map<String, JSITDefinitions> existingDefinitions,
                                                     final DMNIncludedModel model,
                                                     final List<DMNIncludedNode> result) {
        String filePath = "";
        for (final Map.Entry<String, JSITDefinitions> entry : existingDefinitions.entrySet()) {
            filePath = entry.getKey();
            final JSITDefinitions definitions = Js.uncheckedCast(entry.getValue());
            if (Objects.equals(model.getNamespace(), definitions.getNamespace())) {
                break;
            }
        }
        final String path = filePath;
        return contentService.loadFile(path)
                .then(content -> promises.create((success, fail) ->
                    diagramService.transform(content, new ServiceCallback<Diagram>() {
                        @Override
                        public void onSuccess(final Diagram item) {
                            final List<DMNIncludedNode> nodes = diagramUtils
                                    .getDRGElements(item)
                                    .stream()
                                    .map(node -> includedModelFactory.makeDMNIncludeNode(path, model, node))
                                    .collect(Collectors.toList());
                            result.addAll(nodes);
                            success.onInvoke(nodes);
                        }

                        @Override
                        public void onError(final ClientRuntimeError error) {
                            LOGGER.log(Level.SEVERE, error.getMessage());
                            fail.onInvoke(error);
                        }
                    })
                ));
    }

    @Override
    public void loadModels(final ServiceCallback<List<IncludedModel>> callback) {
        final List<IncludedModel> models = new ArrayList<>();
        contentService.getFilteredItems(MODEL_FILES_PATTERN, ResourceListOptions.assetFolder())
            .then(items -> promises.all(Arrays.asList(items), file -> {
                final String fileName = FileUtils.getFileName(file);
                if (fileName.endsWith("." + DMNImportTypes.DMN.getFileExtension())) {
                    return contentService.loadFile(file)
                            .then(fileContent -> promises.create((success, failed) ->
                                diagramService.transform(fileContent, getDMNDiagramCallback(fileName, models, success, failed))));
                }
                if (fileName.endsWith("." + DMNImportTypes.PMML.getFileExtension())) {
                    return contentService.loadFile(file)
                        .then(fileContent -> pmmlMarshallerService.getDocumentMetadata(file, fileContent))
                        .then(pmmlDocumentMetadata -> {
                            int modelCount = pmmlDocumentMetadata.getModels() != null ? pmmlDocumentMetadata.getModels().size() : 0;
                            models.add(new PMMLIncludedModel(fileName,
                                                            "",
                                                             fileName,
                                                             DMNImportTypes.PMML.getDefaultNamespace(),
                                                             modelCount));
                            return promises.resolve();
                        });
                }
                return promises.reject("Error: " + fileName + " is an invalid file. Only " + DMN_FILES_PATTERN +
                                               " and " + PMML_FILES_PATTERN + " are supported");
            }).then(v -> {
                callback.onSuccess(models);
                return promises.resolve();
            }));
    }

    private ServiceCallback<Diagram> getDMNDiagramCallback(final String fileName,
                                                           final List<IncludedModel> models,
                                                           final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Object> success,
                                                           final Promise.PromiseExecutorCallbackFn.RejectCallbackFn failed) {
        return new ServiceCallback<Diagram>() {

            @Override
            public void onSuccess(final Diagram diagram) {
                final String modelPackage = "";
                final String namespace = diagramUtils.getNamespace(diagram);
                final String importType = DMNImportTypes.DMN.getDefaultNamespace();
                final int drgElementCount = diagramUtils.getDRGElements(diagram).size();
                final int itemDefinitionCount = diagramUtils.getDefinitions(diagram) != null ?
                        diagramUtils.getDefinitions(diagram).getItemDefinition().size() : 0;
                models.add(new DMNIncludedModel(fileName,
                                                modelPackage,
                                                fileName,
                                                namespace,
                                                importType,
                                                drgElementCount,
                                                itemDefinitionCount));
                success.onInvoke(promises.resolve());
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                LOGGER.log(Level.SEVERE, error.getMessage());
                failed.onInvoke(promises.reject(error.getMessage()));
            }
        };
    }

    Promise<Void> loadDefinitionFromFile(final String file,
                                         final Map<String, JSITDefinitions> otherDefinitions) {
        return contentService.loadFile(file)
                .then(xml -> promises.create((success, failure) -> {
                    if (!StringUtils.isEmpty(xml)) {
                        final ServiceCallback<Object> callback = getCallback(file, otherDefinitions, success);
                        diagramService.getDefinitions(xml, callback);
                    } else {
                        success.onInvoke(promises.resolve());
                    }
                }));
    }

    private ServiceCallback<Object> getCallback(final String filePath,
                                                final Map<String, JSITDefinitions> otherDefinitions,
                                                final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Void> success) {
        return new ServiceCallback<Object>() {
            @Override
            public void onSuccess(final Object item) {
                final JSITDefinitions def = Js.uncheckedCast(item);
                otherDefinitions.put(filePath, def);
                success.onInvoke(promises.resolve());
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                LOGGER.log(Level.SEVERE, error.getMessage());
            }
        };
    }

    private Optional<JSITImport> findImportByDefinitions(final JSITDefinitions definitions,
                                                         final List<JSITImport> imports) {
        for (int i = 0; i < imports.size(); i++) {
            final JSITImport anImport = Js.uncheckedCast(imports.get(i));
            if (Objects.equals(anImport.getNamespace(), definitions.getNamespace())) {
                return Optional.of(anImport);
            }
        }
        return Optional.empty();
    }

    private Optional<JSITImport> findImportByPMMLDocument(final String includedPMMLModelFile,
                                                          final List<JSITImport> imports) {
        for (int i = 0; i < imports.size(); i++) {
            final JSITImport anImport = Js.uncheckedCast(imports.get(i));
            if (Objects.equals(anImport.getLocationURI(), includedPMMLModelFile)) {
                return Optional.of(anImport);
            }
        }
        return Optional.empty();
    }

    @Override
    public Promise<Map<JSITImport, PMMLDocumentMetadata>> getPMMLDocumentsAsync(final Metadata metadata,
                                                                                final List<JSITImport> imports) {
        if (!imports.isEmpty()) {
            return loadPMMLDefinitions().then(otherDefinitions -> {
                final Map<JSITImport, PMMLDocumentMetadata> importDefinitions = new HashMap<>();

                for (final Map.Entry<String, PMMLDocumentMetadata> entry : otherDefinitions.entrySet()) {
                    final PMMLDocumentMetadata def = entry.getValue();
                    findImportByPMMLDocument(def.getName(), imports).ifPresent(anImport -> {
                        final JSITImport foundImported = Js.uncheckedCast(anImport);
                        importDefinitions.put(foundImported, def);
                    });
                }

                return promises.resolve(importDefinitions);
            });
        }
        return promises.resolve(Collections.emptyMap());
    }

    private Promise<Map<String, PMMLDocumentMetadata>> loadPMMLDefinitions() {
        return contentService.getFilteredItems(PMML_FILES_PATTERN, ResourceListOptions.assetFolder()).
                then(files -> {
                    if (files.length == 0) {
                        return promises.resolve(Collections.emptyMap());
                    } else {
                        final Map<String, PMMLDocumentMetadata> definitions = new HashMap<>();
                        return promises.all(Arrays.asList(files), file -> loadPMMLDefinitionFromFile(file, definitions)
                                .then(v -> promises.resolve(definitions)));
                    }
                });
    }

    private Promise<Void> loadPMMLDefinitionFromFile(final String file,
                                                     final Map<String, PMMLDocumentMetadata> definitions) {
        return contentService.loadFile(file)
                .then(fileContent -> pmmlMarshallerService.getDocumentMetadata(file, fileContent))
                .then(pmmlDocumentMetadata -> {
                    definitions.put(file, pmmlDocumentMetadata);
                    return promises.resolve();
                });
    }

    @Override
    public Map<JSITImport, String> getImportXML(final Metadata metadata,
                                                final List<JSITImport> imports) {
        return Collections.emptyMap();
    }

    @Override
    public List<JSITDRGElement> getImportedDRGElements(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        final List<JSITDRGElement> importedNodes = new ArrayList<>();
        for (final Map.Entry<JSITImport, JSITDefinitions> entry : importDefinitions.entrySet()) {
            final JSITImport anImport = Js.uncheckedCast(entry.getKey());
            final JSITDefinitions definitions = Js.uncheckedCast(entry.getValue());
            importedNodes.addAll(getDrgElementsWithNamespace(definitions, anImport));
        }
        return importedNodes;
    }

    private List<JSITDRGElement> getDrgElementsWithNamespace(final JSITDefinitions definitions,
                                                             final JSITImport anImport) {
        final List<JSITDRGElement> result = new ArrayList<>();
        final List<JSITDRGElement> drgElements = definitions.getDrgElement();
        for (int i = 0; i < drgElements.size(); i++) {
            final JSITDRGElement drgElement = Js.uncheckedCast(drgElements.get(i));
            final JSITDRGElement element = Js.uncheckedCast(drgElementWithNamespace(drgElement, anImport));
            result.add(element);
        }
        return result;
    }

    private JSITDRGElement drgElementWithNamespace(final JSITDRGElement drgElement,
                                                   final JSITImport anImport) {
        final String namespace = anImport.getName();
        final QName qname = QName.valueOf("Namespace");
        final Map<QName, String> map = JSITDMNElement.getOtherAttributesMap(drgElement);
        map.put(qname, anImport.getNamespace());
        drgElement.setOtherAttributes(map);
        drgElement.setName(namespace + "." + drgElement.getName());
        updateInformationItem(namespace, drgElement);

        return drgElement;
    }

    private void updateInformationItem(final String namespace,
                                       final JSITDRGElement drgElement) {

        getInformationItem(drgElement).ifPresent(informationItem -> {

            final JSITInformationItem tInformationItem = new JSITInformationItem();
            final String typeRef = informationItem.getTypeRef();

            if (!StringUtils.isEmpty(typeRef) && !isBuiltInType(typeRef)) {
                tInformationItem.setTypeRef(namespace + "." + typeRef);
                setInformationItem(drgElement, tInformationItem);
            }
        });
    }

    private void setInformationItem(final JSITDRGElement drgElement,
                                    final JSITInformationItem informationItem) {
        if (JSITDecision.instanceOf(drgElement)) {
            final JSITDecision decision = Js.uncheckedCast(drgElement);
            decision.setVariable(informationItem);
        } else if (JSITInputData.instanceOf(drgElement)) {
            final JSITInputData inputData = Js.uncheckedCast(drgElement);
            inputData.setVariable(informationItem);
        } else if (JSITInvocable.instanceOf(drgElement)) {
            final JSITInvocable invocable = Js.uncheckedCast(drgElement);
            invocable.setVariable(informationItem);
        }
    }

    private Optional<JSITInformationItem> getInformationItem(final JSITDRGElement drgElement) {
        final JSITInformationItem variable;
        if (JSITDecision.instanceOf(drgElement)) {
            final JSITDecision decision = Js.uncheckedCast(drgElement);
            variable = Js.uncheckedCast(decision.getVariable());
        } else if (JSITInputData.instanceOf(drgElement)) {
            final JSITInputData inputData = Js.uncheckedCast(drgElement);
            variable = Js.uncheckedCast(inputData.getVariable());
        } else if (JSITInvocable.instanceOf(drgElement)) {
            final JSITInvocable invocable = Js.uncheckedCast(drgElement);
            variable = Js.uncheckedCast(invocable.getVariable());
        } else {
            return Optional.empty();
        }
        return Optional.of(variable);
    }

    @Override
    public List<JSITItemDefinition> getImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        final List<JSITItemDefinition> itemDefinitions = new ArrayList<>();
        for (final Map.Entry<JSITImport, JSITDefinitions> entry : importDefinitions.entrySet()) {
            final JSITImport anImport = Js.uncheckedCast(entry.getKey());
            final JSITDefinitions definitions = Js.uncheckedCast(entry.getValue());
            final List<JSITItemDefinition> items = getItemDefinitionsWithNamespace(definitions, anImport);
            itemDefinitions.addAll(items);
        }

        return itemDefinitions;
    }

    List<JSITItemDefinition> getItemDefinitionsWithNamespace(final JSITDefinitions definitions,
                                                             final JSITImport anImport) {

        final List<JSITItemDefinition> itemDefinitions = definitions.getItemDefinition();
        final String prefix = anImport.getName();
        final List<JSITItemDefinition> result = new ArrayList<>();

        for (int i = 0; i < itemDefinitions.size(); i++) {
            final JSITItemDefinition itemDefinition = Js.uncheckedCast(itemDefinitions.get(i));
            final JSITItemDefinition item = Js.uncheckedCast(withNamespace(itemDefinition, prefix));
            result.add(item);
        }
        return result;
    }

    @Override
    public void getImportedItemDefinitionsByNamespaceAsync(final String modelName,
                                                           final String namespace,
                                                           final ServiceCallback<List<ItemDefinition>> callback) {
        loadDMNDefinitions().then(definitions -> {
            final List<ItemDefinition> result = new ArrayList<>();
            for (final Map.Entry<String, JSITDefinitions> entry : definitions.entrySet()) {
                final JSITDefinitions definition = Js.uncheckedCast(entry.getValue());
                if (Objects.equals(definition.getNamespace(), namespace)) {
                    final List<JSITItemDefinition> items = definition.getItemDefinition();
                    for (int j = 0; j < items.size(); j++) {
                        final JSITItemDefinition jsitItemDefinition = Js.uncheckedCast(items.get(j));
                        final ItemDefinition converted = ImportedItemDefinitionPropertyConverter.wbFromDMN(jsitItemDefinition, modelName);
                        result.add(converted);
                    }
                }
            }
            result.sort(Comparator.comparing(o -> o.getName().getValue()));
            callback.onSuccess(result);
            return promises.resolve(result);
        });
    }
}
