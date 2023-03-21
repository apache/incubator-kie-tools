/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;

public abstract class ServerlessWorkflowShapeView<VIEW extends ServerlessWorkflowShapeView<VIEW>> extends WiresShapeViewExt<VIEW> {

    protected final static String SHAPE_STROKE_COLOR = "#ccc";
    protected final static String SHAPE_FILL_COLOR = "#fff";
    protected final static double SHAPE_STROKE_WIDTH = 2.00;
    protected final static String SHAPE_TITLE_FONT_COLOR = "#929292";
    protected final static double SHAPE_TITLE_FONT_SIZE = 12.00;
    protected final static String SHAPE_TITLE_FONT_FAMILY = "Open Sans";

    private ServerlessWorkflowShape<VIEW> controller;

    private ShapeStateDefaultHandler shapeStateHandler = new ShapeStateDefaultHandler().setBorderShape((() -> this))
            .setBackgroundShape(this);

    private HandlerRegistration mouseEnterHandler;

    private HandlerRegistration mouseExitHandler;

    public ServerlessWorkflowShapeView(MultiPath path, String title) {
        this(path);
        setTitle(title);
        setTitlePosition(VerticalAlignment.MIDDLE, HorizontalAlignment.CENTER, ReferencePosition.INSIDE, Orientation.HORIZONTAL);
        setTitleFontColor(SHAPE_TITLE_FONT_COLOR);
        setTitleFontFamily(SHAPE_TITLE_FONT_FAMILY);
        setTitleFontSize(SHAPE_TITLE_FONT_SIZE);
        setTitleStrokeWidth(0);
        setTitleStrokeAlpha(0);
        isTitleListening(false);
    }

    public ServerlessWorkflowShapeView(MultiPath path) {
        super(path
                      .setAlpha(1.00)
                      .setDraggable(false)
                      .setListening(true)
                      .setFillColor(SHAPE_FILL_COLOR)
                      .setStrokeColor(SHAPE_STROKE_COLOR)
                      .setStrokeWidth(SHAPE_STROKE_WIDTH), new WiresLayoutContainer());

        setEventHandlerManager(new ViewEventHandlerManager(getShape(), getShape(), ShapeViewSupportedEvents.ALL_DESKTOP_EVENT_TYPES));

        getShape().setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);
    }

    public void setController(ServerlessWorkflowShape<VIEW> controller) {
        this.controller = controller;
        mouseEnterHandler = getShape().addNodeMouseEnterHandler(getEnterHandler());
        mouseExitHandler = getShape().addNodeMouseExitHandler(getExitHandler());
    }

    public NodeMouseExitHandler getExitHandler() {
        return controller.getExitHandler();
    }

    public NodeMouseEnterHandler getEnterHandler() {
        return controller.getEnterHandler();
    }

    public void applyState(ShapeState state) {
        this.asAbstractShape().applyState(state);
    }

    public ShapeState getShapeState() {
        return shapeStateHandler.getShapeState();
    }

    public AbstractShape<ServerlessWorkflowShapeView<VIEW>> asAbstractShape() {
        return new AbstractShape<ServerlessWorkflowShapeView<VIEW>>() {
            @Override
            public ShapeStateHandler getShapeStateHandler() {
                return ServerlessWorkflowShapeView.this.shapeStateHandler;
            }

            @Override
            public void setUUID(String uuid) {
                ServerlessWorkflowShapeView.this.setUUID(uuid);
            }

            @Override
            public String getUUID() {
                return ServerlessWorkflowShapeView.this.getUUID();
            }

            @Override
            public ServerlessWorkflowShapeView<VIEW> getShapeView() {
                return ServerlessWorkflowShapeView.this;
            }

            @Override
            public void applyState(ShapeState state) {
                super.applyState(state);
                getShapeView().getShape().getLayer().batch();
            }
        };
    }

    protected Circle newCircle(double radius) {
        return setDefaultSettings(new Circle(radius));
    }

    protected Group newGroup() {
        return setDefaultSettings(new Group());
    }

    protected MultiPath newMultiPath(String path) {
        return setDefaultSettings(new MultiPath(path));
    }

    @SuppressWarnings("unchecked")
    private <N extends Node> N setDefaultSettings(N shape) {
        return (N) shape
                .setDraggable(false)
                .setListening(false)
                .setAlpha(1.00);
    }

    @Override
    public void destroy() {
        super.destroy();
        shapeStateHandler = null;
        mouseEnterHandler.removeHandler();
        mouseExitHandler.removeHandler();
    }
}
