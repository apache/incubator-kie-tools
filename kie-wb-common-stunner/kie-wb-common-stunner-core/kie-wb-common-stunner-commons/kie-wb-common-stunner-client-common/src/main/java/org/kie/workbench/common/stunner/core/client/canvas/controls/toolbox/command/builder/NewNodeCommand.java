/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.mvp.Command;

@Dependent
public class NewNodeCommand<I> extends AbstractElementBuilderCommand<I> {

    private static Logger LOGGER = Logger.getLogger(NewNodeCommand.class.getName());

    private final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory;
    private final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl;
    private final Event<CanvasElementSelectedEvent> elementSelectedEvent;
    private final DefinitionUtils definitionUtils;
    private final CanvasLayoutUtils canvasLayoutUtils;

    private String definitionId;
    private Magnet sourceMagnet;
    private Magnet targetMagnet;
    private HasEventHandlers<?, ?> layerEventHandlers;

    protected NewNodeCommand() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public NewNodeCommand(final ClientFactoryService clientFactoryServices,
                          final ShapeManager shapeManager,
                          final DefinitionGlyphTooltip<?> glyphTooltip,
                          final GraphBoundsIndexer graphBoundsIndexer,
                          final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                          final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                          final DefinitionUtils definitionUtils,
                          final CanvasLayoutUtils canvasLayoutUtils,
                          final Event<CanvasElementSelectedEvent> elementSelectedEvent) {
        super(clientFactoryServices,
              shapeManager,
              glyphTooltip,
              graphBoundsIndexer);
        this.nodeDragProxyFactory = nodeDragProxyFactory;
        this.nodeBuilderControl = nodeBuilderControl;
        this.definitionUtils = definitionUtils;
        this.canvasLayoutUtils = canvasLayoutUtils;
        this.elementSelectedEvent = elementSelectedEvent;
        this.layerEventHandlers = null;
    }

    // TODO: i18n.
    @PostConstruct
    public void init() {
        getGlyphTooltip().setPrefix("Click to create a ");
    }

    public void setDefinitionIdentifier(final String definitionId) {
        this.definitionId = definitionId;
    }

    private String getEdgeIdentifier(final Context<AbstractCanvasHandler> context) {
        final String defSetId = context.getCanvasHandler().getDiagram().getMetadata().getDefinitionSetId();
        return definitionUtils.getDefaultConnectorId(defSetId);
    }

    @Override
    protected String getDefinitionIdentifier(final Context<AbstractCanvasHandler> context) {
        return getEdgeIdentifier(context);
    }

    @Override
    protected String getGlyphDefinitionId() {
        return this.definitionId;
    }

    // TODO: I18n.
    @Override
    public String getTitle() {
        return "Creates a new node";
    }

    @Override
    public void click(final Context<AbstractCanvasHandler> context,
                      final Element element) {
        super.click(context,
                    element);
        log(Level.INFO,
            "Click - Start adding a new node...");
        addOnNextLayoutPosition(context,
                                element);
    }

    // TODO: Move all these stuff to canvas builder controls?
    @SuppressWarnings("unchecked")
    private void addOnNextLayoutPosition(final Context<AbstractCanvasHandler> context,
                                         final Element element) {
        fireLoadingStarted(context);
        final AbstractCanvasHandler canvasHandler = context.getCanvasHandler();
        getGraphBoundsIndexer().setRootUUID(canvasHandler.getDiagram().getMetadata().getCanvasRootUUID());
        getGraphBoundsIndexer().build(canvasHandler.getDiagram().getGraph());
        getClientFactoryServices().newElement(UUID.uuid(),
                                              getDefinitionIdentifier(context),
                                              new ServiceCallback<Element>() {

                                                  @Override
                                                  public void onSuccess(final Element newEdgeElement) {
                                                      onDefinitionInstanceBuilt(context,
                                                                                element,
                                                                                newEdgeElement,
                                                                                new Command() {

                                                                                    @Override
                                                                                    public void execute() {
                                                                                        getBuilderControl().enable(canvasHandler);
                                                                                        getBuilderControl().setCommandManagerProvider(context::getCommandManager);
                                                                                        getGraphBoundsIndexer().build(canvasHandler.getDiagram().getGraph());
                                                                                        // TODO: Use right magnets.
                                                                                        NewNodeCommand.this.sourceMagnet = MagnetImpl.Builder.build(Magnet.MagnetType.OUTGOING);
                                                                                        NewNodeCommand.this.targetMagnet = MagnetImpl.Builder.build(Magnet.MagnetType.INCOMING);
                                                                                        final double[] next = canvasLayoutUtils.getNext(canvasHandler,
                                                                                                                                        (Node<View<?>, Edge>) element);
                                                                                        log(Level.INFO,
                                                                                            "New edge request complete - [UUID=" + newEdgeElement.getUUID()
                                                                                                    + ", x=" + next[0] + ", y=" + next[1] + "]");
                                                                                        NewNodeCommand.this.onComplete(context,
                                                                                                                       element,
                                                                                                                       newEdgeElement,
                                                                                                                       (int) next[0],
                                                                                                                       (int) next[1]);
                                                                                    }
                                                                                });
                                                  }

                                                  @Override
                                                  public void onError(final ClientRuntimeError error) {
                                                      NewNodeCommand.this.onError(context,
                                                                                  error);
                                                  }
                                              });
    }

    @Override
    protected DragProxy getDragProxyFactory() {
        return nodeDragProxyFactory;
    }

    @Override
    protected BuilderControl getBuilderControl() {
        return nodeBuilderControl;
    }

    @Override
    protected DragProxyCallback getDragProxyCallback(final Context<AbstractCanvasHandler> context,
                                                     final Element element,
                                                     final Element item) {
        return new NodeDragProxyCallback() {

            @Override
            public void onStart(final int x,
                                final int y) {
                NewNodeCommand.this.onStart(context,
                                            element,
                                            item,
                                            x,
                                            y);
            }

            @Override
            public void onMove(final int x,
                               final int y) {
                NewNodeCommand.this.onMove(context,
                                           element,
                                           item,
                                           x,
                                           y);
            }

            @Override
            public void onComplete(final int x,
                                   final int y) {
            }

            @Override
            public void onComplete(final int x,
                                   final int y,
                                   final Magnet sourceMagnet,
                                   final Magnet targetMagnet) {
                NewNodeCommand.this.sourceMagnet = sourceMagnet;
                NewNodeCommand.this.targetMagnet = targetMagnet;
                NewNodeCommand.this.onComplete(context,
                                               element,
                                               item,
                                               x,
                                               y);
            }
        };
    }

    @Override
    protected void onStart(final Context<AbstractCanvasHandler> context,
                           final Element element,
                           final Element item,
                           final int x1,
                           final int y1) {
        super.onStart(context,
                      element,
                      item,
                      x1,
                      y1);
        // Disable layer events handlers in order to avoid layer events while using the drag def.
        this.layerEventHandlers = getLayer(context);
        disableHandlers();
    }

    @Override
    protected void onItemBuilt(final Context<AbstractCanvasHandler> context,
                               final String uuid) {
        super.onItemBuilt(context,
                          uuid);
        fireElementSelectedEvent(elementSelectedEvent,
                                 context.getCanvasHandler(),
                                 uuid);
    }

    @Override
    protected void onError(final Context<AbstractCanvasHandler> context,
                           final ClientRuntimeError error) {
        super.onError(context,
                      error);
        // Enable layer events handlers again.
        enableHandlers();
    }

    protected Layer getLayer(final Context<AbstractCanvasHandler> context) {
        return context.getCanvasHandler().getCanvas().getLayer();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onDefinitionInstanceBuilt(final Context<AbstractCanvasHandler> context,
                                             final Element source,
                                             final Element newElement,
                                             final Command callback) {
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) source;
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) newElement;
        // Create the new node.
        getClientFactoryServices().newElement(UUID.uuid(),
                                              definitionId,
                                              new ServiceCallback<Element>() {

                                                  @Override
                                                  public void onSuccess(final Element item) {
                                                      final Node<View<?>, Edge> node = (Node<View<?>, Edge>) item.asNode();
                                                      // Perform the temporal def connections.
                                                      edge.setSourceNode(sourceNode);
                                                      edge.setTargetNode(node);
                                                      NewNodeCommand.super.onDefinitionInstanceBuilt(context,
                                                                                                     source,
                                                                                                     newElement,
                                                                                                     callback);
                                                  }

                                                  @Override
                                                  public void onError(final ClientRuntimeError error) {
                                                      NewNodeCommand.this.onError(context,
                                                                                  error);
                                                  }
                                              });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object createtBuilderControlItem(final Context<AbstractCanvasHandler> context,
                                               final Element source,
                                               final Element newElement) {
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) source;
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) newElement;
        final ShapeFactory<?, AbstractCanvasHandler, ?> shapeFactory = getFactory(context.getCanvasHandler());
        return new NodeDragProxy.Item<AbstractCanvasHandler>() {
            @Override
            public Node<View<?>, Edge> getNode() {
                return edge.getTargetNode();
            }

            @Override
            public ShapeFactory<?, AbstractCanvasHandler, ?> getNodeShapeFactory() {
                return shapeFactory;
            }

            @Override
            public Edge<View<?>, Node> getInEdge() {
                return edge;
            }

            @Override
            public Node<View<?>, Edge> getInEdgeSourceNode() {
                return sourceNode;
            }

            @Override
            public ShapeFactory<?, AbstractCanvasHandler, ?> getInEdgeShapeFactory() {
                return shapeFactory;
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean onDragProxyMove(final int x,
                                      final int y,
                                      final Element source,
                                      final Element newElement,
                                      final Node parent) {
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) newElement;
        final Node<View<?>, Edge> node = (Node<View<?>, Edge>) edge.getTargetNode();
        final NodeBuildRequest request = new NodeBuildRequestImpl(x,
                                                                  y,
                                                                  node,
                                                                  edge);
        final boolean accepts = nodeBuilderControl.allows(request);
        if (accepts) {
            if (null != parent) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BuildRequest createBuildRequest(final int x,
                                              final int y,
                                              final Element source,
                                              final Element newElement,
                                              final Node targetNode) {
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) newElement;
        final Node<View<?>, Edge> node = (Node<View<?>, Edge>) edge.getTargetNode();
        return new NodeBuildRequestImpl(x,
                                        y,
                                        node,
                                        edge,
                                        this.sourceMagnet,
                                        this.targetMagnet);
    }

    @Override
    protected void clearDragProxy() {
        super.clearDragProxy();
        // Enable layers' events handlers again.
        enableHandlers();
    }

    private void disableHandlers() {
        if (null != this.layerEventHandlers) {
            this.layerEventHandlers.disableHandlers();
        }
    }

    private void enableHandlers() {
        if (null != this.layerEventHandlers) {
            this.layerEventHandlers.enableHandlers();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.layerEventHandlers = null;
    }

    protected String getDefinitionId(final Object def) {
        return definitionUtils.getDefinitionManager().adapters().forDefinition().getId(def);
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}