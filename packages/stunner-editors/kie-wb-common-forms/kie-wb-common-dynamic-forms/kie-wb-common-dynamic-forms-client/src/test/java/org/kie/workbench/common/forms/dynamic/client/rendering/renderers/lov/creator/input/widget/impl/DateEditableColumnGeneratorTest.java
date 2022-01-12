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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DateEditableColumnGeneratorTest extends AbstractEditableColumnGeneratorTest<Date, DateEditableColumnGenerator> {

    @Mock
    private DateTimePickerCell dateTimePickerCell;

    @Mock
    private ManagedInstance<DateTimePickerCell> dateTimePickerCells;

    @Override
    public void init() {
        super.init();

        when(dateTimePickerCells.get()).thenReturn(dateTimePickerCell);
    }

    @Override
    public void testGetEditableColumn() {
        super.testGetEditableColumn();

        verify(dateTimePickerCells).get();
    }

    @Override
    protected DateEditableColumnGenerator getGeneratorInstance(TranslationService translationService) {
        return new DateEditableColumnGenerator(translationService, dateTimePickerCells);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{Date.class.getName(), "java.time.LocalDate", "java.time.LocalDateTime", "java.time.LocalTime", "java.time.OffsetDateTime"};
    }
}
