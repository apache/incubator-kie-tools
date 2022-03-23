/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.Arrays;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

import static org.junit.Assert.assertEquals;

public class DefinitionAdapterBindingsTest {

    @Test
    public void testBindings() {
        DefinitionAdapterBindings bindings = new DefinitionAdapterBindings();
        bindings.setBaseType(SomeBaseType.class);
        bindings.setGraphFactory(NodeFactory.class);
        bindings.setIdField("idField");
        bindings.setCategoryField("categoryField");
        bindings.setLabelsField("labelsField");
        bindings.setTitleField("titleField");
        bindings.setDescriptionField("descField");
        bindings.setPropertiesFieldNames(Arrays.asList("p1", "p2"));
        bindings.setTypedPropertyFields(Arrays.asList(true, false));
        DefinitionAdapterBindings.PropertyMetaTypes metaTypes = new DefinitionAdapterBindings.PropertyMetaTypes();
        bindings.setMetaTypes(metaTypes);
        assertEquals(SomeBaseType.class, bindings.getBaseType());
        assertEquals(NodeFactory.class, bindings.getGraphFactory());
        assertEquals("idField", bindings.getIdField());
        assertEquals("categoryField", bindings.getCategoryField());
        assertEquals("labelsField", bindings.getLabelsField());
        assertEquals("titleField", bindings.getTitleField());
        assertEquals("descField", bindings.getDescriptionField());
        assertEquals(Arrays.asList("p1", "p2"), bindings.getPropertiesFieldNames());
        assertEquals(Arrays.asList(true, false), bindings.getTypedPropertyFields());
        assertEquals(metaTypes, bindings.getMetaTypes());
    }

    @Test
    public void testMetaTypes() {
        DefinitionAdapterBindings.PropertyMetaTypes metaTypes = new DefinitionAdapterBindings.PropertyMetaTypes();
        metaTypes.setNameIndex(4);
        metaTypes.setIdIndex(5);
        metaTypes.setWidthIndex(1);
        metaTypes.setHeightIndex(2);
        metaTypes.setRadiusIndex(3);
        assertEquals(4, metaTypes.getNameIndex());
        assertEquals(4, metaTypes.getIndex(PropertyMetaTypes.NAME));
        assertEquals(5, metaTypes.getIdIndex());
        assertEquals(5, metaTypes.getIndex(PropertyMetaTypes.ID));
        assertEquals(1, metaTypes.getWidthIndex());
        assertEquals(1, metaTypes.getIndex(PropertyMetaTypes.WIDTH));
        assertEquals(2, metaTypes.getHeightIndex());
        assertEquals(2, metaTypes.getIndex(PropertyMetaTypes.HEIGHT));
        assertEquals(3, metaTypes.getRadiusIndex());
        assertEquals(3, metaTypes.getIndex(PropertyMetaTypes.RADIUS));
        assertEquals("4,1,2,3,5", metaTypes.format());
        assertEquals("4,1,2,3,5", DefinitionAdapterBindings.PropertyMetaTypes.parse("4,1,2,3,5").format());
    }

    private static class SomeBaseType {

    }
}
