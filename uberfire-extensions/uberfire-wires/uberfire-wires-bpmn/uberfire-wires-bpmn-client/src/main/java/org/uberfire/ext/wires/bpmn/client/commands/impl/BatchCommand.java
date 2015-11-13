/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.bpmn.client.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

/**
 * A batch of Commands to be executed as an atomic unit
 */
public class BatchCommand implements Command {

    private List<Command> commands;

    public BatchCommand( final List<Command> commands ) {
        this.commands = PortablePreconditions.checkNotNull( "commands",
                                                            commands );
    }

    public BatchCommand( final Command... commands ) {
        this.commands = Arrays.asList( PortablePreconditions.checkNotNull( "commands",
                                                                           commands ) );
    }

    @Override
    public Results apply( final RuleManager ruleManager ) {
        final Results results = new DefaultResultsImpl();
        final Stack<Command> appliedCommands = new Stack<Command>();
        for ( Command command : commands ) {
            results.getMessages().addAll( command.apply( ruleManager ).getMessages() );
            if ( results.contains( ResultType.ERROR ) ) {
                for ( Command undo : appliedCommands ) {
                    undo.undo( ruleManager );
                }
                return results;
            } else {
                appliedCommands.add( command );
            }
        }
        return results;
    }

    @Override
    public Results undo( final RuleManager ruleManager ) {
        final Results results = new DefaultResultsImpl();
        final Stack<Command> appliedCommands = new Stack<Command>();
        for ( Command command : commands ) {
            results.getMessages().addAll( command.undo( ruleManager ).getMessages() );
            if ( results.contains( ResultType.ERROR ) ) {
                for ( Command cmd : appliedCommands ) {
                    cmd.apply( ruleManager );
                }
                return results;
            } else {
                appliedCommands.add( command );
            }
        }
        return results;
    }

}
