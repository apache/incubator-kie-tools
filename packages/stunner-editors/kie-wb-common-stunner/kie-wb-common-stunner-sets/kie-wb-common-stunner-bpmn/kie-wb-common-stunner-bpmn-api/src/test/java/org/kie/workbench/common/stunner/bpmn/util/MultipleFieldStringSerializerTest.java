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


package org.kie.workbench.common.stunner.bpmn.util;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class MultipleFieldStringSerializerTest {

    private static final String SERIALIZED_FIELDS = "tiago;dolphine";
    private static final String[] DESERIALIZED_FIELDS = {"tiago", "dolphine"};
    private static final String SERIALIZED_SUBFIELDS = "tiago.dolphine";
    private static final String[] DESERIALIZED_SUBFIELDS = {"tiago", "dolphine"};

    @Test
    public void testSerialize() throws Exception {
        Assert.assertEquals(SERIALIZED_FIELDS,
                            MultipleFieldStringSerializer.serialize(DESERIALIZED_FIELDS));
    }

    @Test
    public void testDeserialize() throws Exception {
        Assert.assertEquals(Arrays.asList(DESERIALIZED_FIELDS),
                            MultipleFieldStringSerializer.deserialize(SERIALIZED_FIELDS));
    }

    @Test
    public void testSerializeSubfields() throws Exception {
        Assert.assertEquals(SERIALIZED_SUBFIELDS,
                            MultipleFieldStringSerializer.serializeSubfields(DESERIALIZED_SUBFIELDS));
    }

    @Test
    public void testDeserializeSubfields() throws Exception {
        Assert.assertEquals(Arrays.asList(DESERIALIZED_SUBFIELDS),
                            MultipleFieldStringSerializer.deserializeSubfields(SERIALIZED_SUBFIELDS));
    }
}