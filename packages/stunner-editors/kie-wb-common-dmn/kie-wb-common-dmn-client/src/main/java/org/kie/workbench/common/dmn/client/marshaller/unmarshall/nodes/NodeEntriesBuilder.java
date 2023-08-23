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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.stunner.core.graph.Node;

import static java.util.Collections.emptyList;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.uniqueId;

class NodeEntriesBuilder {

    private final Map<JSIDMNShape, String> shapesByDiagramId = new HashMap<>();

    private final List<JSITDRGElement> drgElements = new ArrayList<>();

    private final List<JSITDRGElement> includedDRGElements = new ArrayList<>();

    private final List<JSIDMNDiagram> dmnDiagrams = new ArrayList<>();

    private final List<JSITTextAnnotation> textAnnotations = new ArrayList<>();

    private final StunnerConverter nodeFactory;

    private BiConsumer<String, HasComponentWidths> componentWidthsConsumer;

    NodeEntriesBuilder(final StunnerConverter nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    List<NodeEntry> buildEntries() {

        final boolean modelHasDMNDI = shapesByDiagramId.size() > 0;
        if (modelHasDMNDI) {
            return shapesByDiagramId
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        final String diagramId = entry.getValue();
                        final JSIDMNShape shape = Js.uncheckedCast(entry.getKey());
                        return makeEntry(diagramId, shape);
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }

        final List<JSITDMNElement> dmnElements = getDMNElements();
        final boolean modelDoesNotHaveDMNDI = dmnDiagrams.size() == 1 && dmnElements.size() > 0;

        if (modelDoesNotHaveDMNDI) {
            return dmnElements
                    .stream()
                    .map(dmnElement -> {
                        final JSIDMNDiagram diagram = Js.uncheckedCast(dmnDiagrams.get(0));
                        final String diagramId = diagram.getId();
                        return makeEntry(diagramId, makeStandardShape(), dmnElement);
                    })
                    .collect(Collectors.toList());
        }

        return emptyList();
    }

    NodeEntriesBuilder withShapesByDiagramId(final Map<JSIDMNShape, String> shapesByDiagramId) {
        this.shapesByDiagramId.putAll(shapesByDiagramId);
        return this;
    }

    NodeEntriesBuilder withDRGElements(final List<JSITDRGElement> drgElements) {
        this.drgElements.addAll(drgElements);
        return this;
    }

    NodeEntriesBuilder withTextAnnotations(final List<JSITTextAnnotation> textAnnotations) {
        this.textAnnotations.addAll(textAnnotations);
        return this;
    }

    NodeEntriesBuilder withIncludedDRGElements(final List<JSITDRGElement> includedDRGElements) {
        this.includedDRGElements.addAll(includedDRGElements);
        return this;
    }

    public NodeEntriesBuilder withDMNDiagrams(final List<JSIDMNDiagram> dmnDiagrams) {
        this.dmnDiagrams.addAll(dmnDiagrams);
        return this;
    }

    NodeEntriesBuilder withComponentWidthsConsumer(final BiConsumer<String, HasComponentWidths> componentWidthsConsumer) {
        this.componentWidthsConsumer = componentWidthsConsumer;
        return this;
    }

    private Optional<NodeEntry> makeEntry(final String diagramId,
                                          final JSIDMNShape shape) {
        return getDMNElement(shape)
                .map(dmnElement -> makeEntry(diagramId, shape, dmnElement));
    }

    private NodeEntry makeEntry(final String diagramId,
                                final JSIDMNShape shape,
                                final JSITDMNElement dmnElement) {
        final NodeEntry nodeEntry = new NodeEntry(diagramId,
                                                  copy(shape),
                                                  dmnElement,
                                                  isIncluded(dmnElement),
                                                  componentWidthsConsumer);
        final Node node = nodeFactory.make(nodeEntry);
        nodeEntry.setNode(node);
        return nodeEntry;
    }

    private JSIDMNShape copy(final JSIDMNShape shape) {
        final JSIDMNShape copy = Js.uncheckedCast(JsInteropUtils.jsCopy(shape));
        copy.setId(uniqueId());
        return copy;
    }

    private boolean isIncluded(final JSITDMNElement dmnElement) {
        final JSITDRGElement drgElement = Js.uncheckedCast(dmnElement);
        return includedDRGElements.contains(drgElement);
    }

    private Optional<JSITDMNElement> getDMNElement(final JSIDMNShape shape) {
        return getDMNElements()
                .stream()
                .filter(dmnElement -> {
                    final QName dmnElementRef = shape.getDmnElementRef();
                    final String dmnElementId = dmnElement.getId();
                    return dmnElementRef.getLocalPart().endsWith(dmnElementId);
                })
                .findFirst();
    }

    private JSIDMNShape makeStandardShape() {
        final JSIDMNShape jsidmnShape = JSIDMNShape.newInstance();
        final JSIBounds boundsParam = JSIBounds.newInstance();
        boundsParam.setX(0);
        boundsParam.setY(0);
        boundsParam.setWidth(100);
        boundsParam.setHeight(50);
        jsidmnShape.setBounds(boundsParam);
        return jsidmnShape;
    }

    private List<JSITDMNElement> getDMNElements() {
        return Stream
                .of(drgElements, includedDRGElements, textAnnotations)
                .<JSITDMNElement>flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
