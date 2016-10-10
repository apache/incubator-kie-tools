/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.components.actions.NameEditBox;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class CanvasNameEditionControlImpl
        extends AbstractCanvasHandlerRegistrationControl
        implements CanvasNameEditionControl<AbstractCanvasHandler, Element> {

    private static final int FLOATING_VIEW_TIMEOUT = 3000;

    FloatingView<IsWidget> floatingView;
    NameEditBox<AbstractCanvasHandler, Element> nameEditBox;
    Event<CanvasElementSelectedEvent> elementSelectedEvent;

    private String uuid;

    @Inject
    public CanvasNameEditionControlImpl( final FloatingView<IsWidget> floatingView,
                                         final NameEditBox<AbstractCanvasHandler, Element> nameEditBox,
                                         final Event<CanvasElementSelectedEvent> elementSelectedEvent ) {
        this.floatingView = floatingView;
        this.nameEditBox = nameEditBox;
        this.elementSelectedEvent = elementSelectedEvent;
        this.uuid = null;
    }

    @Override
    public void enable( final AbstractCanvasHandler canvasHandler ) {
        super.enable( canvasHandler );
        nameEditBox.initialize( canvasHandler, () -> {
            floatingView.hide();
            elementSelectedEvent.fire( new CanvasElementSelectedEvent( canvasHandler, CanvasNameEditionControlImpl.this.uuid ) );
        } );
        floatingView
                .hide()
                .setTimeOut( FLOATING_VIEW_TIMEOUT )
                .add( nameEditBox.asWidget() );

    }

    @Override
    public void register( final Element element ) {
        final Shape<?> shape = getCanvas().getShape( element.getUUID() );
        if ( null != shape ) {
            final ShapeView shapeView = shape.getShapeView();
            if ( shapeView instanceof HasEventHandlers ) {
                final HasEventHandlers hasEventHandlers = ( HasEventHandlers ) shapeView;
                // Double click event.
                final MouseDoubleClickHandler doubleClickHandler = new MouseDoubleClickHandler() {

                    @Override
                    public void handle( final MouseDoubleClickEvent event ) {
                        CanvasNameEditionControlImpl.this.show( element, event.getClientX(), event.getClientY() );

                    }

                };
                hasEventHandlers.addHandler( ViewEventType.MOUSE_DBL_CLICK, doubleClickHandler );
                registerHandler( shape.getUUID(), doubleClickHandler );
            }

        }

    }

    @Override
    public CanvasNameEditionControl<AbstractCanvasHandler, Element> show( final Element item,
                                                                          final double x,
                                                                          final double y ) {
        this.uuid = item.getUUID();
        nameEditBox.show( item );
        floatingView
                .setX( x - 40 )
                .setY( y )
                .show();
        return this;
    }

    @Override
    public CanvasNameEditionControl<AbstractCanvasHandler, Element> hide() {
        this.uuid = null;
        nameEditBox.hide();
        floatingView.hide();
        return this;
    }

    @Override
    protected void doDisable() {
        super.doDisable();
        this.uuid = null;
        nameEditBox.hide();
        nameEditBox = null;
        floatingView.destroy();
        floatingView = null;

    }

    private Canvas getCanvas() {
        return canvasHandler.getCanvas();
    }

    void onKeyDownEvent( @Observes KeyDownEvent keyDownEvent ) {
        checkNotNull( "keyDownEvent", keyDownEvent );
        final KeyboardEvent.Key key = keyDownEvent.getKey();
        if ( null != key && KeyboardEvent.Key.ESC.equals( key ) ) {
            hide();

        }

    }

    void onCanvasFocusedEvent( @Observes CanvasFocusedEvent canvasFocusedEvent ) {
        checkNotNull( "canvasFocusedEvent", canvasFocusedEvent );
        hide();
    }

}
