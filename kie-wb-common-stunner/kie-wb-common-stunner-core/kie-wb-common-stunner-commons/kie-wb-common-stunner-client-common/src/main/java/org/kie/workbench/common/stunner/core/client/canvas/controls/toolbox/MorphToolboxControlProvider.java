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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.palette.AbstractPaletteMorphCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A toolbox control provider implementation that provides buttons for each of the target
 * types in which source node can morph into.
 */
@Dependent
public class MorphToolboxControlProvider extends AbstractToolboxControlProvider {

    private static Logger LOGGER = Logger.getLogger( MorphToolboxControlProvider.class.getName() );

    private final AbstractPaletteMorphCommand morphCommand;
    private final DefinitionUtils definitionUtils;

    protected MorphToolboxControlProvider() {
        this( null, null, null );
    }

    @Inject
    public MorphToolboxControlProvider( final ToolboxFactory toolboxFactory,
                                        final AbstractPaletteMorphCommand morphCommand,
                                        final DefinitionUtils definitionUtils ) {
        super( toolboxFactory );
        this.morphCommand = morphCommand;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public boolean supports( final Object definition ) {
        return true;
    }

    @Override
    public ToolboxButtonGrid getGrid( final AbstractCanvasHandler context,
                                      final Element item ) {
        final ToolboxButtonGridBuilder buttonGridBuilder = toolboxFactory.toolboxGridBuilder();
        return buttonGridBuilder
                .setRows( 1 )
                .setColumns( 1 )
                .setPadding( DEFAULT_PADDING )
                .build();
    }

    @Override
    public ToolboxBuilder.Direction getOn() {
        return ToolboxBuilder.Direction.SOUTH_WEST;
    }

    @Override
    public ToolboxBuilder.Direction getTowards() {
        return ToolboxBuilder.Direction.SOUTH_EAST;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ToolboxCommand<AbstractCanvasHandler, ?>> getCommands( final AbstractCanvasHandler context,
                                                                       final Element item ) {
        return !hasMorphTargets( item ) ? null :
                new ArrayList<ToolboxCommand<AbstractCanvasHandler, ?>>( 1 ) {{
                    add( morphCommand );
                }};
    }

    private boolean hasMorphTargets( final Element item ) {
        try {
            final Object def = ( ( Definition<?> ) item.getContent() ).getDefinition();
            return definitionUtils.hasMorphTargets( def );
        } catch ( final ClassCastException e ) {
            LOGGER.log( Level.SEVERE, "Only contents for type Definition are expected on the toolbox " +
                    "morphing control provider.", e );
            return false;
        }
    }
}
