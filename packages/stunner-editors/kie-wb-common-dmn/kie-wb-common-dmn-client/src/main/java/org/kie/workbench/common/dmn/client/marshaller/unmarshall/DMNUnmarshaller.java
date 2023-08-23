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
package org.kie.workbench.common.dmn.client.marshaller.unmarshall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSessionState;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNDiagramElementsUtils;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.marshaller.converters.DefinitionsConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.ItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class DMNUnmarshaller {

    private final FactoryManager factoryManager;

    private final DMNMarshallerImportsClientHelper dmnMarshallerImportsHelper;

    private final Promises promises;

    private final NodeEntriesFactory modelToStunnerConverter;

    private final DMNDiagramElementsUtils dmnDiagramElementsUtils;

    private final DMNDiagramsSession dmnDiagramsSession;

    public DMNUnmarshaller() {
        this(null, null, null, null, null, null);
    }

    @Inject
    public DMNUnmarshaller(final FactoryManager factoryManager,
                           final DMNMarshallerImportsClientHelper dmnMarshallerImportsHelper,
                           final Promises promises,
                           final NodeEntriesFactory modelToStunnerConverter,
                           final DMNDiagramElementsUtils dmnDiagramElementsUtils,
                           final DMNDiagramsSession dmnDiagramsSession) {
        this.factoryManager = factoryManager;
        this.dmnMarshallerImportsHelper = dmnMarshallerImportsHelper;
        this.promises = promises;
        this.modelToStunnerConverter = modelToStunnerConverter;
        this.dmnDiagramElementsUtils = dmnDiagramElementsUtils;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    public Promise<Graph> unmarshall(final Metadata metadata,
                                     final JSITDefinitions jsiDefinitions) {

        return getImportDefinitions(metadata, jsiDefinitions)
                .then(importDefinitions -> getPMMLDocuments(metadata, jsiDefinitions)
                        .then(pmmlDocumentMetadata -> unmarshall(metadata,
                                                                 jsiDefinitions,
                                                                 importDefinitions,
                                                                 pmmlDocumentMetadata)));
    }

    Promise<Map<JSITImport, JSITDefinitions>> getImportDefinitions(final Metadata metadata,
                                                                   final JSITDefinitions jsiDefinitions) {
        final List<JSITImport> imports = jsiDefinitions.getImport();
        return dmnMarshallerImportsHelper.getImportDefinitionsAsync(metadata, imports);
    }

    Promise<Map<JSITImport, PMMLDocumentMetadata>> getPMMLDocuments(final Metadata metadata, final JSITDefinitions jsiDefinitions) {
        final List<JSITImport> imports = jsiDefinitions.getImport();
        return dmnMarshallerImportsHelper.getPMMLDocumentsAsync(metadata, imports);
    }

    private Promise<Graph> unmarshall(final Metadata metadata,
                                      final JSITDefinitions dmnDefinitions,
                                      final Map<JSITImport, JSITDefinitions> importDefinitions,
                                      final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments) {

        final Map<String, HasComponentWidths> hasComponentWidthsMap = new HashMap<>();
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {
            if (Objects.nonNull(uuid)) {
                hasComponentWidthsMap.put(uuid, hcw);
            }
        };
        final boolean isDMNDIPresent = Optional.ofNullable(dmnDefinitions.getDMNDI()).isPresent(); // Check before the DRG creation ('ensureDRGElementExists').

        ensureDRGElementExists(dmnDefinitions);

        final Definitions wbDefinitions = DefinitionsConverter.wbFromDMN(dmnDefinitions, importDefinitions, pmmlDocuments);
        final List<NodeEntry> nodeEntries = modelToStunnerConverter.makeNodes(dmnDefinitions, importDefinitions, isDMNDIPresent, hasComponentWidthsConsumer);
        final List<JSITDecisionService> dmnDecisionServices = getDecisionServices(nodeEntries);

        //Ensure all locations are updated to relative for Stunner
        nodeEntries.forEach(e -> PointUtils.convertToRelativeBounds(e.getNode()));

        final Map<String, Diagram> stunnerDiagramsById = new HashMap<>();
        final Map<String, DMNDiagramElement> dmnDiagramsById = new HashMap<>();

        for (final DMNDiagramElement dmnDiagramElement : wbDefinitions.getDiagramElements()) {
            final String dmnDiagramId = dmnDiagramElement.getId().getValue();
            final Diagram value = factoryManager.newDiagram(dmnDiagramId,
                                                            BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                            metadata);
            stunnerDiagramsById.put(dmnDiagramId, value);
            dmnDiagramsById.put(dmnDiagramId, dmnDiagramElement);
        }

        final DMNDiagramsSessionState state = dmnDiagramsSession.setState(metadata, stunnerDiagramsById, dmnDiagramsById);

        nodeEntries.forEach(nodeEntry -> {
            final String diagramId = nodeEntry.getDiagramId();
            final Graph graph = stunnerDiagramsById.get(diagramId).getGraph();
            graph.addNode(nodeEntry.getNode());
        });

        final Graph drgGraph = state.getDRGDiagram().getGraph();
        loadImportedItemDefinitions(wbDefinitions, importDefinitions);

        for (final Diagram value : stunnerDiagramsById.values()) {
            final Node<?, ?> dmnDiagramRoot = DMNGraphUtils.findDMNDiagramRoot(value.getGraph());
            ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition().setDefinitions(wbDefinitions);

            nodeEntries.forEach(nodeEntry -> {
                if (Objects.equals(stunnerDiagramsById.get(nodeEntry.getDiagramId()), value)) {
                    connectRootWithChild(dmnDiagramRoot, nodeEntry.getNode());
                }
            });
        }

        //Only connect Nodes to the Diagram that are not referenced by DecisionServices
        final List<String> references = new ArrayList<>();
        final List<JSITDecisionService> lstDecisionServices = new ArrayList<>(dmnDecisionServices);
        for (int iDS = 0; iDS < lstDecisionServices.size(); iDS++) {
            final JSITDecisionService jsiDecisionService = Js.uncheckedCast(lstDecisionServices.get(iDS));
            final List<JSITDMNElementReference> jsiEncapsulatedDecisions = jsiDecisionService.getEncapsulatedDecision();
            if (Objects.nonNull(jsiEncapsulatedDecisions)) {
                for (int i = 0; i < jsiEncapsulatedDecisions.size(); i++) {
                    final JSITDMNElementReference jsiEncapsulatedDecision = Js.uncheckedCast(jsiEncapsulatedDecisions.get(i));
                    references.add(jsiEncapsulatedDecision.getHref());
                }
            }

            final List<JSITDMNElementReference> jsiOutputDecisions = jsiDecisionService.getOutputDecision();
            if (Objects.nonNull(jsiOutputDecisions)) {
                for (int i = 0; i < jsiOutputDecisions.size(); i++) {
                    final JSITDMNElementReference jsiOutputDecision = Js.uncheckedCast(jsiOutputDecisions.get(i));
                    references.add(jsiOutputDecision.getHref());
                }
            }
        }

        //Copy ComponentWidths information
        final List<JSITComponentsWidthsExtension> extensions = findComponentsWidthsExtensions(dmnDefinitions.getDMNDI().getDMNDiagram());
        extensions.forEach(componentsWidthsExtension -> {
            //This condition is required because a node with ComponentsWidthsExtension
            //can be imported from another diagram but the extension is not imported or present in this diagram.
            if (Objects.nonNull(componentsWidthsExtension.getComponentWidths())) {
                hasComponentWidthsMap.entrySet().forEach(es -> {
                    final List<JSITComponentWidths> jsiComponentWidths = componentsWidthsExtension.getComponentWidths();
                    for (int i = 0; i < jsiComponentWidths.size(); i++) {
                        final JSITComponentWidths jsiWidths = Js.uncheckedCast(jsiComponentWidths.get(i));
                        if (Objects.equals(jsiWidths.getDmnElementRef(), es.getKey())) {
                            final List<Double> widths = es.getValue().getComponentWidths();
                            if (Objects.nonNull(jsiWidths.getWidth())) {
                                widths.clear();
                                for (int w = 0; w < jsiWidths.getWidth().size(); w++) {
                                    final double width = jsiWidths.getWidth().get(w).doubleValue();
                                    widths.add(width);
                                }
                            }
                        }
                    }
                });
            }
        });

        return promises.resolve(drgGraph);
    }

    private void ensureDRGElementExists(final JSITDefinitions dmnDefinitions) {
        dmnDiagramElementsUtils.ensureDRGElementExists(dmnDefinitions);
    }

    private List<JSITDecisionService> getDecisionServices(final List<NodeEntry> nodeEntries) {
        return nodeEntries.stream()
                .filter(nodeEntry -> JSITDecisionService.instanceOf(nodeEntry.getDmnElement()))
                .map(nodeEntry -> {
                    final JSITDecisionService jsitDecisionService = Js.uncheckedCast(nodeEntry.getDmnElement());
                    return jsitDecisionService;
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectRootWithChild(final Node dmnDiagramRoot,
                                      final Node child) {
        final String uuid = UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge, dmnDiagramRoot, child);
        final Definitions definitions = ((DMNDiagram) ((View) dmnDiagramRoot.getContent()).getDefinition()).getDefinitions();
        final DMNModelInstrumentedBase childDRG = (DMNModelInstrumentedBase) ((View) child.getContent()).getDefinition();
        childDRG.setParent(definitions);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectEdge(final Edge edge,
                             final Node source,
                             final Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }

    private List<JSITComponentsWidthsExtension> findComponentsWidthsExtensions(final List<JSIDMNDiagram> dmnDDDiagrams) {

        final List<JSITComponentsWidthsExtension> componentsWidthsExtensions = new ArrayList<>();

        for (int index = 0, dmnDiagram1Size = dmnDDDiagrams.size(); index < dmnDiagram1Size; index++) {

            final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagrams.get(index));
            final JSIDiagramElement.JSIExtension dmnDDExtensions = Js.uncheckedCast(jsiDiagram.getExtension());

            if (Objects.isNull(dmnDDExtensions)) {
                break;
            }
            if (Objects.isNull(dmnDDExtensions.getAny())) {
                break;
            }
            final List<Object> extensions = dmnDDExtensions.getAny();
            if (!Objects.isNull(extensions)) {
                for (int i = 0; i < extensions.size(); i++) {
                    final Object wrapped = extensions.get(i);
                    final Object extension = JsUtils.getUnwrappedElement(wrapped);
                    if (JSITComponentsWidthsExtension.instanceOf(extension)) {
                        final JSITComponentsWidthsExtension jsiExtension = Js.uncheckedCast(extension);
                        componentsWidthsExtensions.add(jsiExtension);
                    }
                }
            }
        }
        return componentsWidthsExtensions;
    }

    private void loadImportedItemDefinitions(final Definitions definitions,
                                             final Map<JSITImport, JSITDefinitions> importDefinitions) {
        definitions.getItemDefinition().addAll(getWbImportedItemDefinitions(importDefinitions));
    }

    private List<ItemDefinition> getWbImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        final List<ItemDefinition> definitions = new ArrayList<>();
        final List<JSITItemDefinition> importedDefinitions = dmnMarshallerImportsHelper.getImportedItemDefinitions(importDefinitions);
        for (int i = 0; i < importedDefinitions.size(); i++) {
            final JSITItemDefinition definition = Js.uncheckedCast(importedDefinitions.get(i));
            final ItemDefinition converted = ItemDefinitionPropertyConverter.wbFromDMN(definition);

            if (converted != null) {
                converted.setAllowOnlyVisualChange(true);
                definitions.add(converted);
            }
        }
        return definitions;
    }
}
