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

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDynamicDefinitionId;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractBindableDefinitionAdapterTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private AbstractBindableDefinitionAdapter delegate;

    private final Map<Class, String> propertyIdFieldNames = new HashMap<>();
    private final Map<Class, Class> baseTypes = new HashMap<>();

    private AbstractBindableDefinitionAdapterStub testeed;
    private SomePojo pojo;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        pojo = new SomePojo();
        testeed = new AbstractBindableDefinitionAdapterStub(definitionUtils);
        testeed.setBindings(mock(Map.class),
                            baseTypes,
                            mock(Map.class),
                            mock(Map.class),
                            mock(Map.class),
                            propertyIdFieldNames,
                            mock(Map.class),
                            mock(Map.class),
                            mock(Map.class),
                            mock(Map.class),
                            mock(Map.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetId() {
        DefinitionId id = testeed.getId(pojo);
        assertEquals(getDefinitionId(SomePojo.class), id.value());
        assertEquals(getDefinitionId(SomePojo.class), id.type());
        assertFalse(id.isDynamic());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetIdWhenDynamic() {
        String idFieldName = "theIdField";
        String idFieldValue = "theIdFieldValue";
        propertyIdFieldNames.put(SomePojo.class, idFieldName);
        when(delegate.getStringFieldValue(eq(pojo), eq(idFieldName))).thenReturn(idFieldValue);
        DefinitionId id = testeed.getId(pojo);
        assertEquals(getDynamicDefinitionId(SomePojo.class, idFieldValue), id.value());
        assertEquals(getDefinitionId(SomePojo.class), id.type());
        assertTrue(id.isDynamic());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetBaseType() {
        String somePojoClassId = getDefinitionId(SomeBasePojo.class);
        baseTypes.put(SomePojo.class, SomeBasePojo.class);
        assertEquals(somePojoClassId, testeed.getBaseType(SomePojo.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTypes() {
        String somePojoClassId = getDefinitionId(SomeBasePojo.class);
        String pojoClassId = getDefinitionId(SomePojo.class);
        String[] types = testeed.getTypes(somePojoClassId);
        assertNull(types);
        baseTypes.put(SomePojo.class, SomeBasePojo.class);
        types = testeed.getTypes(somePojoClassId);
        assertEquals(1, types.length);
        assertEquals(pojoClassId, types[0]);
    }

    private static class SomePojo {

    }

    private static class SomeBasePojo {

    }

    private class AbstractBindableDefinitionAdapterStub<T> extends AbstractBindableDefinitionAdapter<T> {

        public AbstractBindableDefinitionAdapterStub(final DefinitionUtils definitionUtils) {
            super(definitionUtils);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Set<?> getBindProperties(T pojo) {
            return delegate.getBindProperties(pojo);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected String getStringFieldValue(T pojo, String fieldName) {
            return delegate.getStringFieldValue(pojo, fieldName);
        }

        @Override
        @SuppressWarnings("unchecked")
        public String getCategory(T pojo) {
            return delegate.getCategory(pojo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public String getTitle(T pojo) {
            return delegate.getTitle(pojo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public String getDescription(T pojo) {
            return delegate.getDescription(pojo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Set<String> getLabels(T pojo) {
            return delegate.getLabels(pojo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Set<?> getPropertySets(T pojo) {
            return delegate.getPropertySets(pojo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Optional<?> getProperty(T pojo, String propertyName) {
            return delegate.getProperty(pojo, propertyName);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Optional<String> getNameField(T pojo) {
            return delegate.getNameField(pojo);
        }
    }
}
