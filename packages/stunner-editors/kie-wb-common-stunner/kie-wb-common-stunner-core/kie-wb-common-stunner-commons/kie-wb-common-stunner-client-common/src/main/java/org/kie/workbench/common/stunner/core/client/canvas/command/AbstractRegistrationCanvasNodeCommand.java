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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public abstract class AbstractRegistrationCanvasNodeCommand extends AbstractCanvasCommand {

    private final Node<? extends View<?>, Edge> candidate;
    private final String shapeSetId;

    protected AbstractRegistrationCanvasNodeCommand(final Node<? extends View<?>, Edge> candidate,
                                                    final String shapeSetId) {
        this.candidate = candidate;
        this.shapeSetId = shapeSetId;
    }

    @SuppressWarnings("unchecked")
    protected void register(final AbstractCanvasHandler context) {
        context.register(shapeSetId,
                         candidate);
        // Update view bounds for the element, if not set, by using the values from the shape view.
        final double[] size = GraphUtils.getNodeSize(candidate.getContent());
        if (size[0] <= 0 || size[1] <= 0) {
            final Shape shape = context.getCanvas().getShape(candidate.getUUID());
            final ShapeView shapeView = shape.getShapeView();
            final Point2D location = GraphUtils.getPosition(candidate.getContent());
            final BoundingBox boundingBox = shapeView.getBoundingBox();
            candidate.getContent()
                    .setBounds(Bounds.create(location.getX(),
                                             location.getY(),
                                             location.getX() + boundingBox.getWidth(),
                                             location.getY() + boundingBox.getHeight()));
        }
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        register(context);
        context.applyElementMutation(candidate,
                                     MutationContext.STATIC);
        return buildResult();
    }

    public Node getCandidate() {
        return candidate;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }
}
