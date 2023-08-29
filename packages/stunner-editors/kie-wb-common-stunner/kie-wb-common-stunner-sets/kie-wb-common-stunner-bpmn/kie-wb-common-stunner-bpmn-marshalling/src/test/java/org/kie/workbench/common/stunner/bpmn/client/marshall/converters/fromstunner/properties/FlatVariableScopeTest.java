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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FlatVariableScopeTest {

    private FlatVariableScope tested;
    private String scope = "scope";
    private String id = "id";
    private String type = "type";

    @Before
    public void setUp() {
        tested = new FlatVariableScope();
    }

    @Test
    public void declareAndlookup() {
        tested.declare(scope, id, type);

        Optional<VariableScope.Variable> lookup = tested.lookup(id);
        VariableScope.Variable variable = lookup.get();
        assertEquals(scope, variable.getParentScopeId());
        assertEquals(Ids.item(id), variable.getTypeDeclaration().getId());
        assertEquals(type, variable.getTypeDeclaration().getStructureRef());
    }

    @Test
    public void lookupNotFound() {
        Optional<VariableScope.Variable> variable = tested.lookup(UUID.randomUUID().toString());
        assertFalse(variable.isPresent());
    }
}