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
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.security.Identity;

public class ConcurrentChangePopup extends Modal {

    interface ConcurrentChangePopupWidgetBinder
            extends
            UiBinder<Widget, ConcurrentChangePopup> {

    }

    private ConcurrentChangePopupWidgetBinder uiBinder = GWT.create( ConcurrentChangePopupWidgetBinder.class );

    @UiField
    protected HTML message;

    private ConcurrentChangePopup( final String content,
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

    private ConcurrentChangePopup( final String content,
                                   final Command onIgnore,
                                   final Command onReOpen ) {
        this( content, onIgnore, onReOpen, CommonConstants.INSTANCE.ReOpen() );
    }

    private ConcurrentChangePopup( final String content,
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

    public static ConcurrentChangePopup newConcurrentUpdate( final Path path,
                                                             final Identity identity,
                                                             final Command onForceSave,
                                                             final Command onCancel,
                                                             final Command onReOpen ) {
        final String message = CommonConstants.INSTANCE.ConcurrentUpdate( identity.getName(), path.toURI() );

        return new ConcurrentChangePopup( message, onForceSave, onCancel, onReOpen );
    }

    public static ConcurrentChangePopup newConcurrentRename( final Path source,
                                                             final Path target,
                                                             final Identity identity,
                                                             final Command onIgnore,
                                                             final Command onReOpen ) {
        final String message = CommonConstants.INSTANCE.ConcurrentRename( identity.getName(), source.toURI(), target.toURI() );
        return new ConcurrentChangePopup( message, onIgnore, onReOpen );
    }

    public static ConcurrentChangePopup newConcurrentDelete( final Path path,
                                                             final Identity identity,
                                                             final Command onIgnore,
                                                             final Command onClose ) {
        final String message = CommonConstants.INSTANCE.ConcurrentDelete( identity.getName(), path.toURI() );
        return new ConcurrentChangePopup( message, onIgnore, onClose, CommonConstants.INSTANCE.Close() );
    }
}