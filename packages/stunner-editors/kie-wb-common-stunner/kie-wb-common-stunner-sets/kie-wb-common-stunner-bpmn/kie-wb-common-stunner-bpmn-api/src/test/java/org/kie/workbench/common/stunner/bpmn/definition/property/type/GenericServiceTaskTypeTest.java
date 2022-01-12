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

package org.kie.workbench.common.stunner.bpmn.definition.property.type;

import org.junit.Assert;
import org.junit.Test;

public class GenericServiceTaskTypeTest {

    @Test
    public void getName() {
        Assert.assertEquals("stunner.bpmn.GenericServiceTaskType", new GenericServiceTaskType().getName());
    }

    @Test
    public void equals() {
        GenericServiceTaskType a = new GenericServiceTaskType();
        GenericServiceTaskType b = new GenericServiceTaskType();
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testHashCode() {
        GenericServiceTaskType a = new GenericServiceTaskType();
        GenericServiceTaskType b = new GenericServiceTaskType();
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("GenericServiceTaskType{name='stunner.bpmn.GenericServiceTaskType'}", new GenericServiceTaskType().toString());
    }
}