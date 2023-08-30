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

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class DataTypeManagerStackStoreTest {

    private DataTypeManagerStackStore typeStack;

    private String uuid = "123";

    private List<String> types = asList("tCity", "tPerson");

    @Before
    public void setup() {
        this.typeStack = new DataTypeManagerStackStore();

        typeStack.put(uuid, types);
    }

    @Test
    public void testGetWhenTypeStackHasTheUUID() {
        assertEquals(types, typeStack.get(uuid));
    }

    @Test
    public void testGetWhenTypeStackDoesNotHaveTheUUID() {

        final List<Object> expectedTypes = emptyList();
        final List<String> actualTypes = typeStack.get("otherUUID");

        assertEquals(expectedTypes, actualTypes);
    }

    @Test
    public void testPut() {

        final String uuid = "456";
        final List<String> expectedTypes = asList("tCompany", "tPerson");

        typeStack.put(uuid, expectedTypes);

        final List<String> actualTypes = typeStack.get(uuid);

        assertEquals(expectedTypes, actualTypes);
    }

    @Test
    public void testClear() {
        typeStack.clear();

        assertEquals(0, typeStack.size());
    }
}
