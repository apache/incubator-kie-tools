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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.gwtbootstrap3.client.ui.html.Div;

/**
 * A Widget to display a list of Work Item parameters
 */
public class WorkItemParametersWidget extends Div {

    private List<PortableParameterDefinition> parameters;

    private final IBindingProvider bindingProvider;

    private final boolean isReadOnly;

    public WorkItemParametersWidget(final IBindingProvider bindingProvider,
                                    final boolean isReadOnly) {
        this.bindingProvider = bindingProvider;
        this.isReadOnly = isReadOnly;
    }

    public void setParameters(Collection<PortableParameterDefinition> parameters) {
        this.clear();
        this.parameters = sort(parameters);
        for (PortableParameterDefinition ppd : this.parameters) {
            WorkItemParameterWidget pw = WorkItemParameterWidgetFactory.getWidget(ppd,
                                                                                  bindingProvider,
                                                                                  isReadOnly);
            add(pw);
        }
    }

    private List<PortableParameterDefinition> sort(Collection<PortableParameterDefinition> parameters) {
        List<PortableParameterDefinition> sortedParameters = new ArrayList<PortableParameterDefinition>();
        sortedParameters.addAll(parameters);
        Collections.sort(sortedParameters,
                         new Comparator<PortableParameterDefinition>() {

                             public int compare(PortableParameterDefinition o1,
                                                PortableParameterDefinition o2) {
                                 return o1.getName().compareTo(o2.getName());
                             }
                         });
        return sortedParameters;
    }
}
