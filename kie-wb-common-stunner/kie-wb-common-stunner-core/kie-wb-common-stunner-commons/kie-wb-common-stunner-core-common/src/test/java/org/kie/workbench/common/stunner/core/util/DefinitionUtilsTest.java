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

package org.kie.workbench.common.stunner.core.util;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionUtilsTest {

    public static final String NAME_FIELD = "nameField";
    public static final String NAME_VALUE = "nameValue";
    private static final String METADATA_FIELD = "nameMetadataField";
    private DefinitionUtils tested;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private Object definition;

    @Mock
    private AdapterManager adapters;

    @Mock
    private DefinitionAdapter<Object> adapter;

    private Optional namePropertyOptional;

    @Mock
    private Object nameProperty;

    @Mock
    private PropertyAdapter<Object, Object> propertyAdapter;

    @Before
    public void setUp() throws Exception {
        namePropertyOptional = Optional.of(nameProperty);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinition()).thenReturn(adapter);
        when(adapter.getNameField(definition)).thenReturn(Optional.of(NAME_FIELD));
        when(adapter.getProperty(definition, NAME_FIELD)).thenReturn(namePropertyOptional);
        when(adapters.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getValue(nameProperty)).thenReturn(NAME_VALUE);

        tested = new DefinitionUtils(definitionManager, factoryManager, definitionsRegistry);
    }

    @Test
    public void getNameFromField() {
        final String name = tested.getName(definition);
        assertEquals(name, NAME_VALUE);
    }

    @Test
    public void getNameFromMetadata() {
        when(adapter.getNameField(definition)).thenReturn(Optional.empty());
        when(adapter.getMetaProperty(PropertyMetaTypes.NAME, definition)).thenReturn(nameProperty);

        final String name = tested.getName(definition);
        assertEquals(name, NAME_VALUE);
    }

    @Test
    public void getNameFromNullProperty() {
        when(propertyAdapter.getValue(nameProperty)).thenReturn(null);
        final String name = tested.getName(definition);
        assertEquals(name, "");
    }

    @Test
    public void getNameIdentifier() {
        final String nameIdentifier = tested.getNameIdentifier(definition);
        assertEquals(nameIdentifier, NAME_FIELD);
    }

    @Test
    public void getNameIdentifierFromMetadata() {
        when(adapter.getNameField(definition)).thenReturn(Optional.empty());
        when(adapter.getMetaProperty(PropertyMetaTypes.NAME, definition)).thenReturn(nameProperty);
        when(propertyAdapter.getId(nameProperty)).thenReturn(METADATA_FIELD);

        final String nameIdentifier = tested.getNameIdentifier(definition);
        assertEquals(nameIdentifier, METADATA_FIELD);
    }
}