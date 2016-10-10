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
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.AbstractToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

public abstract class RemoveToolboxCommand<I> extends AbstractToolboxCommand<I> {

    CanvasCommandFactory commandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    private final I icon;

    public RemoveToolboxCommand( final CanvasCommandFactory commandFactory,
                                 final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                 final I icon ) {
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.icon = icon;
    }

    @Override
    public I getIcon( final double width, final double height ) {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Remove";
    }

    @Override
    public void click( final Context<AbstractCanvasHandler> context,
                       final Element element ) {
        // TODO: Remove use of hardcoded confirm box here.
        if ( Window.confirm( "Are you sure?" ) ) {
            canvasCommandManager.execute( context.getCanvasHandler(), commandFactory.DELETE_NODE( ( Node ) element ) );
        }

    }

    @Override
    public void destroy() {
        this.commandFactory = null;
        this.canvasCommandManager = null;
    }

}
