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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.EdgeBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CancelCanvasAction;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * A toolbox's action which goal is to create a new connection, as from the
 * toolbox's related node, to any other candidate canvas' node.
 */
@Dependent
public class CreateConnectorToolboxAction extends AbstractToolboxAction {

    private static Logger LOGGER = Logger.getLogger(CreateConnectorToolboxAction.class.getName());
    static final String KEY_TITLE = "org.kie.workbench.common.stunner.core.client.toolbox.createNewConnector";

    private final ClientFactoryManager clientFactoryManager;
    private final GraphBoundsIndexer graphBoundsIndexer;
    private final ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory;
    private final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final CanvasHighlight canvasHighlight;

    private String edgeId;
    private DragProxy<AbstractCanvasHandler, ConnectorDragProxy.Item, DragProxyCallback> dragProxy;

    @Inject
    public CreateConnectorToolboxAction(final DefinitionUtils definitionUtils,
                                        final ClientFactoryManager clientFactoryManager,
                                        final GraphBoundsIndexer graphBoundsIndexer,
                                        final ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory,
                                        final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl,
                                        final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                        final ClientTranslationService translationService,
                                        final CanvasHighlight canvasHighlight) {
        super(definitionUtils,
              translationService);
        this.clientFactoryManager = clientFactoryManager;
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.connectorDragProxyFactory = connectorDragProxyFactory;
        this.edgeBuilderControl = edgeBuilderControl;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasHighlight = canvasHighlight;
    }

    public CreateConnectorToolboxAction setEdgeId(final String edgeId) {
        this.edgeId = edgeId;
        return this;
    }

    public String getEdgeId() {
        return edgeId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        // Obtain the connector and source node instances for proxying.
        final Element<?> element = CanvasLayoutUtils.getElement(canvasHandler,
                                                                uuid);
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) element.asNode();
        final Edge<? extends ViewConnector<?>, Node> connector =
                (Edge<? extends ViewConnector<?>, Node>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    edgeId)
                        .asEdge();

        // Set the transient connection to the source node, for further rule evaluations.
        connector.setSourceNode(sourceNode);

        // Built and show a connector drag proxy, to check and finally set
        // candidate the target node.
        final double x = event.getX();
        final double y = event.getY();
        dragProxy = showDragProxy(canvasHandler,
                                  connector,
                                  sourceNode,
                                  (int) x,
                                  (int) y);
        return this;
    }

    protected void cancelConnector(@Observes CancelCanvasAction cancelCanvasAction) {
        if (Objects.nonNull(dragProxy)) {
            dragProxy.clear();
            dragProxy = null;
        }
    }

    @Override
    protected String getTitleKey(final AbstractCanvasHandler canvasHandler,
                                 final String uuid) {
        return KEY_TITLE;
    }

    @Override
    protected String getTitleDefinitionId(final AbstractCanvasHandler canvasHandler,
                                          final String uuid) {
        return edgeId;
    }

    @Override
    protected String getGlyphId(final AbstractCanvasHandler canvasHandler,
                                final String uuid) {
        return edgeId;
    }

    @SuppressWarnings("unchecked")
    private DragProxy<AbstractCanvasHandler, ConnectorDragProxy.Item, DragProxyCallback> showDragProxy(final AbstractCanvasHandler canvasHandler,
                                                                                                       final Edge<? extends ViewConnector<?>, Node> connector,
                                                                                                       final Node<? extends View<?>, Edge> sourceNode,
                                                                                                       final int x,
                                                                                                       final int y) {

        // Built and show the drag proxy.
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        final ShapeFactory shapeFactory = canvasHandler.getShapeFactory(ssid);
        final ConnectorDragProxy.Item connectorDragItem = new ConnectorDragProxy.Item() {

            @Override
            public Edge<? extends ViewConnector<?>, Node> getEdge() {
                return connector;
            }

            @Override
            public Node<? extends View<?>, Edge> getSourceNode() {
                return sourceNode;
            }

            @Override
            public ShapeFactory<?, ?> getShapeFactory() {
                return shapeFactory;
            }
        };
        return connectorDragProxyFactory
                .proxyFor(canvasHandler)
                .show(connectorDragItem,
                      x,
                      y,
                      new DragProxyCallback() {
                          @Override
                          public void onStart(final int x,
                                              final int y) {
                              start(canvasHandler);
                          }

                          @Override
                          public void onMove(final int x,
                                             final int y) {
                              final Node targetNode = graphBoundsIndexer.getAt(x,
                                                                               y);
                              final boolean allow = allow(x,
                                                          y,
                                                          connector,
                                                          sourceNode,
                                                          targetNode);
                              canvasHighlight.unhighLight();
                              if (null != targetNode && allow) {
                                  canvasHighlight.highLight(targetNode);
                              } else if (null != targetNode) {
                                  canvasHighlight.invalid(targetNode);
                              }
                          }

                          @Override
                          public void onComplete(final int x,
                                                 final int y) {
                              final Node targetNode = graphBoundsIndexer.getAt(x,
                                                                               y);
                              accept(x,
                                     y,
                                     connector,
                                     sourceNode,
                                     targetNode);
                          }
                      });
    }

    @SuppressWarnings("unchecked")
    private void start(final AbstractCanvasHandler canvasHandler) {
        canvasHighlight.setCanvasHandler(canvasHandler);
        graphBoundsIndexer.setRootUUID(canvasHandler.getDiagram().getMetadata().getCanvasRootUUID());
        graphBoundsIndexer.build(canvasHandler.getDiagram().getGraph());
        edgeBuilderControl.init(canvasHandler);
        edgeBuilderControl.setCommandManagerProvider(() -> sessionCommandManager);
    }

    @SuppressWarnings("unchecked")
    private boolean allow(final int x,
                          final int y,
                          final Edge<? extends ViewConnector<?>, Node> connector,
                          final Node<? extends View<?>, Edge> sourceNode,
                          final Node targetNode) {
        if (null != targetNode) {
            EdgeBuildRequestImpl buildRequest =
                    new EdgeBuildRequestImpl(x,
                                             y,
                                             connector,
                                             sourceNode,
                                             targetNode);
            return edgeBuilderControl.allows(buildRequest);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void accept(final int x,
                        final int y,
                        final Edge<? extends ViewConnector<?>, Node> connector,
                        final Node<? extends View<?>, Edge> sourceNode,
                        final Node targetNode) {
        if (null != targetNode) {
            EdgeBuildRequestImpl buildRequest =
                    new EdgeBuildRequestImpl(x,
                                             y,
                                             connector,
                                             sourceNode,
                                             targetNode);
            edgeBuilderControl.build(buildRequest,
                                     new BuilderControl.BuildCallback() {
                                         @Override
                                         public void onSuccess(final String uuid) {
                                             complete();
                                         }

                                         @Override
                                         public void onError(final ClientRuntimeError error) {
                                             error(error);
                                         }
                                     });
        }
    }

    private void complete() {
        edgeBuilderControl.setCommandManagerProvider(null);
        canvasHighlight.destroy();
    }

    private void error(final ClientRuntimeError error) {
        complete();
        LOGGER.log(Level.SEVERE,
                   error.toString());
    }

    CanvasHighlight getCanvasHighlight() {
        return canvasHighlight;
    }

    protected DragProxy<AbstractCanvasHandler, ConnectorDragProxy.Item, DragProxyCallback> getDragProxy() {
        return dragProxy;
    }

    @PreDestroy
    public void destroy() {
        graphBoundsIndexer.destroy();
        connectorDragProxyFactory.destroy();
        edgeBuilderControl.destroy();
        canvasHighlight.destroy();
        if (null != dragProxy) {
            dragProxy.destroy();
            dragProxy = null;
        }
        edgeId = null;
    }

    @Override
    public int hashCode() {
        return edgeId.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof CreateConnectorToolboxAction) {
            CreateConnectorToolboxAction other = (CreateConnectorToolboxAction) o;
            return other.edgeId.equals(edgeId);
        }
        return false;
    }
}
