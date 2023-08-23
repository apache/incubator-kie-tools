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

package org.kie.workbench.common.dmn.client.docks.navigator.factories;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemBuilder;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ROOT;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.SEPARATOR;
import static org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils.isDRG;

@Dependent
public class DecisionNavigatorItemFactory {

    private final DecisionNavigatorBaseItemFactory baseItemFactory;

    private final Event<DMNDiagramSelected> selectedEvent;

    private final DMNDiagramsSession dmnDiagramsSession;

    @Inject
    public DecisionNavigatorItemFactory(final DecisionNavigatorBaseItemFactory baseItemFactory,
                                        final Event<DMNDiagramSelected> selectedEvent,
                                        final DMNDiagramsSession dmnDiagramsSession) {
        this.baseItemFactory = baseItemFactory;
        this.selectedEvent = selectedEvent;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node) {
        final String nodeClassName = Optional.ofNullable(DefinitionUtils.getElementDefinition(node))
                .map(elementDefinition -> elementDefinition.getClass().getSimpleName())
                .orElse(Node.class.getSimpleName());

        return baseItemFactory.makeItem(node, Type.ofExpressionNodeClassName(nodeClassName));
    }

    public DecisionNavigatorItem makeRoot(final DMNDiagramTuple diagramTuple) {

        final DMNDiagramElement dmnDiagramElement = diagramTuple.getDMNDiagram();
        final String uuid = dmnDiagramElement.getId().getValue();
        final String diagramName = dmnDiagramElement.getName().getValue();
        final String label;
        final boolean isDRG = isDRG(dmnDiagramElement);

        if (isDRG) {
            final Graph graph = diagramTuple.getStunnerDiagram().getGraph();
            final Node<?, ?> rootNode = getRootNode(graph);
            label = getNodeName(rootNode);
        } else {
            label = diagramName;
        }

        return navigatorItemBuilder()
                .withUUID(uuid)
                .withLabel(label)
                .withType(ROOT)
                .withIsDRG(isDRG)
                .withOnClick(getOnClickAction(dmnDiagramElement))
                .withOnUpdate(getOnUpdate(dmnDiagramElement))
                .withOnRemove(getOnRemove(dmnDiagramElement))
                .build();
    }

    Command getOnClickAction(final DMNDiagramElement dmnDiagramElement) {
        return () -> {
            selectedEvent.fire(new DMNDiagramSelected(dmnDiagramElement));
        };
    }

    Consumer<DecisionNavigatorItem> getOnUpdate(final DMNDiagramElement dmnDiagramElement) {
        return (item) -> {
            dmnDiagramElement.getName().setValue(item.getLabel());
            selectedEvent.fire(new DMNDiagramSelected(dmnDiagramElement));
        };
    }

    Consumer<DecisionNavigatorItem> getOnRemove(final DMNDiagramElement dmnDiagramElement) {
        return (item) -> {
            removeFromModel(dmnDiagramElement);
            removeFromSession(dmnDiagramElement);
            selectedEvent.fire(new DMNDiagramSelected(dmnDiagramsSession.getDRGDiagramElement()));
        };
    }

    private void removeFromSession(final DMNDiagramElement dmnDiagramElement) {
        dmnDiagramsSession.remove(dmnDiagramElement);
    }

    private void removeFromModel(final DMNDiagramElement dmnDiagramElement) {

        final Graph graph = dmnDiagramsSession.getDRGDiagram().getGraph();
        final Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) DMNGraphUtils.findDMNDiagramRoot(graph);
        final Definitions definitions = ((DMNDiagram) DefinitionUtils.getElementDefinition(dmnDiagramRoot)).getDefinitions();

        definitions.getDiagramElements().removeIf(e -> {
            final String diagramId = e.getId().getValue();
            final String removedDiagramId = dmnDiagramElement.getId().getValue();
            return Objects.equals(diagramId, removedDiagramId);
        });
    }

    public DecisionNavigatorItem makeSeparator(final String label) {
        return navigatorItemBuilder()
                .withUUID(UUID.uuid())
                .withLabel(label)
                .withType(SEPARATOR)
                .build();
    }

    @SuppressWarnings("unchecked")
    private Node getRootNode(final Graph graph) {
        return DMNGraphUtils.findDMNDiagramRoot(graph);
    }

    @SuppressWarnings("unchecked")
    private String getNodeName(final Node<?, ?> rootNode) {
        return baseItemFactory.getLabel((Node<View, Edge>) rootNode);
    }

    private DecisionNavigatorItemBuilder navigatorItemBuilder() {
        return new DecisionNavigatorItemBuilder();
    }
}
