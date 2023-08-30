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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

public abstract class MultipleInstanceNodeFilterProvider implements StunnerFormElementFilterProvider {

    static final String IS_MULTIPLE_INSTANCE = "executionSet.isMultipleInstance";
    static final String MULTIPLE_INSTANCE_COLLECTION_INPUT = "executionSet.multipleInstanceCollectionInput";
    static final String MULTIPLE_INSTANCE_DATA_INPUT = "executionSet.multipleInstanceDataInput";
    static final String MULTIPLE_INSTANCE_COLLECTION_OUTPUT = "executionSet.multipleInstanceCollectionOutput";
    static final String MULTIPLE_INSTANCE_DATA_OUTPUT = "executionSet.multipleInstanceDataOutput";
    static final String MULTIPLE_INSTANCE_COMPLETION_CONDITION = "executionSet.multipleInstanceCompletionCondition";
    static final String MULTIPLE_INSTANCE_EXECUTION_MODE = "executionSet.multipleInstanceExecutionMode";

    protected final SessionManager sessionManager;

    protected final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    public MultipleInstanceNodeFilterProvider() {
        this(null, null);
    }

    public MultipleInstanceNodeFilterProvider(final SessionManager sessionManager,
                                              final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent) {
        this.sessionManager = sessionManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
    }

    public abstract boolean isMultipleInstance(final Object definition);

    @Override
    @SuppressWarnings("unchecked")
    public Collection<FormElementFilter> provideFilters(final String elementUUID, final Object definition) {
        final List<FormElementFilter> filters = new ArrayList<>();
        final Predicate predicate = o -> isMultipleInstance(definition);
        filters.add(new FormElementFilter(MULTIPLE_INSTANCE_COLLECTION_INPUT, predicate));
        filters.add(new FormElementFilter(MULTIPLE_INSTANCE_DATA_INPUT, predicate));
        filters.add(new FormElementFilter(MULTIPLE_INSTANCE_COLLECTION_OUTPUT, predicate));
        filters.add(new FormElementFilter(MULTIPLE_INSTANCE_DATA_OUTPUT, predicate));
        filters.add(new FormElementFilter(MULTIPLE_INSTANCE_COMPLETION_CONDITION, predicate));
        filters.add(new FormElementFilter(MULTIPLE_INSTANCE_EXECUTION_MODE, predicate));
        return filters;
    }

    void onFormFieldChanged(@Observes final FormFieldChanged formFieldChanged) {
        applyFormFieldChange(formFieldChanged);
    }

    protected void applyFormFieldChange(final FormFieldChanged formFieldChanged) {
        if (IS_MULTIPLE_INSTANCE.equals(formFieldChanged.getName())) {
            refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession(), formFieldChanged.getUuid()));
        }
    }
}
