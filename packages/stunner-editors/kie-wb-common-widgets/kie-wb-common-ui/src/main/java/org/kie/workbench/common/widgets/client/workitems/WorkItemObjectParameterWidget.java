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

import org.gwtproject.core.client.GWT;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.uibinder.client.UiHandler;
import org.gwtproject.uibinder.client.UiTemplate;
import org.gwtproject.user.client.ui.HTMLPanel;
import org.gwtproject.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.workitems.PortableObjectParameterDefinition;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

/**
 * A Widget to display a Work Item Object parameter
 */
public class WorkItemObjectParameterWidget extends WorkItemParameterWidget {

    @UiTemplate
    interface WorkItemObjectParameterWidgetBinder
            extends
            UiBinder<HTMLPanel, WorkItemObjectParameterWidget> {

    }

    @UiField
    FormLabel parameterName;

    @UiField
    ListBox lstAvailableBindings;

    private static WorkItemObjectParameterWidgetBinder uiBinder = new WorkItemObjectParameterWidget_WorkItemObjectParameterWidgetBinderImpl();

    public WorkItemObjectParameterWidget(PortableObjectParameterDefinition ppd,
                                         IBindingProvider bindingProvider,
                                         boolean isReadOnly) {
        super(ppd,
              bindingProvider);

        //Setup widget to use bindings
        this.parameterName.setText(ppd.getName());
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
        }
    }

    @Override
    protected Widget getWidget() {
        return uiBinder.createAndBindUi(this);
    }

    @UiHandler("lstAvailableBindings")
    void lstAvailableBindingsOnChange(ChangeEvent event) {
        int index = lstAvailableBindings.getSelectedIndex();
        if (index > 0) {
            ((PortableObjectParameterDefinition) ppd).setBinding(lstAvailableBindings.getItemText(index));
        } else {
            ((PortableObjectParameterDefinition) ppd).setBinding("");
        }
    }
}
