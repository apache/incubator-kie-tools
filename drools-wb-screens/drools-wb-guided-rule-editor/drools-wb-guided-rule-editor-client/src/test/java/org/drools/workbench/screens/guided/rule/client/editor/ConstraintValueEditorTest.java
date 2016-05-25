/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.rule.client.editor;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.guvnor.common.services.workingset.client.WorkingSetManager;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.LiteralTextBox;
import org.mockito.Mock;

@WithClassesToStub(AddConstraintButton.class)
@RunWith(GwtMockitoTestRunner.class)
public class ConstraintValueEditorTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;
    @Mock
    private SingleFieldConstraint constraint;
    @Mock
    private WorkingSetManager wsManager;
    @GwtMock
    private RuleModeller ruleModeller;

    @Before
    public void setup() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DATE_FORMAT, "dd MMM yyyy");
        ApplicationPreferences.setUp(map);

        when(ruleModeller.getDataModelOracle()).thenReturn(oracle);
        when(oracle.getFieldType(anyString(), anyString())).thenReturn(DataType.TYPE_STRING);
        when(constraint.getConstraintValueType()).thenReturn(SingleFieldConstraint.TYPE_LITERAL);
    }

    @Test
    public void correctWidgetForStringField() {
        ConstraintValueEditor editor = createEditor();
        assertTrue(editor.getConstraintWidget() instanceof LiteralTextBox);
    }

    @Test
    public void correctWidgetForStringFieldValueInList() {
        when(constraint.getOperator()).thenReturn("in");
        ConstraintValueEditor editor = createEditor();
        assertTrue(editor.getConstraintWidget() instanceof LiteralTextBox);
    }

    @Test
    public void correctWidgetForStringFieldValueNotInList() {
        when(constraint.getOperator()).thenReturn("not in");
        ConstraintValueEditor editor = createEditor();
        assertTrue(editor.getConstraintWidget() instanceof LiteralTextBox);
    }

    private ConstraintValueEditor createEditor() {
        return new ConstraintValueEditor(constraint,
                                         mock(CompositeFieldConstraint.class),
                                         ruleModeller,
                                         mock(EventBus.class),
                                         false) {
            @Override
            void setBoxSize(final TextBox box) {
                //do nothing - avoid calling JavaScriptObject.cast()
            }

            @Override
            Widget wrap( Widget widget ) {
                return widget;
            }

            @Override
            WorkingSetManager getWorkingSetManager() {
                return wsManager;
            }
         };
    }
}