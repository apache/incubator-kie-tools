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


package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.DirectionalAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.NonDirectionalAssociation;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AssociationConnectorDefTest {

    private AssociationConnectorDef tested;

    @Before
    public void setUp() throws Exception {
        tested = new AssociationConnectorDef();
    }

    @Test
    public void getDirection() {
        ConnectorShapeDef.Direction direction = tested.getDirection(new DirectionalAssociation());
        assertEquals(ConnectorShapeDef.Direction.ONE, direction);

        direction = tested.getDirection(new NonDirectionalAssociation());
        assertEquals(ConnectorShapeDef.Direction.NONE, direction);

        direction = tested.getDirection(mock(Association.class));
        assertEquals(ConnectorShapeDef.Direction.ONE, direction);
    }
}