/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Collection;

import javax.enterprise.event.Event;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

public abstract class BaseReusableSubProcessFilterProvider<T extends BaseReusableSubprocess> extends MultipleInstanceNodeFilterProvider {

    static final String INDEPENDENT = "executionSet.independent";
    static final String ABORT_PARENT = "executionSet.abortParent";

    private Class<T> definitionType;

    protected BaseReusableSubProcessFilterProvider(final SessionManager sessionManager,
                                                   final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                                   final Class<T> definitionType) {
        super(sessionManager, refreshFormPropertiesEvent);
        this.definitionType = definitionType;
    }

    public Class<T> getDefinitionType() {
        return definitionType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isMultipleInstance(final Object definition) {
        final T subProcess = (T) definition;
        return subProcess.getExecutionSet().getIsMultipleInstance().getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<FormElementFilter> provideFilters(String elementUUID, Object definition) {
        Collection<FormElementFilter> filters = super.provideFilters(elementUUID, definition);
        BaseReusableSubprocessTaskExecutionSet executionSet = ((T) definition).getExecutionSet();
        filters.add(new FormElementFilter(ABORT_PARENT, p -> Boolean.FALSE.equals(executionSet.getIndependent().getValue())));
        return filters;
    }

    @Override
    protected void applyFormFieldChange(FormFieldChanged formFieldChanged) {
        if (INDEPENDENT.equals(formFieldChanged.getName())) {
            refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession(), formFieldChanged.getUuid()));
        } else {
            super.applyFormFieldChange(formFieldChanged);
        }
    }
}