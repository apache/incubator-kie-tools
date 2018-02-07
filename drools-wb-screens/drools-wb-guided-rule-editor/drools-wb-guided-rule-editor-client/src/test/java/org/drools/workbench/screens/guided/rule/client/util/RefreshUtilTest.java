/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.ActionValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.ConstraintValueEditor;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@WithClassesToStub(DateTimeFormat.class)
@RunWith(GwtMockitoTestRunner.class)
public class RefreshUtilTest {

    @Test
    public void testConstraintValueEditorRefreshPredicateNotRefreshed() throws Exception {
        final SingleFieldConstraint constraintOne = mock(SingleFieldConstraint.class);
        final SingleFieldConstraint constraintTwo = mock(SingleFieldConstraint.class);
        final ConstraintValueEditor editorOne = mock(ConstraintValueEditor.class);
        final ConstraintValueEditor editorTwo = mock(ConstraintValueEditor.class);
        final Map<SingleFieldConstraint, ConstraintValueEditor> editors = new HashMap<>();
        editors.put(constraintOne, editorOne);
        editors.put(constraintTwo, editorTwo);

        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(constraintOne).getConstraintValueType();
        doReturn(BaseSingleFieldConstraint.TYPE_PREDICATE).when(constraintTwo).getConstraintValueType();

        RefreshUtil.refreshConstraintValueEditorsDropDownData(editors, mock(SingleFieldConstraint.class));

        verify(editorOne).refresh();
        verify(editorTwo, never()).refresh();
    }

    @Test
    public void testConstraintValueEditorRefreshMultipleEditors() throws Exception {
        final SingleFieldConstraint constraintOne = mock(SingleFieldConstraint.class);
        final SingleFieldConstraint constraintTwo = mock(SingleFieldConstraint.class);
        final ConstraintValueEditor editorOne = mock(ConstraintValueEditor.class);
        final ConstraintValueEditor editorTwo = mock(ConstraintValueEditor.class);
        final Map<SingleFieldConstraint, ConstraintValueEditor> editors = new HashMap<>();
        editors.put(constraintOne, editorOne);
        editors.put(constraintTwo, editorTwo);

        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(constraintOne).getConstraintValueType();
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(constraintTwo).getConstraintValueType();

        RefreshUtil.refreshConstraintValueEditorsDropDownData(editors, mock(SingleFieldConstraint.class));

        verify(editorOne).refresh();
        verify(editorTwo).refresh();
    }

    @Test
    public void testActionValueEditorRefreshPredicateNotRefreshed() throws Exception {
        final ActionFieldValue valueOne = mock(ActionFieldValue.class);
        final ActionFieldValue valueTwo = mock(ActionFieldValue.class);
        final ActionValueEditor editorOne = mock(ActionValueEditor.class);
        final ActionValueEditor editorTwo = mock(ActionValueEditor.class);
        final Map<ActionFieldValue, ActionValueEditor> editors = new HashMap<>();
        editors.put(valueOne, editorOne);
        editors.put(valueTwo, editorTwo);

        doReturn(FieldNatureType.TYPE_LITERAL).when(valueOne).getNature();
        doReturn(FieldNatureType.TYPE_PREDICATE).when(valueTwo).getNature();

        RefreshUtil.refreshActionValueEditorsDropDownData(editors, mock(ActionFieldValue.class));

        verify(editorOne).refresh();
        verify(editorTwo, never()).refresh();
    }

    @Test
    public void testActionValueEditorRefreshMultipleEditors() throws Exception {
        final ActionFieldValue valueOne = mock(ActionFieldValue.class);
        final ActionFieldValue valueTwo = mock(ActionFieldValue.class);
        final ActionValueEditor editorOne = mock(ActionValueEditor.class);
        final ActionValueEditor editorTwo = mock(ActionValueEditor.class);
        final Map<ActionFieldValue, ActionValueEditor> editors = new HashMap<>();
        editors.put(valueOne, editorOne);
        editors.put(valueTwo, editorTwo);

        doReturn(FieldNatureType.TYPE_LITERAL).when(valueOne).getNature();
        doReturn(FieldNatureType.TYPE_LITERAL).when(valueTwo).getNature();

        RefreshUtil.refreshActionValueEditorsDropDownData(editors, mock(ActionFieldValue.class));

        verify(editorOne).refresh();
        verify(editorTwo).refresh();
    }
}
