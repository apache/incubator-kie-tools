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

package org.kie.workbench.common.dmn.api.editors.types;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectTest {

    @Test
    public void testExtractName() {

        final String name1 = "org.java.SomeClass";
        final DataObject dataObject = new DataObject(name1);
        final String expected1 = "SomeClass";

        final String actual1 = dataObject.getClassNameWithoutPackage();
        assertEquals(expected1, actual1);

        final String name2 = "SomeOtherClass";
        final String expected2 = "SomeOtherClass";

        final DataObject dataObject2 = new DataObject(name2);
        final String actual2 = dataObject2.getClassNameWithoutPackage();
        assertEquals(expected2, actual2);
    }
}