/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.client.mvp.UberView;

@Dependent
public class TreeExplorer implements IsWidget {

    static final String NO_NAME = "- No name -";

    private final int icoHeight = 16;
    private final int icoWidth = 16;
    private final ChildrenTraverseProcessor childrenTraverseProcessor;
    private final TextPropertyProviderFactory textPropertyProviderFactory;
    private final DefinitionUtils definitionUtils;
    private final ShapeManager shapeManager;
    private final Event<CanvasElementSelectedEvent> elementSelectedEventEvent;
    private final View view;
    private final DOMGlyphRenderers domGlyphRenderers;

    private String selectedItemCanvasUuid;
    private CanvasHandler canvasHandler;

    @Inject
    public TreeExplorer(final ChildrenTraverseProcessor childrenTraverseProcessor,
                        final TextPropertyProviderFactory textPropertyProviderFactory,
                        final Event<CanvasElementSelectedEvent> elementSelectedEventEvent,
                        final DefinitionUtils definitionUtils,
                        final ShapeManager shapeManager,
                        final DOMGlyphRenderers domGlyphRenderers,
                        final View view) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.elementSelectedEventEvent = elementSelectedEventEvent;
        this.definitionUtils = definitionUtils;
        this.shapeManager = shapeManager;
        this.domGlyphRenderers = domGlyphRenderers;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @SuppressWarnings("unchecked")
    public void show(final CanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        if (null != canvasHandler && null != canvasHandler.getDiagram()) {
            doShow(canvasHandler.getDiagram().getGraph());
        }
    }

    private void doShow(final Graph<org.kie.workbench.common.stunner.core.graph.content.view.View, Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> graph) {
        traverseChildrenEdges(graph,
                              true);
    }

    private void traverseChildrenEdges(final Graph<org.kie.workbench.common.stunner.core.graph.content.view.View, Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> graph,
                                       final boolean expand) {
        assert graph != null;
        clear();
        childrenTraverseProcessor.traverse(graph,
                                           new AbstractChildrenTraverseCallback<Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>, Edge<Child, Node>>() {

                                               @Override
                                               public boolean startNodeTraversal(final List<Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> parents,
                                                                                 final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node) {
                                                   super.startNodeTraversal(parents,
                                                                            node);
                                                   addItem(parents.get(parents.size() - 1),
                                                           node,
                                                           expand,
                                                           false);
                                                   return true;
                                               }

                                               @Override
                                               public void startNodeTraversal(final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node) {
                                                   super.startNodeTraversal(node);
                                                   addItem(node,
                                                           expand,
                                                           false);
                                               }
                                           });
    }

    private Glyph getGlyph(final String shapeSetId,
                           final Element<org.kie.workbench.common.stunner.core.graph.content.view.View> element) {
        final Object definition = element.getContent().getDefinition();
        final String defId = definitionUtils.getDefinitionManager().adapters().forDefinition().getId(definition);
        final ShapeFactory factory = shapeManager.getShapeSet(shapeSetId).getShapeFactory();
        return factory.getGlyph(defId);
    }

    private void inc(final List<Integer> levels,
                     final int level) {
        if (levels.size() < (level + 1)) {
            levels.add(0);
        } else {
            final int idx = levels.get(level);
            levels.set(level,
                       idx + 1);
        }
    }

    private int[] getParentsIdx(final List<Integer> idxList,
                                final int maxLevel) {

        if (!idxList.isEmpty()) {
            final int targetPos = (idxList.size() - (idxList.size() - maxLevel)) + 1;
            final int[] resultArray = new int[targetPos];
            for (int x = 0; x < targetPos; x++) {
                resultArray[x] = idxList.get(x);
            }
            return resultArray;
        }
        return new int[]{};
    }

    public void clear() {
        view.clear();
    }

    public void destroy() {
        this.selectedItemCanvasUuid = null;
        view.destroy();
    }

    void onSelect(final String uuid) {
        selectShape(canvasHandler.getCanvas(),
                    uuid);
    }

    private void selectShape(final Canvas canvas,
                             final String uuid) {
        elementSelectedEventEvent.fire(new CanvasElementSelectedEvent(canvasHandler,
                                                                      uuid));
    }

    void onCanvasClearEvent(@Observes CanvasClearEvent canvasClearEvent) {
        if (null != canvasHandler &&
                null != canvasHandler.getCanvas() &&
                canvasHandler.getCanvas().equals(canvasClearEvent.getCanvas())) {
            clear();
        }
    }

    void onCanvasElementAddedEvent(final @Observes CanvasElementAddedEvent canvasElementAddedEvent) {
        if (checkEventContext(canvasElementAddedEvent)) {
            onElementAdded(canvasElementAddedEvent.getElement());
        }
    }

    private void onElementAdded(Element element) {
        if (isValidTreeItem().test(element)) {
            final Element parent = GraphUtils.getParent((Node<?, ? extends Edge>) element);
            addItem(parent,
                    (Node) element,
                    true,
                    true);
        }
    }

    @SuppressWarnings("unchecked")
    private void onElementUpdated(Element element,
                                  CanvasHandler canvasHandler) {
        if (isValidTreeItem().test(element)) {
            final Element lane = GraphUtils.getParent((Node<?, ? extends Edge>) element);

            if (view.isItemChanged(element.getUUID(),
                                   null != lane ? lane.getUUID() : null,
                                   getItemName(element))) {

                view.removeItem(element.getUUID());
                addItem(lane,
                        (Node) element,
                        true,
                        true);

                final boolean hasChildren = GraphUtils.hasChildren((Node<?, ? extends Edge>) element);
                if (hasChildren) {
                    childrenTraverseProcessor
                            .setRootUUID(element.getUUID())
                            .traverse(canvasHandler.getDiagram().getGraph(),
                                      new AbstractChildrenTraverseCallback<Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>, Edge<Child, Node>>() {

                                          @Override
                                          public boolean startNodeTraversal(final List<Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> parents,
                                                                            final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node) {
                                              super.startNodeTraversal(parents,
                                                                       node);
                                              addItem(parents.get(parents.size() - 1),
                                                      node,
                                                      true,
                                                      false);
                                              return true;
                                          }

                                          @Override
                                          public void startNodeTraversal(final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node) {
                                              super.startNodeTraversal(node);
                                              addItem(node,
                                                      true,
                                                      false);
                                          }
                                      });
                }
            }
        }
    }

    private void onElementRemoved(Element element) {
        if (isValidTreeItem().test(element)) {
            String uuid = element.getUUID();
            view.removeItem(uuid);
        }
    }

    @SuppressWarnings("unchecked")
    private void addItem(final Element parent,
                         final Node element,
                         final boolean expand,
                         final boolean checkParent) {
        final boolean isContainer = isContainer().test(element);
        final Glyph glyph = getGlyph(getShapeSetId(),
                                     element);
        final String name = getItemName(element);
        final IsElement icon = domGlyphRenderers.render(glyph,
                                                        icoWidth,
                                                        icoHeight);
        // Check the parent, in case a TreeItem mutates from/to ITEM type to CONTAINER type.
        final boolean isValidParentItem = null != parent && isValidTreeItem().test(parent);

        if (checkParent && isValidParentItem) {
            final boolean isParentContainer = isContainer().test((Node) parent);
            final boolean wasParentContainer = view.isContainer(parent.getUUID());
            if (isParentContainer != wasParentContainer) {
                view.removeItem(parent.getUUID());
                addItem(GraphUtils.getParent((Node<?, ? extends Edge>) parent),
                        (Node) parent,
                        expand,
                        false);
            }
        }

        final ElementWrapperWidget<?> widget = wrapIconElement(icon);

        // Create and add the tree item.
        if (isValidParentItem) {
            view.addItem(element.getUUID(),
                         parent.getUUID(),
                         name,
                         widget,
                         isContainer,
                         expand);
        } else {
            view.addItem(element.getUUID(),
                         name,
                         widget,
                         isContainer,
                         expand);
        }
    }

    ElementWrapperWidget<?> wrapIconElement(final IsElement icon) {
        return ElementWrapperWidget.getWidget(icon.getElement());
    }

    private Predicate<Node> isContainer() {
        return e -> GraphUtils.countChildren(e) > 0;
    }

    private Predicate<Element<?>> isValidTreeItem() {
        return isViewElement();
    }

    private Predicate<Element<?>> isViewElement() {
        return e -> (null != e && (!(e instanceof Edge)) && (e.getContent() instanceof org.kie.workbench.common.stunner.core.graph.content.view.View));
    }

    private void addItem(final Node item,
                         final boolean expand,
                         final boolean checkParent) {
        addItem(null,
                item,
                expand,
                checkParent);
    }

    void onCanvasElementRemovedEvent(final @Observes CanvasElementRemovedEvent elementRemovedEvent) {
        if (checkEventContext(elementRemovedEvent)) {
            onElementRemoved(elementRemovedEvent.getElement());
        }
    }

    void onCanvasElementsClearEvent(final @Observes CanvasElementsClearEvent canvasClearEvent) {
        if (checkEventContext(canvasClearEvent)) {
            showEventGraph(canvasClearEvent);
        }
    }

    void onCanvasElementUpdatedEvent(final @Observes CanvasElementUpdatedEvent canvasElementUpdatedEvent) {
        if (checkEventContext(canvasElementUpdatedEvent)) {
            onElementUpdated(canvasElementUpdatedEvent.getElement(),
                             canvasElementUpdatedEvent.getCanvasHandler());
        }
    }

    void onCanvasElementSelectedEvent(final @Observes CanvasElementSelectedEvent event) {
        if (checkEventContext(event)) {
            if (null != getCanvasHandler()) {
                final String uuid = event.getElementUUID();

                if (!(uuid.equals(this.selectedItemCanvasUuid))) {
                    this.selectedItemCanvasUuid = uuid;
                    view.setSelectedItem(uuid);
                }
            }
        }
    }

    private boolean checkEventContext(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return canvasHandler != null && canvasHandler.equals(_canvasHandler);
    }

    private boolean checkEventContext(final AbstractCanvasEvent canvasEvent) {
        final Canvas canvas = canvasEvent.getCanvas();
        return null != canvasHandler && null != canvasHandler.getCanvas()
                && canvasHandler.getCanvas().equals(canvas);
    }

    private String getShapeSetId() {
        return canvasHandler.getDiagram().getMetadata().getShapeSetId();
    }

    @SuppressWarnings("unchecked")
    private void showEventGraph(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        doShow(canvasHandlerEvent.getCanvasHandler().getDiagram().getGraph());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public CanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    private String getItemName(final Element<org.kie.workbench.common.stunner.core.graph.content.view.View> item) {
        final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(item);
        final String name = provider.getText(item);
        final String title = definitionUtils.getDefinitionManager().adapters().forDefinition().getTitle(item.getContent().getDefinition());

        if ((name == null || name.trim().equals("")) && title != null) {
            return title;
        }
        return (name != null ? name : NO_NAME);
    }

    public interface View extends UberView<TreeExplorer> {

        View addItem(final String uuid,
                     final String name,
                     final IsWidget icon,
                     final boolean isContainer,
                     final boolean state);

        View addItem(final String uuid,
                     final String parentUuid,
                     final String name,
                     final IsWidget icon,
                     final boolean isContainer,
                     final boolean state);

        View setSelectedItem(final String uuid);

        View removeItem(String uuid);

        View clear();

        View destroy();

        boolean isItemChanged(final String uuid,
                              final String parentUuid,
                              final String name);

        boolean isContainer(final String uuid);
    }
}
