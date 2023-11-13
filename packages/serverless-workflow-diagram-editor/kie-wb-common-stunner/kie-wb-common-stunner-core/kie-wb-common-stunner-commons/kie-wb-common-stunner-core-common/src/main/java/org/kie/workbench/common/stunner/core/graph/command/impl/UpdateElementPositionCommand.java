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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to update an element's bounds.
 */
public final class UpdateElementPositionCommand extends AbstractGraphCommand {

    private final String uuid;
    private final Point2D location;
    private final Point2D previousLocation;
    private transient Node<? extends View<?>, Edge> node;

    public UpdateElementPositionCommand(final String uuid,
                                        final Point2D location,
                                        final Point2D previousLocation) {
        this.uuid = checkNotNull("uuid", uuid);
        this.location = checkNotNull("location", location);
        this.previousLocation = checkNotNull("previousLocation", previousLocation);
        this.node = null;
    }

    public UpdateElementPositionCommand(final Node<? extends View<?>, Edge> node,
                                        final Point2D location) {
        this(node.getUUID(),
             location,
             GraphUtils.getPosition(node.getContent()));
        this.node = checkNotNull("node", node);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public Point2D getLocation() {
        return location;
    }

    public Point2D getPreviousLocation() {
        return previousLocation;
    }

    public Node<?, Edge> getNode() {
        return node;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return execute(context,
                       bounds -> {
                       });
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        return execute(context,
                       bounds -> node.getContent().setBounds(bounds));
    }

    @SuppressWarnings("unchecked")
    private CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context,
                                                 final Consumer<Bounds> boundsConsumer) {
        final Bounds targetBounds = getTargetBounds(getNodeNotNull(context));
        boundsConsumer.accept(targetBounds);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private Bounds getTargetBounds(final Element<? extends View<?>> element) {
        return computeCandidateBounds(element, location);
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final UpdateElementPositionCommand undoCommand = new UpdateElementPositionCommand(getNodeNotNull(context),
                                                                                          previousLocation);
        return undoCommand.execute(context);
    }

    private Node<? extends View<?>, Edge> getNodeNotNull(final GraphCommandExecutionContext context) {
        if (null == node) {
            node = super.getNodeNotNull(context,
                                        uuid);
        }
        return node;
    }

    public static Bounds computeCandidateBounds(final Element<? extends View<?>> element,
                                                final Point2D location) {
        final double[] oldSize = GraphUtils.getNodeSize(element.getContent());
        final double w = oldSize[0];
        final double h = oldSize[1];
        return Bounds.create(location.getX(),
                             location.getY(),
                             location.getX() + w,
                             location.getY() + h);
    }

    @Override
    public String toString() {
        return "UpdateElementPositionCommand " +
                "[element=" + uuid +
                ", location=" + location +
                ", previousLocation=" + previousLocation +
                "]";
    }
}
