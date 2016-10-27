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

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManagerFactory;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManager;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collection;

@Dependent
public class CanvasStackCommandManager implements CanvasCommandManager<AbstractCanvasHandler>, StackCommandManager<AbstractCanvasHandler, CanvasViolation> {

    private final StackCommandManager<AbstractCanvasHandler, CanvasViolation> stackCommandManager;

    protected CanvasStackCommandManager() {
        this.stackCommandManager = null;
    }

    @Inject
    public CanvasStackCommandManager( final CommandManagerFactory commandManagerFactory,
                                      final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent,
                                      final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent,
                                      final Event<CanvasUndoCommandExecutedEvent> canvasUndoCommandExecutedEvent ) {
        this.stackCommandManager =
                commandManagerFactory
                        .newStackCommandManagerFor(
                                new CanvasCommandManagerImpl( isCanvasCommandAllowedEvent,
                                        canvasCommandExecutedEvent,
                                        canvasUndoCommandExecutedEvent,
                                        commandManagerFactory ) );
    }

    @Override
    public CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> getRegistry() {
        return stackCommandManager.getRegistry();
    }

    @Override
    public BatchCommandManager<AbstractCanvasHandler, CanvasViolation> batch( final Command<AbstractCanvasHandler, CanvasViolation> command ) {
        return stackCommandManager.batch( command );
    }

    @Override
    public BatchCommandResult<CanvasViolation> executeBatch( final AbstractCanvasHandler context ) {
        return stackCommandManager.executeBatch( context );
    }

    @Override
    public BatchCommandResult<CanvasViolation> undoBatch( final AbstractCanvasHandler context ) {
        return stackCommandManager.undoBatch( context );
    }

    @Override
    public Collection<Command<AbstractCanvasHandler, CanvasViolation>> getBatchCommands() {
        return stackCommandManager.getBatchCommands();
    }

    @Override
    public CommandResult<CanvasViolation> allow( final AbstractCanvasHandler context,
                                                 final Command<AbstractCanvasHandler, CanvasViolation> command ) {
        return stackCommandManager.allow( context, command );
    }

    @Override
    public CommandResult<CanvasViolation> execute( AbstractCanvasHandler context, Command<AbstractCanvasHandler, CanvasViolation> command ) {
        return stackCommandManager.execute( context, command );
    }

    @Override
    public CommandResult<CanvasViolation> undo( final AbstractCanvasHandler context,
                                                final Command<AbstractCanvasHandler, CanvasViolation> command ) {
        return stackCommandManager.undo( context, command );
    }

    @Override
    public CommandResult<CanvasViolation> undo( AbstractCanvasHandler context ) {
        return stackCommandManager.undo( context );
    }

    @Override
    public String toString() {
        return "[" + super.toString() + "]";
    }
}
