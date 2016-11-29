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

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class CanvasCommandResultBuilder extends CommandResultBuilder<CanvasViolation> {

    public static final CommandResult<CanvasViolation> SUCCESS = new CommandResultImpl<>(
            CommandResult.Type.INFO,
            RESULT_SUCCESS,
            new LinkedList<>()
    );

    public static final CommandResult<CanvasViolation> FAILED = new CommandResultImpl<>(
            CommandResult.Type.ERROR,
            RESULT_FAILED,
            new LinkedList<>()
    );

    public CanvasCommandResultBuilder() {
    }

    public CanvasCommandResultBuilder( final Collection<CanvasViolation> violations ) {
        super( violations );
    }

    public CanvasCommandResultBuilder( final CommandResult<RuleViolation> commandResult ) {
        // Use same message and result type.
        this.setMessage( commandResult.getMessage() );
        this.setType( commandResult.getType() );
        // Translate violations.
        final Iterable<RuleViolation> violations = commandResult.getViolations();
        if ( null != violations ) {
            final Iterator<RuleViolation> violationsIt = violations.iterator();
            while ( violationsIt.hasNext() ) {
                final RuleViolation ruleViolation = violationsIt.next();
                final CanvasViolation canvasViolation =
                        new CanvasViolationImpl.CanvasViolationBuilder( ruleViolation )
                                .build();
                addViolation( canvasViolation );
            }
        }
    }

    @Override
    public boolean isError( final CanvasViolation violation ) {
        return RuleViolation.Type.ERROR.equals( violation.getViolationType() );
    }

    @Override
    public String getMessage( final CanvasViolation violation ) {
        return violation.getMessage();
    }
}
