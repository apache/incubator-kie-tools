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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProcessTypeTest {

    @Test
    public void testEquals() {
        assertTrue(new ProcessType().equals(new ProcessType()));
        ProcessType test = new ProcessType();
        test.setValue("value");
        assertFalse(test.equals(new ProcessType()));
        assertFalse(test.equals(new Object()));
    }

    @Test
    public void testGetValue() {
        assertTrue(new ProcessType().getValue().equals("Public"));
    }

    @Test
    public void testSetValue() {
        ProcessType test = new ProcessType();
        test.setValue("Private");
        assertTrue(test.getValue().equals("Private"));
    }

    @Test
    public void testNewProcessType() {
        ProcessType test = new ProcessType();
        assertTrue(test.getValue().equals("Public"));

        test = new ProcessType("Private");
        assertTrue(test.getValue().equals("Private"));

        test = new ProcessType(null);
        assertEquals(null, test.getValue());

        test = new ProcessType("None");
        assertEquals("Public", test.getValue());
    }


    @Test
    public void testHashCode() {
        ProcessType test = new ProcessType();
        assertTrue(test.getValue().hashCode() == test.hashCode());
    }
}
