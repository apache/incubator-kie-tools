/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.definition.property.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.GenericServiceTaskType;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class GenericServiceTaskInfoTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHashCode() {
        GenericServiceTaskInfo a = new GenericServiceTaskInfo();
        GenericServiceTaskInfo b = new GenericServiceTaskInfo();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEquals() {
        GenericServiceTaskInfo a = new GenericServiceTaskInfo();
        GenericServiceTaskInfo b = new GenericServiceTaskInfo();
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testEqualFalse() {
        GenericServiceTaskInfo a = new GenericServiceTaskInfo();
        GenericServiceTaskInfo b = new GenericServiceTaskInfo();

        a.setValue(null);

        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testEqualTrue() {
        GenericServiceTaskInfo a = new GenericServiceTaskInfo();
        GenericServiceTaskInfo b = new GenericServiceTaskInfo();
        a.setValue(new GenericServiceTaskValue());

        Assert.assertTrue(a.equals(b));

        GenericServiceTaskValue newVal = new GenericServiceTaskValue();
        newVal.setServiceImplementation("any");
        newVal.setServiceInterface("any");
        newVal.setServiceOperation("any");

        a.setValue(newVal);
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testType() {
        Assert.assertEquals(new GenericServiceTaskType(), GenericServiceTaskInfo.type);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(new GenericServiceTaskInfo(), new GenericServiceTaskInfo(new GenericServiceTaskValue()));
    }

    @Test
    public void testGetValue() {
        Assert.assertEquals(new GenericServiceTaskInfo().getValue(), new GenericServiceTaskInfo(new GenericServiceTaskValue()).getValue());
    }
}
