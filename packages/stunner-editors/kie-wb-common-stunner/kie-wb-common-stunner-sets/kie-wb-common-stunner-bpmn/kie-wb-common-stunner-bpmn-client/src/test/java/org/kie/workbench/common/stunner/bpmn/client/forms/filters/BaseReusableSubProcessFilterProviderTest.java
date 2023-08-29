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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.mockito.junit.MockitoJUnitRunner;

import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.BaseReusableSubProcessFilterProvider.ABORT_PARENT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.BaseReusableSubProcessFilterProvider.INDEPENDENT;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseReusableSubProcessFilterProviderTest<T extends BaseReusableSubprocess> extends MultipleInstanceNodeFilterProviderTest {

    @Override
    protected MultipleInstanceNodeFilterProvider newFilterProvider() {
        return newReusableSubProcessFilterProvider();
    }

    protected abstract T newReusableSubProcess();

    protected abstract BaseReusableSubProcessFilterProvider<T> newReusableSubProcessFilterProvider();

    @Override
    protected Object newNonMultipleInstanceDefinition() {
        T subprocess = newReusableSubProcess();
        subprocess.getExecutionSet().getIsMultipleInstance().setValue(false);
        return subprocess;
    }

    @Override
    protected Object newMultipleInstanceDefinition() {
        T subprocess = newReusableSubProcess();
        subprocess.getExecutionSet().getIsMultipleInstance().setValue(true);
        return subprocess;
    }

    @Override
    public void testProvideFiltersForMultipleInstanceDefinition() {
        testProvideFilters(UUID, newMultipleInstanceDefinition(), true, 7);
    }

    @Override
    public void testProvideFiltersForNonMultipleInstanceDefinition() {
        testProvideFilters(UUID, newNonMultipleInstanceDefinition(), false, 7);
    }

    @Test
    public void testProvideFiltersForMultipleInstanceIndependentDefinition() {
        testProvideFilterForAbortParent(true, true);
    }

    @Test
    public void testProvideFiltersForMultipleInstanceNonIndependentDefinition() {
        testProvideFilterForAbortParent(true, false);
    }

    @Test
    public void testProvideFiltersForNonMultipleInstanceIndependentDefinition() {
        testProvideFilterForAbortParent(false, true);
    }

    @Test
    public void testProvideFiltersForNonMultipleInstanceNonIndependentDefinition() {
        testProvideFilterForAbortParent(false, false);
    }

    private void testProvideFilterForAbortParent(boolean isMultipleInstance, boolean isIndependent) {
        T definition = newReusableSubProcess();
        definition.getExecutionSet().getIsMultipleInstance().setValue(isMultipleInstance);
        definition.getExecutionSet().getIndependent().setValue(isIndependent);
        List<FormElementFilter> filters = testProvideFilters(UUID, definition, isMultipleInstance, 7);
        assertExpectedFilter(ABORT_PARENT, !isIndependent, definition, filters.get(6));
    }

    @Test
    public void testOnFormFieldChangedForIndependentField() {
        FormFieldChanged formFieldChanged = mockFormFieldChanged(INDEPENDENT, UUID);
        filterProvider.onFormFieldChanged(formFieldChanged);
        verifyFieldChangeFired();
    }
}