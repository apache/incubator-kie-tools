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


package org.kie.workbench.common.stunner.bpmn.definition.property.artifacts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataObjectTypeValueTest {

    private DataObjectTypeValue dataObjectTypeValue = new DataObjectTypeValue();

    @Test
    public void setValue() {
        dataObjectTypeValue.setType(this.getClass().getSimpleName());
        assertEquals(getClass().getSimpleName(), dataObjectTypeValue.getType());
    }

    @Test
    public void testHashCode() {
        assertEquals(new DataObjectTypeValue().hashCode(), dataObjectTypeValue.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new DataObjectTypeValue(), dataObjectTypeValue);
        assertNotEquals(new DataObjectTypeValue(), new Object());
    }

    @Test
    public void testToString() {
        assertEquals(new DataObjectTypeValue().toString(), dataObjectTypeValue.toString());
    }

}
