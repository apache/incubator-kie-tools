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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.FALSE;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.NOT_SET;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.TRUE;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DRGElementTest {

    private static final String[] READONLY_FIELDS = {
            "NameHolder",
            "AllowedAnswers",
            "Description",
            "Question",
            "DataType",
            "SourceType",
            "LocationURI"};

    @Mock
    private DRGElement drgElement;

    @Before
    public void setup() {
        doCallRealMethod().when(drgElement).getReadOnly(Mockito.<String>any());
        doCallRealMethod().when(drgElement).setAllowOnlyVisualChange(anyBoolean());
        doCallRealMethod().when(drgElement).isAllowOnlyVisualChange();
        doCallRealMethod().when(drgElement).isReadonlyField(Mockito.<String>any());
        doCallRealMethod().when(drgElement).getContentDefinitionId();
        doCallRealMethod().when(drgElement).getStringName();
    }

    @Test
    public void testGetReadOnlyNotSet() {

        drgElement.setAllowOnlyVisualChange(false);
        checkIfItIsNotSet("something");
    }

    @Test
    public void testGetReadOnlyWithReadOnlyValuesAndAllowOnlyVisualChangesNotSet() {

        drgElement.setAllowOnlyVisualChange(false);

        for (final String readonlyField : READONLY_FIELDS) {
            checkIfItIsNotSet(readonlyField);
        }
    }

    private void checkIfItIsNotSet(final String property) {

        final DynamicReadOnly.ReadOnly actual = drgElement.getReadOnly(property);

        assertEquals(NOT_SET, actual);
    }

    @Test
    public void testGetReadOnlyWithReadOnlyValues() {

        drgElement.setAllowOnlyVisualChange(true);

        for (final String readonlyField : READONLY_FIELDS) {
            checkIfIsReadOnly(readonlyField);
        }
    }

    private void checkIfIsReadOnly(final String property) {

        final DynamicReadOnly.ReadOnly actual = drgElement.getReadOnly(property);

        assertEquals(TRUE, actual);
    }

    @Test
    public void testGetReadOnlyWithNotReadOnlyValues() {

        drgElement.setAllowOnlyVisualChange(true);

        checkIfItIsNotReadOnly("Font");
        checkIfItIsNotReadOnly("Something");
    }

    @Test
    public void testGetContentDefinitionId() {

        final String contentDefinitionId = "the id";
        final Id id = mock(Id.class);

        when(drgElement.getId()).thenReturn(id);
        when(id.getValue()).thenReturn(contentDefinitionId);

        final String currentId = drgElement.getContentDefinitionId();

        assertEquals(contentDefinitionId, currentId);
    }

    @Test
    public void testGetStringName() {

        final Name name = mock(Name.class);
        final String theName = "the name";

        when(name.getValue()).thenReturn(theName);

        doReturn(name).when(drgElement).getName();

        final String stringName = drgElement.getStringName();

        assertEquals(theName, stringName);
    }

    private void checkIfItIsNotReadOnly(final String property) {

        final DynamicReadOnly.ReadOnly actual = drgElement.getReadOnly(property);

        assertEquals(FALSE, actual);
    }
}
