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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        store.unIndex(uuid);

        assertNull(store.get(uuid));
    }
}
