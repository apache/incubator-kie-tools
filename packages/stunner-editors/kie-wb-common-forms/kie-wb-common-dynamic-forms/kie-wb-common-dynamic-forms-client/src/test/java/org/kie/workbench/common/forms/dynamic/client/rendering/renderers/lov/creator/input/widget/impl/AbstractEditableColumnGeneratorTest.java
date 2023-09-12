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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import com.google.gwt.user.cellview.client.Column;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractEditableColumnGeneratorTest<TYPE, GENERATOR extends AbstractEditableColumnGenerator<TYPE>> {

    @Mock
    protected UberfirePagedTable<TableEntry<TYPE>> pagedTable;

    @Mock
    protected CellEditionHandler<TYPE> cellEditionHandler;

    @Mock
    protected TranslationService translationService;

    protected GENERATOR generator;

    @Before
    public void init() {
        generator = spy(getGeneratorInstance(translationService));
    }

    @Test
    public void testGetSupportedTypes() {
        Assertions.assertThat(generator.getTypes())
                .isNotNull()
                .isNotEmpty()
                .contains(getSupportedTypes());
    }

    @Test
    public void testGetEditableColumn() {

        testGetColumn(false);

    }

    @Test
    public void testGetReadOnlyColumn() {

        testGetColumn(true);

    }

    protected void testGetColumn(boolean readOnly) {
        generator.registerColumn(pagedTable,
                                 cellEditionHandler, readOnly);

        verify(generator, readOnly ? never() : times(1)).getEditableColumn(pagedTable,
                                                                           cellEditionHandler);
        verify(generator, readOnly ? times(1) : never()).getReadOnlyColumn();

        verify(translationService).getTranslation(FormRenderingConstants.EditableColumnGeneratorValueHeader);

        ArgumentCaptor<Column> columnArgumentCaptor = ArgumentCaptor.forClass(Column.class);

        verify(pagedTable).addColumn(columnArgumentCaptor.capture(), Mockito.<String>any());

        Column column = columnArgumentCaptor.getValue();

        assertNotNull(column);

        if(readOnly) {
            assertNull(column.getFieldUpdater());
        } else {
            assertNotNull(column.getFieldUpdater());
        }
    }

    protected abstract GENERATOR getGeneratorInstance(TranslationService translationService);

    protected abstract String[] getSupportedTypes();
}
