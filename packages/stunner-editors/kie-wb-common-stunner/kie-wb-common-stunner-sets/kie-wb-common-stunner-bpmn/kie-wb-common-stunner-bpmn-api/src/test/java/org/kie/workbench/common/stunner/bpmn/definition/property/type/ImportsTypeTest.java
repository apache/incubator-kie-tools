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


package org.kie.workbench.common.stunner.bpmn.definition.property.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImportsTypeTest {

    @Test
    public void getName() {
        ImportsType tested = new ImportsType();
        assertEquals(ImportsType.name, tested.getName());
    }

    @Test
    public void testEquals() {
        ImportsType tested1 = new ImportsType();
        ImportsType tested2 = new ImportsType();
        assertEquals(tested1, tested2);
    }

    @Test
    public void testHashCode() {
        ImportsType tested1 = new ImportsType();
        ImportsType tested2 = new ImportsType();
        assertEquals(tested1.hashCode(), tested2.hashCode());
    }

    @Test
    public void testToString() {
        ImportsType tested = new ImportsType();
        String expected = "ImportsType{" +
                "name='" + ImportsType.name + "\'" +
                "}";
        assertEquals(expected, tested.toString());
    }
}