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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITArtifact;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;

import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.forEach;

@Dependent
public class NodeEntriesFactory {

    private final StunnerConverter nodeFactory;

    private final NodeConnector nodeConnector;

    private final DMNMarshallerImportsClientHelper dmnMarshallerImportsHelper;

    @Inject
    public NodeEntriesFactory(final StunnerConverter nodeFactory,
                              final NodeConnector nodeConnector,
                              final DMNMarshallerImportsClientHelper dmnMarshallerImportsHelper) {
        this.nodeFactory = nodeFactory;
        this.nodeConnector = nodeConnector;
        this.dmnMarshallerImportsHelper = dmnMarshallerImportsHelper;
    }

    public List<NodeEntry> makeNodes(final JSITDefinitions definitions,
                                     final Map<JSITImport, JSITDefinitions> importDefinitions,
                                     final boolean isDMNDIPresent,
                                     final BiConsumer<String, HasComponentWidths> componentWidthsConsumer) {

        final List<JSIDMNDiagram> dmnDiagrams = definitions.getDMNDI().getDMNDiagram();
        final List<NodeEntry> nodeEntries = entriesBuilder()
                .withShapesByDiagramId(getShapesByDiagramId(definitions))
                .withDRGElements(getDRGElements(definitions))
                .withIncludedDRGElements(getIncludedDRGElements(importDefinitions))
                .withTextAnnotations(getTextAnnotations(definitions))
                .withDMNDiagrams(dmnDiagrams)
                .withComponentWidthsConsumer(componentWidthsConsumer)
                .buildEntries();

        // We need to put all the Decision Services at the begin of the list otherwise the nodes inside
        // those Decision Services will not be correctly positioned since its parents (the Decision Services nodes)
        // needs to be positioned first.
        nodeEntries.sort((n1, n2) -> {
            if (JSITDecisionService.instanceOf(n1.getDmnElement())) {
                return -1;
            } else if (JSITDecisionService.instanceOf(n2.getDmnElement())){
                return 1;
            }
            return 0;
        });

        forEach(dmnDiagrams, dmnDiagram -> {

            final String diagramId = dmnDiagram.getId();
            final List<JSIDMNEdge> edges = getEdges(dmnDiagram);
            final List<JSITAssociation> associations = getAssociations(definitions);
            final List<NodeEntry> nodes = nodeEntries
                    .stream()
                    .filter(n -> Objects.equals(n.getDiagramId(), diagramId))
                    .collect(Collectors.toList());

            nodeConnector.connect(dmnDiagram, edges, associations, nodes, isDMNDIPresent);
        });

        return nodeEntries;
    }

    private NodeEntriesBuilder entriesBuilder() {
        return new NodeEntriesBuilder(nodeFactory);
    }

    private List<JSIDMNEdge> getEdges(final JSIDMNDiagram dmnDiagram) {

        final List<JSIDMNEdge> edges = new ArrayList<>();
        final List<JSIDiagramElement> dmnDiagramElements = dmnDiagram.getDMNDiagramElement();

        forEach(dmnDiagramElements, dmnDiagramElement -> {
            if (JSIDMNEdge.instanceOf(dmnDiagramElement)) {
                final JSIDMNEdge jsiEdge = Js.uncheckedCast(dmnDiagramElement);
                edges.add(jsiEdge);
            }
        });

        return edges;
    }

    private List<JSITAssociation> getAssociations(final JSITDefinitions definitions) {
        final List<JSITAssociation> associations = new ArrayList<>();
        final List<JSITArtifact> artifacts = definitions.getArtifact();

        forEach(artifacts, artifact -> {
            if (JSITAssociation.instanceOf(artifact)) {
                final JSITAssociation association = Js.uncheckedCast(artifact);
                associations.add(association);
            }
        });

        return associations;
    }

    private Map<JSIDMNShape, String> getShapesByDiagramId(final JSITDefinitions definitions) {

        final Map<JSIDMNShape, String> dmnShapesByDiagramId = new HashMap<>();
        final List<JSIDMNDiagram> diagrams = definitions.getDMNDI().getDMNDiagram();

        forEach(diagrams, diagram -> {
            final String diagramId = diagram.getId();
            final List<JSIDiagramElement> diagramElements = diagram.getDMNDiagramElement();

            forEach(diagramElements, diagramElement -> {
                if (JSIDMNShape.instanceOf(diagramElement)) {
                    final JSIDMNShape shape = Js.uncheckedCast(diagramElement);
                    dmnShapesByDiagramId.put(shape, diagramId);
                }
            });
        });

        return dmnShapesByDiagramId;
    }

    private List<JSITDRGElement> getDRGElements(final JSITDefinitions definitions) {
        return definitions.getDrgElement();
    }

    private List<JSITDRGElement> getIncludedDRGElements(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        return dmnMarshallerImportsHelper.getImportedDRGElements(importDefinitions);
    }

    private List<JSITTextAnnotation> getTextAnnotations(final JSITDefinitions definitions) {

        final List<JSITTextAnnotation> textAnnotations = new ArrayList<>();
        final List<JSITArtifact> artifacts = definitions.getArtifact();

        forEach(artifacts, artifact -> {
            if (JSITTextAnnotation.instanceOf(artifact)) {
                final JSITTextAnnotation annotation = Js.uncheckedCast(artifact);
                textAnnotations.add(annotation);
            }
        });

        return textAnnotations;
    }
}
