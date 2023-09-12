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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeUtilsTest {

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private TranslationService translationService;

    @Mock
    private ItemDefinitionRecordEngine recordEngine;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private ManagedInstance<DataTypeManager> dataTypeManagers;

    @Mock
    private DataTypeNameValidator dataTypeNameValidator;

    @Mock
    private DataTypeManagerStackStore typeStack;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    private Definitions definitions = makeModelDefinitions();

    private final String STRUCTURE = "STRUCTURE";

    private final String IMPORT_NAME = "MYMODEL";

    private DataTypeManager dataTypeManager;

    private DataTypeUtils utils;

    @Before
    public void setup() {
        dataTypeManager = spy(new DataTypeManager(translationService, recordEngine, itemDefinitionStore, dataTypeStore, itemDefinitionUtils, dataTypeManagers, dataTypeNameValidator, typeStack));
        utils = spy(new DataTypeUtils(dataTypeStore, dataTypeManager, dmnGraphUtils));

        when(dataTypeManager.structure()).thenReturn(STRUCTURE);
        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
    }

    @Test
    public void testDefaultDataTypes() {

        final List<DataType> dataTypes = utils.defaultDataTypes();

        assertEquals(10, dataTypes.size());
        assertEquals("Any", dataTypes.get(0).getType());
        assertEquals("boolean", dataTypes.get(1).getType());
        assertEquals("context", dataTypes.get(2).getType());
        assertEquals("date", dataTypes.get(3).getType());
        assertEquals("date and time", dataTypes.get(4).getType());
        assertEquals("days and time duration", dataTypes.get(5).getType());
        assertEquals("number", dataTypes.get(6).getType());
        assertEquals("string", dataTypes.get(7).getType());
        assertEquals("time", dataTypes.get(8).getType());
        assertEquals("years and months duration", dataTypes.get(9).getType());
    }

    @Test
    public void testCustomDataTypes() {

        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> unorderedDataTypes = asList(dataType1, dataType2);
        final List<DataType> expectedDataTypes = asList(dataType2, dataType1);

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(unorderedDataTypes);
        when(dataType1.getName()).thenReturn("z");
        when(dataType2.getName()).thenReturn("a");

        final List<DataType> actualDataTypes = utils.customDataTypes();

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testGetTopLevelParent() {

        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final String uuid1 = "0000";
        final String uuid2 = "1111";
        final String uuid3 = "2222";

        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);

        when(dataType1.getParentUUID()).thenReturn("");
        when(dataType2.getParentUUID()).thenReturn(uuid1);
        when(dataType3.getParentUUID()).thenReturn(uuid2);

        when(dataTypeStore.get(uuid1)).thenReturn(dataType1);
        when(dataTypeStore.get(uuid2)).thenReturn(dataType2);
        when(dataTypeStore.get(uuid3)).thenReturn(dataType3);

        final DataType topLevelParent = utils.getTopLevelParent(dataType3);

        assertEquals(dataType1, topLevelParent);
    }

    @Test
    public void testGetDataTypeKindWhenItsBuiltIn() {

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(emptyList());

        final DataTypeKind expectedKind = DataTypeKind.BUILT_IN;
        final DataTypeKind actualKind = utils.getDataTypeKind("number");

        assertEquals(expectedKind, actualKind);
    }

    @Test
    public void testGetDataTypeKindWhenItsCustom() {

        final DataType dataType = mock(DataType.class);

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(singletonList(dataType));
        when(dataType.getName()).thenReturn("tUUID");
        when(dataType.getType()).thenReturn("string");

        final DataTypeKind expectedKind = DataTypeKind.CUSTOM;
        final DataTypeKind actualKind = utils.getDataTypeKind("tUUID");

        assertEquals(expectedKind, actualKind);
    }

    @Test
    public void testGetDataTypeKindWhenItsStructure() {

        final DataType dataType = mock(DataType.class);

        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(singletonList(dataType));
        when(dataType.getName()).thenReturn("tPerson");
        when(dataType.getType()).thenReturn(STRUCTURE);

        final DataTypeKind expectedKind = DataTypeKind.STRUCTURE;
        final DataTypeKind actualKind = utils.getDataTypeKind("tPerson");

        assertEquals(expectedKind, actualKind);
    }

    @Test
    public void testGetDataTypeKindWhenItsIncluded() {

        final DataType dataType = mock(DataType.class);
        final String typeName = IMPORT_NAME + "tPerson";

        when(dataType.getName()).thenReturn(typeName);
        when(dataType.getType()).thenReturn(STRUCTURE);
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(singletonList(dataType));

        final DataTypeKind expectedKind = DataTypeKind.INCLUDED;
        final DataTypeKind actualKind = utils.getDataTypeKind(typeName);

        assertEquals(expectedKind, actualKind);
    }

    private Definitions makeModelDefinitions() {
        final Definitions definitions = mock(Definitions.class);
        final List<Import> imports = singletonList(makeImport());
        when(definitions.getImport()).thenReturn(imports);
        return definitions;
    }

    private Import makeImport() {
        final Import anImport = mock(Import.class);
        final Name myModelName = new Name(IMPORT_NAME);
        when(anImport.getName()).thenReturn(myModelName);
        return anImport;
    }
}
