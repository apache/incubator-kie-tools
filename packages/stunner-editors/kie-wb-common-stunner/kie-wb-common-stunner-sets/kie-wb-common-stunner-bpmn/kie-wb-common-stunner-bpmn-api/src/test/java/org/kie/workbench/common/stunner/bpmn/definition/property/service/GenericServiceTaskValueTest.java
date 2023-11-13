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
import org.junit.Test;

public class GenericServiceTaskValueTest {

    @Test
    public void testConstructor() {
        GenericServiceTaskValue a = new GenericServiceTaskValue("aaa", "bbb", "ccc", "ddd", "eee");
        Assert.assertEquals("aaa", a.getServiceImplementation());
        Assert.assertEquals("bbb", a.getServiceInterface());
        Assert.assertEquals("ccc", a.getServiceOperation());
        Assert.assertEquals("ddd", a.getInMessageStructure());
        Assert.assertEquals("eee", a.getOutMessagetructure());
    }


    @Test
    public void setAndGetServiceImplementation() {
        GenericServiceTaskValue a = new GenericServiceTaskValue();
        a.setServiceImplementation("setAndGetServiceImplementation");
        Assert.assertEquals("setAndGetServiceImplementation", a.getServiceImplementation());
    }

    @Test
    public void setAndGetServiceInterface() {
        GenericServiceTaskValue a = new GenericServiceTaskValue();
        a.setServiceInterface("setAndGetServiceInterface");
        Assert.assertEquals("setAndGetServiceInterface", a.getServiceInterface());
    }

    @Test
    public void setAndGetServiceOperation() {
        GenericServiceTaskValue a = new GenericServiceTaskValue();
        a.setServiceOperation("setAndGetServiceOperation");
        Assert.assertEquals("setAndGetServiceOperation", a.getServiceOperation());
    }

    @Test
    public void testToString() {
        GenericServiceTaskValue a = new GenericServiceTaskValue();
        Assert.assertEquals("GenericServiceTaskValue{serviceImplementation='Java', serviceInterface='', " +
                                    "serviceOperation='', inMessageStructure='', outMessagetructure=''}", a.toString());
    }

    @Test
    public void testHashCode() {
        GenericServiceTaskValue a = new GenericServiceTaskValue();
        GenericServiceTaskValue b = new GenericServiceTaskValue();
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void equals() {
        GenericServiceTaskValue a = new GenericServiceTaskValue();
        GenericServiceTaskValue b = new GenericServiceTaskValue();
        Assert.assertTrue(a.equals(b));
    }
}