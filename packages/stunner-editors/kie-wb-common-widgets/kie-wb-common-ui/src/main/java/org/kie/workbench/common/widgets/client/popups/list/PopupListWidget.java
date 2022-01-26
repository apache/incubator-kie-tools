/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.popups.list;

import org.gwtproject.core.client.GWT;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.uibinder.client.UiTemplate;
import org.gwtproject.user.client.ui.VerticalPanel;
import org.gwtproject.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

/**
 * A popup that can contain a list of items
 */
public class PopupListWidget extends BaseModal {

    @UiTemplate
    interface PopupListWidgetBinder
            extends
            UiBinder<Widget, PopupListWidget> {

    }

    private static PopupListWidgetBinder uiBinder = new PopupListWidget_PopupListWidgetBinderImpl();

    @UiField
    protected VerticalPanel list;

    public PopupListWidget() {
        setWidth("900px");

        setBody(uiBinder.createAndBindUi(this));
        add(new ModalFooterOKButton(() -> hide()));
    }

    @Override
    public void clear() {
        list.clear();
    }

    public void addListMessage(final MessageType messageType,
                               final String message) {
        this.list.add(new ConversionMessageWidget(messageType,
                                                  message));
    }
}
