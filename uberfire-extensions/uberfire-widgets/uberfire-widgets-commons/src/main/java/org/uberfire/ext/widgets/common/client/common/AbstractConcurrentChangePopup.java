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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterForceSaveReOpenCancelButtons;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterReOpenIgnoreButtons;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public abstract class AbstractConcurrentChangePopup extends BaseModal {

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

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( AbstractConcurrentChangePopup.this ) );
        }} );
        add( new ModalFooterReOpenIgnoreButtons( this,
                                                 onAction,
                                                 onIgnore,
                                                 buttonText ) );

        message.setHTML( SafeHtmlUtils.fromTrustedString( content ) );
    }

    protected AbstractConcurrentChangePopup( final String content,
                                             final Command onIgnore,
                                             final Command onReOpen ) {
        this( content,
              onIgnore,
              onReOpen,
              CommonConstants.INSTANCE.ReOpen() );
    }

    protected AbstractConcurrentChangePopup( final String content,
                                             final Command onForceSave,
                                             final Command onIgnore,
                                             final Command onReOpen ) {
        setTitle( CommonConstants.INSTANCE.Error() );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( AbstractConcurrentChangePopup.this ) );
        }} );
        add( new ModalFooterForceSaveReOpenCancelButtons( this,
                                                          onForceSave,
                                                          onReOpen,
                                                          onIgnore ) );

        message.setHTML( SafeHtmlUtils.fromTrustedString( content ) );
    }

}
