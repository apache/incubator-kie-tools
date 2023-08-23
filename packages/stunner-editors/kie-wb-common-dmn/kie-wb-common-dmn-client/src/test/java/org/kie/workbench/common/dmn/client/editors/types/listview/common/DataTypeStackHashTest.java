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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class DataTypeStackHashTest {

    private DataTypeStore dataTypeStore;

    private DataTypeStackHash dataTypeStackHash;

    private DataType tCity;

    private DataType tCityId;

    private DataType tCityName;

    private DataType tPerson;

    private DataType tPersonId;

    private DataType tPersonName;

    private DataType tPersonCity;

    private DataType tPersonCityId;

    private DataType tPersonCityName;

    @Before
    public void setup() {
        dataTypeStore = new DataTypeStore();
        dataTypeStackHash = new DataTypeStackHash(dataTypeStore);

        tCity = makeDataType("001", "tCity", TOP_LEVEL_PARENT_UUID);
        tCityId = makeDataType("002", "id", tCity.getUUID());
        tCityName = makeDataType("003", "name", tCity.getUUID());
        tPerson = makeDataType("004", "tPerson", TOP_LEVEL_PARENT_UUID);
        tPersonId = makeDataType("005", "id", tPerson.getUUID());
        tPersonName = makeDataType("006", "name", tPerson.getUUID());
        tPersonCity = makeDataType("007", "city", tPerson.getUUID());
        tPersonCityId = makeDataType("008", "id", tPersonCity.getUUID());
        tPersonCityName = makeDataType("009", "name", tPersonCity.getUUID());

        index(tCity, tCityId, tCityName, tPerson, tPersonId, tPersonName, tPersonCity, tPersonCityId, tPersonCityName);
    }

    @Test
    public void testCalculateHash() {

        assertEquals("tCity", dataTypeStackHash.calculateHash(tCity));
        assertEquals("tCity.id", dataTypeStackHash.calculateHash(tCityId));
        assertEquals("tCity.name", dataTypeStackHash.calculateHash(tCityName));
        assertEquals("tPerson", dataTypeStackHash.calculateHash(tPerson));
        assertEquals("tPerson.id", dataTypeStackHash.calculateHash(tPersonId));
        assertEquals("tPerson.name", dataTypeStackHash.calculateHash(tPersonName));
        assertEquals("tPerson.city", dataTypeStackHash.calculateHash(tPersonCity));
        assertEquals("tPerson.city.id", dataTypeStackHash.calculateHash(tPersonCityId));
        assertEquals("tPerson.city.name", dataTypeStackHash.calculateHash(tPersonCityName));
    }

    @Test
    public void testCalculateParentHash() {

        index(tCity, tCityId, tCityName, tPerson, tPersonId, tPersonName, tPersonCity, tPersonCityId, tPersonCityName);

        assertEquals("", dataTypeStackHash.calculateParentHash(tCity));
        assertEquals("tCity", dataTypeStackHash.calculateParentHash(tCityId));
        assertEquals("tCity", dataTypeStackHash.calculateParentHash(tCityName));
        assertEquals("", dataTypeStackHash.calculateParentHash(tPerson));
        assertEquals("tPerson", dataTypeStackHash.calculateParentHash(tPersonId));
        assertEquals("tPerson", dataTypeStackHash.calculateParentHash(tPersonName));
        assertEquals("tPerson", dataTypeStackHash.calculateParentHash(tPersonCity));
        assertEquals("tPerson.city", dataTypeStackHash.calculateParentHash(tPersonCityId));
        assertEquals("tPerson.city", dataTypeStackHash.calculateParentHash(tPersonCityName));
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
