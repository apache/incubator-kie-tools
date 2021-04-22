/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CanvasCommandResultBuilder extends CommandResultBuilder<CanvasViolation> {

    public static final CommandResult<CanvasViolation> SUCCESS = new CommandResultImpl<>(CommandResult.Type.INFO,
                                                                                         new LinkedList<>()
    );

    public static CommandResult<CanvasViolation> failed() {
        return new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.emptyList());
    }

    @Override
    public CommandResult.Type getType(final CanvasViolation violation) {
        switch (violation.getViolationType()) {
            case ERROR:
                return CommandResult.Type.ERROR;
            case WARNING:
                return CommandResult.Type.WARNING;
        }
        return CommandResult.Type.INFO;
    }

    public CanvasCommandResultBuilder() {
    }

    public CanvasCommandResultBuilder(final Collection<CanvasViolation> violations) {
        super(violations);
    }

    public CanvasCommandResultBuilder(final CommandResult<RuleViolation> commandResult) {
        // Use same message and result type.
        this.setType(commandResult.getType());
        // Translate violations.
        final Iterable<RuleViolation> violations = commandResult.getViolations();
        if (null != violations) {
            final Iterator<RuleViolation> violationsIt = violations.iterator();
            while (violationsIt.hasNext()) {
                final RuleViolation ruleViolation = violationsIt.next();
                final CanvasViolation canvasViolation = CanvasViolationImpl.Builder.build(ruleViolation);
                addViolation(canvasViolation);
            }
        }
    }
}
