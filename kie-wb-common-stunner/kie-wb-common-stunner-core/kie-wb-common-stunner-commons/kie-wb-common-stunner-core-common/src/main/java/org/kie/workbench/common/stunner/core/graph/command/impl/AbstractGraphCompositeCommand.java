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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CommandUtils;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.command.EmptyRulesCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGraphCompositeCommand extends AbstractCompositeCommand<GraphCommandExecutionContext, RuleViolation> {

    protected abstract CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context );

    @Override
    protected CommandResult<RuleViolation> doUndo( final GraphCommandExecutionContext context,
                                                   final Command<GraphCommandExecutionContext, RuleViolation> command ) {
        return command.undo( buildEmptyExecutionContext( context ) );
    }

    @Override
    protected CommandResult<RuleViolation> doExecute( final GraphCommandExecutionContext context,
                                                      final Command<GraphCommandExecutionContext, RuleViolation> command ) {
        return command.execute( buildEmptyExecutionContext( context ) );
    }

    @Override
    protected CommandResult<RuleViolation> buildResult( final List<CommandResult<RuleViolation>> violations ) {
        final Collection<RuleViolation> v = new LinkedList<>();
        for ( final CommandResult<RuleViolation> result : violations ) {
            v.addAll( CommandUtils.toList( result.getViolations() ) );
        }
        return new GraphCommandResultBuilder( v ).build();
    }

    protected CommandResult<RuleViolation> check( final GraphCommandExecutionContext context ) {
        // Check if rules are present.
        if ( null == context.getRulesManager() ) {
            return GraphCommandResultBuilder.RESULT_OK;
        }
        return doCheck( context );
    }

    private EmptyRulesCommandExecutionContext buildEmptyExecutionContext( final GraphCommandExecutionContext context ) {
        return new EmptyRulesCommandExecutionContext( context.getDefinitionManager(),
                context.getFactoryManager() );
    }
}
