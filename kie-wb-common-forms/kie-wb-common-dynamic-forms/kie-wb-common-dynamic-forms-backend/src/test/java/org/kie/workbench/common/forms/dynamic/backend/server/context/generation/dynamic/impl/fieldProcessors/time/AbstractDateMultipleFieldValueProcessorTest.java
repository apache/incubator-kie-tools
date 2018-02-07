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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public abstract class AbstractDateMultipleFieldValueProcessorTest<F extends FieldDefinition, PROCESSOR extends AbstractDateMultipleFieldValueProcessor<F>> {

    @Spy
    protected LocalDateFieldValueProcessor dateProcessor = new LocalDateFieldValueProcessor();

    protected PROCESSOR processor;

    @Mock
    protected F field;

    @Mock
    private BackendFormRenderingContext context;

    @Before
    public void init() {
        dateProcessor.init();

        processor = getProcessor(dateProcessor);
    }

    @Test
    public void testNullValue() {
        when(field.getStandaloneClassName()).thenReturn(Date.class.getName());

        Assertions.assertThat(processor.toFlatValue(field, null, context))
                .isNotNull()
                .isEmpty();

        Assertions.assertThat(processor.toRawValue(field, null, null, context))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void testDateValue() {
        when(field.getStandaloneClassName()).thenReturn(Date.class.getName());

        Date originalValue = new Date();

        List<Date> originalValues = new ArrayList<>();
        originalValues.add(originalValue);

        List<Date> flatValue = processor.toFlatValue(field,
                                                     originalValues,
                                                     context);

        Assertions.assertThat(flatValue)
                .isNotNull()
                .isNotEmpty()
                .containsOnly(originalValue)
                .isEqualTo(originalValues);


        List rawValue = processor.toRawValue(field,
                                               flatValue,
                                               originalValues,
                                               context);

        Assertions.assertThat(rawValue)
                .isNotNull()
                .isNotEmpty()
                .containsOnly(originalValue)
                .isEqualTo(originalValues);

    }

    @Test
    public void testToLocalDateValue() {
        when(field.getStandaloneClassName()).thenReturn(LocalDate.class.getName());

        LocalDate originalValue = LocalDate.now();

        List<LocalDate> originalValues = new ArrayList<>();
        originalValues.add(originalValue);

        List<Date> flatValue = processor.toFlatValue(field,
                                               originalValues,
                                               context);

        Assertions.assertThat(flatValue)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        LocalDate newValue = flatValue.get(0).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        assertEquals(originalValue, newValue);



        List rawValue = processor.toRawValue(field,
                                               flatValue,
                                               originalValues,
                                               context);

        Assertions.assertThat(rawValue)
                .isNotNull()
                .isNotEmpty()
                .containsOnly(originalValue)
                .isEqualTo(originalValues);
    }

    @Test
    public void testToLocalDateTimeValue() {
        when(field.getStandaloneClassName()).thenReturn(LocalDateTime.class.getName());

        LocalDateTime originalValue = LocalDateTime.now();

        List<LocalDateTime> originalValues = new ArrayList<>();
        originalValues.add(originalValue);

        List<Date> flatValue = processor.toFlatValue(field,
                                                     originalValues,
                                                     context);

        Assertions.assertThat(flatValue)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        LocalDateTime newValue = flatValue.get(0).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        assertEquals(originalValue, newValue);

        List rawValue = processor.toRawValue(field,
                                             flatValue,
                                             originalValues,
                                             context);

        Assertions.assertThat(rawValue)
                .isNotNull()
                .isNotEmpty()
                .containsOnly(originalValue)
                .isEqualTo(originalValues);
    }


        @Test
        public void testToLocalTimeValue() {
            when(field.getStandaloneClassName()).thenReturn(LocalTime.class.getName());

            LocalTime originalValue = LocalTime.now();

            List<LocalTime> originalValues = new ArrayList<>();
            originalValues.add(originalValue);

            List<Date> flatValue =  processor.toFlatValue(field,
                                                          originalValues,
                                                          context);
            Assertions.assertThat(flatValue)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(1);

            LocalTime newValue = flatValue.get(0).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            assertEquals(originalValue, newValue);

            List rawValue = processor.toRawValue(field,
                                                 flatValue,
                                                 originalValues,
                                                 context);

            Assertions.assertThat(rawValue)
                    .isNotNull()
                    .isNotEmpty()
                    .containsOnly(originalValue)
                    .isEqualTo(originalValues);
        }

        @Test
        public void testToOffsetDateTimeValue() {
            when(field.getStandaloneClassName()).thenReturn(OffsetDateTime.class.getName());

            OffsetDateTime originalValue = OffsetDateTime.now();

            List<OffsetDateTime> originalValues = new ArrayList<>();
            originalValues.add(originalValue);

            List<Date> flatValue = processor.toFlatValue(field,
                                                   originalValues,
                                                   context);

            OffsetDateTime newValue = flatValue.get(0).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

            assertEquals(originalValue, newValue);

            List rawValue = processor.toRawValue(field,
                                                 flatValue,
                                                 originalValues,
                                                 context);

            Assertions.assertThat(rawValue)
                    .isNotNull()
                    .isNotEmpty()
                    .containsOnly(originalValue)
                    .isEqualTo(originalValues);
        }

    abstract PROCESSOR getProcessor(LocalDateFieldValueProcessor dateProcessor);
}
