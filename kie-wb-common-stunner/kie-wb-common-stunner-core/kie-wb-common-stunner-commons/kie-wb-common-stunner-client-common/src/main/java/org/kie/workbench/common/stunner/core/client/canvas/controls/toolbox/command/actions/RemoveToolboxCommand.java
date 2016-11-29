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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions;

import com.google.gwt.user.client.Window;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class RemoveToolboxCommand<I> extends AbstractActionToolboxCommand<I> {

    private final CanvasCommandFactory commandFactory;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    protected RemoveToolboxCommand() {
        this( null, null, null );
    }

    @Inject
    public RemoveToolboxCommand( final DefinitionGlyphTooltip<?> glyphTooltip,
                                 final CanvasCommandFactory commandFactory,
                                 final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager ) {
        super( glyphTooltip );
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
    }

    // TODO: i18n.
    @Override
    public String getTitle() {
        return "Delete";
    }

    @Override
    public void click( final Context<AbstractCanvasHandler> context,
                       final Element element ) {
        super.click( context, element );
        // TODO: Remove use of hardcoded confirm box here & I18n.
        if ( Window.confirm( "Are you sure?" ) ) {
            canvasCommandManager.execute( context.getCanvasHandler(), commandFactory.DELETE_NODE( ( Node ) element ) );
        }

    }

    @Override
    public void destroy() {
    }

}
