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
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.IS_MULTIPLE_INSTANCE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.MULTIPLE_INSTANCE_COLLECTION_INPUT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.MULTIPLE_INSTANCE_COLLECTION_OUTPUT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.MULTIPLE_INSTANCE_COMPLETION_CONDITION;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.MULTIPLE_INSTANCE_DATA_INPUT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.MULTIPLE_INSTANCE_DATA_OUTPUT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.filters.MultipleInstanceNodeFilterProvider.MULTIPLE_INSTANCE_EXECUTION_MODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class MultipleInstanceNodeFilterProviderTest {

    static final String UUID = "UUID";

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected ClientSession currentSession;

    @Mock
    protected EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Captor
    protected ArgumentCaptor<RefreshFormPropertiesEvent> fieldChangedCaptor;

    protected MultipleInstanceNodeFilterProvider filterProvider;

    @Before
    public void setUp() {
        when(sessionManager.getCurrentSession()).thenReturn(currentSession);
        filterProvider = newFilterProvider();
    }

    protected abstract MultipleInstanceNodeFilterProvider newFilterProvider();

    protected abstract Object newNonMultipleInstanceDefinition();

    protected abstract Object newMultipleInstanceDefinition();

    protected abstract Class<?> getExpectedDefinitionType();

    @Test
    public void testGetDefinitionType() {
        assertEquals(getExpectedDefinitionType(), filterProvider.getDefinitionType());
    }

    @Test
    public void testProvideFiltersForMultipleInstanceDefinition() {
        testProvideFilters(UUID, newMultipleInstanceDefinition(), true, 6);
    }

    @Test
    public void testProvideFiltersForNonMultipleInstanceDefinition() {
        testProvideFilters(UUID, newNonMultipleInstanceDefinition(), false, 6);
    }

    protected List<FormElementFilter> testProvideFilters(String elementUUID, Object definition, boolean expectedValue, int expectedSize) {
        ArrayList<FormElementFilter> filters = new ArrayList<>(filterProvider.provideFilters(elementUUID, definition));
        assertExpectedMIFilters(expectedSize, definition, expectedValue, filters);
        return filters;
    }

    protected void assertExpectedMIFilters(int expectedSize, Object definition, boolean expectedValue, ArrayList<FormElementFilter> filters) {
        assertEquals(expectedSize, filters.size());
        assertExpectedFilter(MULTIPLE_INSTANCE_COLLECTION_INPUT, expectedValue, definition, filters.get(0));
        assertExpectedFilter(MULTIPLE_INSTANCE_DATA_INPUT, expectedValue, definition, filters.get(1));
        assertExpectedFilter(MULTIPLE_INSTANCE_COLLECTION_OUTPUT, expectedValue, definition, filters.get(2));
        assertExpectedFilter(MULTIPLE_INSTANCE_DATA_OUTPUT, expectedValue, definition, filters.get(3));
        assertExpectedFilter(MULTIPLE_INSTANCE_COMPLETION_CONDITION, expectedValue, definition, filters.get(4));
        assertExpectedFilter(MULTIPLE_INSTANCE_EXECUTION_MODE, expectedValue, definition, filters.get(5));
    }

    @SuppressWarnings("unchecked")
    protected void assertExpectedFilter(String expectedElementName, boolean expectedValue, Object objectToTest, FormElementFilter filter) {
        assertEquals(expectedElementName, filter.getElementName());
        assertEquals(expectedValue, filter.getPredicate().test(objectToTest));
    }

    @Test
    public void testOnFormFieldChangedForMultipleInstance() {
        FormFieldChanged formFieldChanged = mockFormFieldChanged(IS_MULTIPLE_INSTANCE, UUID);
        filterProvider.onFormFieldChanged(formFieldChanged);
        verifyFieldChangeFired();
    }

    protected void verifyFieldChangeFired() {
        verify(refreshFormPropertiesEvent).fire(fieldChangedCaptor.capture());
        assertEquals(currentSession, fieldChangedCaptor.getValue().getSession());
        assertEquals(UUID, fieldChangedCaptor.getValue().getUuid());
    }

    @Test
    public void testOnFormFieldChangedForOtherThanMultipleInstance() {
        FormFieldChanged formFieldChanged = mockFormFieldChanged("anyOtherField", "anyOtherUUID");
        filterProvider.onFormFieldChanged(formFieldChanged);
        verify(refreshFormPropertiesEvent, never()).fire(any());
    }

    protected FormFieldChanged mockFormFieldChanged(String fieldName, String uuid) {
        FormFieldChanged formFieldChanged = mock(FormFieldChanged.class);
        when(formFieldChanged.getName()).thenReturn(fieldName);
        when(formFieldChanged.getUuid()).thenReturn(uuid);
        return formFieldChanged;
    }
}
