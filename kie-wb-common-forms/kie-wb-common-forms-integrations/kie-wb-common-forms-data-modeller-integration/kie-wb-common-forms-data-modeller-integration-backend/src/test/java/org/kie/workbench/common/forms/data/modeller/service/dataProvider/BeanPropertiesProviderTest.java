/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.dataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.service.impl.AbstractDataObjectFinderTest;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFormModelHandler;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BeanPropertiesProviderTest extends AbstractDataObjectFinderTest {

    protected FormEditorRenderingContext parentContext;

    protected FormRenderingContext context;

    protected BeanPropertiesProvider provider;

    protected TableColumnMeta currentEditedMeta;

    protected MultipleSubFormFieldDefinition field;

    protected int expectedFields;

    @Before
    public void init() {
        super.init();

        service = spy(service);

        provider = new BeanPropertiesProvider(service);

        currentEditedMeta = new TableColumnMeta();

        context = new FormEditorRenderingContext("", path);

        context.setModel(currentEditedMeta);
    }

    @Test
    public void testGetAllModelProperties() {
        testGetModelProperties();
    }

    @Test
    public void testGetAllModelPropertiesWithExistingColumn() {
        currentEditedMeta.setProperty(NAME_PROPERTY);
        currentEditedMeta.setLabel(NAME_PROPERTY);
        testGetModelProperties();
    }

    @Test
    public void testSomeModelProperties() {
        testGetModelProperties(NAME_PROPERTY,
                               LAST_NAME_PROPERTY);
    }

    @Test
    public void testSomeModelPropertiesWithExistingColumn() {
        currentEditedMeta.setProperty(MARRIED_PROPERTY);
        currentEditedMeta.setLabel(MARRIED_PROPERTY);
        testGetModelProperties(NAME_PROPERTY,
                               LAST_NAME_PROPERTY);
    }

    protected void testGetModelProperties(String... columns) {
        field = new MultipleSubFormFieldDefinition();

        for (String column : columns) {
            field.getColumnMetas().add(new TableColumnMeta(column,
                                                           column));
        }

        List<String> bannedColumns = new ArrayList<>(Arrays.asList(columns));

        if (!StringUtils.isEmpty(currentEditedMeta.getProperty())) {
            bannedColumns.remove(currentEditedMeta.getProperty());
        }

        field.setStandaloneClassName(TYPE_NAME);

        parentContext = new FormEditorRenderingContext("", path);

        parentContext.setModel(field);

        context.setParentContext(parentContext);

        expectedFields = DATA_OBJECT_VALID_FIELDS - bannedColumns.size();

        SelectorData data = provider.getSelectorData(context);

        verify(service).getDataObjectProperties(any(),
                                                any());

        assertNotNull(data);
        assertNotNull(data.getValues());
        assertNull(data.getSelectedValue());

        assertFalse(data.getValues().isEmpty());
        assertEquals(expectedFields,
                     data.getValues().size());

        for (String column : bannedColumns) {
            assertNull(data.getValues().get(column));
        }

        assertNull(data.getValues().get(DataObjectFormModelHandler.SERIAL_VERSION_UID));
        assertNull(data.getValues().get(PERSISTENCE_ID_PROPERTY));
    }

    @Test
    public void testWithoutParentContext() {
        SelectorData data = provider.getSelectorData(context);

        verify(service,
               never()).getDataObjectProperties(any(),
                                                any());

        assertNotNull(data);
        assertNotNull(data.getValues());
        assertTrue(data.getValues().isEmpty());
        assertNull(data.getSelectedValue());
    }
}
