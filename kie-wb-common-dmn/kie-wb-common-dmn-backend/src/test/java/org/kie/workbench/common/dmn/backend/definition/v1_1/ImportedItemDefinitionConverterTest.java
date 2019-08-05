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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.dmn.model.v1_2.TItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImportedItemDefinitionConverterTest {

    @Test
    public void testWbFromDMN() {

        final org.kie.dmn.model.api.ItemDefinition itemDefinition = new TItemDefinition();
        final org.kie.dmn.model.api.ItemDefinition itemComponent1 = new TItemDefinition();
        final org.kie.dmn.model.api.ItemDefinition itemComponent2 = new TItemDefinition();
        final org.kie.dmn.model.api.ItemDefinition itemComponent3 = new TItemDefinition();

        itemDefinition.setName("tPerson");
        itemDefinition.setTypeRef(null);
        itemDefinition.getItemComponent().addAll(asList(itemComponent1, itemComponent2, itemComponent3));

        itemComponent1.setName("id");
        itemComponent1.setTypeRef(new QName("tUUID"));

        itemComponent2.setName("name");
        itemComponent2.setTypeRef(new QName("string"));

        itemComponent3.setName("age");
        itemComponent3.setTypeRef(new QName("number"));

        final ItemDefinition actualItemDefinition = ImportedItemDefinitionConverter.wbFromDMN(itemDefinition, "model");

        assertEquals("model.tPerson", actualItemDefinition.getName().getValue());
        assertNull(actualItemDefinition.getTypeRef());
        assertTrue(actualItemDefinition.isAllowOnlyVisualChange());

        assertEquals(3, actualItemDefinition.getItemComponent().size());

        final ItemDefinition actualItemDefinition1 = actualItemDefinition.getItemComponent().get(0);
        assertEquals("model.id", actualItemDefinition1.getName().getValue());
        assertEquals("model.tUUID", actualItemDefinition1.getTypeRef().getLocalPart());
        assertTrue(actualItemDefinition1.isAllowOnlyVisualChange());

        final ItemDefinition actualItemDefinition2 = actualItemDefinition.getItemComponent().get(1);
        assertEquals("model.name", actualItemDefinition2.getName().getValue());
        assertEquals("string", actualItemDefinition2.getTypeRef().getLocalPart());
        assertTrue(actualItemDefinition2.isAllowOnlyVisualChange());

        final ItemDefinition actualItemDefinition3 = actualItemDefinition.getItemComponent().get(2);
        assertEquals("model.age", actualItemDefinition3.getName().getValue());
        assertEquals("number", actualItemDefinition3.getTypeRef().getLocalPart());
        assertTrue(actualItemDefinition3.isAllowOnlyVisualChange());
    }
}
