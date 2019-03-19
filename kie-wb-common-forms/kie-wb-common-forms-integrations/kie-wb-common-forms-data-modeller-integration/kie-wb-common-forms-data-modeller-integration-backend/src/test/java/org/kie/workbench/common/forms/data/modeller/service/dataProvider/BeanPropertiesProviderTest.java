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
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.service.impl.AbstractModelFinderTest;
import org.kie.workbench.common.forms.data.modeller.service.shared.ModelFinderService;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.forms.data.modeller.service.impl.ext.util.ModelReaderUtil.SERIAL_VERSION_UID;

public class BeanPropertiesProviderTest extends AbstractModelFinderTest {

    protected ModelFinderService modelFinderService;

    protected FormEditorRenderingContext parentContext;

    protected FormRenderingContext context;

    protected BeanPropertiesProvider provider;

    protected TableColumnMeta currentEditedMeta;

    protected MultipleSubFormFieldDefinition field;

    protected int expectedFields;

    @BeforeClass
    public static void setUp() throws Exception {
        initialize();

        buildModules("module1", "module2");
    }

    @Before
    public void init() {
        modelFinderService = weldContainer.select(ModelFinderService.class).get();

        provider = new BeanPropertiesProvider(modelFinderService);

        currentEditedMeta = new TableColumnMeta();

        context = new FormEditorRenderingContext("", currentModulePath);

        context.setModel(currentEditedMeta);
    }

    @Test
    public void testGetAllModelProperties() {
        testGetModelProperties();
    }

    @Test
    public void testGetAllModelPropertiesWithExistingColumn() {
        currentEditedMeta.setProperty(ADDRESS_STREET);
        currentEditedMeta.setLabel(ADDRESS_STREET_LABEL);
        testGetModelProperties();
    }

    @Test
    public void testSomeModelProperties() {
        testGetModelProperties(ADDRESS_CITY, ADDRESS_CP);
    }

    @Test
    public void testSomeModelPropertiesWithExistingColumn() {
        currentEditedMeta.setProperty(ADDRESS_MAIN_ADDRESS);
        currentEditedMeta.setLabel(ADDRESS_MAIN_ADDRESS_LABEL);
        testGetModelProperties(ADDRESS_STREET, ADDRESS_NUM);
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

        field.setStandaloneClassName(ADDRESS_TYPE);

        parentContext = new FormEditorRenderingContext("", currentModulePath);

        parentContext.setModel(field);

        context.setParentContext(parentContext);

        expectedFields = ADDRESS_VALID_FIELDS - bannedColumns.size();

        SelectorData data = provider.getSelectorData(context);

        assertNotNull(data);
        assertNotNull(data.getValues());
        assertNull(data.getSelectedValue());

        assertFalse(data.getValues().isEmpty());
        assertEquals(expectedFields,
                     data.getValues().size());

        for (String column : bannedColumns) {
            assertNull(data.getValues().get(column));
        }

        assertNull(data.getValues().get(SERIAL_VERSION_UID));
        assertNull(data.getValues().get(PERSISTENCE_ID_PROPERTY));
    }

    @Test
    public void testWithoutParentContext() {
        SelectorData data = provider.getSelectorData(context);
        /**/
        assertNotNull(data);
        assertNotNull(data.getValues());
        assertTrue(data.getValues().isEmpty());
        assertNull(data.getSelectedValue());
    }
}
