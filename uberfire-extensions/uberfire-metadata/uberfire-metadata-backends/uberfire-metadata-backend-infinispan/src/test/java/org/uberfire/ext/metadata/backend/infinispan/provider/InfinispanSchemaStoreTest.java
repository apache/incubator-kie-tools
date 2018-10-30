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
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Field;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Message;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.ProtobufScope;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.ProtobufType;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InfinispanSchemaStoreTest {

    @Mock
    private InfinispanContext infinispanContext;
    private InfinispanSchemaStore infinispanSchemaStore;

    @Mock
    private MappingProvider mappingProvider;

    @Before
    public void setUp() {
        this.infinispanSchemaStore = new InfinispanSchemaStore(this.infinispanContext,
                                                               this.mappingProvider);
    }

    @Test
    public void testMerge() {
        String oldFieldName = "oldField";
        String newFieldName = "newField";
        Message oldMessage = new Message();
        Field oldField = new Field(ProtobufScope.OPTIONAL,
                                   ProtobufType.STRING,
                                   oldFieldName,
                                   1);
        oldMessage.setFields(Collections.singleton(oldField));

        Message newMessage = new Message();
        Field newField = new Field(ProtobufScope.OPTIONAL,
                                   ProtobufType.STRING,
                                   newFieldName,
                                   2);

        newMessage.setFields(new HashSet<>(Arrays.asList(oldField,
                                                         newField)));

        Message mergedMessage = this.infinispanSchemaStore.merge(oldMessage,
                                                                 newMessage);

        assertThat(mergedMessage.getFields().size()).isEqualTo(2);
        assertThat(mergedMessage.getFields()).anySatisfy(field -> field.getName().equals(oldFieldName));
        assertThat(mergedMessage.getFields()).anySatisfy(field -> field.getName().equals(newFieldName));
    }

    @Test
    public void testMaxIndexNumbert() {

        List<Field> fields = Arrays.asList(new Field(ProtobufScope.OPTIONAL,
                                                     ProtobufType.STRING,
                                                     "field1",
                                                     1),
                                           new Field(ProtobufScope.OPTIONAL,
                                                     ProtobufType.STRING,
                                                     "field2",
                                                     2));
        int max = this.infinispanSchemaStore.getMaxIndexNumber(fields);
        assertThat(max).isEqualTo(2);
    }
}