/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Creates a new node on the target graph and creates/defines a new dock relationship so new node will be docked into the
 * given parent.
 */
@Portable
public class AddDockedNodeCommand extends AbstractGraphCompositeCommand {

    private final String parentUUID;
    private final Node candidate;
    private transient Node parent;

    public AddDockedNodeCommand(final @MapsTo("parentUUID") String parentUUID,
                                final @MapsTo("candidate") Node candidate) {
        this.parentUUID = PortablePreconditions.checkNotNull("parentUUID",
                                                             parentUUID);
        this.candidate = PortablePreconditions.checkNotNull("candidate",
                                                            candidate);
    }

    public AddDockedNodeCommand(final Node parent,
                                final Node candidate) {
        this(parent.getUUID(),
             candidate);
        this.parent = parent;
    }

    protected AddDockedNodeCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        this.addCommand(new RegisterNodeCommand(candidate))
                .addCommand(new DockNodeCommand(getParent(context),
                                                candidate));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return new SafeDeleteNodeCommand(getCandidate()).execute(context);
    }

    @SuppressWarnings("unchecked")
    private Node<?, Edge> getParent(final GraphCommandExecutionContext context) {
        if (null == parent) {
            parent = checkNodeNotNull(context,
                                      parentUUID);
        }
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }

    @Override
    public String toString() {
        return "AddDockedNodeCommand [parent=" + parentUUID + ", candidate=" + candidate.getUUID() + "]";
    }
}
