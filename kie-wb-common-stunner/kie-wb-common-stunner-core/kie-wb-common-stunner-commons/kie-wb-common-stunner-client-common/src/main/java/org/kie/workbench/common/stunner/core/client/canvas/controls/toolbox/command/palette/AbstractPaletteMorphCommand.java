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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;

import javax.enterprise.event.Event;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractPaletteMorphCommand<I> extends AbstractPaletteCommand<I> {

    protected final DefinitionUtils definitionUtils;
    protected final CanvasCommandFactory commandFactory;
    protected final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    protected final Event<CanvasElementSelectedEvent> elementSelectedEvent;

    protected final Map<String, MorphDefinition> morphDefinitions = new HashMap<>();

    public AbstractPaletteMorphCommand( final DefinitionUtils definitionUtils,
                                        final CanvasCommandFactory commandFactory,
                                        final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                        final ClientFactoryService clientFactoryServices,
                                        final CommonLookups commonLookups,
                                        final ShapeManager shapeManager,
                                        final DefinitionsPaletteBuilder definitionsPaletteBuilder,
                                        final Palette<HasPaletteItems<? extends GlyphPaletteItem>> palette,
                                        final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                        final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                        final GraphBoundsIndexer graphBoundsIndexer,
                                        final I icon,
                                        final Event<CanvasElementSelectedEvent> elementSelectedEvent ) {
        super( clientFactoryServices, commonLookups, shapeManager, definitionsPaletteBuilder, palette,
                nodeDragProxyFactory, nodeBuilderControl, graphBoundsIndexer, icon );
        this.definitionUtils = definitionUtils;
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.elementSelectedEvent = elementSelectedEvent;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Set<String> getDefinitions() {
        this.morphDefinitions.clear();
        final Object source = this.sourceNode.getContent().getDefinition();
        final String id = getDefinitionManager().adapters().forDefinition().getId( source );
        final MorphAdapter<Object> morphAdapter = getDefinitionManager().adapters().registry().getMorphAdapter( source.getClass() );
        final Iterable<MorphDefinition> morphDefinitions = morphAdapter.getMorphDefinitions( source );
        if ( null != morphDefinitions && morphDefinitions.iterator().hasNext() ) {
            for ( final MorphDefinition morphDefinition : morphDefinitions ) {
                final Iterable<String> morphTargets = morphAdapter.getTargets( source, morphDefinition );
                if ( null != morphTargets && morphTargets.iterator().hasNext() ) {
                    for ( final String morphTarget : morphTargets ) {
                        if ( !id.equals( morphTarget ) ) {
                            this.morphDefinitions.put( morphTarget, morphDefinition );
                        }
                    }
                }
            }
        }
        if ( !this.morphDefinitions.isEmpty() ) {
            return new HashSet<>( this.morphDefinitions.keySet() );
        }
        return null;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void onItemSelected( final String definitionId,
                                   final double x,
                                   final double y ) {
        final MorphDefinition morphDefinition = morphDefinitions.get( definitionId );
        final Node node = ( Node ) sourceNode;
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        canvasCommandManager.execute( canvasHandler,
                commandFactory.MORPH_NODE( node, morphDefinition, definitionId, ssid ) );
        this.morphDefinitions.clear();
        clear();
        fireElementSelectedEvent( elementSelectedEvent, canvasHandler, node.getUUID() );
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

}
