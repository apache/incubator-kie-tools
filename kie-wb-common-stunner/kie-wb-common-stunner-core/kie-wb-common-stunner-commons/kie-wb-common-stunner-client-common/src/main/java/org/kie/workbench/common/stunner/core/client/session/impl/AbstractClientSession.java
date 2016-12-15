/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistationControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractClientSession implements ClientSession<AbstractCanvas, AbstractCanvasHandler> {

    private final transient AbstractCanvas canvas;
    private final transient AbstractCanvasHandler canvasHandler;
    private final transient String uuid;
    boolean isOpened;

    public AbstractClientSession( final AbstractCanvas canvas,
                                  final AbstractCanvasHandler canvasHandler ) {
        this.uuid = UUID.uuid();
        this.canvas = canvas;
        this.canvasHandler = canvasHandler;
        this.isOpened = false;
    }

    protected abstract void doOpen();

    protected abstract void doPause();

    protected abstract void doResume();

    protected abstract void doDispose();

    public void open() {
        doOpen();
        this.isOpened = true;
    }

    public void pause() {
        if ( !isOpened ) {
            throw new IllegalStateException( "Session cannot be paused as it has been not opened yet." );
        }
        doPause();
    }

    public void resume() {
        if ( !isOpened ) {
            throw new IllegalStateException( "Session cannot be resumed as it has been not opened yet." );
        }
        doResume();
    }

    public void dispose() {
        if ( !isOpened ) {
            throw new IllegalStateException( "Session cannot be disposed as it has been not opened yet." );
        }
        doDispose();
        canvasHandler.destroy();
        isOpened = false;
    }

    @Override
    public AbstractCanvas getCanvas() {
        return canvas;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    protected void enableControl( final CanvasControl<AbstractCanvasHandler> control, final AbstractCanvasHandler handler ) {
        if ( null != control ) {
            control.enable( handler );
        }

    }

    protected void enableControl( final CanvasControl<AbstractCanvas> control, final AbstractCanvas handler ) {
        if ( null != control ) {
            control.enable( handler );
        }

    }

    protected void fireRegistrationListeners( final CanvasControl<AbstractCanvasHandler> control,
                                              final Element element,
                                              final boolean add ) {
        if ( null != control && null != element && control instanceof CanvasRegistationControl ) {
            final CanvasRegistationControl<AbstractCanvasHandler, Element> registationControl =
                    ( CanvasRegistationControl<AbstractCanvasHandler, Element> ) control;
            if ( add ) {
                registationControl.register( element );

            } else {
                registationControl.deregister( element );

            }

        }

    }

    protected void fireRegistrationListeners( final CanvasControl<AbstractCanvas> control,
                                              final Shape shape,
                                              final boolean add ) {
        if ( null != control && null != shape && control instanceof CanvasRegistationControl ) {
            final CanvasRegistationControl<AbstractCanvas, Shape> registationControl =
                    ( CanvasRegistationControl<AbstractCanvas, Shape> ) control;
            if ( add ) {
                registationControl.register( shape );

            } else {
                registationControl.deregister( shape );

            }

        }

    }

    protected void fireRegistrationUpdateListeners( final CanvasControl<AbstractCanvasHandler> control,
                                                    final Element element ) {
        if ( null != control && null != element && control instanceof AbstractCanvasHandlerRegistrationControl ) {
            final AbstractCanvasHandlerRegistrationControl registationControl =
                    ( AbstractCanvasHandlerRegistrationControl ) control;
            registationControl.update( element );

        }

    }

    protected void fireRegistrationClearListeners( final CanvasControl<AbstractCanvasHandler> control ) {
        if ( null != control && control instanceof AbstractCanvasHandlerRegistrationControl ) {
            final AbstractCanvasHandlerRegistrationControl registationControl =
                    ( AbstractCanvasHandlerRegistrationControl ) control;
            registationControl.deregisterAll();

        }

    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractClientSession ) ) {
            return false;
        }
        AbstractClientSession that = ( AbstractClientSession ) o;
        return uuid.equals( that.uuid );
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }

    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [uuid=" + uuid + "]";
    }
}
