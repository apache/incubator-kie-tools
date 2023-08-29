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

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ActivityPropertyReaderTest.mockDataInput;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ActivityPropertyReaderTest.mockDataInputAssociation;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ActivityPropertyReaderTest.mockDataOutput;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ActivityPropertyReaderTest.mockDataOutputAssociation;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ActivityPropertyReaderTest.mockItemAwareElement;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MultipleInstanceActivityPropertyReaderTest {

    private static final String ITEM_ID = "ITEM_ID";
    private static final String PROPERTY_ID = "PROPERTY_ID";
    private static final String EXPRESSION = "EXPRESSION";
    private static final String DATA_TYPE = "java.lang.Object";
    private static final String DELIMITER = ":";

    private MultipleInstanceActivityPropertyReader reader;

    @Mock
    private Activity activity;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    @Mock
    private MultiInstanceLoopCharacteristics miloop;

    @Before
    public void setUp() {
        reader = new MultipleInstanceActivityPropertyReader(activity, diagram, definitionResolver);
        when(activity.getLoopCharacteristics()).thenReturn(miloop);
    }

    @Test
    public void testGetCollectionInput() {
        ItemAwareElement item = mockItemAwareElement(ITEM_ID);
        when(miloop.getLoopDataInputRef()).thenReturn(item);
        EList<DataInputAssociation> inputAssociations = ECollections.singletonEList(mockDataInputAssociation(ITEM_ID, PROPERTY_ID));
        when(activity.getDataInputAssociations()).thenReturn(inputAssociations);
        assertEquals(PROPERTY_ID, reader.getCollectionInput());
    }

    @Test
    public void testGetDataInput() {
        DataInput item = mockDataInput(ITEM_ID, PROPERTY_ID);
        when(miloop.getInputDataItem()).thenReturn(item);
        assertEquals(PROPERTY_ID + DELIMITER + DATA_TYPE, reader.getDataInput());
    }

    @Test
    public void testGetCollectionOutput() {
        ItemAwareElement item = mockItemAwareElement(ITEM_ID);
        when(miloop.getLoopDataOutputRef()).thenReturn(item);
        EList<DataOutputAssociation> outputAssociations = ECollections.singletonEList(mockDataOutputAssociation(ITEM_ID, PROPERTY_ID));
        when(activity.getDataOutputAssociations()).thenReturn(outputAssociations);
        assertEquals(PROPERTY_ID, reader.getCollectionOutput());
    }

    @Test
    public void testGetDataOutput() {
        DataOutput item = mockDataOutput(ITEM_ID, PROPERTY_ID);
        when(miloop.getOutputDataItem()).thenReturn(item);
        assertEquals(PROPERTY_ID + DELIMITER + DATA_TYPE, reader.getDataOutput());
    }

    @Test
    public void testGetDataOutputForNullType() {
        DataOutput item = mockDataOutput(ITEM_ID, PROPERTY_ID);
        when(item.getItemSubjectRef()).thenReturn(null);
        when(miloop.getOutputDataItem()).thenReturn(item);
        assertEquals(PROPERTY_ID + DELIMITER + "Object", reader.getDataOutput());
    }

    @Test
    public void testGetDataOutputForEmptyType() {
        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getStructureRef()).thenReturn("");

        DataOutput item = mockDataOutput(ITEM_ID, PROPERTY_ID);
        when(item.getItemSubjectRef()).thenReturn(itemDefinition);
        when(miloop.getOutputDataItem()).thenReturn(item);
        assertEquals(PROPERTY_ID + DELIMITER + "Object", reader.getDataOutput());
    }

    // TODO: Kogito - @Test
    public void getGetCompletionCondition() {
        FormalExpression expression = mock(FormalExpression.class);
        when(expression.getBody()).thenReturn(EXPRESSION);
        when(miloop.getCompletionCondition()).thenReturn(expression);
        assertEquals(EXPRESSION, reader.getCompletionCondition());
    }

    @Test
    public void testGetIsSequentialTrue() {
        testIsSequential(true);
    }

    @Test
    public void testGetIsSequentialFalse() {
        testIsSequential(false);
    }

    private void testIsSequential(boolean sequential) {
        when(miloop.isIsSequential()).thenReturn(sequential);
        assertEquals(sequential, reader.isSequential());
    }
}
