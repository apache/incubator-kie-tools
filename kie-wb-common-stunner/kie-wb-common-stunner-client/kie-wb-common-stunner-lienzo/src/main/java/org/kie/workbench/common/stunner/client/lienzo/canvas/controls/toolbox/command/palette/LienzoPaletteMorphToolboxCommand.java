/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.toolbox.command.palette;

import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.client.lienzo.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.AbstractLienzoGlyphItemsPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoGlyphsHoverPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoPalette;
import org.kie.workbench.common.stunner.client.lienzo.util.SVGUtils;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.palette.AbstractPaletteMorphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteView;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryServices;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class LienzoPaletteMorphToolboxCommand extends AbstractPaletteMorphCommand<Shape<?>> {

    @Inject
    public LienzoPaletteMorphToolboxCommand( final DefinitionUtils definitionUtils,
                                             final CanvasCommandFactory commandFactory,
                                             final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                             final ClientFactoryServices clientFactoryServices,
                                             final CommonLookups commonLookups,
                                             final ShapeManager shapeManager,
                                             final DefinitionsPaletteBuilder definitionsPaletteBuilder,
                                             final LienzoGlyphsHoverPalette palette,
                                             final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                             final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                             final GraphBoundsIndexer graphBoundsIndexer,
                                             final Event<CanvasElementSelectedEvent> elementSelectedEvent ) {
        super( definitionUtils, commandFactory, canvasCommandManager, clientFactoryServices,
                commonLookups, shapeManager, definitionsPaletteBuilder, palette, nodeDragProxyFactory,
                nodeBuilderControl, graphBoundsIndexer, SVGUtils.createSVGIcon( SVGUtils.getGearIcon() ),
                elementSelectedEvent );

    }

    @PostConstruct
    public void init() {
        getLienzoPalette()
                .collapse()
                .setExpandable( false )
                .setIconSize( 18 )
                .setPadding( 10 )
                .setLayout( LienzoPalette.Layout.HORIZONTAL );
        ( ( AbstractLienzoGlyphItemsPalette ) getLienzoPalette() ).getDefinitionGlyphTooltip().setPrefix( "Convert to " );

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void attachPaletteView() {
        final LienzoLayer lienzoLayer = ( LienzoLayer ) canvasHandler.getCanvas().getLayer();
        getPaletteView().attach( lienzoLayer.getLienzoLayer() );
    }

    @Override
    protected PaletteView getPaletteView() {
        return getLienzoPalette().getView();
    }

    protected LienzoPalette getLienzoPalette() {
        return ( LienzoPalette ) palette;
    }
}
