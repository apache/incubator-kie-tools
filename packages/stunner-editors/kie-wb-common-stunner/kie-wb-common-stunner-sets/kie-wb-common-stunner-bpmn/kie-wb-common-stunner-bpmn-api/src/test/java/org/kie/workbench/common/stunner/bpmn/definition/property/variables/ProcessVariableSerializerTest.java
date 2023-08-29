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


package org.kie.workbench.common.stunner.bpmn.definition.property.variables;

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProcessVariableSerializerTest {

    private static final String VARIABLE = "PV1:java.lang.String:[internal;output],PV2:java.lang.Boolean:[internal;readonly;customTag]";

    @Test
    public void deserialize() {
        final Map<String, ProcessVariableSerializer.VariableInfo> deserialized = ProcessVariableSerializer.deserialize(VARIABLE);
        assertEquals(deserialized.size(), 2);
        assertEquals(deserialized.get("PV1").getType(), "java.lang.String");
        assertEquals(deserialized.get("PV1").getTags(), "[internal;output]");

        assertEquals(deserialized.get("PV2").getType(), "java.lang.Boolean");
        assertEquals(deserialized.get("PV2").getTags(), "[internal;readonly;customTag]");
    }
}