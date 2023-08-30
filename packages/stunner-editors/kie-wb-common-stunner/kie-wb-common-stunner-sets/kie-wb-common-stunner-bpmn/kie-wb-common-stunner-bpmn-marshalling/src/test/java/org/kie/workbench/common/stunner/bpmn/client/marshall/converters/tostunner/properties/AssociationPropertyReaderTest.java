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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.DirectionalAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.NonDirectionalAssociation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssociationPropertyReaderTest {

    private final static String SOURCE_ID = "SOURCE_ID";

    private final static String TARGET_ID = "TARGET_ID";

    private final static String ASSOCIATION_ID = "ASSOCIATION_ID";

    private final static double X = 1;

    private final static double Y = 2;

    @Mock
    private DefinitionResolver definitionResolver;

    @Mock
    private Association association;

    @Mock
    private BaseElement sourceRef;

    @Mock
    private BaseElement targetRef;

    @Mock
    private BPMNDiagram bpmnDiagram;

    @Mock
    private BPMNPlane bpmnLane;

    @Mock
    private AssociationPropertyReader propertyReader;

    @Before
    public void setUp() {
        when(bpmnDiagram.getPlane()).thenReturn(bpmnLane);
        when(association.getId()).thenReturn(ASSOCIATION_ID);
        when(sourceRef.getId()).thenReturn(SOURCE_ID);
        when(targetRef.getId()).thenReturn(TARGET_ID);
        when(association.getSourceRef()).thenReturn(sourceRef);
        when(association.getTargetRef()).thenReturn(targetRef);

        propertyReader = new AssociationPropertyReader(association, bpmnDiagram, definitionResolver);
    }

    @Test
    public void testGetSourceId() {
        assertEquals(SOURCE_ID, propertyReader.getSourceId());
    }

    @Test
    public void testGetTargetId() {
        assertEquals(TARGET_ID, propertyReader.getTargetId());
    }

    @Test
    public void testGetAssociationByDirection() {
        final Association association = Bpmn2Factory.eINSTANCE.createAssociation();

        //null direction
        association.setAssociationDirection(null);
        propertyReader = new AssociationPropertyReader(association, bpmnDiagram, definitionResolver);
        assertEquals(NonDirectionalAssociation.class, propertyReader.getAssociationByDirection());

        //none direction
        association.setAssociationDirection(AssociationDirection.NONE);
        assertEquals(NonDirectionalAssociation.class, propertyReader.getAssociationByDirection());

        //one direction
        association.setAssociationDirection(AssociationDirection.ONE);
        assertEquals(DirectionalAssociation.class, propertyReader.getAssociationByDirection());
    }
}
