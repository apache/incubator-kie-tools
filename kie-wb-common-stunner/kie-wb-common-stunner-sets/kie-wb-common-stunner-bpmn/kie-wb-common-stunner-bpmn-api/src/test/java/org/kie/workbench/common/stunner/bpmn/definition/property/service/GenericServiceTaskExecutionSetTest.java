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

package org.kie.workbench.common.stunner.bpmn.definition.property.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class GenericServiceTaskExecutionSetTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHashCode() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEquals() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testEqualFalse() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();

        a.setGenericServiceTaskInfo(null);

        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testEqualTrue() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        a.setGenericServiceTaskInfo(new GenericServiceTaskInfo());
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testGetGenericServiceTaskInfo() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        Assert.assertTrue(a.getGenericServiceTaskInfo().equals(new GenericServiceTaskInfo()));
    }
}
