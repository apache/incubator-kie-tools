/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.views.pfly.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalHideEvent;
import org.gwtbootstrap3.client.shared.event.ModalHideHandler;
import org.uberfire.client.views.pfly.modal.Bs3Modal;
import org.uberfire.client.workbench.widgets.popup.PopupView;

@Dependent
public class PopupViewImpl extends Composite implements PopupView {

    @Inject
    private Bs3Modal modal;

    @PostConstruct
    public void init() {
        final SimplePanel panel = new SimplePanel( modal );
        initWidget( panel );
    }

    @Override
    public void setContent( final IsWidget widget ) {
        modal.setContent( widget );
    }

    @Override
    public void setTitle( final String title ) {
        modal.setTitle( title );
    }

    @Override
    public void show() {
        modal.show();

        modal.addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( final ModalHiddenEvent hiddenEvent ) {
                CloseEvent.fire( PopupViewImpl.this, PopupViewImpl.this, false );
            }
        } );

        modal.addHideHandler( new ModalHideHandler() {
            @Override
            public void onHide( ModalHideEvent evt ) {
                CloseEvent.fire( PopupViewImpl.this, PopupViewImpl.this, false );
            }
        } );
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<PopupView> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

}