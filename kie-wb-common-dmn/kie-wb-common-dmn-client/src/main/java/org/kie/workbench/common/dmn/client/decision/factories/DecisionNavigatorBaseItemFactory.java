/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorBaseItemFactory_NoName;

@Dependent
public class DecisionNavigatorBaseItemFactory {

    private final DecisionNavigatorNestedItemFactory nestedItemFactory;

    private final DecisionNavigatorPresenter decisionNavigatorPresenter;

    private final TextPropertyProviderFactory textPropertyProviderFactory;

    private final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    private final DefinitionUtils definitionUtils;

    private final TranslationService translationService;

    @Inject
    public DecisionNavigatorBaseItemFactory(final DecisionNavigatorNestedItemFactory nestedItemFactory,
                                            final DecisionNavigatorPresenter decisionNavigatorPresenter,
                                            final TextPropertyProviderFactory textPropertyProviderFactory,
                                            final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent,
                                            final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                            final DefinitionUtils definitionUtils,
                                            final TranslationService translationService) {
        this.nestedItemFactory = nestedItemFactory;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.canvasFocusedSelectionEvent = canvasFocusedSelectionEvent;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.definitionUtils = definitionUtils;
        this.translationService = translationService;
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node,
                                          final DecisionNavigatorItem.Type type) {

        final String uuid = node.getUUID();
        final String label = getLabel(node);
        final Command onClick = makeOnClickCommand(node);
        final List<DecisionNavigatorItem> nestedItems = makeNestedItems(node);
        final String diagramUUID = diagramUUID();

        final DecisionNavigatorItem item = new DecisionNavigatorItem(uuid, label, type, onClick, diagramUUID);
        nestedItems.forEach(item::addChild);

        return item;
    }

    @SuppressWarnings("unchecked")
    String diagramUUID() {
        final Diagram diagram = decisionNavigatorPresenter.getDiagram();
        final Graph<?, Node> graph = diagram.getGraph();

        return StreamSupport.stream(graph.nodes().spliterator(), false)
                .filter(n -> n.getContent() instanceof Definition)
                .filter(n -> ((Definition) n.getContent()).getDefinition() instanceof DMNDiagram)
                .findFirst()
                .map(Node::getUUID)
                .orElse("");
    }

    Command makeOnClickCommand(final Node<View, Edge> node) {

        final CanvasHandler canvasHandler = decisionNavigatorPresenter.getHandler();
        final String uuid = node.getUUID();

        return () -> {
            canvasSelectionEvent.fire(makeCanvasSelectionEvent(canvasHandler, uuid));
            canvasFocusedSelectionEvent.fire(makeCanvasFocusedShapeEvent(canvasHandler, uuid));
            if (canvasHandler != null && canvasHandler.getCanvas() != null) {
                canvasHandler.getCanvas().focus();
            }
        };
    }

    String getLabel(final Element<View> element) {

        final String name = getName(element);
        final String title = getTitle(element);

        if (isNil(name) && !isNil(title)) {
            return title;
        }

        return (name != null ? name : getDefaultName());
    }

    String getName(final Element<View> element) {
        final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(element);
        return provider.getText(element);
    }

    String getTitle(final Element<View> element) {

        final AdapterManager adapters = definitionUtils.getDefinitionManager().adapters();
        final DefinitionAdapter<Object> objectDefinitionAdapter = adapters.forDefinition();

        return objectDefinitionAdapter.getTitle(element.getContent().getDefinition());
    }

    List<DecisionNavigatorItem> makeNestedItems(final Node<View, Edge> node) {
        final List<DecisionNavigatorItem> nestedItems = new ArrayList<>();
        if (hasNestedElement(node)) {
            nestedItems.add(nestedItemFactory.makeItem(node));
        }
        return nestedItems;
    }

    CanvasSelectionEvent makeCanvasSelectionEvent(final CanvasHandler canvas,
                                                  final String uuid) {
        return new CanvasSelectionEvent(canvas, uuid);
    }

    CanvasFocusedShapeEvent makeCanvasFocusedShapeEvent(final CanvasHandler canvas,
                                                        final String uuid) {
        return new CanvasFocusedShapeEvent(canvas, uuid);
    }

    private String getDefaultName() {
        return translationService.format(DecisionNavigatorBaseItemFactory_NoName);
    }

    private boolean hasNestedElement(final Node<View, Edge> node) {
        return nestedItemFactory.hasNestedElement(node);
    }

    private boolean isNil(final String s) {
        return s == null || s.trim().isEmpty();
    }
}
