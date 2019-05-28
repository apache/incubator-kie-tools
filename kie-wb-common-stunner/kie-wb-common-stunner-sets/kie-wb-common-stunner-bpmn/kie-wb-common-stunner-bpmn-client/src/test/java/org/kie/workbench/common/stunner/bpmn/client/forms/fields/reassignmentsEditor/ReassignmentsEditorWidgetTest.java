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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget.ReassignmentWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget.ReassignmentWidgetViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ReassignmentsEditorWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private ReassignmentsEditorWidget reassignmentsEditorWidget;

    @GwtMock
    private ReassignmentWidget reassignmentWidget;

    @GwtMock
    private ReassignmentWidgetViewImpl reassignmentWidgetViewImpl;

    @GwtMock
    private HTMLButtonElement reassignmentButton;

    @Mock
    private ReassignmentTypeListValue values;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        doCallRealMethod().when(reassignmentsEditorWidget).setValue(any(ReassignmentTypeListValue.class));
        doCallRealMethod().when(reassignmentsEditorWidget).setValue(any(ReassignmentTypeListValue.class), any(boolean.class));
        doCallRealMethod().when(reassignmentsEditorWidget).getValue();
        doCallRealMethod().when(reassignmentsEditorWidget).init();
        doCallRealMethod().when(reassignmentsEditorWidget).addValueChangeHandler(any(ValueChangeHandler.class));
        doCallRealMethod().when(reassignmentsEditorWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(reassignmentsEditorWidget).showReassignmentsDialog();

        doCallRealMethod().when(reassignmentWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(reassignmentWidget).show();
        doCallRealMethod().when(reassignmentWidgetViewImpl).setReadOnly(any(boolean.class));

        doCallRealMethod().when(values).setValues(any(List.class));
        doCallRealMethod().when(values).getValues();
        doCallRealMethod().when(values).addValue(any(ReassignmentValue.class));
        doCallRealMethod().when(values).isEmpty();

        setFieldValue(reassignmentsEditorWidget, "reassignmentsTextBox", new HTMLInputElement());
    }

    @Test
    public void testZeroReassignments() {
        values.setValues(new ArrayList<>());
        reassignmentsEditorWidget.setValue(values);

        Assert.assertEquals(0, values.getValues().size());
        Assert.assertTrue(values.isEmpty());
        Assert.assertEquals(0, reassignmentsEditorWidget.getValue().getValues().size());

        HTMLInputElement input = getFieldValue(ReassignmentsEditorWidget.class,
                                               reassignmentsEditorWidget,
                                               "reassignmentsTextBox");
        Assert.assertEquals("0 reassignments", input.value);
    }

    @Test
    public void testOneReassignment() {
        values.setValues(new ArrayList<>());
        values.addValue(new ReassignmentValue());

        Assert.assertEquals(1, values.getValues().size());
        Assert.assertFalse(values.isEmpty());

        reassignmentsEditorWidget.setValue(values);

        Assert.assertEquals(1, values.getValues().size());
        Assert.assertEquals(1, reassignmentsEditorWidget.getValue().getValues().size());

        HTMLInputElement input = getFieldValue(ReassignmentsEditorWidget.class,
                                               reassignmentsEditorWidget,
                                               "reassignmentsTextBox");
        Assert.assertEquals("1 reassignments", input.value);
    }

    @Test
    public void testShowReassignmentsDialog() {
        values.setValues(new ArrayList<>());
        reassignmentsEditorWidget.setValue(values);

        setFieldValue(reassignmentWidget, "view", reassignmentWidgetViewImpl);
        setFieldValue(reassignmentsEditorWidget, "reassignmentWidget", reassignmentWidget);

        reassignmentsEditorWidget.showReassignmentsDialog();
        verify(reassignmentWidget, times(1)).show();
    }

    @Test
    public void testReadOnly() {

        setFieldValue(reassignmentWidgetViewImpl, "addButton", new HTMLButtonElement());
        setFieldValue(reassignmentWidgetViewImpl, "saveButton", new HTMLButtonElement());

        setFieldValue(reassignmentWidget, "view", reassignmentWidgetViewImpl);
        setFieldValue(reassignmentsEditorWidget, "reassignmentWidget", reassignmentWidget);

        reassignmentsEditorWidget.setReadOnly(true);

        boolean readOnly = getFieldValue(ReassignmentWidgetViewImpl.class,
                                         reassignmentWidgetViewImpl,
                                         "readOnly");

        HTMLButtonElement addButton = getFieldValue(ReassignmentWidgetViewImpl.class,
                                                    reassignmentWidgetViewImpl,
                                                    "addButton");
        HTMLButtonElement saveButton = getFieldValue(ReassignmentWidgetViewImpl.class,
                                                     reassignmentWidgetViewImpl,
                                                     "saveButton");

        Assert.assertTrue(readOnly);
        Assert.assertTrue(addButton.disabled);
        Assert.assertTrue(saveButton.disabled);
    }
}
