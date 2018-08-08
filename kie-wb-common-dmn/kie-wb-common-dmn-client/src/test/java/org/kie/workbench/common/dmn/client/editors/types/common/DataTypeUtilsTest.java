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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.List;
import java.util.Optional;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataTypeUtilsTest {

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private DataTypeFactory dataTypeFactory;

    private DataTypeUtils utils;

    @Before
    public void setup() {
        dataTypeFactory = spy(new DataTypeFactory(itemDefinitionUtils, translationService));
        utils = new DataTypeUtils(dataTypeFactory, dmnGraphUtils);
    }

    @Test
    public void testDefaultDataTypes() {

        final List<DataType> dataTypes = utils.defaultDataTypes();

        assertEquals(10, dataTypes.size());
        assertEquals("any", dataTypes.get(0).getType());
        assertEquals("boolean", dataTypes.get(1).getType());
        assertEquals("context", dataTypes.get(2).getType());
        assertEquals("date", dataTypes.get(3).getType());
        assertEquals("date and time", dataTypes.get(4).getType());
        assertEquals("days and time duration", dataTypes.get(5).getType());
        assertEquals("number", dataTypes.get(6).getType());
        assertEquals("string", dataTypes.get(7).getType());
        assertEquals("time", dataTypes.get(8).getType());
        assertEquals("years and months duration", dataTypes.get(9).getType());
    }

    @Test
    public void testCustomDataTypes() {

        final ItemDefinition item1 = makeItem("itemB");
        final ItemDefinition item2 = makeItem("itemA");
        final Definitions definitions = mock(Definitions.class);
        final List<ItemDefinition> itemDefinitions = asList(item1, item2);

        when(itemDefinitionUtils.findByName(any())).thenReturn(Optional.empty());
        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(itemDefinitions);

        final List<DataType> dataTypes = utils.customDataTypes();

        assertEquals(2, dataTypes.size());
        assertEquals("itemA", dataTypes.get(0).getName());
        assertEquals("itemB", dataTypes.get(1).getName());
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }
}
