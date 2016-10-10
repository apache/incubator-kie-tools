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

package org.kie.workbench.common.stunner.core.client.components.views;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;

import javax.enterprise.context.Dependent;

/**
 * Floating view implementation for generic GWT Widgets.
 */
@Dependent
public class FloatingWidgetView extends FlowPanel implements FloatingView<IsWidget> {

    private double x;
    private double y;
    private boolean attached;
    private Timer timer;
    private int timeout = 800;
    private final HandlerRegistrationImpl handlerRegistrationManager = new HandlerRegistrationImpl();

    public FloatingWidgetView() {
        this.attached = false;
    }

    @Override
    public void destroy() {
        detach();
    }

    @Override
    public FloatingWidgetView setX( final double x ) {
        this.x = x;
        return this;
    }

    @Override
    public FloatingWidgetView setY( final double y ) {
        this.y = y;
        return this;
    }

    @Override
    public FloatingWidgetView setTimeOut( final int timeout ) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public FloatingView<IsWidget> clearTimeOut() {
        setTimeOut( -1 );
        return this;
    }

    @Override
    public FloatingWidgetView show() {
        attach();
        startTimeout();
        this.getElement().getStyle().setLeft( x, Style.Unit.PX );
        this.getElement().getStyle().setTop( y, Style.Unit.PX );
        this.getElement().getStyle().setDisplay( Style.Display.INLINE );
        return this;
    }

    @Override
    public FloatingWidgetView hide() {
        stopTimeout();
        doHide();
        return this;
    }

    private void doHide() {
        this.getElement().getStyle().setDisplay( Style.Display.NONE );
    }

    private void attach() {
        if ( !attached ) {
            RootPanel.get().add( this );
            registerHoverEventHandlers();
            this.getElement().getStyle().setPosition( Style.Position.FIXED );
            this.getElement().getStyle().setZIndex( Integer.MAX_VALUE );
            doHide();
            attached = true;

        }

    }

    private void detach() {
        if ( attached ) {
            handlerRegistrationManager.removeHandler();
            RootPanel.get().remove( this );
            attached = false;
        }

    }

    public void startTimeout() {
        if ( timeout > 0 &&
                ( null == timer || !timer.isRunning() ) ) {
            timer = new Timer() {
                @Override
                public void run() {
                    FloatingWidgetView.this.doHide();
                }
            };
            timer.schedule( timeout );

        }

    }

    public void stopTimeout() {
        if ( null != timer && timer.isRunning() ) {
            timer.cancel();
        }

    }

    private void registerHoverEventHandlers() {
        handlerRegistrationManager.register(
                this.addDomHandler( mouseOverEvent -> stopTimeout(), MouseOverEvent.getType() )
        );
        handlerRegistrationManager.register(
                this.addDomHandler( mouseOutEvent -> startTimeout(), MouseOutEvent.getType() )
        );

    }

}
