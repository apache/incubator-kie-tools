/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.widgets.splash;

import org.uberfire.client.workbench.widgets.common.Modal;
import org.uberfire.mvp.ParameterizedCommand;

import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Skeleton for splash screen
 */
public class SplashView
        extends Composite
        implements HasCloseHandlers<SplashView> {

    private Boolean showAgain = null;

    private final Modal modal = getModal();

    Modal getModal() {
        return new Modal( true, true );
    }

    final SplashModalFooter footer = new SplashModalFooter( new ParameterizedCommand<Boolean>() {
        @Override
        public void execute( final Boolean parameter ) {
            showAgain = parameter;
            hide();
        }
    } );

    public SplashView() {
        modal.addHideHandler( createHideHandler() );

        final SimplePanel panel = new SimplePanel( modal );
        initWidget( panel );
    }

    public void setContent( final IsWidget widget,
                            final Integer height ) {
        showAgain = null;
        modal.add( widget );
        modal.add( footer );
        if ( height != null ) {
            modal.setBodyHeigth( height );
        }
    }

    @Override
    public void setTitle( final String title ) {
        modal.setTitle( title );
    }

    public Boolean showAgain() {
        return showAgain;
    }

    public void show() {
        modal.show();
    }

    private HideHandler createHideHandler() {
        return new HideHandler() {
            @Override
            public void onHide( final HideEvent hideEvent ) {
                showAgain = footer.getShowAgain();
                CloseEvent.fire( SplashView.this, SplashView.this, false );
                ( (DivWidget) modal.getWidget( 1 ) ).clear();
            }
        };
    }

    public void hide() {
        modal.hide();
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<SplashView> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

}