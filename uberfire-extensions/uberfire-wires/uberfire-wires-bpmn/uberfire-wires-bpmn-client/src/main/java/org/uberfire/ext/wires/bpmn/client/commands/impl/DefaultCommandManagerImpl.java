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

import java.util.Stack;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

/**
 * Default implementation of CommandManager
 */
@ApplicationScoped
public class DefaultCommandManagerImpl implements CommandManager {

    private Stack<Command> commands = new Stack<Command>();

    @Override
    public Results execute( final RuleManager ruleManager,
                            final Command command ) {
        PortablePreconditions.checkNotNull( "command",
                                            command );
        final Results results = command.apply( ruleManager );
        if ( !results.contains( ResultType.ERROR ) ) {
            commands.push( command );
        }
        return results;
    }

    @Override
    public Results undo( final RuleManager ruleManager ) {
        if ( commands.isEmpty() ) {
            throw new IllegalStateException( "No commands to undo" );
        }
        return commands.pop().undo( ruleManager );
    }

}
