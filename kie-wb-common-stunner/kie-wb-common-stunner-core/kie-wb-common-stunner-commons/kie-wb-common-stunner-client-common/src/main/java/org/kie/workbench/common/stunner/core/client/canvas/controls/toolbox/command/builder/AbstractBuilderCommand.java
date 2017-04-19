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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.AbstractToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.mvp.Command;

public abstract class AbstractBuilderCommand<I> extends AbstractToolboxCommand<I> {

    private static Logger LOGGER = Logger.getLogger(AbstractBuilderCommand.class.getName());

    private final ClientFactoryService clientFactoryServices;
    private final GraphBoundsIndexer graphBoundsIndexer;

    private CanvasHighlight canvasHighlight;

    public AbstractBuilderCommand(final ClientFactoryService clientFactoryServices,
                                  final GraphBoundsIndexer graphBoundsIndexer) {
        this.clientFactoryServices = clientFactoryServices;
        this.graphBoundsIndexer = graphBoundsIndexer;
    }

    protected abstract String getDefinitionIdentifier(final Context<AbstractCanvasHandler> context);

    protected abstract DragProxy getDragProxyFactory();

    protected abstract DragProxyCallback getDragProxyCallback(final Context<AbstractCanvasHandler> context,
                                                              final Element element,
                                                              final Element newElement);

    protected abstract BuilderControl getBuilderControl();

    protected abstract Object createtBuilderControlItem(final Context<AbstractCanvasHandler> context,
                                                        final Element source,
                                                        final Element newElement);

    protected abstract boolean onDragProxyMove(final int x,
                                               final int y,
                                               final Element source,
                                               final Element newElement,
                                               final Node targetNode);

    protected abstract BuildRequest createBuildRequest(final int x,
                                                       final int y,
                                                       final Element source,
                                                       final Element newElement,
                                                       final Node targetNode);

    protected void onDefinitionInstanceBuilt(final Context<AbstractCanvasHandler> context,
                                             final Element source,
                                             final Element newElement,
                                             final Command callback) {
        callback.execute();
    }

    @Override
    public void mouseDown(final Context<AbstractCanvasHandler> context,
                          final Element element) {
        super.mouseDown(context,
                        element);
        showDragProxy(context,
                      element);
    }

    @Override
    public void click(final Context<AbstractCanvasHandler> context,
                      final Element element) {
        super.click(context,
                    element);
    }

    @SuppressWarnings("unchecked")
    private void showDragProxy(final Context<AbstractCanvasHandler> context,
                               final Element element) {
        final AbstractCanvasHandler canvasHandler = context.getCanvasHandler();
        final double x = context.getAbsoluteX();
        final double y = context.getAbsoluteY();
        graphBoundsIndexer.setRootUUID(canvasHandler.getDiagram().getMetadata().getCanvasRootUUID());
        clientFactoryServices.newElement(UUID.uuid(),
                                         getDefinitionIdentifier(context),
                                         new ServiceCallback<Element>() {

                                             @Override
                                             public void onSuccess(final Element item) {
                                                 onDefinitionInstanceBuilt(context,
                                                                           element,
                                                                           item,
                                                                           () -> {
                                                                               getBuilderControl().enable(canvasHandler);
                                                                               getBuilderControl().setCommandManagerProvider(context::getCommandManager);
                                                                               canvasHighlight = new CanvasHighlight(canvasHandler);
                                                                               graphBoundsIndexer.build(canvasHandler.getDiagram().getGraph());
                                                                               DragProxyCallback proxyCallback = getDragProxyCallback(context,
                                                                                                                                      element,
                                                                                                                                      item);
                                                                               getDragProxyFactory().proxyFor(canvasHandler)
                                                                                       .show(
                                                                                               createtBuilderControlItem(context,
                                                                                                                         element,
                                                                                                                         item),
                                                                                               (int) x,
                                                                                               (int) y,
                                                                                               proxyCallback
                                                                                       );
                                                                           });
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 AbstractBuilderCommand.this.onError(context,
                                                                                     error);
                                             }
                                         });
    }

    @SuppressWarnings("unchecked")
    protected void onStart(final Context<AbstractCanvasHandler> context,
                           final Element element,
                           final Element item,
                           final int x1,
                           final int y1) {
    }

    @SuppressWarnings("unchecked")
    protected void onMove(final Context<AbstractCanvasHandler> context,
                          final Element element,
                          final Element item,
                          final int x1,
                          final int y1) {
        // TODO: Two expensive calls to bounds indexer, this one and the one inside connectorDragProxyFactory.
        final Node targetNode = graphBoundsIndexer.getAt(x1,
                                                         y1);
        final boolean accepts = onDragProxyMove(x1,
                                                y1,
                                                element,
                                                item,
                                                targetNode);
        canvasHighlight.unhighLight();
        if (null != targetNode && accepts) {
            canvasHighlight.highLight(targetNode);
        } else if (null != targetNode) {
            canvasHighlight.invalid(targetNode);
        }
    }

    @SuppressWarnings("unchecked")
    protected void onComplete(final Context<AbstractCanvasHandler> context,
                              final Element element,
                              final Element item,
                              final int x1,
                              final int y1) {
        fireLoadingStarted(context);
        final Node targetNode = graphBoundsIndexer.getAt(x1,
                                                         y1);
        log(Level.FINE,
            "Completing element creation - Creating node for parent ["
                    + (null != targetNode ? targetNode.getUUID() : "null"));
        if (null != targetNode) {
            // Ensure back to NONE shape state before any further operations.
            ensureUnHighLight();
            // Create the build request.
            final BuildRequest buildRequest = createBuildRequest(x1,
                                                                 y1,
                                                                 element,
                                                                 item,
                                                                 targetNode);
            // Use the builder control to perform the operation.
            getBuilderControl().build(buildRequest,
                                      new BuilderControl.BuildCallback() {

                                          @Override
                                          public void onSuccess(final String uuid) {
                                              log(Level.INFO,
                                                  "Item build with UUID [" + uuid + "]");
                                              onItemBuilt(context,
                                                          uuid);
                                              getBuilderControl().setCommandManagerProvider(null);
                                              fireLoadingCompleted(context);
                                          }

                                          @Override
                                          public void onError(final ClientRuntimeError error) {
                                              AbstractBuilderCommand.this.onError(context,
                                                                                  error);
                                          }
                                      });
        } else {
            log(Level.FINE,
                "No candidate node found at [" + x1 + ", " + y1 + "]. Nothing to do.");
        }
        context.getCanvasHandler().getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.AUTO);
    }

    protected void onError(final Context<AbstractCanvasHandler> context,
                           final ClientRuntimeError error) {
        fireLoadingCompleted(context);
        LOGGER.log(Level.SEVERE,
                   error.toString());
    }

    protected void onItemBuilt(final Context<AbstractCanvasHandler> context,
                               final String uuid) {
        // Nothing to do by default.
    }

    @Override
    @SuppressWarnings("unchecked")
    public void destroy() {
        this.getDragProxyFactory().destroy();
        getBuilderControl().setCommandManagerProvider(null);
        this.getBuilderControl().disable();
        this.graphBoundsIndexer.destroy();
        if (null != canvasHighlight) {
            this.canvasHighlight.destroy();
            this.canvasHighlight = null;
        }
    }

    protected ClientFactoryService getClientFactoryServices() {
        return clientFactoryServices;
    }

    protected GraphBoundsIndexer getGraphBoundsIndexer() {
        return graphBoundsIndexer;
    }

    private void ensureUnHighLight() {
        if (null != canvasHighlight) {
            canvasHighlight.unhighLight();
        }
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
