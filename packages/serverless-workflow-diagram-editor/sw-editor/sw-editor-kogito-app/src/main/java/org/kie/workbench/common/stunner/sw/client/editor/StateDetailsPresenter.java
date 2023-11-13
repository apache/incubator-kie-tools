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


package org.kie.workbench.common.stunner.sw.client.editor;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.StunnerWiresShapeView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.client.command.DrawParentNodeCommand;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;

// TODO: This is just a PoC fow now. Its goal is to display state node details, by unmarshalling and drawing nodes on demand.
@Singleton
public class StateDetailsPresenter {

    private static final boolean ENABLED = false;

    @Inject
    private Marshaller marshaller;

    @Inject
    private SessionCommandManager sessionCommandManager;

    private String selectedNode = null;
    private Node presented;
    private Group decorator;

    @SuppressWarnings("all")
    void show(CanvasHandler handler, String uuid) {
        if (!ENABLED) {
            return;
        }
        if (null != selectedNode && !selectedNode.equals(uuid)) {
            close(handler);
        }
        if (null == selectedNode) {
            AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) handler;
            Node node = canvasHandler.getGraphIndex().getNode(uuid);
            if (null != node) {
                show(canvasHandler, node);
            }
        }
    }

    @SuppressWarnings("all")
    void show(CanvasHandler canvasHandler, Node sourceNode) {
        if (!ENABLED) {
            return;
        }
        selectedNode = sourceNode.getUUID();
        Object sourceNodeDef = ((View) sourceNode.getContent()).getDefinition();
        Object definition = getDetailsObject(sourceNodeDef);
        if (null != definition) {
            Promise<Node> nodeDefPromise = marshaller.unmarshallNode(definition);
            // TODO: Handle error bus.
            nodeDefPromise.then(nodeDef -> {
                Bounds sourceNodeBounds = ((View) sourceNode.getContent()).getBounds();
                draw(canvasHandler,
                     sourceNode,
                     nodeDef,
                     new Point2D(sourceNodeBounds.getX() + sourceNodeBounds.getWidth() + 15,
                                 sourceNodeBounds.getY() + 0));
                return null;
            });
        }
    }

    @SuppressWarnings("all")
    private void draw(CanvasHandler canvasHandler,
                      Node sourceNode,
                      Node node,
                      Point2D location) {
        presented = node;
        String uuid = presented.getUUID();
        DomGlobal.console.log("Showing container [" + uuid + "]");

        Bounds bounds = UpdateElementPositionCommand.computeCandidateBounds(presented, location);
        ((View) presented.getContent()).setBounds(bounds);

        // Draw graph structure.
        DrawParentNodeCommand drawParentNodeCommand = new DrawParentNodeCommand();
        drawParentNodeCommand.rootUUID = uuid;
        //drawParentNodeCommand.execute((AbstractCanvasHandler) canvasHandler);
        sessionCommandManager.execute(canvasHandler, drawParentNodeCommand);
        // Present the container.
        Shape sourceShape = canvasHandler.getCanvas().getShape(sourceNode.getUUID());
        Shape parentShape = canvasHandler.getCanvas().getShape(uuid);
        presentModalContainer(sourceShape, parentShape);
    }

    private void presentModalContainer(Shape sourceShape, Shape parentShape) {
        presentModalContainer(parentShape, () -> {
            drawBoundingPoints(sourceShape, parentShape);
        });
    }

    private void drawBoundingPoints(Shape sourceShape,
                                    Shape parentShape) {
        if (null != decorator) {
            dropDecorator();
        }
        decorator = new Group();
        Group sourceGroup = ((StunnerWiresShapeView) sourceShape.getShapeView()).getGroup();
        sourceGroup.getLayer().add(decorator);

        drawBoundingPoints(sourceShape, parentShape, decorator);
    }

    private static void presentModalContainer(Shape parentShape,
                                              Runnable onDrawn) {
        StunnerWiresShapeView wiresParent = (StunnerWiresShapeView) parentShape.getShapeView();
        Group parentGroup = wiresParent.getGroup();
        wiresParent.getShape().setShadow(new Shadow(ColorName.GREY, 5, 5, 5));
        if (true) {
            onDrawn.run();
            return;
        }
        parentGroup.setScale(new com.ait.lienzo.client.core.types.Point2D(0.5, 0.5));
        parentGroup.setAlpha(0);
        parentGroup.animate(AnimationTweener.EASE_IN,
                            AnimationProperties.toPropertyList(AnimationProperty.Properties.SCALE(1),
                                                               AnimationProperty.Properties.ALPHA(1)),
                            1000,
                            new IAnimationCallback() {
                                @Override
                                public void onStart(IAnimation animation, IAnimationHandle handle) {

                                }

                                @Override
                                public void onFrame(IAnimation animation, IAnimationHandle handle) {

                                }

                                @Override
                                public void onClose(IAnimation animation, IAnimationHandle handle) {
                                    onDrawn.run();
                                }
                            });
    }

    private static final double DECORATOR_PADDING = 15;

    private static void drawBoundingPoints(Shape sourceShape,
                                           Shape parentShape,
                                           Group decoratorGroup) {
        StunnerWiresShapeView wiresSource = (StunnerWiresShapeView) sourceShape.getShapeView();
        StunnerWiresShapeView wiresParent = (StunnerWiresShapeView) parentShape.getShapeView();
        Group sourceGroup = wiresSource.getGroup();
        BoundingBox sourceBox = sourceGroup.getComputedBoundingPoints().getBoundingBox();

        Group parentGroup = wiresParent.getGroup();
        BoundingBox parentBox = parentGroup.getComputedBoundingPoints().getBoundingBox();

        BoundingBox boundingBox = new BoundingBox();
        boundingBox.addBoundingBox(sourceBox);
        boundingBox.addBoundingBox(parentBox);

        com.ait.lienzo.client.core.types.Point2D sourceLocation = sourceGroup.getComputedLocation();

        Rectangle decorator = new Rectangle(boundingBox.getWidth() + (DECORATOR_PADDING * 2),
                                            boundingBox.getHeight() + (DECORATOR_PADDING * 2),
                                            5)
                .setListening(false)
                .setFillAlpha(0.2)
                .setFillColor(ColorName.LIGHTGREY)
                .setStrokeAlpha(1)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeWidth(1.5)
                .setDashArray(5);

        decorator.setX(sourceLocation.getX() - DECORATOR_PADDING);
        decorator.setY(sourceLocation.getY() - DECORATOR_PADDING);

        decoratorGroup.add(decorator);
        decorator.getLayer().draw();
    }

    void close(CanvasHandler canvasHandler) {
        if (!ENABLED) {
            return;
        }
        deleteNode(canvasHandler);
        dropDecorator();
        selectedNode = null;
    }

    @SuppressWarnings("all")
    private void deleteNode(CanvasHandler canvasHandler) {
        if (null != presented) {
            DomGlobal.console.log("DELETING container [" + presented.getUUID() + "]");
            DeleteNodeCommand deleteNodeCommand = new DeleteNodeCommand(presented);
            CommandResult<CanvasViolation> result = sessionCommandManager.execute(canvasHandler, deleteNodeCommand);
            DomGlobal.console.log("RESULT=" + result.getType().name());
            presented = null;
        }
    }

    private void dropDecorator() {
        if (null != decorator) {
            decorator.removeFromParent();
            decorator = null;
        }
    }

    void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        if (null != event.getCanvasHandler()) {
            if (event.getIdentifiers().size() == 1) {
                final String uuid = event.getIdentifiers().iterator().next();
                show(event.getCanvasHandler(), uuid);
            } else {
                close(event.getCanvasHandler());
            }
        }
    }

    void onCanvasClearSelectionEvent(@Observes CanvasClearSelectionEvent event) {
        if (null != event.getCanvasHandler()) {
            close(event.getCanvasHandler());
        }
    }

    // TODO: Get this info from marshallers?
    private static Object getDetailsObject(Object def) {
        if (def instanceof EventState) {
            EventState es = (EventState) def;
            OnEvent[] onEvents = es.getOnEvents();
            return onEvents;
        }
        if (def instanceof OperationState) {
            OperationState os = (OperationState) def;
            ActionNode[] actions = os.getActions();
            return actions;
        }
        if (def instanceof ForEachState) {
            ForEachState os = (ForEachState) def;
            ActionNode[] actions = os.getActions();
            return actions;
        }
        return null;
    }
}
