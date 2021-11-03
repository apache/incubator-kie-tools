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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.uberfire.client.views.pfly.widgets.FormLabelHelp;

public class FormStyleItem extends Composite {

    private static FormStyleLayoutBinder uiBinder = GWT.create(FormStyleLayoutBinder.class);
    @UiField
    FormGroup formGroup;
    @UiField
    FlowPanel group;
    @UiField
    FlowPanel labelContainer;
    int index = -1;

    public FormStyleItem() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setup(final String labelText,
                      final IsWidget field,
                      final int index) {
        final FormLabel formLabel = GWT.create(FormLabel.class);
        formLabel.setText(labelText);
        labelContainer.add(formLabel);
        group.add(field);
        this.index = index;
    }

    public void setup(final String labelText,
                      final String helpTitle,
                      final String helpContent,
                      final IsWidget widget,
                      final int index) {
        final FormLabelHelp formLabel = GWT.create(FormLabelHelp.class);
        formLabel.setText(labelText);
        formLabel.setHelpTitle(helpTitle);
        formLabel.setHelpContent(helpContent);
        labelContainer.add(formLabel);
        group.add(widget);
        this.index = index;
    }

    public FormGroup getFormGroup() {
        return this.formGroup;
    }

    public FlowPanel getGroup() {
        return this.group;
    }

    public int getIndex() {
        return this.index;
    }

    interface FormStyleLayoutBinder
            extends
            UiBinder<Widget, FormStyleItem> {

    }
}
