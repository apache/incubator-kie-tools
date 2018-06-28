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

package org.uberfire.ext.metadata.backend.elastic.metamodel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.elastic.index.ElasticSearchIndexProvider;
import org.uberfire.ext.metadata.model.schema.MetaProperty;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ElasticSearchMappingStoreTest {

    private ElasticSearchMappingStore store;

    @Before
    public void setUp() {
        store = new ElasticSearchMappingStore(mock(ElasticSearchIndexProvider.class));
    }

    @Test
    public void testInspectMappingSingleLevel() {

        String field = "fieldName";
        Map<String, Object> tree = new LinkedHashMap<>();

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("type",
                       "text");
        attributes.put("analyzer",
                       "simple");

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(field,
                       attributes);

        tree.put("properties",
                 properties);

        Set<MetaProperty> metaProps = this.store.inspectTree(Optional.empty(),
                                                             tree);

        assertEquals(1,
                     metaProps.size());
        assertEquals(field,
                     metaProps.iterator().next().getName());
        assertEquals(String.class,
                     metaProps.iterator().next().getTypes().iterator().next());
    }

    @Test
    public void testInspectMappingMultiLevel() {

        String propertiesField = "properties";
        String field = "fieldName";
        String secondLevelFieldName = "secondLevelfieldName";

        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("type",
                       "text");
        attributes.put("analyzer",
                       "simple");

        Map<String, Object> secondLevelField = new LinkedHashMap<>();
        secondLevelField.put(secondLevelFieldName,
                             attributes);

        Map<String, Object> secondLevelProperties = new LinkedHashMap<>();
        secondLevelProperties.put(propertiesField,
                                  secondLevelField);

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(field,
                       secondLevelProperties);

        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put(propertiesField,
                 properties);

        Set<MetaProperty> metaProps = this.store.inspectTree(Optional.empty(),
                                                             tree);

        assertEquals(1,
                     metaProps.size());
        assertEquals(field + "." + secondLevelFieldName,
                     metaProps.iterator().next().getName());
        assertEquals(String.class,
                     metaProps.iterator().next().getTypes().iterator().next());
    }

    @Test
    public void testConvertType() {
        assertEquals(String.class,
                     this.store.convertType("text"));
        assertEquals(String.class,
                     this.store.convertType("keyword"));
        assertEquals(String.class,
                     this.store.loadClass("string"));
        assertEquals(Long.class,
                     this.store.loadClass("long"));
        assertEquals(Integer.class,
                     this.store.loadClass("integer"));
    }
}