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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.toolbox.command.palette;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.AbstractLienzoGlyphItemsPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoGlyphsHoverPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoPalette;
import org.kie.workbench.common.stunner.client.lienzo.util.SVGUtils;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.palette.AbstractPaletteMorphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteView;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class LienzoPaletteMorphToolboxCommand extends AbstractPaletteMorphCommand<Shape<?>> {

    private static final int ICON_SIZE = 20;
    private static final int PADDING = 10;

    @Inject
    public LienzoPaletteMorphToolboxCommand(final DefinitionUtils definitionUtils,
                                            final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                            final ClientFactoryService clientFactoryServices,
                                            final CommonLookups commonLookups,
                                            final ShapeManager shapeManager,
                                            final DefinitionsPaletteBuilder definitionsPaletteBuilder,
                                            final LienzoGlyphsHoverPalette palette,
                                            final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                            final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                            final GraphBoundsIndexer graphBoundsIndexer,
                                            final Event<CanvasElementSelectedEvent> elementSelectedEvent) {
        super(definitionUtils,
              commandFactory,
              clientFactoryServices,
              commonLookups,
              shapeManager,
              definitionsPaletteBuilder,
              palette,
              nodeDragProxyFactory,
              nodeBuilderControl,
              graphBoundsIndexer,
              SVGUtils.createSVGIcon(SVGUtils.getGearIcon()),
              elementSelectedEvent);
    }

    @PostConstruct
    public void init() {
        // Initialize some lienzo palette layout stuff.
        getLienzoPalette()
                .collapse()
                .setExpandable(false)
                .setIconSize(ICON_SIZE)
                .setPadding(PADDING)
                .setLayout(LienzoPalette.Layout.HORIZONTAL);
        // Set the prefix to use on tooltips.
        ((AbstractLienzoGlyphItemsPalette) getLienzoPalette()).getDefinitionGlyphTooltip().setPrefix("Convert to ");
        // Show the tooltip at the right coordinates..
        getLienzoPalette().onShowGlyTooltip((glyphTooltip,
                                             item,
                                             mouseX,
                                             mouseY,
                                             itemX, itemY) -> {
            final Transform transform = canvasHandler.getCanvas().getLayer().getTransform();
            final double ax = canvasHandler.getAbstractCanvas().getAbsoluteX();
            final double ay = canvasHandler.getAbstractCanvas().getAbsoluteY();
            // As tooltip is a floating view (not part of the canvas), need to transform the cartesian coordinates
            // using current transform attributes to obtain the right absolute position on the screen.
            final Point2D t = transform.transform(getPaletteView().getX(),
                                                  getPaletteView().getY());
            glyphTooltip
                    .showTooltip(item.getDefinitionId(),
                                 ax + t.getX() + itemX,
                                 ay + t.getY() + itemY + ICON_SIZE + (PADDING / 2),
                                 GlyphTooltip.Direction.NORTH);
            return false;
        });
    }

    @Override
    protected void beforeBindPalette(final DefinitionsPalette paletteDefinition,
                                     final Context<AbstractCanvasHandler> context) {
        super.beforeBindPalette(paletteDefinition,
                                context);
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        getLienzoPalette().setShapeSetId(ssid);
    }

    @Override
    protected void showPaletteViewAt(final double x,
                                     final double y) {
        // Adjust the palette view taking into account the lienzo shape size used as icon.
        super.showPaletteViewAt(x - ICON_SIZE + PADDING,
                                y + ICON_SIZE);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void attachPaletteView() {
        final LienzoLayer lienzoLayer = (LienzoLayer) canvasHandler.getCanvas().getLayer();
        getPaletteView().attach(lienzoLayer.getLienzoLayer());
    }

    @Override
    protected PaletteView getPaletteView() {
        return getLienzoPalette().getView();
    }

    private LienzoGlyphsHoverPalette getLienzoPalette() {
        return (LienzoGlyphsHoverPalette) palette;
    }
}
