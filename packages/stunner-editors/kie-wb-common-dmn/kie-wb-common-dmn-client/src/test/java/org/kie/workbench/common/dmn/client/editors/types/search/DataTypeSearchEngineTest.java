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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeSearchEngineTest {

    private DataTypeStore dataTypeStore;

    private DataTypeSearchEngine searchEngine;

    @Before
    public void setup() {
        dataTypeStore = spy(new DataTypeStore());
        searchEngine = new DataTypeSearchEngine(dataTypeStore);
    }

    @Test
    public void testSearch() {

        /* -------------------------------------------------------------------------------------------------------------
         *
         * The Data Type below has the following structure:
         *
         * - tCity (Structure)
         *   - id (Text)
         *   - name (Text)
         * - tPerson (Structure)
         *   - id (Text)
         *   - name (Text)
         *   - city (tAddress)
         *     - id (Text)
         *     - name (Text)
         *
         * -------------------------------------------------------------------------------------------------------------
         */

        final DataType tCity = makeDataType("001", "tCity", TOP_LEVEL_PARENT_UUID);
        final DataType tCityId = makeDataType("002", "id", "001");
        final DataType tCityName = makeDataType("003", "name", "001");
        final DataType tPerson = makeDataType("004", "tPerson", TOP_LEVEL_PARENT_UUID);
        final DataType tPersonId = makeDataType("005", "id", "004");
        final DataType tPersonName = makeDataType("006", "name", "004");
        final DataType tPersonCity = makeDataType("007", "city", "004");
        final DataType tPersonCityId = makeDataType("008", "id", "007");
        final DataType tPersonCityName = makeDataType("009", "name", "007");

        index(tCity, tCityId, tCityName, tPerson, tPersonId, tPersonName, tPersonCity, tPersonCityId, tPersonCityName);

        final List<DataType> results = searchEngine.search("name");

        /* -------------------------------------------------------------------------------------------------------------
         *
         * The result of the search has the following structure:
         *
         * - tCity (Structure)
         *   - name (Text)
         * - tPerson (Structure)
         *   - name (Text)
         *   - city (tAddress)
         *     - name (Text)
         *
         * -------------------------------------------------------------------------------------------------------------
         */

        assertTrue(results.contains(tCity));
        assertFalse(results.contains(tCityId));
        assertTrue(results.contains(tCityName));
        assertTrue(results.contains(tPerson));
        assertFalse(results.contains(tPersonId));
        assertTrue(results.contains(tPersonName));
        assertTrue(results.contains(tPersonCity));
        assertFalse(results.contains(tPersonCityId));
        assertTrue(results.contains(tPersonCityName));
    }

    @Test
    public void testSearchWhenKeywordIsEmpty() {

        final List<DataType> results = searchEngine.search("");

        verifyNoMoreInteractions(dataTypeStore);

        assertTrue(results.isEmpty());
    }

    private void index(final DataType... dataTypes) {
        for (final DataType dataType : dataTypes) {
            dataTypeStore.index(dataType.getUUID(), dataType);
        }
    }

    private DataType makeDataType(final String uuid,
                                  final String name,
                                  final String parentUUID) {

        final DataType dataType = spy(new DataType(null));

        doReturn(uuid).when(dataType).getUUID();
        doReturn(name).when(dataType).getName();
        doReturn(parentUUID).when(dataType).getParentUUID();

        return dataType;
    }
}
