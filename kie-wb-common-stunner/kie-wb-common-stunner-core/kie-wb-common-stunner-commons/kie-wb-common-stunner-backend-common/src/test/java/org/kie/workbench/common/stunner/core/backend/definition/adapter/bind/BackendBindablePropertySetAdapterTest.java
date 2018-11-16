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
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooPropertySetTestBean;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BackendBindablePropertySetAdapterTest extends AbstractBackendBindableAdapterTest {

    private BackendBindablePropertySetAdapter tested;

    @Before
    public void setUp() {
        super.setUp();
        tested = new BackendBindablePropertySetAdapter();
        tested.setBindings(propertyNameFields, propertiesFieldNames);
        propertiesFieldNames.put(instancePropertySet.getClass(),
                                 Stream.of(FooPropertySetTestBean.FOO_PROPERTY_NAME).collect(Collectors.toSet()));
    }

    @Test
    public void getPropertyByName() {
        final Optional property = tested.getProperty(instancePropertySet, FooPropertySetTestBean.FOO_PROPERTY_NAME);
        assertEquals(property.get(), instancePropertySet.fooProperty);
    }
}