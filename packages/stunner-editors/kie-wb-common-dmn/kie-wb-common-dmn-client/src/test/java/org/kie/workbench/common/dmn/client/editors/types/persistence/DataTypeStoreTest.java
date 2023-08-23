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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DataTypeStoreTest {

    private DataTypeStore store;

    @Mock
    private DataType dataType;

    private String uuid = "123";

    @Before
    public void setup() {
        store = new DataTypeStore();
        store.index(uuid, dataType);
    }

    @Test
    public void testGetWhenItReturnsNull() {
        assertNull(store.get("456"));
    }

    @Test
    public void testGetWhenItDoesNotReturnNull() {
        assertNotNull(store.get(uuid));
        assertEquals(dataType, store.get(uuid));
    }

    @Test
    public void testIndex() {
        // initial state
        assertEquals(1, store.size());

        // index new data type
        final DataType secondDataType = mock(DataType.class);
        final String secondUUID = "789";

        store.index(secondUUID, secondDataType);

        assertEquals(2, store.size());
        assertEquals(dataType, store.get(uuid));
        assertEquals(secondDataType, store.get(secondUUID));
    }

    @Test
    public void testClear() {
        store.clear();
        assertEquals(0, store.size());
    }

    @Test
    public void testGetTopLevelDataTypes() {

        final String secondUUID = "789";
        final String thirdUUID = "012";
        final DataType secondDataType = mock(DataType.class);
        final DataType thirdDataType = mock(DataType.class);

        when(secondDataType.isTopLevel()).thenReturn(true);

        store.index(secondUUID, secondDataType);
        store.index(thirdUUID, thirdDataType);

        final List<DataType> topLevelDataTypes = store.getTopLevelDataTypes();

        assertEquals(topLevelDataTypes, singletonList(secondDataType));
    }

    @Test
    public void testUnIndex() {

        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataType dataType4 = mock(DataType.class);
        final DataType dataType5 = mock(DataType.class);

        when(dataType0.getUUID()).thenReturn("012");
        when(dataType1.getUUID()).thenReturn("345");
        when(dataType2.getUUID()).thenReturn("678");
        when(dataType3.getUUID()).thenReturn("901");
        when(dataType4.getUUID()).thenReturn("234");
        when(dataType5.getUUID()).thenReturn("567");

        when(dataType0.getParentUUID()).thenReturn(TOP_LEVEL_PARENT_UUID);
        when(dataType1.getParentUUID()).thenReturn("012");
        when(dataType2.getParentUUID()).thenReturn("012");
        when(dataType3.getParentUUID()).thenReturn("678");
        when(dataType4.getParentUUID()).thenReturn("678");
        when(dataType5.getParentUUID()).thenReturn(TOP_LEVEL_PARENT_UUID);

        store.index(dataType0.getUUID(), dataType0);
        store.index(dataType1.getUUID(), dataType1);
        store.index(dataType2.getUUID(), dataType2);
        store.index(dataType3.getUUID(), dataType3);
        store.index(dataType4.getUUID(), dataType4);
        store.index(dataType5.getUUID(), dataType5);

        store.unIndex("012");

        assertNull(store.get("012"));
        assertNull(store.get("345"));
        assertNull(store.get("678"));
        assertNull(store.get("901"));
        assertNull(store.get("234"));
        assertNotNull(store.get("567"));
    }

    @Test
    public void testAll() {

        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);

        store.index("0", dataType0);
        store.index("1", dataType1);

        final List<DataType> all = store.all();

        assertEquals(3, all.size());
        assertTrue(all.contains(dataType));
        assertTrue(all.contains(dataType0));
        assertTrue(all.contains(dataType1));
    }
}
