/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.exception;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

/**
 * The bounds exceed the graph ones for the given domain.
 * The command's executor should check the bounds calling the command.
 * For example if not using or bad configured whatever drag enforcer for client side.
 */
public final class BoundsExceededException extends CommandException {

    private final Bounds candidate;
    private final double maxX;
    private final double maxY;

    public BoundsExceededException( final Command<?, ?> command,
                                    final Bounds candidate,
                                    final double maxX,
                                    final double maxY ) {
        super( "Bounds exceeded [candidate=" + candidate.toString()
                       + ", maxX=" + maxX
                       + ", maxY=" + maxY + "]",
               command );
        this.candidate = candidate;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public Bounds getCandidate() {
        return candidate;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }
}
