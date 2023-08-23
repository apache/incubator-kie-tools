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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeActiveRecordTest {

    @Test
    public void testCreate() {

        final DataTypeRecordEngine engine = makeRecordEngine();
        final DataType record = spy(new DataType(engine));
        final DataType reference = mock(DataType.class);
        final List<DataType> expectedDataTypes = asList(mock(DataType.class), mock(DataType.class));
        final CreationType creationType = mock(CreationType.class);

        doReturn(expectedDataTypes).when(engine).create(record, reference, creationType);

        final List<DataType> actualDataTypes = record.create(reference, creationType);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testDestroyWithoutDependentTypes() {

        final DataTypeRecordEngine engine = makeRecordEngine();
        final DataType record = spy(new DataType(engine));
        final List<DataType> expectedDataTypes = singletonList(mock(DataType.class));

        doReturn(expectedDataTypes).when(engine).destroyWithoutDependentTypes(record);

        final List<DataType> actualDataTypes = record.destroyWithoutDependentTypes();

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    private DataTypeRecordEngine makeRecordEngine() {
        return spy(new DataTypeRecordEngine() {

            @Override
            public List<DataType> update(final DataType record) {
                return null;
            }

            @Override
            public List<DataType> destroy(final DataType record) {
                return null;
            }

            @Override
            public List<DataType> create(final DataType record) {
                return null;
            }

            @Override
            public boolean isValid(final DataType record) {
                return false;
            }

            @Override
            public List<DataType> create(final DataType record,
                                         final DataType reference,
                                         final CreationType creationType) {
                return null;
            }

            @Override
            public List<DataType> destroyWithoutDependentTypes(final DataType record) {
                return null;
            }
        });
    }
}
