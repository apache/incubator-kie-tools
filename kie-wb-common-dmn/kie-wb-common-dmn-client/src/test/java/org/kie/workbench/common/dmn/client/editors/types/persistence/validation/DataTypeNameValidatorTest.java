/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.persistence.validation;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeNameValidatorTest {

    @Mock
    private EventSourceMock<DataTypeFlashMessage> flashMessageEvent;

    @Mock
    private NameIsBlankErrorMessage blankErrorMessage;

    @Mock
    private NameIsNotUniqueErrorMessage notUniqueErrorMessage;

    @Mock
    private DataTypeStore dataTypeStore;

    private DataTypeNameValidator validator;

    @Before
    public void setup() {
        validator = spy(new DataTypeNameValidator(flashMessageEvent, blankErrorMessage, notUniqueErrorMessage, dataTypeStore));
    }

    @Test
    public void testIsValidWhenDataTypeNameIsBlank() {

        final DataType dataType = mock(DataType.class);
        final DataTypeFlashMessage blankMessage = mock(DataTypeFlashMessage.class);

        doReturn(true).when(validator).isBlank(dataType);
        doReturn(true).when(validator).isNotUnique(dataType);
        when(blankErrorMessage.getFlashMessage(dataType)).thenReturn(blankMessage);

        final boolean isValid = validator.isValid(dataType);

        verify(flashMessageEvent).fire(blankMessage);
        assertFalse(isValid);
    }

    @Test
    public void testIsValidWhenDataTypeNameIsNotUnique() {

        final DataType dataType = mock(DataType.class);
        final DataTypeFlashMessage notUniqueMessage = mock(DataTypeFlashMessage.class);

        doReturn(false).when(validator).isBlank(dataType);
        doReturn(true).when(validator).isNotUnique(dataType);
        when(notUniqueErrorMessage.getFlashMessage(dataType)).thenReturn(notUniqueMessage);

        final boolean isValid = validator.isValid(dataType);

        verify(flashMessageEvent).fire(notUniqueMessage);
        assertFalse(isValid);
    }

    @Test
    public void testIsValid() {

        final DataType dataType = mock(DataType.class);

        doReturn(false).when(validator).isBlank(dataType);
        doReturn(false).when(validator).isNotUnique(dataType);

        final boolean isValid = validator.isValid(dataType);

        verify(flashMessageEvent, never()).fire(any());
        verify(flashMessageEvent, never()).fire(any());
        assertTrue(isValid);
    }

    @Test
    public void testIsNotUniqueWhenNameIsUnique() {

        final DataType dataType = makeDataType("uuid1", "tCompany");

        doReturn(asList(makeDataType("uuid2", "tPerson"), makeDataType("uuid3", "tCity"))).when(validator).siblings(dataType);

        assertFalse(validator.isNotUnique(dataType));
    }

    @Test
    public void testIsNotUniqueWhenNameIsNotUnique() {

        final DataType dataType = makeDataType("uuid1", "tCity");

        doReturn(asList(makeDataType("uuid2", "tPerson"), makeDataType("uuid3", "tCity"))).when(validator).siblings(dataType);

        assertTrue(validator.isNotUnique(dataType));
    }

    @Test
    public void testIsNotBlankWhenNameIsBlank() {
        final DataType dataType = makeDataType("uuid", "");

        assertTrue(validator.isBlank(dataType));
    }

    @Test
    public void testIsNotBlankWhenNameIsNull() {
        final DataType dataType = makeDataType("uuid", null);

        assertTrue(validator.isBlank(dataType));
    }

    @Test
    public void testIsNotBlankWhenNameIsNotBlank() {
        final DataType dataType = makeDataType("uuid", "tCity");

        assertFalse(validator.isBlank(dataType));
    }

    @Test
    public void testSiblingsWhenDataTypeDoesNotHaveParent() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> expectedSiblings = Collections.singletonList(mock(DataType.class));

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(expectedSiblings);

        final List<DataType> actualSiblings = validator.siblings(dataType);

        assertEquals(expectedSiblings, actualSiblings);
    }

    @Test
    public void testSiblingsWhenDataTypeHasParent() {

        final DataType dataType = mock(DataType.class);
        final DataType parent = mock(DataType.class);
        final String parentUUID = "parentUUID";
        final List<DataType> expectedSiblings = Collections.singletonList(mock(DataType.class));

        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(parent.getSubDataTypes()).thenReturn(expectedSiblings);
        when(dataTypeStore.get(parentUUID)).thenReturn(parent);

        final List<DataType> actualSiblings = validator.siblings(dataType);

        assertEquals(expectedSiblings, actualSiblings);
    }

    private DataType makeDataType(final String uuid,
                                  final String name) {
        final DataType dataType = spy(new DataType(null));
        doReturn(uuid).when(dataType).getUUID();
        doReturn(name).when(dataType).getName();
        return dataType;
    }
}
