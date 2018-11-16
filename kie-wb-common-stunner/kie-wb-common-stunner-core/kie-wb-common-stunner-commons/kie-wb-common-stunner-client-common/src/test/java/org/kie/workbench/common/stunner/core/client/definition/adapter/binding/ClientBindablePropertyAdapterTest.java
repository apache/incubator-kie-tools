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

package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.definition.property.type.BooleanType;
import org.kie.workbench.common.stunner.core.definition.property.type.StringType;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
public class ClientBindablePropertyAdapterTest extends AbstractClientBindableAdapterTest {

    private ClientBindablePropertyAdapter clientBindablePropertyAdapter;

    private final HashMap<Class, String> typesFieldNames = new HashMap<>();
    private final HashMap<Class, PropertyType> types = new HashMap<>();
    private final HashMap<Class, String> captionFieldNames = new HashMap<>();
    private final HashMap<Class, String> descFieldNames = new HashMap<>();
    private final HashMap<Class, String> readOnlyFieldNames = new HashMap<>();
    private final HashMap<Class, String> optionalFieldNames = new HashMap<>();
    private final HashMap<Class, String> valueFieldNames = new HashMap<>();
    private final HashMap<Class, String> allowedFieldNames = new HashMap<>();

    @Before
    @Override
    public void init() {
        super.init();
        clientBindablePropertyAdapter = new ClientBindablePropertyAdapter(translationService);
        clientBindablePropertyAdapter.setBindings(typesFieldNames,
                                                  types,
                                                  captionFieldNames,
                                                  descFieldNames,
                                                  readOnlyFieldNames,
                                                  optionalFieldNames,
                                                  valueFieldNames,
                                                  allowedFieldNames);
    }

    @Test
    public void test() {
        String description = clientBindablePropertyAdapter.getDescription(model);

        verify(translationService).getPropertyDescription(model.getClass().getName());

        assertEquals(PROPERTY_DESCRIPTION,
                     description);

        String caption = clientBindablePropertyAdapter.getCaption(model);

        verify(translationService).getPropertyCaption(model.getClass().getName());

        assertEquals(PROPERTY_CAPTION,
                     caption);
    }

    @Test
    public void testDefaultTypes() {
        types.put(Foo.class, new StringType());
        types.put(Bar.class, new BooleanType());
        final PropertyType fooType = clientBindablePropertyAdapter.getType(new Foo());
        final PropertyType barType = clientBindablePropertyAdapter.getType(new Bar());
        assertEquals(StringType.class, fooType.getClass());
        assertEquals(BooleanType.class, barType.getClass());
    }

    private static final class Foo {

    }

    private static final class Bar {

    }
}
