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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.toolbox.command.palette;

import javax.annotation.PostConstruct;

import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.AbstractLienzoGlyphItemsPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoGlyphsHoverPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoPalette;
import org.kie.workbench.common.stunner.client.lienzo.util.SVGUtils;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.palette.NewPaletteNodeCommand;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteView;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;

/// Abstract for not being discovered by the CDI environment, for now...
public abstract class LienzoNewPaletteNodeToolboxCommand extends NewPaletteNodeCommand<Shape<?>> {

    public LienzoNewPaletteNodeToolboxCommand(final ClientFactoryService clientFactoryServices,
                                              final CommonLookups commonLookups,
                                              final ShapeManager shapeManager,
                                              final DefinitionsPaletteBuilder definitionsPaletteBuilder,
                                              final LienzoGlyphsHoverPalette palette,
                                              final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                              final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                              final GraphBoundsIndexer graphBoundsIndexer) {
        super(clientFactoryServices,
              commonLookups,
              shapeManager,
              definitionsPaletteBuilder,
              palette,
              nodeDragProxyFactory,
              nodeBuilderControl,
              graphBoundsIndexer,
              SVGUtils.createSVGIcon(SVGUtils.getAddIcon()));
    }

    // TODO: i18n.
    @PostConstruct
    public void init() {
        getLienzoPalette()
                .expand()
                .setIconSize(15)
                .setPadding(5)
                .setLayout(LienzoPalette.Layout.VERTICAL);
        ((AbstractLienzoGlyphItemsPalette) getLienzoPalette()).getDefinitionGlyphTooltip().setPrefix("Click to create a ");
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
    @SuppressWarnings("unchecked")
    protected void attachPaletteView() {
        final LienzoLayer lienzoLayer = (LienzoLayer) canvasHandler.getCanvas().getLayer();
        getPaletteView().attach(lienzoLayer.getLienzoLayer());
    }

    @Override
    protected PaletteView getPaletteView() {
        return getLienzoPalette().getView();
    }

    protected LienzoGlyphsHoverPalette getLienzoPalette() {
        return (LienzoGlyphsHoverPalette) palette;
    }
}
