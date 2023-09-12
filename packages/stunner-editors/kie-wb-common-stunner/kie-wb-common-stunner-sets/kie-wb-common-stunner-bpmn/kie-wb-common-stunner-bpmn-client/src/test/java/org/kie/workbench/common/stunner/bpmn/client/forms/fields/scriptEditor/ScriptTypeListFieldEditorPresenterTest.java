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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.scriptEditor;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScriptTypeListFieldEditorPresenterTest {

    @Mock
    private ScriptTypeFieldEditorPresenter.View view;

    @Mock
    private ScriptTypeFieldEditorPresenter scriptTypePresenter;

    private ScriptTypeListFieldEditorPresenter editor;

    @Mock
    private FieldEditorPresenter.ValueChangeHandler<ScriptTypeListValue> changeHandler;

    @Before
    public void setUp() {
        when(scriptTypePresenter.getView()).thenReturn(view);
        editor = spy(new ScriptTypeListFieldEditorPresenter(scriptTypePresenter));
        editor.init();
        verify(scriptTypePresenter,
               times(1)).addChangeHandler(any(FieldEditorPresenter.ValueChangeHandler.class));
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     editor.getView());
    }

    @Test
    public void testSetValueWhenNull() {
        editor.setValue(null);
        verify(scriptTypePresenter,
               times(1)).setValue(null);
    }

    @Test
    public void testSetValueWhenEmpty() {
        editor.setValue(new ScriptTypeListValue());
        verify(scriptTypePresenter,
               times(1)).setValue(null);
    }

    @Test
    public void testSetValueWhenNotEmpty() {
        ScriptTypeValue value = mock(ScriptTypeValue.class);
        editor.setValue(new ScriptTypeListValue().addValue(value));
        verify(scriptTypePresenter,
               times(1)).setValue(value);
    }

    @Test
    public void testSetMode() {
        Arrays.stream(ScriptTypeMode.values()).forEach(mode -> {
            editor.setMode(mode);
            verify(scriptTypePresenter,
                   times(1)).setMode(mode);
        });
    }

    @Test
    public void testOnChange() {
        ScriptTypeListValue oldValue = mock(ScriptTypeListValue.class);
        ScriptTypeValue oldScriptTypeValue = mock(ScriptTypeValue.class);
        ScriptTypeValue newScriptTypeValue = mock(ScriptTypeValue.class);

        editor.setValue(oldValue);
        editor.addChangeHandler(changeHandler);
        editor.onValueChange(oldScriptTypeValue,
                             newScriptTypeValue);
        changeHandler.onValueChange(oldValue,
                                    new ScriptTypeListValue().addValue(newScriptTypeValue));
    }

    @Test
    public void testSetReadonlyTrue() {
        editor.setReadOnly(true);
        verify(scriptTypePresenter,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        editor.setReadOnly(false);
        verify(scriptTypePresenter,
               times(1)).setReadOnly(false);
    }
}
