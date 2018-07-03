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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalDateFieldValueMarshallerTest {

    private LocalDateFieldValueMarshaller marshaller = new LocalDateFieldValueMarshaller();

    @Mock
    private DatePickerFieldDefinition field;

    @Mock
    private FormDefinition formDefinition;

    @Mock
    private BackendFormRenderingContext context;

    @Test
    public void testNullValue() {
        when(field.getStandaloneClassName()).thenReturn(Date.class.getName());

        marshaller.init(null, field, formDefinition, context);

        assertNull(marshaller.toFlatValue());

        assertNull(marshaller.toRawValue(null));
    }

    @Test
    public void testDateValue() {
        when(field.getStandaloneClassName()).thenReturn(Date.class.getName());

        Date originalValue = new Date();

        marshaller.init(originalValue, field, formDefinition, context);

        Date flatValue = marshaller.toFlatValue();

        assertNotNull(flatValue);

        assertEquals(originalValue, flatValue);

        Object rawValue = marshaller.toRawValue(flatValue);

        assertNotNull(rawValue);

        assertEquals(originalValue, rawValue);
    }

    @Test
    public void testToLocalDateValue() {
        when(field.getStandaloneClassName()).thenReturn(LocalDate.class.getName());

        LocalDate originalValue = LocalDate.now();

        marshaller.init(originalValue, field, formDefinition, context);

        Date flatValue = marshaller.toFlatValue();

        assertNotNull(flatValue);
        assertEquals(originalValue, flatValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        Object rawValue = marshaller.toRawValue(flatValue);

        assertNotNull(rawValue);

        assertEquals(originalValue, rawValue);
    }

    @Test
    public void testToLocalDateTimeValue() {
        when(field.getStandaloneClassName()).thenReturn(LocalDateTime.class.getName());

        LocalDateTime originalValue = LocalDateTime.now();

        marshaller.init(originalValue, field, formDefinition, context);

        Date flatValue = marshaller.toFlatValue();

        assertNotNull(flatValue);
        assertEquals(originalValue, flatValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        Object rawValue = marshaller.toRawValue(flatValue);

        assertNotNull(rawValue);

        assertEquals(originalValue, rawValue);
    }

    @Test
    public void testToLocalTimeValue() {
        when(field.getStandaloneClassName()).thenReturn(LocalTime.class.getName());

        LocalTime originalValue = LocalTime.now();

        marshaller.init(originalValue, field, formDefinition, context);

        Date flatValue = marshaller.toFlatValue();

        assertNotNull(flatValue);

        assertEquals(originalValue, flatValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

        Object rawValue = marshaller.toRawValue(flatValue);

        assertNotNull(rawValue);

        assertEquals(originalValue, rawValue);
    }

    @Test
    public void testToOffsetDateTimeValue() {
        when(field.getStandaloneClassName()).thenReturn(OffsetDateTime.class.getName());

        OffsetDateTime originalValue = OffsetDateTime.now();

        marshaller.init(originalValue, field, formDefinition, context);

        Date flatValue = marshaller.toFlatValue();

        assertNotNull(flatValue);

        assertEquals(originalValue, flatValue.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime());

        Object rawValue = marshaller.toRawValue(flatValue);

        assertNotNull(rawValue);
        assertEquals(originalValue,
                     rawValue);
    }
}
