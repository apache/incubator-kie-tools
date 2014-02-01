/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.uberfire.client.workbench.widgets.common;

import com.github.gwtbootstrap.client.ui.Modal;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

/**
 * A popup that shows an error message
 */
public class ErrorPopup extends Modal {

    interface ErrorPopupWidgetBinder
            extends
            UiBinder<Widget, ErrorPopup> {

    }

    private static ErrorPopupWidgetBinder uiBinder = GWT.create( ErrorPopupWidgetBinder.class );

    private static ErrorPopup instance = new ErrorPopup();

    @UiField
    protected HTML message;

    private ErrorPopup() {
        setTitle( "Error" );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setHideOthers( false );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );
    }

    public void setMessage( final String message ) {
        this.message.setHTML( SafeHtmlUtils.fromTrustedString( message ) );
    }

    public static void showMessage( String message ) {
        instance.setMessage( message );
        instance.show();
    }

    public static void showMessage( final String msg,
                                    final Command afterShow,
                                    final Command afterClose ) {
        new ErrorPopup() {{
            setMessage( msg );
            addShowHandler( new ShowHandler() {
                @Override
                public void onShow( final ShowEvent showEvent ) {
                    if ( afterShow != null ) {
                        afterShow.execute();
                    }
                }
            } );
            addHiddenHandler( new HiddenHandler() {
                @Override
                public void onHidden( final HiddenEvent hiddenEvent ) {
                    if ( afterClose != null ) {
                        afterClose.execute();
                    }
                }
            } );
        }}.show();
    }

}
