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

package org.kie.workbench.common.stunner.core.definition.clone;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    protected PropertyAdapter<Object, Object> propertyAdapter;

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

    protected final Object bindableProperty = new Object();

    protected final BindableTypeMock bindablePropertyValue = mock(BindableTypeMock.class);

    protected final String bindablePropertyId = "bindablePropertyId";

    protected final BindableTypeMock bindableClonedValue = mock(BindableTypeMock.class);

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getId(eq(def1))).thenReturn(ID_DEF1);
        when(definitionAdapter.getId(eq(def2))).thenReturn(ID_DEF2);
        when(definitionAdapter.getId(eq(def3))).thenReturn(ID_DEF3);
        when(definitionAdapter.getMetaProperty(PropertyMetaTypes.NAME,
                                               def1)).thenReturn(nameProperty1);
        when(definitionAdapter.getMetaProperty(PropertyMetaTypes.NAME,
                                               def2)).thenReturn(nameProperty2);
        when(definitionAdapter.getMetaProperty(PropertyMetaTypes.NAME,
                                               def3)).thenReturn(nameProperty3);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getValue(nameProperty1)).thenReturn(nameValue);
        when(propertyAdapter.getValue(textProperty1)).thenReturn(textValue);
        when(propertyAdapter.getValue(booleanProperty1)).thenReturn(booleanValue);
        when(propertyAdapter.getValue(bindableProperty)).thenReturn(bindablePropertyValue);
        when(factoryManager.newDefinition(anyString())).thenReturn(def2);

        when(definitionAdapter.getProperties(def1)).thenReturn(buildSet(nameProperty1,
                                                                        textProperty1,
                                                                        booleanProperty1,
                                                                        bindableProperty));
        when(definitionAdapter.getProperties(def2)).thenReturn(buildSet(nameProperty2,
                                                                        textProperty2,
                                                                        booleanProperty2,
                                                                        bindableProperty));
        when(definitionAdapter.getProperties(def3)).thenReturn(buildSet(nameProperty3,
                                                                        textProperty3,
                                                                        booleanProperty3));
        when(propertyAdapter.isReadOnly(nameProperty1)).thenReturn(false);
        when(propertyAdapter.isReadOnly(textProperty1)).thenReturn(false);
        when(propertyAdapter.isReadOnly(booleanProperty1)).thenReturn(false);
        when(propertyAdapter.isReadOnly(bindableProperty)).thenReturn(false);
        when(propertyAdapter.getId(nameProperty1)).thenReturn(nameId);
        when(propertyAdapter.getId(nameProperty2)).thenReturn(nameId);
        when(propertyAdapter.getId(nameProperty3)).thenReturn(nameId);
        when(propertyAdapter.getId(textProperty1)).thenReturn(textId);
        when(propertyAdapter.getId(textProperty2)).thenReturn(textId);
        when(propertyAdapter.getId(textProperty3)).thenReturn(textId);
        when(propertyAdapter.getId(booleanProperty1)).thenReturn(booleanId);
        when(propertyAdapter.getId(booleanProperty2)).thenReturn(booleanId);
        when(propertyAdapter.getId(booleanProperty3)).thenReturn(booleanId);
        when(propertyAdapter.getId(bindableProperty)).thenReturn(bindablePropertyId);
        BindableProxyFactory.addBindableProxy(bindablePropertyValue.getClass(), mock(BindableProxyProvider.class));
        when(bindablePropertyValue.unwrap()).thenReturn(bindableClonedValue);
        when(bindablePropertyValue.deepUnwrap()).thenReturn(bindableClonedValue);
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

    protected interface BindableTypeMock extends BindableProxy {

    }
}
