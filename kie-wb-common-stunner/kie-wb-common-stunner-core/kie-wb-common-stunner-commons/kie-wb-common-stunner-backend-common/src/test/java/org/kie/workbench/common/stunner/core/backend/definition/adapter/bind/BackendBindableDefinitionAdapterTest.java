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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.bind;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooTestBean;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BackendBindableDefinitionAdapterTest extends AbstractBackendBindableAdapterTest {

    private BackendBindableDefinitionAdapter tested;

    @Before
    public void setUp() {
        super.setUp();

        tested = new BackendBindableDefinitionAdapter(utils);
        tested.setBindings(metaPropertyTypeClasses, baseTypes, propertySetsFieldNames, propertiesFieldNames,
                           propertyGraphFactoryFieldNames, propertyIdFieldNames, propertyLabelsFieldNames,
                           propertyTitleFieldNames, propertyCategoryFieldNames, propertyDescriptionFieldNames,
                           propertyNameFields);

        propertyNameFields.put(instance.getClass(), FooTestBean.FOO_PROPERTY_NAME);
        propertiesFieldNames.put(instance.getClass(),
                                 Stream.of(FooTestBean.FOO_PROPERTY_NAME).collect(Collectors.toSet()));
    }

    @Test
    public void getNameField() {
        final Optional nameField = tested.getNameField(instance);
        assertEquals(nameField.get(), instance.fooProperty);
    }

    @Test
    public void getPropertyByName() {
        final Optional property = tested.getProperty(instance, FooTestBean.FOO_PROPERTY_NAME);
        assertEquals(property.get(), instance.fooProperty);
    }
}