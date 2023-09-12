/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolationImpl;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.violations.DockingRuleViolation;

/**
 * Registers the candidate node into the canvas and docks it into the parent node.
 */
public class AddCanvasDockedNodeCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node candidate;
    private final String ssid;

    public AddCanvasDockedNodeCommand(final Node parent,
                                      final Node candidate,
                                      final String ssid) {
        this.parent = parent;
        this.candidate = candidate;
        this.ssid = ssid;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        context.register(ssid,
                         candidate);
        context.applyElementMutation(candidate,
                                     MutationContext.STATIC);
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);
        // Dock the candidate shape into the parent one.
        if (!context.dock(parent, candidate)) {
            return new CanvasCommandResultBuilder()
                    .addViolation(CanvasViolationImpl.Builder
                                          .build(new DockingRuleViolation(parent.getUUID(), candidate.getUUID())))
                    .build();
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new DeleteCanvasNodeCommand(candidate,
                                           parent).execute(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(parent) + "," +
                "candidate=" + toUUID(candidate) + "," +
                "shapeSet=" + ssid + "]";
    }
}
