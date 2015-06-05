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
package org.uberfire.client.views.pfly.splash;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.views.pfly.modal.Bs3Modal;
import org.uberfire.client.workbench.widgets.splash.SplashView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class SplashViewImpl extends Composite implements SplashView {

    private Boolean showAgain = null;

    @Inject
    private Bs3Modal modal;

    @Inject
    private SplashModalFooter footer;

    @PostConstruct
    public void setup() {
        footer.setCloseCommand( new ParameterizedCommand<Boolean>() {
            @Override
            public void execute( final Boolean parameter ) {
                showAgain = parameter;
                hide();
            }
        } );

        modal.setFooterContent( footer );

        final SimplePanel panel = new SimplePanel( modal );
        initWidget( panel );
    }

    @Override
    public void setContent( final IsWidget widget,
                            final Integer height ) {
        showAgain = null;
        modal.setContent( widget );
        if ( height != null ) {
            modal.setBodyHeight( height );
        }
    }

    @Override
    public void setTitle( final String title ) {
        modal.setModalTitle( title );
    }

    @Override
    public Boolean showAgain() {
        return showAgain;
    }

    @Override
    public void show() {
        modal.show( Commands.DO_NOTHING,
                new Command() {
                    @Override
                    public void execute() {
                        showAgain = footer.getShowAgain();
                        CloseEvent.fire( SplashViewImpl.this, SplashViewImpl.this, false );
                    }
                } );
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<SplashView> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

}