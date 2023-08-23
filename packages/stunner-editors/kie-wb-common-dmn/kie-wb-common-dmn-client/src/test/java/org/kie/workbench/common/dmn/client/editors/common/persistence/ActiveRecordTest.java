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

package org.kie.workbench.common.dmn.client.editors.common.persistence;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ActiveRecordTest {

    @Test
    public void testGetRecordEngineWhenRecordEngineIsNull() {

        final ActiveRecord<Data> activeRecord = new ActiveRecord<Data>(null) {
            @Override
            protected Data getRecord() {
                return null;
            }
        };

        assertThatThrownBy(activeRecord::getRecordEngine)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("'ActiveRecord' operations are not supported. Please provide a record engine.");
    }

    @Test
    public void testGetRecordEngineWhenRecordEngineIsNotNull() {

        final RecordEngine<Data> expectedRecordEngine = makeRecordEngine();
        final ActiveRecord<Data> activeRecord = new ActiveRecord<Data>(expectedRecordEngine) {
            @Override
            protected Data getRecord() {
                return null;
            }
        };
        final RecordEngine<Data> actualRecordEngine = activeRecord.getRecordEngine();

        assertEquals(expectedRecordEngine, actualRecordEngine);
    }

    @Test
    public void testUpdate() {

        final RecordEngine<Data> engine = makeRecordEngine();
        final ActiveRecord<Data> activeRecord = activeRecord(engine);

        activeRecord.update();

        verify(engine).update(activeRecord.getRecord());
    }

    @Test
    public void testDestroy() {

        final RecordEngine<Data> engine = makeRecordEngine();
        final ActiveRecord<Data> activeRecord = activeRecord(engine);

        activeRecord.destroy();

        verify(engine).destroy(activeRecord.getRecord());
    }

    @Test
    public void testCreate() {

        final RecordEngine<Data> engine = makeRecordEngine();
        final ActiveRecord<Data> activeRecord = activeRecord(engine);

        activeRecord.create();

        verify(engine).create(activeRecord.getRecord());
    }

    @Test
    public void testIsValidWhenItIsTrue() {

        final RecordEngine<Data> engine = makeRecordEngine();
        final ActiveRecord<Data> activeRecord = activeRecord(engine);

        doReturn(true).when(engine).isValid(any());

        assertTrue(activeRecord.isValid());
    }

    @Test
    public void testIsValidWhenItIsFalse() {

        final RecordEngine<Data> engine = makeRecordEngine();
        final ActiveRecord<Data> activeRecord = activeRecord(engine);

        doReturn(false).when(engine).isValid(any());

        assertFalse(activeRecord.isValid());
    }

    @Test
    public void testIsRecordEnginePresentWhenRecordEngineIsPresent() {
        final ActiveRecord<Data> record = activeRecord(null);
        assertFalse(record.isRecordEnginePresent());
    }

    @Test
    public void testIsRecordEnginePresentWhenRecordEngineIsNotPresent() {
        final ActiveRecord<Data> record = activeRecord(makeRecordEngine());
        assertTrue(record.isRecordEnginePresent());
    }

    private ActiveRecord<Data> activeRecord(final RecordEngine<Data> engine) {

        final Data record = new Data();

        return spy(new ActiveRecord<Data>(engine) {
            @Override
            protected Data getRecord() {
                return record;
            }
        });
    }

    private RecordEngine<Data> makeRecordEngine() {
        return spy(new RecordEngine<Data>() {

            @Override
            public List<Data> update(final Data record) {
                return null;
            }

            @Override
            public List<Data> destroy(final Data record) {
                return null;
            }

            @Override
            public List<Data> create(final Data record) {
                return null;
            }

            @Override
            public boolean isValid(final Data record) {
                return true;
            }
        });
    }

    private class Data {

    }
}
