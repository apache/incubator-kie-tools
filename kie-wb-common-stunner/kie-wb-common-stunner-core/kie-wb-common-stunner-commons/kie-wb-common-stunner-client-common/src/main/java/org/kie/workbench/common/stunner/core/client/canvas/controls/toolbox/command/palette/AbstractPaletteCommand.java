/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.palette;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.AbstractToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteView;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;

public abstract class AbstractPaletteCommand<I> extends AbstractToolboxCommand<I> {

    private static Logger LOGGER = Logger.getLogger(AbstractPaletteCommand.class.getName());

    private final I icon;

    protected final ClientFactoryService clientFactoryServices;
    protected final CommonLookups commonLookups;
    protected final ShapeManager shapeManager;
    protected final DefinitionsPaletteBuilder definitionsPaletteBuilder;
    protected final Palette<HasPaletteItems<? extends GlyphPaletteItem>> palette;
    protected final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory;
    protected final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl;
    protected final GraphBoundsIndexer graphBoundsIndexer;

    protected AbstractCanvasHandler canvasHandler;
    protected CanvasHighlight canvasHighlight;
    protected Node<? extends Definition<Object>, ? extends Edge> sourceNode;
    protected boolean paletteVisible;
    protected String elementUUID;

    public AbstractPaletteCommand(final ClientFactoryService clientFactoryServices,
                                  final CommonLookups commonLookups,
                                  final ShapeManager shapeManager,
                                  final DefinitionsPaletteBuilder definitionsPaletteBuilder,
                                  final Palette<HasPaletteItems<? extends GlyphPaletteItem>> palette,
                                  final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                  final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                  final GraphBoundsIndexer graphBoundsIndexer,
                                  final I icon) {
        this.clientFactoryServices = clientFactoryServices;
        this.commonLookups = commonLookups;
        this.shapeManager = shapeManager;
        this.definitionsPaletteBuilder = definitionsPaletteBuilder;
        this.palette = palette;
        this.nodeDragProxyFactory = nodeDragProxyFactory;
        this.nodeBuilderControl = nodeBuilderControl;
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.icon = icon;
        this.paletteVisible = false;
    }

    protected abstract Set<String> getDefinitions();

    protected abstract void onItemSelected(final Context<AbstractCanvasHandler> context,
                                           final String definitionId,
                                           final double x,
                                           final double y);

    @Override
    public I getIcon(final AbstractCanvasHandler context,
                     final double width,
                     final double height) {
        return icon;
    }

    // TODO: I18n.
    @Override
    public String getTitle() {
        return "- Empty title -";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void mouseEnter(final Context<AbstractCanvasHandler> context,
                           final Element element) {
        super.mouseEnter(context,
                         element);
        showPalette(context,
                    element);
    }

    @Override
    public void click(final Context<AbstractCanvasHandler> context,
                      final Element element) {
        super.click(context,
                    element);
        if (paletteVisible) {
            clear();
        } else {
            showPalette(context,
                        element);
        }
    }

    @SuppressWarnings("unchecked")
    protected void showPalette(final Context<AbstractCanvasHandler> context,
                               final Element element) {
        this.elementUUID = element.getUUID();
        this.paletteVisible = true;
        this.canvasHandler = context.getCanvasHandler();
        this.sourceNode = (Node<? extends Definition<Object>, ? extends Edge>) element;
        this.graphBoundsIndexer.setRootUUID(canvasHandler.getDiagram().getMetadata().getCanvasRootUUID());
        final Set<String> allowedDefinitions = getDefinitions();
        log(Level.FINE,
            "Allowed Definitions -> " + allowedDefinitions);
        if (null != allowedDefinitions && !allowedDefinitions.isEmpty()) {

            final String definitionSetId = canvasHandler.getDiagram().getMetadata().getDefinitionSetId();
            final PaletteDefinitionBuilder.Configuration configuration = new PaletteDefinitionBuilder.Configuration() {

                @Override
                public String getDefinitionSetId() {
                    return definitionSetId;
                }

                @Override
                public Set<String> getDefinitionIds() {
                    return allowedDefinitions;
                }
            };

            definitionsPaletteBuilder.build(configuration,
                                            new PaletteDefinitionBuilder.Callback<DefinitionsPalette, ClientRuntimeError>() {

                                                @Override
                                                public void onSuccess(final DefinitionsPalette paletteDefinition) {
                                                    initializeView(paletteDefinition,
                                                                   context);
                                                }

                                                @Override
                                                public void onError(final ClientRuntimeError error) {
                                                    log(Level.SEVERE,
                                                        error.toString());
                                                }
                                            });
        }
    }

    protected abstract void attachPaletteView();

    @SuppressWarnings("unchecked")
    private void initializeView(final DefinitionsPalette paletteDefinition,
                                final Context<AbstractCanvasHandler> context) {
        // Delegate to the builder control instance the current command manager.
        nodeBuilderControl.setCommandManagerProvider(context::getCommandManager);
        // Palette binding.
        beforeBindPalette(paletteDefinition,
                          context);
        palette.bind(paletteDefinition)
                .onItemHover((id, mouseX, mouseY, itemX, itemY) -> AbstractPaletteCommand.this._onItemHover(context,
                                                                                                            id,
                                                                                                            mouseX,
                                                                                                            mouseY,
                                                                                                            itemX,
                                                                                                            itemY))
                .onItemOut((id) -> AbstractPaletteCommand.this._onItemOut(context,
                                                                          id))
                .onItemClick((id, mouseX, mouseY, itemX, itemY) -> AbstractPaletteCommand.this._onItemClick(context,
                                                                                                            id,
                                                                                                            mouseX,
                                                                                                            mouseY,
                                                                                                            itemX,
                                                                                                            itemY))
                .onItemMouseDown((id, mouseX, mouseY, itemX, itemY) -> AbstractPaletteCommand.this._onItemMouseDown(context,
                                                                                                                    id,
                                                                                                                    mouseX,
                                                                                                                    mouseY,
                                                                                                                    itemX,
                                                                                                                    itemY));
        // Use the relative coordinates (x/y) as palette gets added into same canvas' layer as the toolbox.
        showPaletteViewAt(context.getX(),
                          context.getY());
    }

    protected void beforeBindPalette(final DefinitionsPalette paletteDefinition,
                                     final Context<AbstractCanvasHandler> context) {
        // Nothing to do by default.
    }

    protected void showPaletteViewAt(final double x,
                                     final double y) {
        getPaletteView().setX(x);
        getPaletteView().setY(y);
        attachPaletteView();
        getPaletteView().show();
    }

    public void clear() {
        this.nodeBuilderControl.setCommandManagerProvider(null);
        this.paletteVisible = false;
        this.elementUUID = null;
        getPaletteView().clear();
    }

    private boolean _onItemClick(final Context<AbstractCanvasHandler> context,
                                 final String id,
                                 final double mouseX,
                                 final double mouseY,
                                 final double itemX,
                                 final double itemY) {
        // TODO
        return true;
    }

    private boolean _onItemHover(final Context<AbstractCanvasHandler> context,
                                 final String id,
                                 final double mouseX,
                                 final double mouseY,
                                 final double itemX,
                                 final double itemY) {
        canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.POINTER);
        return true;
    }

    private boolean _onItemOut(final Context<AbstractCanvasHandler> context,
                               final String id) {
        canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.AUTO);
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean _onItemMouseDown(final Context<AbstractCanvasHandler> context,
                                     final String id,
                                     final double mouseX,
                                     final double mouseY,
                                     final double itemX,
                                     final double itemY) {
        onItemSelected(context,
                       id,
                       mouseX,
                       mouseY);
        return true;
    }

    @Override
    public void destroy() {
        this.palette.destroy();
        this.nodeDragProxyFactory.destroy();
        this.nodeBuilderControl.setCommandManagerProvider(null);
        this.nodeBuilderControl.disable();
        this.graphBoundsIndexer.destroy();
        this.canvasHighlight.destroy();
        this.canvasHandler = null;
        this.canvasHighlight = null;
        this.sourceNode = null;
    }

    protected abstract PaletteView getPaletteView();

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
