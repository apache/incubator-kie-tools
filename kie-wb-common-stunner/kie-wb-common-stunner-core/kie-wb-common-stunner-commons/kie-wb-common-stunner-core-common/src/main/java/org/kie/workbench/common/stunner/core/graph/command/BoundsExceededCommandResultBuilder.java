/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command;

import java.util.Collections;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

@NonPortable
public class BoundsExceededCommandResultBuilder extends CommandResultBuilder<RuleViolation> {

    public BoundsExceededCommandResultBuilder(final String uuid,
                                              final Bounds bounds) {
        super(Collections.emptyList());
        this.setType(CommandResult.Type.ERROR);
        this.setMessage("Bounds exceeded [candidate=" + uuid
                                + ", maxX=" + bounds.getLowerRight().getX()
                                + ", maxY=" + bounds.getLowerRight().getY()
                                + "]");
    }

    @Override
    public boolean isError(final RuleViolation violation) {
        return true;
    }

    @Override
    public String getMessage(final RuleViolation violation) {
        return violation.getMessage();
    }
}
