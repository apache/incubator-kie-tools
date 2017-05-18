/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.palette;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class NewPaletteNodeCommand<I> extends AbstractPaletteCommand<I> {

    private static Logger LOGGER = Logger.getLogger(NewPaletteNodeCommand.class.getName());

    public NewPaletteNodeCommand(final ClientFactoryService clientFactoryServices,
                                 final CommonLookups commonLookups,
                                 final ShapeManager shapeManager,
                                 final DefinitionsPaletteBuilder definitionsPaletteBuilder,
                                 final Palette<HasPaletteItems<? extends GlyphPaletteItem>> palette,
                                 final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                 final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                 final GraphBoundsIndexer graphBoundsIndexer,
                                 final I icon) {
        super(clientFactoryServices,
              commonLookups,
              shapeManager,
              definitionsPaletteBuilder,
              palette,
              nodeDragProxyFactory,
              nodeBuilderControl,
              graphBoundsIndexer,
              icon);
    }

    protected abstract String getDefinitionSetIdentifier();

    protected abstract String getEdgeIdentifier();

    @Override
    @SuppressWarnings("unchecked")
    protected Set<String> getDefinitions() {
        // TODO: Finish this implementation & Handle all response buckets/pages. Currently no palettes
        // are used on toolbox ( no implementation for this class )
        final Set<Object> allowedDefinitions = commonLookups.getAllowedTargetDefinitions(
                getDefinitionSetIdentifier(),
                canvasHandler.getDiagram().getGraph(),
                this.sourceNode,
                getEdgeIdentifier(),
                0,
                10);
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onItemSelected(final Context<AbstractCanvasHandler> context,
                                  final String definitionId,
                                  final double x,
                                  final double y) {
        clientFactoryServices.newElement(UUID.uuid(),
                                         getEdgeIdentifier(),
                                         new ServiceCallback<Element>() {

                                             @Override
                                             public void onSuccess(final Element edgeItem) {
                                                 final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) edgeItem;
                                                 // Manually set the source node as the drag def will need it.
                                                 edge.setSourceNode(sourceNode);
                                                 final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
                                                 final ShapeFactory<?, AbstractCanvasHandler, ?> shapeFactory = shapeManager.getShapeSet(ssid).getShapeFactory();
                                                 clientFactoryServices.newElement(UUID.uuid(),
                                                                                  definitionId,
                                                                                  new ServiceCallback<Element>() {
                                                                                      @Override
                                                                                      public void onSuccess(final Element nodeItem) {
                                                                                          final Node<View<?>, Edge> node = (Node<View<?>, Edge>) nodeItem;
                                                                                          final NodeDragProxy.Item<AbstractCanvasHandler> item = new NodeDragProxy.Item<AbstractCanvasHandler>() {
                                                                                              @Override
                                                                                              public Node<View<?>, Edge> getNode() {
                                                                                                  return node;
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
                                                                                                  return edge.getSourceNode();
                                                                                              }

                                                                                              @Override
                                                                                              public ShapeFactory<?, AbstractCanvasHandler, ?> getInEdgeShapeFactory() {
                                                                                                  return shapeFactory;
                                                                                              }
                                                                                          };
                                                                                          nodeBuilderControl.enable(canvasHandler);
                                                                                          nodeBuilderControl.setCommandManagerProvider(context::getCommandManager);
                                                                                          canvasHighlight = new CanvasHighlight(canvasHandler);
                                                                                          graphBoundsIndexer.build(canvasHandler.getDiagram().getGraph());
                                                                                          nodeDragProxyFactory
                                                                                                  .proxyFor(canvasHandler)
                                                                                                  .show(item,
                                                                                                        (int) x,
                                                                                                        (int) y,
                                                                                                        new NodeDragProxyCallback() {

                                                                                                            @Override
                                                                                                            public void onStart(final int x,
                                                                                                                                final int y) {
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onMove(final int x,
                                                                                                                               final int y) {
                                                                                                                final NodeBuildRequest request = new NodeBuildRequestImpl(x,
                                                                                                                                                                          y,
                                                                                                                                                                          node,
                                                                                                                                                                          edge);
                                                                                                                final boolean accepts = nodeBuilderControl.allows(request);
                                                                                                                if (accepts) {
                                                                                                                    final Node parent = graphBoundsIndexer.getAt(x,
                                                                                                                                                                 y);
                                                                                                                    if (null != parent) {
                                                                                                                        canvasHighlight.unhighLight().highLight(parent);
                                                                                                                    }
                                                                                                                } else {
                                                                                                                    canvasHighlight.unhighLight();
                                                                                                                }
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
                                                                                                                final NodeBuildRequest request = new NodeBuildRequestImpl(x,
                                                                                                                                                                          y,
                                                                                                                                                                          node,
                                                                                                                                                                          edge,
                                                                                                                                                                          sourceMagnet,
                                                                                                                                                                          targetMagnet);
                                                                                                                nodeBuilderControl.build(request,
                                                                                                                                         new BuilderControl.BuildCallback() {

                                                                                                                                             @Override
                                                                                                                                             public void onSuccess(final String uuid) {
                                                                                                                                                 nodeBuilderControl.setCommandManagerProvider(null);
                                                                                                                                                 nodeBuilderControl.disable();
                                                                                                                                                 canvasHighlight.unhighLight();
                                                                                                                                             }

                                                                                                                                             @Override
                                                                                                                                             public void onError(final ClientRuntimeError error) {
                                                                                                                                                 log(Level.SEVERE,
                                                                                                                                                     error.toString());
                                                                                                                                             }
                                                                                                                                         });
                                                                                                            }
                                                                                                        });
                                                                                      }

                                                                                      @Override
                                                                                      public void onError(final ClientRuntimeError error) {
                                                                                          log(Level.SEVERE,
                                                                                              error.toString());
                                                                                      }
                                                                                  });
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 log(Level.SEVERE,
                                                     error.toString());
                                             }
                                         });
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
