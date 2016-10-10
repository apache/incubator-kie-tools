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

package org.kie.workbench.common.stunner.client.widgets.session.toolbar.command;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasFullSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class UndoCommand extends AbstractToolbarCommand<DefaultCanvasFullSession> {

    @Override
    public IconType getIcon() {
        return IconType.UNDO;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Undo";
    }

    @Override
    public ToolbarCommand<DefaultCanvasFullSession> initialize( final Toolbar<DefaultCanvasFullSession> toolbar,
                                                                final DefaultCanvasFullSession session ) {
        super.initialize( toolbar, session );
        checkState();
        return this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> void execute( final ToolbarCommandCallback<T> callback ) {
        final StackCommandManager<AbstractCanvasHandler, CanvasViolation> scm = getStackCommandManager();
        if ( null != scm ) {
            final CommandResult<CanvasViolation> result =
                    getStackCommandManager().undo( session.getCanvasHandler() );
            if ( null != callback ) {
                callback.onCommandExecuted( ( T ) result );

            }
            checkState();

        }

    }

    void onCommandExecuted( @Observes CanvasCommandExecutedEvent commandExecutedEvent ) {
        checkNotNull( "commandExecutedEvent", commandExecutedEvent );
        checkState();
    }

    void onCommandUndoExecuted( @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent ) {
        checkNotNull( "commandUndoExecutedEvent", commandUndoExecutedEvent );
        checkState();
    }

    protected boolean getState() {
        if ( null != session ) {
            final StackCommandManager<AbstractCanvasHandler, CanvasViolation> canvasCommManager = getStackCommandManager();
            final boolean isHistoryEmpty = canvasCommManager == null || canvasCommManager.getRegistry().getCommandHistorySize() == 0;
            return !isHistoryEmpty;

        }
        return false;
    }

    @SuppressWarnings( "unchecked" )
    private StackCommandManager<AbstractCanvasHandler, CanvasViolation> getStackCommandManager() {
        try {
            return ( StackCommandManager<AbstractCanvasHandler, CanvasViolation> ) session.getCanvasCommandManager();
        } catch ( ClassCastException e ) {
            return null;
        }
    }

}
