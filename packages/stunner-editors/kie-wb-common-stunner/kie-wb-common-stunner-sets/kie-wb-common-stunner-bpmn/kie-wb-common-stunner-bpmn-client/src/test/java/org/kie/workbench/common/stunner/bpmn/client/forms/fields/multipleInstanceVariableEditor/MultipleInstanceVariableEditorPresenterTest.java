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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenterBaseTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.createMapForSimpleDataTypes;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getDataType;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getDisplayName;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getFirstIfExistsOrSecond;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getListObjectThenOnFulfilledCallbackFn;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getMapDataTypeNamesToDisplayNames;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getNonNullName;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getRealType;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.simpleDataTypes;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MultipleInstanceVariableEditorPresenterTest
        extends FieldEditorPresenterBaseTest<String, MultipleInstanceVariableEditorPresenter, MultipleInstanceVariableEditorPresenter.View> {

    @Mock
    SessionManager sessionManager;

    @Override
    public ArgumentCaptor<String> newArgumentCaptor() {
        return ArgumentCaptor.forClass(String.class);
    }

    @Override
    public MultipleInstanceVariableEditorPresenter.View mockEditorView() {
        return mock(MultipleInstanceVariableEditorPresenter.View.class);
    }

    @Override
    public MultipleInstanceVariableEditorPresenter newEditorPresenter(MultipleInstanceVariableEditorPresenter.View view) {
        return new MultipleInstanceVariableEditorPresenter(view, sessionManager);
    }

    @Test
    public void testSetValue() {
        editor.setValue("value:String:");
        Variable variable = new Variable("value", Variable.VariableType.INPUT, "String", null);
        verify(view).setModel(variable);
    }

    @Test
    public void testSetReadonlyTrue() {
        editor.setReadOnly(true);
        verify(view).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        editor.setReadOnly(false);
        verify(view).setReadOnly(false);
    }

    @Test
    public void testOnVariableNameChange() {
        editor.setValue("oldValue");
        when(view.getVariableName()).thenReturn("newValue");
        when(view.getVariableType()).thenReturn("Float");
        editor.onVariableChange();
        verifyValueChange("oldValue", "newValue:Float");
    }

    @Test
    public void testFirstIfExistsOrSecond() {
        String first;
        String second = "some value";

        assertEquals(second, getFirstIfExistsOrSecond(null, second));

        first = "";
        assertEquals(second, getFirstIfExistsOrSecond(first, second));

        first = "new value";
        assertEquals(first, getFirstIfExistsOrSecond(first, second));
    }

    @Test
    public void testCreateMapForSimpleDataTypes() {
        Map<String, String> tested = createMapForSimpleDataTypes();
        assertEquals(simpleDataTypes.size(), tested.size());
        for (String type : simpleDataTypes) {
            assertEquals(type, tested.get(type));
        }
    }

    @Test
    public void testFullListOfTypes() {
        List<String> testData = new ArrayList<>();
        testData.add("org.test.Person");
        testData.add("net.second.Test");

        getListObjectThenOnFulfilledCallbackFn(simpleDataTypes, new ListBoxValues("Custom", "Edit", null)).onInvoke(testData);

        assertEquals("org.test.Person", getMapDataTypeNamesToDisplayNames().get("Person [org.test]"));
        assertEquals("net.second.Test", getMapDataTypeNamesToDisplayNames().get("Test [net.second]"));
    }

    @Test
    public void testDisplayNameDoesNotExist() {
        String dataType = "java.util.List";

        assertEquals(dataType, getDisplayName(dataType));
    }

    @Test
    public void testDisplayName() {
        String realDataType = "java.util.List";
        String displayDataType = "List [java.util]";
        getMapDataTypeNamesToDisplayNames().put(realDataType, displayDataType);

        assertEquals(displayDataType, getDisplayName(realDataType));
    }

    @Test
    public void testEmptyRealType() {
        assertEquals("", getRealType(null));
        assertEquals("", getRealType(""));
    }

    @Test
    public void testDefaultRealType() {
        String dataType = "java.util.List";

        assertEquals(dataType, getRealType(dataType));

        getMapDataTypeNamesToDisplayNames().clear();
        assertEquals(dataType, getRealType(dataType));
    }

    @Test
    public void testRealType() {
        String realDataType = "java.util.List";
        String displayDataType = "List [java.util]";
        getMapDataTypeNamesToDisplayNames().put(realDataType, displayDataType);

        assertEquals(realDataType, getRealType(displayDataType));
    }

    @Test
    public void testNonNullName() {
        String name;
        assertEquals("", getNonNullName(null));

        name = "";
        assertEquals(name, getNonNullName(name));

        name = "Some name";
        assertEquals(name, getNonNullName(name));
    }

    @Test
    public void testCustomDataType() {
        String customType = "java.util.List";
        Variable variable = new Variable("input", Variable.VariableType.INPUT, null, customType);

        assertEquals(customType, getDataType(variable));

        variable.setDataType("java.lang.Float");
        assertEquals(customType, getDataType(variable));
    }

    @Test
    public void testDefaultVariableType() {
        Variable variable = new Variable("input", Variable.VariableType.INPUT, null, null);

        assertEquals("Object", getDataType(variable));
    }

    @Test
    public void testDataType() {
        String dataType = "java.lang.Float";
        Variable variable = new Variable("input", Variable.VariableType.INPUT, dataType, null);

        assertEquals(dataType, getDataType(variable));
    }

    @Override
    @SuppressWarnings("unchecked")
    public FieldEditorPresenter.ValueChangeHandler<String> mockChangeHandler() {
        return mock(FieldEditorPresenter.ValueChangeHandler.class);
    }
}
