/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresHandlerFactoryImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragEndEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragStartEvent;

@ApplicationScoped
@Default
public class StunnerWiresHandlerFactory implements WiresHandlerFactory {

    private final Event<CanvasControlPointDragEndEvent> controlPointDragEndEventEvent;
    private final Event<CanvasControlPointDragStartEvent> controlPointDragStartEventEvent;
    private final Event<CanvasControlPointDoubleClickEvent> controlPointDoubleClickEventEvent;
    private WiresHandlerFactory delegate;

    @Inject
    public StunnerWiresHandlerFactory(final Event<CanvasControlPointDragStartEvent> controlPointDragStartEventEvent,
                                      final Event<CanvasControlPointDragEndEvent> controlPointDragEndEventEvent,
                                      final Event<CanvasControlPointDoubleClickEvent> controlPointDoubleClickEventEvent) {
        this.controlPointDragStartEventEvent = controlPointDragStartEventEvent;
        this.controlPointDragEndEventEvent = controlPointDragEndEventEvent;
        this.controlPointDoubleClickEventEvent = controlPointDoubleClickEventEvent;
        this.delegate = new WiresHandlerFactoryImpl();
    }

    public StunnerWiresHandlerFactory() {
        this(null, null, null);
    }

    @Override
    public WiresConnectorHandler newConnectorHandler(WiresConnector wiresConnector, WiresManager wiresManager) {
        return new StunnerWiresConnectorHandler(wiresConnector, wiresManager);
    }

    @Override
    public WiresControlPointHandler newControlPointHandler(WiresConnector wiresConnector, WiresConnectorControl wiresConnectorControl) {
        return new StunnerWiresControlPointHandler(wiresConnector, wiresConnectorControl, controlPointDragStartEventEvent, controlPointDragEndEventEvent, controlPointDoubleClickEventEvent);
    }

    @Override
    public WiresShapeHandler newShapeHandler(WiresShapeControl wiresShapeControl, WiresShapeHighlight<PickerPart.ShapePart> wiresShapeHighlight, WiresManager wiresManager) {
        return delegate.newShapeHandler(wiresShapeControl, wiresShapeHighlight, wiresManager);
    }
}