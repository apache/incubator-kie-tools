/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Row;

@Dependent
public class DisplayerError extends Composite {

    interface Binder extends UiBinder<Widget, DisplayerError> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    HTML errorMessage;

    @UiField
    HTML errorCause;

    @UiField
    Panel errorMessageRow;

    @UiField
    Panel errorCauseRow;

    public DisplayerError() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void show(final String message, final String cause) {
        errorMessage.setText(message != null ? message : "");
        errorMessageRow.setVisible(message != null);
        errorCause.setText(cause != null ? cause : "");
        errorCauseRow.setVisible(cause != null);
    }
}