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

package org.kie.workbench.common.dmn.client.marshaller.included;

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

import javax.inject.Inject;
import javax.xml.namespace.QName;

import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.marshalling.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.client.marshaller.converters.ImportedItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.FileUtils;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;
import static org.kie.workbench.common.dmn.client.marshaller.converters.ImportedItemDefinitionPropertyConverter.withNamespace;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class DMNMarshallerImportsClientHelper implements DMNMarshallerImportsHelper<JSITImport, JSITDefinitions, JSITDRGElement, JSITItemDefinition> {

    private final DMNMarshallerImportsService dmnImportsService;
    private final DMNMarshallerImportsContentService dmnImportsContentService;
    private final Promises promises;
    private final DMNIncludedNodeFactory includedModelFactory;

    private static final Logger LOGGER = Logger.getLogger(DMNMarshallerImportsClientHelper.class.getName());

    @Inject
    public DMNMarshallerImportsClientHelper(final DMNMarshallerImportsService dmnImportsService,
                                            final DMNMarshallerImportsContentService dmnImportsContentService,
                                            final Promises promises,
                                            final DMNIncludedNodeFactory includedModelFactory) {
        this.dmnImportsService = dmnImportsService;
        this.dmnImportsContentService = dmnImportsContentService;
        this.promises = promises;
        this.includedModelFactory = includedModelFactory;
    }

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
            }).catch_(error -> {
                LOGGER.severe(error::toString);
                return promises.reject(error);
            });
        }
        return promises.resolve(Collections.emptyMap());
    }

    private Promise<Map<String, JSITDefinitions>> loadDMNDefinitions() {
        return dmnImportsContentService.getModelsDMNFilesURIs()
                .then(list -> {
                    if (list.length == 0) {
                        return promises.resolve(Collections.emptyMap());
                    } else {
                        final Map<String, JSITDefinitions> otherDefinitions = new ConcurrentHashMap<>();
                        return promises.all(Arrays.asList(list),
                                        (String file) -> loadDefinitionFromFile(file, otherDefinitions))
                                .then(v -> promises.resolve(otherDefinitions))
                                .catch_(error -> {
                                    LOGGER.severe(error::toString);
                                    return promises.reject(error);
                                });
                    }
                }).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }

    public void loadNodesFromModels(final List<DMNIncludedModel> includedModels,
                                    final ServiceCallback<List<DMNIncludedNode>> callback) {
        final List<DMNIncludedNode> result = new ArrayList<>();
        if (includedModels.isEmpty()) {
            callback.onSuccess(result);
        } else {
            loadDMNDefinitions()
                    .then(existingDefinitions -> promises.all(includedModels, model -> loadNodes(existingDefinitions, model, result))
                            .then(p -> {
                                callback.onSuccess(result);
                                return promises.resolve();
                            }).catch_(error -> {
                                LOGGER.severe(error::toString);
                                return promises.reject(error);
                            })
                    ).catch_(error -> {
                        LOGGER.severe(error::toString);
                        return promises.reject(error);
                    });
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

        if (isEmpty(filePath)) {
            return promises.resolve();
        }

        final String path = filePath;
        return dmnImportsContentService.loadFile(path)
                .then(content -> promises.create((success, fail) ->
                        dmnImportsService.getDRGElements(content, new ServiceCallback<List<DRGElement>>() {
                            @Override
                            public void onSuccess(final List<DRGElement> drgElements) {
                                final List<DMNIncludedNode> nodes = drgElements
                                        .stream()
                                        .map(node -> includedModelFactory.makeDMNIncludeNode(path, model, node))
                                        .collect(Collectors.toList());
                                result.addAll(nodes);
                                success.onInvoke(nodes);
                            }

                            @Override
                            public void onError(final ClientRuntimeError error) {
                                LOGGER.severe(error::getErrorMessage);
                                fail.onInvoke(error);
                            }
                        })
                )).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }

    public void loadModels(final ServiceCallback<List<IncludedModel>> callback) {
        final List<IncludedModel> models = new ArrayList<>();
        dmnImportsContentService.getModelsURIs()
                .then(items -> promises.all(Arrays.asList(items), file -> {
                    final String fileName = FileUtils.getFileName(file);
                    if (fileName.endsWith("." + DMNImportTypes.DMN.getFileExtension())) {
                        return dmnImportsContentService.loadFile(file)
                                .then(fileContent -> promises.create((success, failed) -> dmnImportsService.getWbDefinitions(fileContent, new ServiceCallback<Definitions>() {
                                    @Override
                                    public void onSuccess(final Definitions definitions) {
                                        final String modelPackage = "";
                                        final String namespace = definitions.getNamespace().getValue();
                                        final String importType = DMNImportTypes.DMN.getDefaultNamespace();
                                        final int drgElementCount = definitions.getDrgElement().size();
                                        final int itemDefinitionCount = definitions.getItemDefinition().size();
                                        models.add(new DMNIncludedModel(fileName,
                                                modelPackage,
                                                file,
                                                namespace,
                                                importType,
                                                drgElementCount,
                                                itemDefinitionCount));
                                        success.onInvoke(promises.resolve());
                                    }

                                    @Override
                                    public void onError(final ClientRuntimeError error) {
                                        LOGGER.warning(error::getErrorMessage);
                                        //Swallow. Since it must try to load other paths.
                                        success.onInvoke(promises.resolve());
                                    }
                                }))).catch_(error -> {
                                    LOGGER.severe(error::toString);
                                    return promises.reject(error);
                                });
                    }
                    if (fileName.endsWith("." + DMNImportTypes.PMML.getFileExtension())) {
                        return dmnImportsContentService.getPMMLDocumentMetadata(file)
                                .then(pmmlDocumentMetadata -> {
                                    int modelCount = pmmlDocumentMetadata.getModels() != null ? pmmlDocumentMetadata.getModels().size() : 0;
                                    models.add(new PMMLIncludedModel(fileName,
                                            "",
                                            file,
                                            DMNImportTypes.PMML.getDefaultNamespace(),
                                            "https://kie.org/pmml#" + (file.startsWith("./") ? file.substring(2) : file),
                                            modelCount));
                                    return promises.resolve();
                                }).catch_(error -> {
                                    LOGGER.severe(error::toString);
                                    return promises.reject(error);
                                });
                    }
                    return promises.reject("Error: " + fileName + " is an invalid file. Only *.dmn and *.pmml are supported");
                }).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                }).then(v -> {
                    callback.onSuccess(models);
                    return promises.resolve();
                })).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }

    private Promise<Void> loadDefinitionFromFile(final String file,
                                                 final Map<String, JSITDefinitions> otherDefinitions) {
        return dmnImportsContentService.loadFile(file)
                .then(xml -> promises.create((success, failure) -> {
                    if (!isEmpty(xml)) {
                        final ServiceCallback<JSITDefinitions> callback = Js.uncheckedCast(getCallback(file, otherDefinitions, success));
                        dmnImportsService.getDMNDefinitions(xml, callback);
                    } else {
                        success.onInvoke(promises.resolve());
                    }
                })).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }

    private ServiceCallback<Object> getCallback(final String filePath,
                                                final Map<String, JSITDefinitions> otherDefinitions,
                                                final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Object> success) {
        return new ServiceCallback<Object>() {
            @Override
            public void onSuccess(final Object item) {
                final JSITDefinitions def = Js.uncheckedCast(item);
                otherDefinitions.put(filePath, def);
                success.onInvoke(promises.resolve());
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                LOGGER.log(Level.SEVERE, error.getErrorMessage());
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

    public Promise<Map<JSITImport, PMMLDocumentMetadata>> getPMMLDocumentsAsync(final Metadata metadata,
                                                                                final List<JSITImport> imports) {
        if (!imports.isEmpty()) {
            return loadPMMLDefinitions()
                    .then(otherDefinitions -> {
                        final Map<JSITImport, PMMLDocumentMetadata> importDefinitions = new HashMap<>();

                        for (final Map.Entry<String, PMMLDocumentMetadata> entry : otherDefinitions.entrySet()) {
                            final PMMLDocumentMetadata def = entry.getValue();
                            findImportByPMMLDocument(def.getPath(), imports).ifPresent(anImport -> {
                                final JSITImport foundImported = Js.uncheckedCast(anImport);
                                importDefinitions.put(foundImported, def);
                            });
                        }

                        return promises.resolve(importDefinitions);
                    }).catch_(error -> {
                        LOGGER.severe(error::toString);
                        return promises.reject(error);
                    });
        }
        return promises.resolve(Collections.emptyMap());
    }

    private Promise<Map<String, PMMLDocumentMetadata>> loadPMMLDefinitions() {
        return dmnImportsContentService.getModelsPMMLFilesURIs().
                then(files -> {
                    if (files.length == 0) {
                        return promises.resolve(Collections.emptyMap());
                    } else {
                        final Map<String, PMMLDocumentMetadata> definitions = new HashMap<>();
                        return promises.all(Arrays.asList(files), file -> loadPMMLDefinitionFromFile(file, definitions)
                                        .then(v -> promises.resolve(definitions)))
                                .catch_(error -> {
                                    LOGGER.severe(error::toString);
                                    return promises.reject(error);
                                });
                    }
                }).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }

    private Promise<Void> loadPMMLDefinitionFromFile(final String file,
                                                     final Map<String, PMMLDocumentMetadata> definitions) {
        return dmnImportsContentService.getPMMLDocumentMetadata(file)
                .then(pmmlDocumentMetadata -> {
                    definitions.put(file, pmmlDocumentMetadata);
                    return promises.resolve();
                }).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
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

            final JSITInformationItem tInformationItem = JSITInformationItem.newInstance();
            final String typeRef = informationItem.getTypeRef();

            if (!isEmpty(typeRef) && !isBuiltInType(typeRef)) {
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

    private List<JSITItemDefinition> getItemDefinitionsWithNamespace(final JSITDefinitions definitions,
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

    public void getPMMLDocumentsMetadataFromFiles(final List<PMMLIncludedModel> includedModels,
                                                  final ServiceCallback<List<PMMLDocumentMetadata>> callback) {
        if (includedModels == null || includedModels.isEmpty()) {
            callback.onSuccess(Collections.emptyList());
            return;
        }
        loadPMMLDefinitions()
                .then(allDefinitions -> {
                    final Map<String, String> filesToNameMap = includedModels.stream().collect(Collectors.toMap(PMMLIncludedModel::getPath,
                            PMMLIncludedModel::getModelName));
                    final List<PMMLDocumentMetadata> pmmlDocumentMetadata = allDefinitions.entrySet().stream()
                            .filter(entry -> filesToNameMap.keySet().contains(entry.getKey()))
                            .map(entry -> new PMMLDocumentMetadata(entry.getValue().getPath(),
                                    filesToNameMap.get(entry.getKey()),
                                    entry.getValue().getImportType(),
                                    entry.getValue().getModels()))
                            .collect(Collectors.toList());
                    pmmlDocumentMetadata.sort(Comparator.comparing(PMMLDocumentMetadata::getName));
                    callback.onSuccess(pmmlDocumentMetadata);
                    return promises.resolve();
                }).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }

    public void getImportedItemDefinitionsByNamespaceAsync(final String modelName,
                                                           final String namespace,
                                                           final ServiceCallback<List<ItemDefinition>> callback) {
        loadDMNDefinitions()
                .then(definitions -> {
                    final List<ItemDefinition> result = new ArrayList<>();
                    for (final Map.Entry<String, JSITDefinitions> entry : definitions.entrySet()) {
                        final JSITDefinitions definition = Js.uncheckedCast(entry.getValue());
                        if (Objects.equals(definition.getNamespace(), namespace)) {
                            final List<JSITItemDefinition> items = definition.getItemDefinition();
                            for (int j = 0; j < items.size(); j++) {
                                final JSITItemDefinition jsitItemDefinition = Js.uncheckedCast(items.get(j));
                                final ItemDefinition converted = ImportedItemDefinitionPropertyConverter.wbFromDMN(jsitItemDefinition,
                                        modelName);
                                result.add(converted);
                            }
                        }
                    }
                    result.sort(Comparator.comparing(o -> o.getName().getValue()));
                    callback.onSuccess(result);
                    return promises.resolve(result);
                }).catch_(error -> {
                    LOGGER.severe(error::toString);
                    return promises.reject(error);
                });
    }
}
