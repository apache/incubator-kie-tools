/*
 * Copyright 2015 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.Result;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;

/**
 * Default implementation of CommandManager
 */
public class DefaultCommandManagerImpl implements CommandManager {

    private static final CommandManager INSTANCE = new DefaultCommandManagerImpl();

    private Stack<Command> commands = new Stack<Command>();

    private DefaultCommandManagerImpl() {
        //Singleton
    }

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Result> execute( final Command command ) {
        if ( command == null ) {
            return new ArrayList<Result>() {{
                add( new DefaultResultImpl( ResultType.ERROR,
                                            "Null command" ) );
            }};
        }
        final List<Result> results = command.apply();
        if ( success( results ) ) {
            commands.push( command );
        }
        return results;
    }

    @Override
    public List<Result> undo() {
        if ( commands.isEmpty() ) {
            throw new IllegalStateException( "No commands to undo" );
        }
        return commands.pop().undo();
    }

    private boolean success( final List<Result> results ) {
        if ( results == null || results.isEmpty() ) {
            return true;
        }
        for ( Result result : results ) {
            if ( result.getType().equals( ResultType.ERROR ) ) {
                return false;
            }
        }
        return true;
    }

}
