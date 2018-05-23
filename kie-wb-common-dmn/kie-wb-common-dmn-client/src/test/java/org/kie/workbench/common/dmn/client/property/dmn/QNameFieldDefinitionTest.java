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

package org.kie.workbench.common.dmn.client.property.dmn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QNameFieldDefinitionTest {

    private static final String PLACE_HOLDER = "place-holder";

    @Mock
    private PlaceHolderFieldDefinition other;

    private QNameFieldDefinition definition;

    @Before
    public void setup() {
        this.definition = spy(new QNameFieldDefinition());
    }

    @Test
    public void testGetFieldType() {
        assertEquals(QNameFieldDefinition.FIELD_TYPE, definition.getFieldType());
    }

    @Test
    public void testPlaceHolder() {
        definition.setPlaceHolder(PLACE_HOLDER);
        assertEquals(PLACE_HOLDER, definition.getPlaceHolder());
    }

    @Test
    public void testDoCopyFromWhenDoesSupportPlaceHolder() {
        when(other.getPlaceHolder()).thenReturn(PLACE_HOLDER);
        definition.doCopyFrom(other);

        verify(definition).setPlaceHolder(eq(PLACE_HOLDER));
    }

    @Test
    public void testDoCopyFromWhenDoesNotSupportPlaceHolder() {
        final FieldDefinition other = mock(FieldDefinition.class);
        definition.doCopyFrom(other);

        verify(definition, never()).setPlaceHolder(anyString());
    }

    public interface PlaceHolderFieldDefinition extends FieldDefinition,
                                                        HasPlaceHolder {

    }
}
