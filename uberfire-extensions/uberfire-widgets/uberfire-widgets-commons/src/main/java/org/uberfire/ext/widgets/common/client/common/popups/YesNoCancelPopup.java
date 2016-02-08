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

package org.uberfire.ext.widgets.common.client.common.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterYesNoCancelButtons;
import org.uberfire.mvp.Command;

public class YesNoCancelPopup extends BaseModal {

    interface YesNoCancelPopupWidgetBinder
            extends
            UiBinder<Widget, YesNoCancelPopup> {

    }

    private YesNoCancelPopupWidgetBinder uiBinder = GWT.create( YesNoCancelPopupWidgetBinder.class );

    @UiField
    protected HTML message;

    protected YesNoCancelPopup( final String title,
                                final String content,
                                final Command yesCommand,
                                final String yesButtonText,
                                final ButtonType yesButtonType,
                                final IconType yesButtonIconType,
                                final Command noCommand,
                                final String noButtonText,
                                final ButtonType noButtonType,
                                final IconType noButtonIconType,
                                final Command cancelCommand,
                                final String cancelButtonText,
                                final ButtonType cancelButtonType,
                                final IconType cancelButtonIconType ) {

        setTitle( title );
        setHideOtherModals( false );

        setBody( uiBinder.createAndBindUi( YesNoCancelPopup.this ) );
        add( new ModalFooterYesNoCancelButtons( this, yesCommand, yesButtonText, yesButtonType, yesButtonIconType,
                                                noCommand, noButtonText, noButtonType, noButtonIconType,
                                                cancelCommand, cancelButtonText, cancelButtonType, cancelButtonIconType ) );

        message.setHTML( SafeHtmlUtils.fromTrustedString( content != null ? content : "" ) );
    }

    protected YesNoCancelPopup( final String title,
                                final String content,
                                final Command yesCommand,
                                final Command noCommand,
                                final Command cancelCommand ) {
        this( title, content, yesCommand, null, null, null, noCommand, null, null, null, cancelCommand, null, null, null );

    }

    public static YesNoCancelPopup newYesNoCancelPopup( final String title,
                                                        final String content,
                                                        final Command yesCommand,
                                                        final String yesButtonText,
                                                        final ButtonType yesButtonType,
                                                        final IconType yesButtonIconType,
                                                        final Command noCommand,
                                                        final String noButtonText,
                                                        final ButtonType noButtonType,
                                                        final IconType noButtonIconType,
                                                        final Command cancelCommand,
                                                        final String cancelButtonText,
                                                        final ButtonType cancelButtonType,
                                                        final IconType cancelButtonIconType ) {

        return new YesNoCancelPopup( title, content, yesCommand, yesButtonText, yesButtonType, yesButtonIconType,
                                     noCommand, noButtonText, noButtonType, noButtonIconType,
                                     cancelCommand, cancelButtonText, cancelButtonType, cancelButtonIconType );

    }

    public static YesNoCancelPopup newYesNoCancelPopup( final String title,
                                                        final String content,
                                                        final Command yesCommand,
                                                        final Command noCommand,
                                                        final Command cancelCommand ) {

        return new YesNoCancelPopup( title, content, yesCommand, null, null, null, noCommand, null, null, null,
                                     cancelCommand, null, null, null );

    }

    public static YesNoCancelPopup newYesNoCancelPopup( final String title,
                                                        final String content,
                                                        final Command yesCommand,
                                                        final String yesButtonText,
                                                        final Command noCommand,
                                                        final String noButtonText,
                                                        final Command cancelCommand,
                                                        final String cancelButtonText ) {

        return new YesNoCancelPopup( title, content, yesCommand, yesButtonText, null, null,
                                     noCommand, noButtonText, null, null,
                                     cancelCommand, cancelButtonText, null, null );

    }

    public static YesNoCancelPopup newYesNoCancelPopup( final String title,
                                                        final String content,
                                                        final Command yesCommand,
                                                        final String yesButtonText,
                                                        final ButtonType yesButtonType,
                                                        final Command noCommand,
                                                        final String noButtonText,
                                                        final ButtonType noButtonType,
                                                        final Command cancelCommand,
                                                        final String cancelButtonText,
                                                        final ButtonType cancelButtonType ) {

        return new YesNoCancelPopup( title, content,
                                     yesCommand, yesButtonText, yesButtonType, null,
                                     noCommand, noButtonText, noButtonType, null,
                                     cancelCommand, cancelButtonText, cancelButtonType, null );
    }
}
