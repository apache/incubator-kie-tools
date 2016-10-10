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

package org.kie.workbench.common.stunner.client.widgets.session.presenter.impl;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.CanvasSessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistationControl;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.CanvasSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractCanvasSessionPresenter<S extends CanvasSession<AbstractCanvas, AbstractCanvasHandler>>
        implements CanvasSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S> {

    ErrorPopupPresenter errorPopupPresenter;
    View view;
    protected S session;

    @Inject
    public AbstractCanvasSessionPresenter( final ErrorPopupPresenter errorPopupPresenter,
                                           final View view ) {
        this.errorPopupPresenter = errorPopupPresenter;
        this.view = view;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void initialize( final S session,
                            final int width,
                            final int height ) {
        this.session = session;
        // Create the canvas with a given size.
        session.getCanvas().initialize( width, height );
        // Initialize the canvas to handle.
        getCanvasHandler().initialize( session.getCanvas() );
        // Initialize the view.
        initializeView();
        doInitialize( session, width, height );
        afterInitialize( session, width, height );

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

    protected void doInitialize( final S session,
                                 final int width,
                                 final int height ) {
    }

    protected void afterInitialize( final S session,
                                    final int width,
                                    final int height ) {
    }

    protected void initializeView() {
        view.setCanvas( session.getCanvas().getView().asWidget() );

    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return session.getCanvasHandler();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    protected void onCanvasSessionDisposed( @Observes SessionDisposedEvent sessionDisposedEvent ) {
        checkNotNull( "sessionDisposedEvent", sessionDisposedEvent );
        if ( null != session && session.equals( sessionDisposedEvent.getSession() ) ) {
            onDisposeSession();
        }
    }

    protected void onCanvasSessionPaused( @Observes SessionPausedEvent sessionPausedEvent ) {
        checkNotNull( "sessionPausedEvent", sessionPausedEvent );
        if ( null != session && session.equals( sessionPausedEvent.getSession() ) ) {
            onPauseSession();
        }
    }

    private void onDisposeSession() {
        // Implementations can clear its state here.
        disposeSession();
        // Destroy the view.
        this.view.destroy();
        // Nullify
        this.view = null;
        this.session = null;

    }

    private void onPauseSession() {
        pauseSession();
    }

    protected abstract void disposeSession();

    protected abstract void pauseSession();

    protected void showError( final ClientRuntimeError error ) {
        final String message = error.getCause() != null ? error.getCause() : error.getMessage();
        showError( message );
    }

    protected void showError( final Throwable throwable ) {
        errorPopupPresenter.showMessage( throwable != null ? throwable.getMessage() : "Error" );
    }

    protected void showError( final String message ) {
        errorPopupPresenter.showMessage( message );
    }

}
