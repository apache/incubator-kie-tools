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

package org.drools.guvnor.client.common.content.multi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ClosableLabel extends Composite implements HasCloseHandlers<ClosableLabel> {

    interface ClosableLabelBinder extends UiBinder<Widget, ClosableLabel> {

    }

    private ClosableLabelBinder uiBinder = GWT.create(ClosableLabelBinder.class);

    @UiField Label text;
    @UiField Image closeButton;

    public ClosableLabel(final String title) {
        initWidget(uiBinder.createAndBindUi(this));

        text.setText(title);
    }

    @UiHandler("basePanel") void showCloseButton(MouseOverEvent event) {
        closeButton.setVisible(true);
    }

    @UiHandler("basePanel") void hideCloseButton(MouseOutEvent event) {
        closeButton.setVisible(false);
    }

    @UiHandler("closeButton") void closeTab(ClickEvent clickEvent) {
        CloseEvent.fire(this, this);
    }

    public HandlerRegistration addCloseHandler(CloseHandler<ClosableLabel> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

}
