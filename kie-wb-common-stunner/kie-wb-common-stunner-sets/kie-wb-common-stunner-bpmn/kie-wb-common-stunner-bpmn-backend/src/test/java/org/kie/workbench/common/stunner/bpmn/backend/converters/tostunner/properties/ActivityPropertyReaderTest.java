/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

        List<ExtensionAttributeValue> extensions = mockExtensions(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, onEntryScripts);
        when(activity.getExtensionValues()).thenReturn(extensions);

        assertScript(JAVA, SCRIPT, reader.getOnEntryAction());
    }

    @Test
    public void testGetOnExitScript() {
        OnExitScriptType onExitScript = Mockito.mock(OnExitScriptType.class);
        when(onExitScript.getScript()).thenReturn(SCRIPT);
        when(onExitScript.getScriptFormat()).thenReturn(JAVA_FORMAT);
        List<OnExitScriptType> onExitScripts = Collections.singletonList(onExitScript);

        List<ExtensionAttributeValue> extensions = mockExtensions(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, onExitScripts);
        when(activity.getExtensionValues()).thenReturn(extensions);

        assertScript(JAVA, SCRIPT, reader.getOnExitAction());
    }

    @Test
    public void testGetAssignmentsInfo() {
        List<DataInput> dataInputs = new ArrayList<>();
        DataInput dataInput1 = mockDataInput("INPUT_ID_1", "INPUT_NAME_1", mockEntry("dtype", "Integer"));
        DataInput dataInput2 = mockDataInput("INPUT_ID_2", "INPUT_NAME_2", mockEntry("dtype", "String"));

        dataInputs.add(dataInput1);
        dataInputs.add(dataInput2);
        InputOutputSpecification ioSpec = mock(InputOutputSpecification.class);

        List<DataInputAssociation> dataInputAssociations = new ArrayList<>();
        DataInputAssociation inputAssociation = mockDataInputAssociation(dataInput1, "VARIABLE1");
        DataInputAssociation inputAssociation2 = mockDataInputAssociation(dataInput2, "VARIABLE2");
        dataInputAssociations.add(inputAssociation);
        dataInputAssociations.add(inputAssociation2);

        List<DataOutput> dataOutputs = new ArrayList<>();
        DataOutput dataOutput1 = mockDataOutput("OUTPUT_ID_1", "OUTPUT_NAME_1", mockEntry("dtype", "Boolean"));
        DataOutput dataOutput2 = mockDataOutput("OUTPUT_ID_2", "OUTPUT_NAME_2", mockEntry("dtype", "Float"));
        dataOutputs.add(dataOutput1);
        dataOutputs.add(dataOutput2);

        List<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();
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
        List<DataInput> dataInputs = new ArrayList<>();
        List<DataInputAssociation> dataInputAssociations = new ArrayList<>();
        List<DataOutput> dataOutputs = new ArrayList<>();
        List<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();
        InputOutputSpecification ioSpec = mock(InputOutputSpecification.class);
        when(ioSpec.getDataInputs()).thenReturn(dataInputs);
        when(ioSpec.getDataOutputs()).thenReturn(dataOutputs);
        when(activity.getIoSpecification()).thenReturn(ioSpec);
        when(activity.getDataInputAssociations()).thenReturn(dataInputAssociations);
        when(activity.getDataOutputAssociations()).thenReturn(dataOutputAssociations);

        AssignmentsInfo result = reader.getAssignmentsInfo();
        assertEquals("||||", result.getValue());
    }

    private static void assertScript(String expectedLanguage, String expectedScript, ScriptTypeListValue value) {
        assertEquals(1, value.getValues().size());
        assertEquals(expectedLanguage, value.getValues().get(0).getLanguage());
        assertEquals(expectedScript, value.getValues().get(0).getScript());
    }

    public static List<ExtensionAttributeValue> mockExtensions(EStructuralFeature feature, Object value) {
        FeatureMap featureMap = mock(FeatureMap.class);
        when(featureMap.get(feature, true)).thenReturn(value);
        ExtensionAttributeValue attributeValue = Mockito.mock(ExtensionAttributeValue.class);
        when(attributeValue.getValue()).thenReturn(featureMap);
        return Collections.singletonList(attributeValue);
    }

    public static DataInput mockDataInput(String id, String name) {
        return mockDataInput(id, name, new FeatureMap.Entry[0]);
    }

    public static DataInput mockDataInput(String id, String name, FeatureMap.Entry... entries) {
        DataInput dataInput = mock(DataInput.class);
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
        DataOutput dataOutput = mock(DataOutput.class);
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
        List<ItemAwareElement> sourceRefs = Collections.singletonList(sourceRefItem);
        when(inputAssociation.getSourceRef()).thenReturn(sourceRefs);
        return inputAssociation;
    }

    public static DataOutputAssociation mockDataOutputAssociation(String sourceRef, String targetRef) {
        return mockDataOutputAssociation(mockItemAwareElement(sourceRef), targetRef);
    }

    public static DataOutputAssociation mockDataOutputAssociation(ItemAwareElement sourceRefItem, String targetRef) {
        DataOutputAssociation outputAssociation = mock(DataOutputAssociation.class);
        List<ItemAwareElement> sourceRefs = Collections.singletonList(sourceRefItem);
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
