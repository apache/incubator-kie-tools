/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic.GenericSelector;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class TypedValueComponentSelectorTest {

    @Mock
    private DateSelector dateSelector;

    @Mock
    private GenericSelector genericSelector;

    private TypedValueComponentSelector selector;

    @Before
    public void setup() {

        selector = new TypedValueComponentSelector(genericSelector, dateSelector);
    }

    @Test
    public void testGetDateSelector() {

        final TypedValueSelector actual = selector.makeSelectorForType(BuiltInType.DATE.getName());

        assertEquals(dateSelector, actual);
    }

    @Test
    public void testGetGenericSelector() {

        final TypedValueSelector actual = selector.makeSelectorForType("unknown");

        assertEquals(genericSelector, actual);
    }
}