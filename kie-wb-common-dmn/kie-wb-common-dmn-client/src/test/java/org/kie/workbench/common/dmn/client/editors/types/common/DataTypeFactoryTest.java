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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.commons.uuid.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeFactory_None;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeFactory_Structure;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({UUID.class})
@RunWith(PowerMockRunner.class)
public class DataTypeFactoryTest {

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private TranslationService translationService;

    private DataTypeFactory factory;

    @Before
    public void setup() {
        mockStatic(UUID.class);
        PowerMockito.when(UUID.uuid()).thenReturn("uuid");

        when(translationService.format(DataTypeFactory_None)).thenReturn("--");
        when(translationService.format(DataTypeFactory_Structure)).thenReturn("(Structure)");

        factory = new DataTypeFactory(itemDefinitionUtils, translationService);
    }

    @Test
    public void testMakeDataTypeFromItemDefinition() {

        final ItemDefinition simpleDataTypeFromMainDataType = makeItem("name", "Text");
        final ItemDefinition simpleDataTypeFromStructureDataType = makeItem("company", "Text");
        final ItemDefinition structureDataType = makeItem("employee", null, simpleDataTypeFromStructureDataType);
        final ItemDefinition existingDataType = makeItem("address", "tAddress");
        final ItemDefinition simpleDataTypeFromExistingDataTye = makeItem("street", "Text");
        final ItemDefinition existingDataTypeWithFields = makeItem("address", "tAddress", simpleDataTypeFromExistingDataTye);
        final ItemDefinition mainDataType = makeItem("tPerson", null, simpleDataTypeFromMainDataType, existingDataType, structureDataType);

        /* -------------------------------------------------------------------------------------------------------------
         * The Item Definition above has the following structure:
         *
         * - tPerson (null)                  # ItemDefinition with 'null' indicates that it has one or more sub DataType(s).
         *   - name (Text)                   #
         *   - address (tAddress)            # Since 'tAddress' is an existing custom DataType, we need to fetch its fields
         *     * street (Text)               # 'street' is a fetched field from 'tAddress'
         *   - employee (null)               # (again) ItemDefinition with 'null' indicates that it has one or more sub DataType(s).
         *     - company (Text)              #
         * -------------------------------------------------------------------------------------------------------------
         * */

        when(itemDefinitionUtils.findByName(any())).thenReturn(Optional.empty());
        when(itemDefinitionUtils.findByName(eq("tAddress"))).thenReturn(Optional.of(existingDataTypeWithFields));

        final DataType tPerson = factory.makeDataType(mainDataType);

        assertEquals("uuid", tPerson.getUUID());
        assertEquals("tPerson", tPerson.getName());
        assertEquals("(Structure)", tPerson.getType());
        assertEquals(3, tPerson.getSubDataTypes().size());
        assertFalse(tPerson.isBasic());
        assertTrue(tPerson.hasSubDataTypes());
        assertFalse(tPerson.isExternal());
        assertFalse(tPerson.isDefault());

        final DataType name = tPerson.getSubDataTypes().get(0);
        final DataType address = tPerson.getSubDataTypes().get(1);
        final DataType street = address.getSubDataTypes().get(0);
        final DataType employee = tPerson.getSubDataTypes().get(2);
        final DataType company = employee.getSubDataTypes().get(0);

        assertEquals("uuid", name.getUUID());
        assertEquals("name", name.getName());
        assertEquals("Text", name.getType());
        assertEquals(0, name.getSubDataTypes().size());
        assertTrue(name.isBasic());
        assertFalse(name.hasSubDataTypes());
        assertFalse(name.isExternal());
        assertFalse(name.isDefault());

        assertEquals("uuid", address.getUUID());
        assertEquals("address", address.getName());
        assertEquals("tAddress", address.getType());
        assertEquals(1, address.getSubDataTypes().size());
        assertTrue(address.isBasic());
        assertTrue(address.hasSubDataTypes());
        assertFalse(address.isExternal());
        assertFalse(address.isDefault());

        assertEquals("uuid", street.getUUID());
        assertEquals("street", street.getName());
        assertEquals("Text", street.getType());
        assertEquals(0, street.getSubDataTypes().size());
        assertTrue(street.isBasic());
        assertFalse(street.hasSubDataTypes());
        assertTrue(street.isExternal());
        assertFalse(street.isDefault());

        assertEquals("uuid", employee.getUUID());
        assertEquals("employee", employee.getName());
        assertEquals("(Structure)", employee.getType());
        assertEquals(1, employee.getSubDataTypes().size());
        assertFalse(employee.isBasic());
        assertTrue(employee.hasSubDataTypes());
        assertFalse(employee.isExternal());
        assertFalse(employee.isDefault());

        assertEquals("uuid", company.getUUID());
        assertEquals("company", company.getName());
        assertEquals("Text", company.getType());
        assertEquals(0, company.getSubDataTypes().size());
        assertTrue(company.isBasic());
        assertFalse(company.hasSubDataTypes());
        assertFalse(company.isExternal());
        assertFalse(company.isDefault());
    }

    @Test
    public void testMakeDataTypeFromBuiltInType() {

        final BuiltInType builtInType = BuiltInType.values()[0];
        final DataType dataType = factory.makeDataType(builtInType);

        assertEquals("uuid", dataType.getUUID());
        assertEquals("--", dataType.getName());
        assertEquals("number", dataType.getType());
        assertEquals(Collections.emptyList(), dataType.getSubDataTypes());
        assertFalse(dataType.isBasic());
        assertFalse(dataType.hasSubDataTypes());
        assertFalse(dataType.isExternal());
        assertTrue(dataType.isDefault());
    }

    @Test
    public void testIsDefaultNull() {
        assertFalse(factory.isDefault(null));
    }

    @Test
    public void testIsDefaultUnknown() {
        assertFalse(factory.isDefault("unknown"));
    }

    @Test
    public void testIsDefault() {
        assertTrue(factory.isDefault(BuiltInType.ANY.getName()));
    }

    private ItemDefinition makeItem(final String itemName,
                                    final String itemType,
                                    final ItemDefinition... subItemDefinitions) {

        final List<ItemDefinition> itemDefinitions = new ArrayList<>(Arrays.asList(subItemDefinitions));
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);
        final QName typeRefMock = mock(QName.class);
        final QName typeRef = subItemDefinitions.length == 0 ? typeRefMock : null;

        when(name.getValue()).thenReturn(itemName);
        when(typeRefMock.getLocalPart()).thenReturn(itemType);
        when(itemDefinition.getName()).thenReturn(name);
        when(itemDefinition.getItemComponent()).thenReturn(itemDefinitions);
        when(itemDefinition.getTypeRef()).thenReturn(typeRef);

        return itemDefinition;
    }
}
