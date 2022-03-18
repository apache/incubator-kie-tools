/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.client.workitems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.workitems.PortableEnumParameterDefinition;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;

/**
 * A Widget to display a Work Item Enum parameter
 */
public class WorkItemEnumParameterWidget extends WorkItemParameterWidget {

    interface WorkItemEnumParameterWidgetBinder
            extends
            UiBinder<HTMLPanel, WorkItemEnumParameterWidget> {

    }

    @UiField
    FormLabel parameterName;

    @UiField
    ListBox parameterValues;

    private static WorkItemEnumParameterWidgetBinder uiBinder = GWT.create(WorkItemEnumParameterWidgetBinder.class);

    public WorkItemEnumParameterWidget(PortableEnumParameterDefinition ppd,
                                       IBindingProvider bindingProvider,
                                       boolean isReadOnly) {
        super(ppd,
              bindingProvider);
        this.parameterName.setText(ppd.getName());
        this.parameterValues.setEnabled(!isReadOnly);

        boolean isItemSelected = false;
        String selectedItem = ppd.getValue();
        if (ppd.getValues() != null) {
            for (int index = 0; index < ppd.getValues().length; index++) {
                String item = ppd.getValues()[index];
                this.parameterValues.addItem(item);
                if (item.equals(selectedItem)) {
                    this.parameterValues.setSelectedIndex(index);
                    isItemSelected = true;
                }
            }
            if (!isItemSelected) {
                this.parameterValues.setSelectedIndex(0);
                ppd.setValue(this.parameterValues.getItemText(0));
            }
        }
    }

    @Override
    protected Widget getWidget() {
        return uiBinder.createAndBindUi(this);
    }

    @UiHandler("parameterValues")
    void parameterValuesOnChange(ChangeEvent event) {
        int index = this.parameterValues.getSelectedIndex();
        if (index == -1) {
            ((PortableEnumParameterDefinition) ppd).setValue(null);
        } else {
            ((PortableEnumParameterDefinition) ppd).setValue(this.parameterValues.getItemText(index));
        }
    }
}
