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

package org.kie.workbench.common.dmn.client.decision.included.components;

import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.MouseDownEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.NamespaceHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionComponentsItemView_DuplicatedNode;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Dependent
@Templated
public class DecisionComponentsItemView implements DecisionComponentsItem.View {

    @DataField("icon")
    private final HTMLImageElement icon;

    @DataField("name")
    private final HTMLHeadingElement name;

    @DataField("decision-component-item")
    private final HTMLDivElement decisionComponentItem;

    @DataField("file")
    private final HTMLParagraphElement file;

    private DecisionComponentsItem presenter;

    private final DMNShapeSet dmnShapeSet;

    private final SessionManager sessionManager;

    private final ShapeGlyphDragHandler<?> shapeGlyphDragHandler;

    private final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;

    private final Event<NotificationEvent> notificationEvent;

    private final ClientTranslationService clientTranslationService;

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public DecisionComponentsItemView(final HTMLImageElement icon,
                                      final @Named("h5") HTMLHeadingElement name,
                                      final HTMLParagraphElement file,
                                      final DMNShapeSet dmnShapeSet,
                                      final SessionManager sessionManager,
                                      final ShapeGlyphDragHandler<?> shapeGlyphDragHandler,
                                      final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent,
                                      final HTMLDivElement decisionComponentItem,
                                      final Event<NotificationEvent> notificationEvent,
                                      final ClientTranslationService clientTranslationService,
                                      final DMNGraphUtils dmnGraphUtils) {
        this.icon = icon;
        this.name = name;
        this.file = file;
        this.dmnShapeSet = dmnShapeSet;
        this.sessionManager = sessionManager;
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
        this.buildCanvasShapeEvent = buildCanvasShapeEvent;
        this.decisionComponentItem = decisionComponentItem;
        this.notificationEvent = notificationEvent;
        this.clientTranslationService = clientTranslationService;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    @Override
    public void init(final DecisionComponentsItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setIcon(final String iconURI) {
        icon.src = iconURI;
    }

    @Override
    public void setName(final String name) {
        this.name.textContent = name;
    }

    @Override
    public void setFile(final String file) {
        this.file.textContent = file;
    }

    @EventHandler("decision-component-item")
    public void decisionComponentItemMouseDown(final MouseDownEvent mouseDownEvent) {

        final DRGElement drgElement = presenter.getDrgElement();
        final ShapeFactory factory = dmnShapeSet.getShapeFactory();
        final Glyph glyph = factory.getGlyph(drgElement.getClass().getName());
        final ShapeGlyphDragHandler.Item item = makeDragHandler(glyph);
        final DragProxyCallback proxy = makeDragProxyCallbackImpl(drgElement, factory);

        shapeGlyphDragHandler.show(item, mouseDownEvent.getX(), mouseDownEvent.getY(), proxy);
    }

    DragProxyCallback makeDragProxyCallbackImpl(final DRGElement drgElement,
                                                final ShapeFactory factory) {

        final Map<String, String> nsContext = getNsContext();

        final String alias = NamespaceHandler.addIncludedNamespace(nsContext, drgElement.getDefaultNamespace());

        final String currentId = drgElement.getId().getValue();
        final String id = currentId.split(":")[1];
        drgElement.getId().setValue(alias + ":" + id);

        return new DragProxyCallbackImpl(drgElement, factory, sessionManager, notificationEvent, clientTranslationService);
    }

    Map<String, String> getNsContext() {

        final Optional<Diagram> diagram = Optional.of(dmnGraphUtils.getDiagram());
        return diagram.map(dmnGraphUtils::getDefinitions)
                      .map(DMNModelInstrumentedBase::getNsContext)
                      .orElseThrow(UnsupportedOperationException::new);
    }

    Graph<?, Node> getGraph(){
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        return diagram.getGraph();
    }

    ShapeGlyphDragHandler.Item makeDragHandler(final Glyph glyph) {
        return new DragHandler(glyph);
    }

    class DragProxyCallbackImpl implements DragProxyCallback {

        private final DRGElement drgElement;

        private final ShapeFactory<?, ?> factory;

        private final SessionManager sessionManager;

        private final Event<NotificationEvent> notificationEvent;

        private final ClientTranslationService clientTranslationService;

        DragProxyCallbackImpl(final DRGElement drgElement,
                              final ShapeFactory factory,
                              final SessionManager sessionManager,
                              final Event<NotificationEvent> notificationEvent,
                              final ClientTranslationService clientTranslationService) {
            this.drgElement = drgElement;
            this.factory = factory;
            this.sessionManager = sessionManager;
            this.notificationEvent = notificationEvent;
            this.clientTranslationService = clientTranslationService;
        }

        @Override
        public void onStart(final int x,
                            final int y) {
            // empty.
        }

        @Override
        public void onMove(final int x,
                           final int y) {
            // empty.
        }

        @Override
        public void onComplete(final int x,
                               final int y) {

            if (isDuplicatedNode(drgElement)) {
                fireDuplicatedNodeWarningMessage();
            } else {
                fireBuildShapeEvent(x, y);
            }
        }

        private boolean isDuplicatedNode(final DRGElement drgElement) {

            final Optional<Map.Entry<String, String>> existingAlias = NamespaceHandler.getAlias(getNsContext(), drgElement.getDefaultNamespace());

            if (existingAlias.isPresent()) {

                final String alias = existingAlias.get().getKey();
                final String id = alias + ":" + drgElement.getId().getValue().split(":")[1];
                final Graph<?, Node> graph = getGraph();

                return StreamSupport
                           .stream(graph.nodes().spliterator(), false)
                           .filter(node -> node.getContent() instanceof View)
                           .map(node -> (View) node.getContent())
                           .filter(view -> view.getDefinition() instanceof DMNElement)
                           .map(Definition::getDefinition)
                           .anyMatch(d -> ((DMNElement) d).getId().getValue().equals(id));
            }

            return false;
        }

        private void fireDuplicatedNodeWarningMessage() {
            final String message = clientTranslationService.getValue(DecisionComponentsItemView_DuplicatedNode);
            notificationEvent.fire(new NotificationEvent(message, WARNING));
        }

        private void fireBuildShapeEvent(final int x,
                                         final int y) {
            buildCanvasShapeEvent.fire(new BuildCanvasShapeEvent(getCanvasHandler(), drgElement, factory, x, y));
        }

        private AbstractCanvasHandler getCanvasHandler() {
            return (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        }
    }

    class DragHandler implements ShapeGlyphDragHandler.Item {

        private final Glyph glyph;

        DragHandler(final Glyph glyph) {
            this.glyph = glyph;
        }

        @Override
        public Glyph getShape() {
            return glyph;
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }
    }
}
