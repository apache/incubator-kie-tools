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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;

/**
 * Creates a new node on the target graph and creates/defines a new parent-child connection so new node will be added as a child of
 * given parent.
 */
@Portable
public class AddChildNodeCommand extends AbstractGraphCompositeCommand {

    private final String parentUUID;
    private final Node candidate;
    private final Point2D location;
    private transient Node<?, Edge> parent;

    public AddChildNodeCommand(final @MapsTo("parentUUID") String parentUUID,
                               final @MapsTo("candidate") Node candidate,
                               final @MapsTo("location") Point2D location) {
        this.parentUUID = PortablePreconditions.checkNotNull("parentUUID",
                                                             parentUUID);
        this.candidate = PortablePreconditions.checkNotNull("candidate",
                                                            candidate);
        this.location = location;
    }

    public AddChildNodeCommand(final Node<?, Edge> parent,
                               final Node candidate,
                               final Point2D location) {
        this(parent.getUUID(),
             candidate,
             location);
        this.parent = parent;
    }

    public AddChildNodeCommand(final Node<?, Edge> parent,
                               final Node candidate) {
        this(parent,
             candidate,
             null);
    }

    @SuppressWarnings("unchecked")
    protected AddChildNodeCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        final Node<?, Edge> parent = getParent(context);
        this.addCommand(getRegisterNodeCommand(candidate));
        this.addCommand(getSetChildNodeCommand(parent, candidate));
        if (null != location) {
            this.addCommand(getUpdateElementPositionCommand(candidate, location));
        }
        return this;
    }

    protected RegisterNodeCommand getRegisterNodeCommand(final Node candidate) {
        return new RegisterNodeCommand(candidate);
    }

    protected SetChildNodeCommand getSetChildNodeCommand(final Node<?, Edge> parent,
                                                         final Node candidate) {
        return new SetChildNodeCommand(parent, candidate);
    }

    protected UpdateElementPositionCommand getUpdateElementPositionCommand(final Node candidate,
                                                                           final Point2D location) {
        return new UpdateElementPositionCommand(candidate, location);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> allow(final GraphCommandExecutionContext context) {
        ensureInitialized(context);
        // Check if rules are present.
        if (null == context.getRuleManager()) {
            return GraphCommandResultBuilder.SUCCESS;
        }
        final Element<? extends Definition<?>> parent = (Element<? extends Definition<?>>) getParent(context);
        final Collection<RuleViolation> containmentRuleViolations =
                doEvaluate(context,
                           RuleContextBuilder.GraphContexts.containment(getGraph(context),
                                                                        parent,
                                                                        candidate));
        final Collection<RuleViolation> cardinalityRuleViolations =
                doEvaluate(context,
                           RuleContextBuilder.GraphContexts.cardinality(getGraph(context),
                                                                        Optional.of(candidate),
                                                                        Optional.of(CardinalityContext.Operation.ADD)));
        final Collection<RuleViolation> violations = new LinkedList<RuleViolation>();
        violations.addAll(containmentRuleViolations);
        violations.addAll(cardinalityRuleViolations);
        return new GraphCommandResultBuilder(violations).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return new SafeDeleteNodeCommand(getCandidate()).execute(context);
    }

    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getParent(final GraphCommandExecutionContext context) {
        if (null == parent) {
            parent = checkNodeNotNull(context,
                                      parentUUID);
        }
        return parent;
    }

    public Node<?, Edge> getParent() {
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    public Point2D getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "AddChildNodeCommand [parent=" + parentUUID + ", candidate=" + candidate.getUUID() + "]";
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return false;
    }
}
