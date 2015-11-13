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
package org.uberfire.client.views.bs2.modal;

import javax.enterprise.context.Dependent;

import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.github.gwtbootstrap.client.ui.event.ShowEvent;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup that shows an error message
 */
@Dependent
public class ErrorPopupView extends Composite implements ErrorPopupPresenter.View {

    interface ErrorPopupWidgetBinder
            extends
            UiBinder<Widget, ErrorPopupView> {

    }

    private static ErrorPopupWidgetBinder uiBinder = GWT.create( ErrorPopupWidgetBinder.class );

    @UiField
    protected HTML message;

    public ErrorPopupView() {
    }

    private void setMessage( final String message ) {
        this.message.setHTML( SafeHtmlUtils.fromTrustedString( message ) );
    }

    @Override
    public void showMessage( final String msg,
                             final Command afterShow,
                             final Command afterClose ) {
        final Modal modal = new Modal();

        modal.setTitle( "Error" );
        modal.setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        modal.setBackdrop( BackdropType.STATIC );
        modal.setKeyboard( true );
        modal.setAnimation( true );
        modal.setDynamicSafe( true );
        modal.setHideOthers( false );
        modal.add( uiBinder.createAndBindUi( this ) );
        modal.add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                modal.hide();
            }
        } ) );
        modal.addShowHandler( new ShowHandler() {
            @Override
            public void onShow( final ShowEvent showEvent ) {
                if ( afterShow != null ) {
                    afterShow.execute();
                }
            }
        } );
        modal.addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( final HiddenEvent hiddenEvent ) {
                if ( afterClose != null ) {
                    afterClose.execute();
                }
            }
        } );

        setMessage( msg );
        modal.show();
    }

}
