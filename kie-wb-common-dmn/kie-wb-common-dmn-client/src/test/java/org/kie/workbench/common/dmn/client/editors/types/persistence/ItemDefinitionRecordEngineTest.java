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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeFactory_Structure;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionRecordEngineTest {

    private static final String STRUCTURE = "Structure";

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private DataTypeUtils dataTypeUtils;

    private ItemDefinitionRecordEngine recordEngine;

    @Before
    public void setup() {
        recordEngine = spy(new ItemDefinitionRecordEngine(itemDefinitionStore, itemDefinitionUtils, translationService, dataTypeUtils));

        when(translationService.format(DataTypeFactory_Structure)).thenReturn(STRUCTURE);
    }

    @Test
    public void testUpdate() {

        final String uuid = "uuid";
        final DataType record = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(record.getUUID()).thenReturn(uuid);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        doNothing().when(recordEngine).updateDataType(any(), any());
        doNothing().when(recordEngine).updateItemDefinition(any(), any());

        recordEngine.update(record);

        final InOrder inOrder = Mockito.inOrder(recordEngine);

        inOrder.verify(recordEngine).updateDataType(record, itemDefinition);
        inOrder.verify(recordEngine).updateItemDefinition(record, itemDefinition);
    }

    @Test
    public void testUpdateDataTypeWhenItemDefinitionIsStructure() {

        final ItemDefinition itemDefinition = makeItemDefinition(null);
        final List<DataType> subDataTypes = spy(new ArrayList<>());
        final DataType record = spy(new DataType("uuid",
                                                 "parentUUID",
                                                 "tAddress",
                                                 STRUCTURE,
                                                 subDataTypes,
                                                 false,
                                                 false,
                                                 false,
                                                 recordEngine));

        doReturn(false).when(recordEngine).isExistingItemDefinition(record);

        recordEngine.updateDataType(record, itemDefinition);

        verify(subDataTypes).clear();
        verify(subDataTypes, never()).addAll(any());
        verify(record).setBasic(false);
        verify(record).setDefault(false);
    }

    @Test
    public void testUpdateDataTypeWhenItemDefinitionIsBasic() {

        final ItemDefinition itemDefinition = makeItemDefinition(makeFEELeQName());
        final List<DataType> subDataTypes = spy(new ArrayList<>());
        final DataType record = spy(new DataType("uuid",
                                                 "parentUUID",
                                                 "tEmail",
                                                 "string",
                                                 subDataTypes,
                                                 false,
                                                 false,
                                                 false,
                                                 recordEngine));

        doReturn(false).when(recordEngine).isExistingItemDefinition(record);

        recordEngine.updateDataType(record, itemDefinition);

        verify(subDataTypes).clear();
        verify(subDataTypes, never()).addAll(any());
        verify(record).setBasic(true);
        verify(record).setDefault(true);
    }

    @Test
    public void testUpdateDataTypeWhenItemDefinitionHasExternalDataTypes() {

        final ItemDefinition itemDefinition = makeItemDefinition(makeDMNQName("tAddress"));
        final List<DataType> subDataTypes = spy(new ArrayList<>());
        final List<DataType> existingDataTypes = new ArrayList<>();
        final DataType record = spy(new DataType("uuid",
                                                 "parentUUID",
                                                 "address",
                                                 "tAddress",
                                                 subDataTypes,
                                                 false,
                                                 false,
                                                 false,
                                                 recordEngine));

        doReturn(true).when(recordEngine).isExistingItemDefinition(record);

        when(dataTypeUtils.externalDataTypes(record, record.getType())).thenReturn(existingDataTypes);

        recordEngine.updateDataType(record, itemDefinition);

        verify(subDataTypes).clear();
        verify(subDataTypes).addAll(existingDataTypes);
        verify(record).setBasic(true);
        verify(record).setDefault(false);
    }

    @Test
    public void testUpdateItemDefinitionWhenDataTypeIsBasic() {

        final ItemDefinition itemDefinition = makeItemDefinition(makeDMNQName("tAddress"));
        final QName qName = mock(QName.class);
        final Name name = mock(Name.class);
        final List<DataType> subDataTypes = spy(new ArrayList<>());
        final DataType record = spy(new DataType("uuid",
                                                 "parentUUID",
                                                 "email",
                                                 "tEmail",
                                                 subDataTypes,
                                                 true,
                                                 false,
                                                 false,
                                                 recordEngine));

        doReturn(qName).when(recordEngine).makeQName(record);
        doReturn(name).when(recordEngine).makeName(record);

        recordEngine.updateItemDefinition(record, itemDefinition);

        assertTrue(itemDefinition.getItemComponent().isEmpty());
        assertEquals(qName, itemDefinition.getTypeRef());
        assertEquals(name, itemDefinition.getName());
    }

    @Test
    public void testUpdateItemDefinitionWhenDataTypeIsNotBasic() {

        final ItemDefinition itemDefinition = makeItemDefinition(makeDMNQName("tPerson"));
        final QName qName = mock(QName.class);
        final Name name = mock(Name.class);
        final List<DataType> subDataTypes = spy(new ArrayList<>());
        final DataType record = spy(new DataType("uuid",
                                                 "parentUUID",
                                                 "tPerson",
                                                 STRUCTURE,
                                                 subDataTypes,
                                                 false,
                                                 false,
                                                 false,
                                                 recordEngine));

        doReturn(qName).when(recordEngine).makeQName(record);
        doReturn(name).when(recordEngine).makeName(record);

        recordEngine.updateItemDefinition(record, itemDefinition);

        assertNull(itemDefinition.getTypeRef());
        assertEquals(name, itemDefinition.getName());
    }

    @Test
    public void testMakeName() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "name";

        when(dataType.getName()).thenReturn(expectedName);

        final Name name = recordEngine.makeName(dataType);

        assertEquals(expectedName, name.getValue());
    }

    @Test
    public void testMakeQNameWhenDataTypeIsDefault() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "string";

        when(dataType.isDefault()).thenReturn(true);
        when(dataType.getType()).thenReturn(expectedName);

        final QName name = recordEngine.makeQName(dataType);
        final String actualName = name.getLocalPart();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testMakeQNameWhenDataTypeIsNotDefault() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "tAddress";

        when(dataType.isDefault()).thenReturn(false);
        when(dataType.getType()).thenReturn(expectedName);

        final QName name = recordEngine.makeQName(dataType);
        final String actual = name.toString();
        final String expected = "tAddress";

        assertEquals(expected, actual);
    }

    @Test
    public void testIsExistingItemDefinitionWhenItExists() {

        final DataType dataType = mock(DataType.class);
        final String type = "type";

        when(itemDefinitionUtils.findByName(type)).thenReturn(Optional.of(mock(ItemDefinition.class)));
        when(dataType.getType()).thenReturn(type);

        assertTrue(recordEngine.isExistingItemDefinition(dataType));
    }

    @Test
    public void testIsExistingItemDefinitionWhenItDoesNotExist() {

        final DataType dataType = mock(DataType.class);
        final String type = "type";

        when(itemDefinitionUtils.findByName(type)).thenReturn(Optional.empty());
        when(dataType.getType()).thenReturn(type);

        assertFalse(recordEngine.isExistingItemDefinition(dataType));
    }

    private ItemDefinition makeItemDefinition(final QName value) {
        final ItemDefinition itemDefinition = new ItemDefinition();

        itemDefinition.setTypeRef(value);

        return itemDefinition;
    }

    private QName makeFEELeQName() {
        return new QName(DMNModelInstrumentedBase.Namespace.FEEL.getUri(), BuiltInType.STRING.getName());
    }

    private QName makeDMNQName(final String type) {
        return new QName(DMNModelInstrumentedBase.Namespace.DMN.getUri(), type);
    }
}