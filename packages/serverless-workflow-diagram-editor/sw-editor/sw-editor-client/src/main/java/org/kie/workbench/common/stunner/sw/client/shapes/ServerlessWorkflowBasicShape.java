package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.MultiPath;
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

public abstract class ServerlessWorkflowBasicShape<T extends ServerlessWorkflowBasicShape> extends WiresShapeViewExt<T> {

    private ShapeStateDefaultHandler shapeStateHandler = new ShapeStateDefaultHandler().setBorderShape((() -> this))
            .setBackgroundShape(() -> this);

    private final HandlerRegistration mouseEnterHandler;

    private final HandlerRegistration mouseExitHandler;

    public ServerlessWorkflowBasicShape(MultiPath path) {
        super(path, new WiresLayoutContainer());
        setEventHandlerManager(new ViewEventHandlerManager(getShape(), getShape(), ShapeViewSupportedEvents.ALL_DESKTOP_EVENT_TYPES));

        getShape().setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        NodeMouseEnterHandler enterEvent = event -> {
            if (shapeStateHandler.getShapeState() == ShapeState.SELECTED) {
                return;
            }
            shapeStateHandler.applyState(ShapeState.HIGHLIGHT);
        };
        mouseEnterHandler = getShape().addNodeMouseEnterHandler(enterEvent);

        NodeMouseExitHandler exitEvent = event -> {
            if (shapeStateHandler.getShapeState() == ShapeState.SELECTED) {
                return;
            }
            shapeStateHandler.applyState(ShapeState.NONE);
        };
        mouseExitHandler = getShape().addNodeMouseExitHandler(exitEvent);
    }

    public ShapeStateHandler getShapeStateHandler() {
        return shapeStateHandler;
    }

    public AbstractShape<ServerlessWorkflowBasicShape> asAbstractShape() {
        return new AbstractShape<ServerlessWorkflowBasicShape>() {
            @Override
            public ShapeStateHandler getShapeStateHandler() {
                return ServerlessWorkflowBasicShape.this.getShapeStateHandler();
            }

            @Override
            public void setUUID(String uuid) {
                ServerlessWorkflowBasicShape.this.setUUID(uuid);
            }

            @Override
            public String getUUID() {
                return ServerlessWorkflowBasicShape.this.getUUID();
            }

            @Override
            public ServerlessWorkflowBasicShape getShapeView() {
                return ServerlessWorkflowBasicShape.this;
            }
        };
    }

    @Override
    public void destroy() {
        super.destroy();
        shapeStateHandler = null;
        mouseEnterHandler.removeHandler();
        mouseExitHandler.removeHandler();
    }
}
