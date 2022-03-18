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

package org.kie.workbench.common.stunner.bpmn.definition.property.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MetaDataTypeTest {

    @Test
    public void getName() {
        MetaDataType tested = new MetaDataType();
        assertEquals(MetaDataType.NAME, tested.getName());
    }

    @Test
    public void testEquals() {
        MetaDataType tested1 = new MetaDataType();
        MetaDataType tested2 = new MetaDataType();
        ImportsType tested3 = new ImportsType();
        assertEquals(tested1, tested2);
        assertNotEquals(tested1, tested3);
    }

    @Test
    public void testHashCode() {
        MetaDataType tested1 = new MetaDataType();
        MetaDataType tested2 = new MetaDataType();
        assertEquals(tested1.hashCode(), tested2.hashCode());
    }

    @Test
    public void testToString() {
        MetaDataType tested = new MetaDataType();
        String expected = "MetadataType{" +
                "name='" + MetaDataType.NAME + "\'" +
                "}";
        assertEquals(expected, tested.toString());
    }
}