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
package org.uberfire.ext.widgets.common.client.common.popups.errors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalShowEvent;
import org.gwtbootstrap3.client.shared.event.ModalShowHandler;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

/**
 * A popup that shows an error message
 */
public class ErrorPopup extends BaseModal {

    private static ErrorPopupWidgetBinder uiBinder = GWT.create(ErrorPopupWidgetBinder.class);
    private static ErrorPopup instance = new ErrorPopup();
    @UiField
    protected HTML message;

    private ErrorPopup() {
        setTitle(CommonConstants.INSTANCE.Error());

        setBody(uiBinder.createAndBindUi(ErrorPopup.this));
        add(new ModalFooterOKButton(new Command() {
            @Override
            public void execute() {
                hide();
            }
        }));
    }

    public static void showMessage(String message) {
        instance.setMessage(message);
        instance.show();
    }

    public static void showMessage(final String msg,
                                   final Command afterShow,
                                   final Command afterClose) {
        new ErrorPopup() {{
            setMessage(msg);
            addShowHandler(new ModalShowHandler() {
                @Override
                public void onShow(final ModalShowEvent showEvent) {
                    if (afterShow != null) {
                        afterShow.execute();
                    }
                }
            });
            addHiddenHandler(new ModalHiddenHandler() {
                @Override
                public void onHidden(final ModalHiddenEvent hiddenEvent) {
                    if (afterClose != null) {
                        afterClose.execute();
                    }
                }
            });
        }}.show();
    }

    public void setMessage(final String message) {
        this.message.setHTML(SafeHtmlUtils.fromTrustedString(message));
    }

    interface ErrorPopupWidgetBinder
            extends
            UiBinder<Widget, ErrorPopup> {

    }
}
