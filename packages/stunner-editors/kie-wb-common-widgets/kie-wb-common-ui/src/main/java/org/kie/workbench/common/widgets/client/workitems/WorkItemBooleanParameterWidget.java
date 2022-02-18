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

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.workitems.PortableBooleanParameterDefinition;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

/**
 * A Widget to display a Work Item Boolean parameter
 */
public class WorkItemBooleanParameterWidget extends WorkItemParameterWidget {

    interface WorkItemBooleanParameterWidgetBinder
            extends
            UiBinder<HTMLPanel, WorkItemBooleanParameterWidget> {

    }

    @UiField
    FormLabel parameterName;

    @UiField
    ListBox parameterValues;

    @UiField
    ListBox lstAvailableBindings;

    private static WorkItemBooleanParameterWidgetBinder uiBinder = GWT.create(WorkItemBooleanParameterWidgetBinder.class);

    public WorkItemBooleanParameterWidget(PortableBooleanParameterDefinition ppd,
                                          IBindingProvider bindingProvider,
                                          boolean isReadOnly) {
        super(ppd,
              bindingProvider);
        this.parameterName.setText(ppd.getName());
        this.parameterValues.setEnabled(!isReadOnly);

        //Setup widget to select a literal value
        boolean isItemSelected = false;
        Boolean selectedItem = ppd.getValue();
        if (ppd.getValues() != null) {
            for (int index = 0; index < ppd.getValues().length; index++) {
                Boolean item = ppd.getValues()[index];
                this.parameterValues.addItem(Boolean.toString(item));
                if (item.equals(selectedItem)) {
                    this.parameterValues.setSelectedIndex(index);
                    isItemSelected = true;
                }
            }
            if (!isItemSelected) {
                this.parameterValues.setSelectedIndex(0);
                ppd.setValue(Boolean.valueOf(this.parameterValues.getItemText(0)));
            }
        }

        //Setup widget to use bindings
        Set<String> bindings = bindingProvider.getBindings(ppd.getClassName());
        if (bindings.size() > 0) {
            lstAvailableBindings.clear();
            lstAvailableBindings.addItem(CommonConstants.INSTANCE.Choose());
            lstAvailableBindings.setEnabled(true && !isReadOnly);
            lstAvailableBindings.setVisible(true);
            int selectedIndex = 0;
            for (String binding : bindings) {
                lstAvailableBindings.addItem(binding);
                if (binding.equals(ppd.getBinding())) {
                    selectedIndex = lstAvailableBindings.getItemCount() - 1;
                }
            }
            lstAvailableBindings.setSelectedIndex(selectedIndex);
            parameterValues.setEnabled(selectedIndex == 0 && !isReadOnly);
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
            ((PortableBooleanParameterDefinition) ppd).setValue(null);
        } else {
            ((PortableBooleanParameterDefinition) ppd).setValue(Boolean.valueOf(this.parameterValues.getItemText(index)));
        }
    }

    @UiHandler("lstAvailableBindings")
    void lstAvailableBindingsOnChange(ChangeEvent event) {
        int index = lstAvailableBindings.getSelectedIndex();
        parameterValues.setEnabled(index == 0);
        if (index > 0) {
            ((PortableBooleanParameterDefinition) ppd).setValue(null);
            ((PortableBooleanParameterDefinition) ppd).setBinding(lstAvailableBindings.getItemText(index));
        } else {
            ((PortableBooleanParameterDefinition) ppd).setBinding("");
        }
    }
}
