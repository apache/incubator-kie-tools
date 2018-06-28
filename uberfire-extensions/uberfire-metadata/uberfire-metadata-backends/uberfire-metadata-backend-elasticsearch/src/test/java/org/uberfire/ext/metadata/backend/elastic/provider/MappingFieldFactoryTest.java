/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.metadata.backend.elastic.provider;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticMetaObject;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticMetaProperty;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticSearchMappingStore;
import org.uberfire.ext.metadata.metamodel.NullMetaModelStore;
import org.uberfire.ext.metadata.model.impl.KObjectImpl;
import org.uberfire.ext.metadata.model.schema.MetaObject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MappingFieldFactoryTest {

    private MappingFieldFactory fieldFactory;

    @Before
    public void setUp() {
        this.fieldFactory = new MappingFieldFactory(mock(ElasticSearchMappingStore.class));
    }

    @Test
    public void testCreateMetaObject() {

        KObjectImpl kobject = new KObjectImpl("1",
                                              "java",
                                              "cluster",
                                              "segment",
                                              "key",
                                              Arrays.asList(),
                                              true);
        ElasticMetaObject metaObject = (ElasticMetaObject) this.fieldFactory.build(kobject);

        assertEquals("cluster",
                     ((ElasticMetaProperty) metaObject.getProperty(MetaObject.META_OBJECT_CLUSTER_ID).get()).getValue());
        assertEquals(false,
                     metaObject.getProperty(MetaObject.META_OBJECT_CLUSTER_ID).get().isSearchable());
    }
}