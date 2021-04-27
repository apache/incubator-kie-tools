/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class InfoWidget extends Composite implements RequiresResize {

    interface NoSuchFileWidgetBinder
            extends
            UiBinder<Widget, InfoWidget> {

    }

    private static NoSuchFileWidgetBinder uiBinder = GWT.create(NoSuchFileWidgetBinder.class);

    @UiField
    HTMLPanel container;

    @UiField
    Label info;

    public InfoWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setText(String text) {
        info.setText(text);
    }

    @Override
    public void onResize() {
        if (getParent() == null) {
            return;
        }
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        container.setPixelSize(width,
                               height);
    }
}
