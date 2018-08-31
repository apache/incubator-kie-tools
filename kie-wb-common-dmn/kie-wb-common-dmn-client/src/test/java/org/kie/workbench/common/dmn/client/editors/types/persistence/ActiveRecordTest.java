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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
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
            public void update(final Data record) {

            }

            @Override
            public void destroy(final Data record) {

            }

            @Override
            public void create(final Data record) {

            }
        });
    }

    private class Data {

    }
}
