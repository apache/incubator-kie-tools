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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.FlatVariableScope;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ActivityPropertyReaderTest {

    private static final String JAVA = "java";
    private static final String JAVA_FORMAT = "http://www.java.com/java";
    private static final String SCRIPT = "SCRIPT";

    @Mock
    private Activity activity;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    private ActivityPropertyReader reader;

    @Before
    public void setUp() {
        reader = new ActivityPropertyReader(activity, diagram, definitionResolver);
    }

    @Test
    public void testGetOnEntryScript() {
        OnEntryScriptType onEntryScript = Mockito.mock(OnEntryScriptType.class);
        when(onEntryScript.getScript()).thenReturn(SCRIPT);
        when(onEntryScript.getScriptFormat()).thenReturn(JAVA_FORMAT);
        List<OnEntryScriptType> onEntryScripts = Collections.singletonList(onEntryScript);

        EList<ExtensionAttributeValue> extensions = mockExtensions(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, onEntryScripts);
        when(activity.getExtensionValues()).thenReturn(extensions);

        assertScript(JAVA, SCRIPT, reader.getOnEntryAction());
    }

    @Test
    public void testGetOnExitScript() {
        OnExitScriptType onExitScript = Mockito.mock(OnExitScriptType.class);
        when(onExitScript.getScript()).thenReturn(SCRIPT);
        when(onExitScript.getScriptFormat()).thenReturn(JAVA_FORMAT);
        List<OnExitScriptType> onExitScripts = Collections.singletonList(onExitScript);

        EList<ExtensionAttributeValue> extensions = mockExtensions(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, onExitScripts);
        when(activity.getExtensionValues()).thenReturn(extensions);

        assertScript(JAVA, SCRIPT, reader.getOnExitAction());
    }

    @Test
    public void testGetAssignmentsInfo() {
        EList<DataInput> dataInputs = ECollections.newBasicEList();
        DataInput dataInput1 = mockDataInput("INPUT_ID_1", "INPUT_NAME_1", mockEntry("dtype", "Integer"));
        DataInput dataInput2 = mockDataInput("INPUT_ID_2", "INPUT_NAME_2", mockEntry("dtype", "String"));

        dataInputs.add(dataInput1);
        dataInputs.add(dataInput2);
        InputOutputSpecification ioSpec = mock(InputOutputSpecification.class);

        EList<DataInputAssociation> dataInputAssociations = ECollections.newBasicEList();
        DataInputAssociation inputAssociation = mockDataInputAssociation(dataInput1, "VARIABLE1");
        DataInputAssociation inputAssociation2 = mockDataInputAssociation(dataInput2, "VARIABLE2");
        dataInputAssociations.add(inputAssociation);
        dataInputAssociations.add(inputAssociation2);

        EList<DataOutput> dataOutputs = ECollections.newBasicEList();
        DataOutput dataOutput1 = mockDataOutput("OUTPUT_ID_1", "OUTPUT_NAME_1", mockEntry("dtype", "Boolean"));
        DataOutput dataOutput2 = mockDataOutput("OUTPUT_ID_2", "OUTPUT_NAME_2", mockEntry("dtype", "Float"));
        dataOutputs.add(dataOutput1);
        dataOutputs.add(dataOutput2);

        EList<DataOutputAssociation> dataOutputAssociations = ECollections.newBasicEList();
        DataOutputAssociation outputAssociation1 = mockDataOutputAssociation(dataOutput1, "VARIABLE3");
        DataOutputAssociation outputAssociation2 = mockDataOutputAssociation(dataOutput2, "VARIABLE4");
        dataOutputAssociations.add(outputAssociation1);
        dataOutputAssociations.add(outputAssociation2);

        when(ioSpec.getDataInputs()).thenReturn(dataInputs);
        when(ioSpec.getDataOutputs()).thenReturn(dataOutputs);
        when(activity.getIoSpecification()).thenReturn(ioSpec);
        when(activity.getDataInputAssociations()).thenReturn(dataInputAssociations);
        when(activity.getDataOutputAssociations()).thenReturn(dataOutputAssociations);

        AssignmentsInfo result = reader.getAssignmentsInfo();

        String expectedResult = "|INPUT_NAME_1:Integer,INPUT_NAME_2:String||OUTPUT_NAME_1:Boolean,OUTPUT_NAME_2:Float|[din]VARIABLE1->INPUT_NAME_1,[din]VARIABLE2->INPUT_NAME_2,[dout]OUTPUT_NAME_1->VARIABLE3,[dout]OUTPUT_NAME_2->VARIABLE4";
        assertEquals(expectedResult, result.getValue());
    }

    @Test
    public void testGetAssignmentsInfoWithNoAssignments() {
        EList<DataInput> dataInputs = ECollections.newBasicEList();
        EList<DataInputAssociation> dataInputAssociations = ECollections.newBasicEList();
        EList<DataOutput> dataOutputs = ECollections.newBasicEList();
        EList<DataOutputAssociation> dataOutputAssociations = ECollections.newBasicEList();
        InputOutputSpecification ioSpec = mock(InputOutputSpecification.class);
        when(ioSpec.getDataInputs()).thenReturn(dataInputs);
        when(ioSpec.getDataOutputs()).thenReturn(dataOutputs);
        when(activity.getIoSpecification()).thenReturn(ioSpec);
        when(activity.getDataInputAssociations()).thenReturn(dataInputAssociations);
        when(activity.getDataOutputAssociations()).thenReturn(dataOutputAssociations);

        AssignmentsInfo result = reader.getAssignmentsInfo();
        assertEquals("||||", result.getValue());
    }


    @Test
    public void testDuplicatedOutputsShouldBeRemoved() {
        Task task = Factories.bpmn2.createTask();
        ActivityPropertyWriter activityPropertyWriter =
                new ActivityPropertyWriter(task, new FlatVariableScope(), new HashSet<>());
        activityPropertyWriter.setAssignmentsInfo(new AssignmentsInfo(
                "|NotCompletedNotify:Object,Skippable:Object||outcome_:String,outcome_:String,outcome_:String,outcome_:String,assignedUser_:String,email_:String,emailBody_:String|[dout]outcome_->outcome,[dout]outcome_->firResult,[dout]outcome_->outcome,[dout]outcome_->firResult,[dout]assignedUser_->assignedUser,[dout]email_->email,[dout]emailBody_->emailBody"
        ));
        List<OutputSet> outputSets = task.getIoSpecification().getOutputSets();
        assertEquals(5, outputSets.get(0).getDataOutputRefs().size());
    }

    private static void assertScript(String expectedLanguage, String expectedScript, ScriptTypeListValue value) {
        assertEquals(1, value.getValues().size());
        assertEquals(expectedLanguage, value.getValues().get(0).getLanguage());
        assertEquals(expectedScript, value.getValues().get(0).getScript());
    }

    public static EList<ExtensionAttributeValue> mockExtensions(EStructuralFeature feature, Object value) {
        FeatureMap featureMap = mock(FeatureMap.class);
        when(featureMap.get(feature, true)).thenReturn(value);
        ExtensionAttributeValue attributeValue = Mockito.mock(ExtensionAttributeValue.class);
        when(attributeValue.getValue()).thenReturn(featureMap);
        return ECollections.singletonEList(attributeValue);
    }

    public static DataInput mockDataInput(String id, String name) {
        return mockDataInput(id, name, new FeatureMap.Entry[0]);
    }

    public static DataInput mockDataInput(String id, String name, FeatureMap.Entry... entries) {
        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getStructureRef()).thenReturn("java.lang.Object");

        DataInput dataInput = mock(DataInput.class);
        when(dataInput.getItemSubjectRef()).thenReturn(itemDefinition);
        when(dataInput.getId()).thenReturn(id);
        when(dataInput.getName()).thenReturn(name);
        List<FeatureMap.Entry> entriesList = new ArrayList<>();
        if (entries != null) {
            entriesList.addAll(Arrays.asList(entries));
        }
        FeatureMap featureMap = mock(FeatureMap.class);
        when(featureMap.stream()).thenReturn(entriesList.stream());
        when(dataInput.getAnyAttribute()).thenReturn(featureMap);
        return dataInput;
    }

    public static DataOutput mockDataOutput(String id, String name) {
        return mockDataOutput(id, name, new FeatureMap.Entry[0]);
    }

    public static DataOutput mockDataOutput(String id, String name, FeatureMap.Entry... entries) {
        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getStructureRef()).thenReturn("java.lang.Object");

        DataOutput dataOutput = mock(DataOutput.class);
        when(dataOutput.getItemSubjectRef()).thenReturn(itemDefinition);
        when(dataOutput.getId()).thenReturn(id);
        when(dataOutput.getName()).thenReturn(name);
        List<FeatureMap.Entry> entriesList = new ArrayList<>();
        if (entries != null) {
            entriesList.addAll(Arrays.asList(entries));
        }
        FeatureMap featureMap = mock(FeatureMap.class);
        when(featureMap.stream()).thenReturn(entriesList.stream());
        when(dataOutput.getAnyAttribute()).thenReturn(featureMap);
        return dataOutput;
    }

    public static DataInputAssociation mockDataInputAssociation(String targetRef, String sourceRef) {
        return mockDataInputAssociation(mockItemAwareElement(targetRef), sourceRef);
    }

    public static DataInputAssociation mockDataInputAssociation(ItemAwareElement targetRefItem, String sourceRef) {
        DataInputAssociation inputAssociation = mock(DataInputAssociation.class);
        when(inputAssociation.getTargetRef()).thenReturn(targetRefItem);

        ItemAwareElement sourceRefItem = mockProperty(sourceRef);
        EList<ItemAwareElement> sourceRefs = ECollections.singletonEList(sourceRefItem);
        when(inputAssociation.getSourceRef()).thenReturn(sourceRefs);
        return inputAssociation;
    }

    public static DataOutputAssociation mockDataOutputAssociation(String sourceRef, String targetRef) {
        return mockDataOutputAssociation(mockItemAwareElement(sourceRef), targetRef);
    }

    public static DataOutputAssociation mockDataOutputAssociation(ItemAwareElement sourceRefItem, String targetRef) {
        DataOutputAssociation outputAssociation = mock(DataOutputAssociation.class);
        EList<ItemAwareElement> sourceRefs = ECollections.singletonEList(sourceRefItem);
        when(outputAssociation.getSourceRef()).thenReturn(sourceRefs);

        ItemAwareElement targetRefItem = mockProperty(targetRef);
        when(outputAssociation.getTargetRef()).thenReturn(targetRefItem);
        return outputAssociation;
    }

    public static ItemAwareElement mockItemAwareElement(String id) {
        ItemAwareElement item = mock(ItemAwareElement.class);
        when(item.getId()).thenReturn(id);
        return item;
    }

    public static Property mockProperty(String id) {
        Property property = mock(Property.class);
        when(property.getId()).thenReturn(id);
        return property;
    }

    public FeatureMap.Entry mockEntry(String name, Object value) {
        FeatureMap.Entry entry = mock(FeatureMap.Entry.class);
        EStructuralFeature feature = mock(EStructuralFeature.class);
        when(feature.getName()).thenReturn(name);
        when(entry.getEStructuralFeature()).thenReturn(feature);
        when(entry.getValue()).thenReturn(value);
        return entry;
    }
}
