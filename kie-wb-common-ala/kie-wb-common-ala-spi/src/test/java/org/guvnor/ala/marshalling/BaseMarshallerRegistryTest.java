/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.marshalling;

import org.guvnor.ala.marshalling.impl.BaseMarshallerRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BaseMarshallerRegistryTest {

    protected BaseMarshallerRegistry marshallerRegistry;

    @Mock
    protected Marshaller marshaller;

    @Before
    public void setUp() {
        marshallerRegistry = new BaseMarshallerRegistry() {
        };
    }

    @Test
    public void testRegister() {
        Marshaller result = marshallerRegistry.get(String.class);
        assertNull(result);

        marshallerRegistry.register(String.class,
                                    marshaller);
        result = marshallerRegistry.get(String.class);
        assertEquals(marshaller,
                     result);
    }

    @Test
    public void testDeregister() {
        Marshaller result = marshallerRegistry.get(String.class);
        assertNull(result);

        marshallerRegistry.register(String.class,
                                    marshaller);
        result = marshallerRegistry.get(String.class);
        assertEquals(marshaller,
                     result);

        marshallerRegistry.deregister(String.class);
        result = marshallerRegistry.get(String.class);
        assertNull(result);
    }

    @Test
    public void testGet() {
        marshallerRegistry.register(String.class,
                                    marshaller);
        Marshaller result = marshallerRegistry.get(String.class);
        assertEquals(marshaller,
                     result);
    }
}
