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

package org.uberfire.client.common;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public abstract class AbstractConcurrentChangePopup extends Modal {

    interface AbstractConcurrentChangePopupWidgetBinder
            extends
            UiBinder<Widget, AbstractConcurrentChangePopup> {

    }

    private AbstractConcurrentChangePopupWidgetBinder uiBinder = GWT.create( AbstractConcurrentChangePopupWidgetBinder.class );

    @UiField
    protected HTML message;

    protected AbstractConcurrentChangePopup( final String content,
                                   final Command onIgnore,
                                   final Command onAction,
                                   final String buttonText ) {
        setTitle( CommonConstants.INSTANCE.Error() );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setHideOthers( false );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterReOpenIgnoreButtons( this, onAction, onIgnore, buttonText ) );

        message.setHTML( SafeHtmlUtils.fromTrustedString( content ) );
    }

    protected AbstractConcurrentChangePopup( final String content,
                                   final Command onIgnore,
                                   final Command onReOpen ) {
        this( content, onIgnore, onReOpen, CommonConstants.INSTANCE.ReOpen() );
    }

    protected AbstractConcurrentChangePopup( final String content,
                                   final Command onForceSave,
                                   final Command onIgnore,
                                   final Command onReOpen ) {
        setTitle( CommonConstants.INSTANCE.Error() );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setHideOthers( false );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterForceSaveReOpenCancelButtons( this, onForceSave, onReOpen, onIgnore ) );

        message.setHTML( SafeHtmlUtils.fromTrustedString( content ) );
    }

}
