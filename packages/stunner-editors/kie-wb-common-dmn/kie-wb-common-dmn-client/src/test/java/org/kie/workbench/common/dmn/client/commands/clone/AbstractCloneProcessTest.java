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

package org.kie.workbench.common.dmn.client.commands.clone;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCloneProcessTest {

    public static final DefinitionId ID_DEF1 = DefinitionId.build("def1");
    public static final DefinitionId ID_DEF2 = DefinitionId.build("def2");
    public static final DefinitionId ID_DEF3 = DefinitionId.build("def3");
    @Mock
    protected FactoryManager factoryManager;

    @Mock
    protected AdapterManager adapterManager;

    @Mock
    protected DefinitionAdapter definitionAdapter;

    @Mock
    protected PropertyAdapter propertyAdapter;

    protected final Object def1 = new Object();

    protected final Object def2 = new Object();

    protected final Object def3 = new Object();

    protected final String nameId = "name id";

    protected final String textId = "text id";

    protected final String booleanId = "boolean id";

    protected final Object nameProperty1 = new Object();

    protected final Object textProperty1 = new Object();

    protected final Object booleanProperty1 = new Object();

    protected final Object nameProperty2 = new Object();

    protected final Object textProperty2 = new Object();

    protected final Object booleanProperty2 = new Object();

    protected final Object nameProperty3 = new Object();

    protected final Object textProperty3 = new Object();

    protected final Object booleanProperty3 = new Object();

    protected final String nameValue = "test name";

    protected final String textValue = "test text";

    protected final Boolean booleanValue = true;

    @Before
    @SuppressWarnings("all")
    public void setUp() throws Exception {
        AdapterRegistry adapterRegistry = mock(AdapterRegistry.class);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterRegistry.getDefinitionAdapter(any())).thenReturn(definitionAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getPropertyFields(any())).thenReturn(new String[0]);
        when(definitionAdapter.getId(eq(def1))).thenReturn(ID_DEF1);
        when(definitionAdapter.getId(eq(def2))).thenReturn(ID_DEF2);
        when(definitionAdapter.getId(eq(def3))).thenReturn(ID_DEF3);
        when(definitionAdapter.getMetaPropertyField(eq(def1), eq(PropertyMetaTypes.NAME))).thenReturn("nameProperty");
        when(definitionAdapter.getProperty(eq(def1), eq("nameProperty"))).thenReturn(Optional.of(nameProperty1));
        when(definitionAdapter.getMetaPropertyField(eq(def2), eq(PropertyMetaTypes.NAME))).thenReturn("nameProperty");
        when(definitionAdapter.getProperty(eq(def2), eq("nameProperty"))).thenReturn(Optional.of(nameProperty2));
        when(definitionAdapter.getMetaPropertyField(eq(def3), eq(PropertyMetaTypes.NAME))).thenReturn("nameProperty");
        when(definitionAdapter.getProperty(eq(def3), eq("nameProperty"))).thenReturn(Optional.of(nameProperty3));
        when(adapterRegistry.getPropertyAdapter(any())).thenReturn(propertyAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getValue(nameProperty1)).thenReturn(nameValue);
        when(propertyAdapter.getValue(textProperty1)).thenReturn(textValue);
        when(propertyAdapter.getValue(booleanProperty1)).thenReturn(booleanValue);
        when(factoryManager.newDefinition(anyString())).thenReturn(def2);
        when(definitionAdapter.getPropertyFields(eq(def1)))
                .thenReturn(new String[]{"nameProperty", "textProperty", "booleanProperty"});
        when(definitionAdapter.getProperty(eq(def1), eq("nameProperty"))).thenReturn(Optional.of(nameProperty1));
        when(definitionAdapter.getProperty(eq(def1), eq("textProperty"))).thenReturn(Optional.of(textProperty1));
        when(definitionAdapter.getProperty(eq(def1), eq("booleanProperty"))).thenReturn(Optional.of(booleanProperty1));
        when(definitionAdapter.getPropertyFields(eq(def2)))
                .thenReturn(new String[]{"nameProperty", "textProperty", "booleanProperty"});
        when(definitionAdapter.getProperty(eq(def2), eq("nameProperty"))).thenReturn(Optional.of(nameProperty2));
        when(definitionAdapter.getProperty(eq(def2), eq("textProperty"))).thenReturn(Optional.of(textProperty2));
        when(definitionAdapter.getProperty(eq(def2), eq("booleanProperty"))).thenReturn(Optional.of(booleanProperty2));
        when(definitionAdapter.getPropertyFields(eq(def3)))
                .thenReturn(new String[]{"nameProperty", "textProperty", "booleanProperty"});
        when(definitionAdapter.getProperty(eq(def3), eq("nameProperty"))).thenReturn(Optional.of(nameProperty3));
        when(definitionAdapter.getProperty(eq(def3), eq("textProperty"))).thenReturn(Optional.of(textProperty3));
        when(definitionAdapter.getProperty(eq(def3), eq("booleanProperty"))).thenReturn(Optional.of(booleanProperty3));
        when(propertyAdapter.getId(nameProperty1)).thenReturn(nameId);
        when(propertyAdapter.getId(nameProperty2)).thenReturn(nameId);
        when(propertyAdapter.getId(nameProperty3)).thenReturn(nameId);
        when(propertyAdapter.getId(textProperty1)).thenReturn(textId);
        when(propertyAdapter.getId(textProperty2)).thenReturn(textId);
        when(propertyAdapter.getId(textProperty3)).thenReturn(textId);
        when(propertyAdapter.getId(booleanProperty1)).thenReturn(booleanId);
        when(propertyAdapter.getId(booleanProperty2)).thenReturn(booleanId);
        when(propertyAdapter.getId(booleanProperty3)).thenReturn(booleanId);
    }

    private <T> Set<T> buildSet(T... objects) {
        return Stream.of(objects).collect(Collectors.toSet());
    }

    protected void testPropertySet(Object clone,
                                   Object source,
                                   Object propertyCloned,
                                   Object expectedValue) {
        ArgumentCaptor<String> nameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(propertyAdapter).setValue(eq(propertyCloned),
                                         nameArgumentCaptor.capture());
        assertNotEquals(clone,
                        source);
        assertEquals(expectedValue,
                     nameArgumentCaptor.getValue());
    }
}
