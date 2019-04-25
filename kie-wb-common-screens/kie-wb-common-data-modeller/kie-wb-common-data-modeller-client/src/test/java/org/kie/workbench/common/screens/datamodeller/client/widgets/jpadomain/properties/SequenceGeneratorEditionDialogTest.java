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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SequenceGeneratorEditionDialogTest {

    private static final String SEQUENCE_NAME_VALUE = "SEQUENCE_NAME_VALUE";
    private static final String NAME_VALUE = "NAME_VALUE";
    private static final Integer INITIAL_VALUE = 1234;
    private static final Integer ALLOCATION_SIZE_VALUE = 5678;

    @Mock
    private SequenceGeneratorEditionDialog.View view;

    @Mock
    private DataModelerPropertyEditorFieldInfo fieldInfo;

    private SequenceGeneratorEditionDialog dialog;

    @Before
    public void setUp() {
        dialog = new SequenceGeneratorEditionDialog(view);
        dialog.init();
        verify(view).init(dialog);
    }

    @Test
    public void testAsWidget() {
        Widget widget = mock(Widget.class);
        when(view.asWidget()).thenReturn(widget);
        assertEquals(widget, dialog.asWidget());
    }

    @Test
    public void testShow() {
        when(fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.SEQUENCE_NAME)).thenReturn(SEQUENCE_NAME_VALUE);
        when(fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.NAME)).thenReturn(NAME_VALUE);
        when(fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.INITIAL_VALUE)).thenReturn(INITIAL_VALUE);
        when(fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.ALLOCATION_SIZE)).thenReturn(ALLOCATION_SIZE_VALUE);
        when(fieldInfo.isDisabled()).thenReturn(false);

        dialog.setProperty(fieldInfo);
        dialog.show();
        verify(view).setSequenceName(SEQUENCE_NAME_VALUE);
        verify(view).setGeneratorName(NAME_VALUE);
        verify(view).setInitialValue(INITIAL_VALUE.toString());
        verify(view).clearInitialValueError();
        verify(view).setAllocationSize(ALLOCATION_SIZE_VALUE.toString());
        verify(view).clearAllocationSizeError();
        verify(view).setEnabled(!fieldInfo.isDisabled());
        verify(view).show();
    }

    @Test
    public void testGetStringValue() {
        testGetStringValue(NAME_VALUE, NAME_VALUE);
    }

    @Test
    public void testGetStringValueWhenNotSet() {
        testGetStringValue(SequenceGeneratorField.NOT_CONFIGURED_LABEL, "");
        testGetStringValue(SequenceGeneratorField.NOT_CONFIGURED_LABEL, null);
    }

    private void testGetStringValue(String expectedValue, String currentValue) {
        when(view.getGeneratorName()).thenReturn(currentValue);
        assertEquals(expectedValue, dialog.getStringValue());
    }

    @Test
    public void testOnOK() {
        Command command = mock(Command.class);
        when(view.getSequenceName()).thenReturn(SEQUENCE_NAME_VALUE);
        when(view.getGeneratorName()).thenReturn(NAME_VALUE);
        when(view.getInitialValue()).thenReturn(INITIAL_VALUE.toString());
        when(view.getAllocationSize()).thenReturn(ALLOCATION_SIZE_VALUE.toString());
        dialog.setProperty(fieldInfo);
        dialog.setOkCommand(command);
        dialog.onOK();
        verify(fieldInfo).setCurrentValue(SequenceGeneratorValueHandler.SEQUENCE_NAME, SEQUENCE_NAME_VALUE);
        verify(fieldInfo).setCurrentValue(SequenceGeneratorValueHandler.NAME, NAME_VALUE);
        verify(fieldInfo).setCurrentValue(SequenceGeneratorValueHandler.INITIAL_VALUE, INITIAL_VALUE);
        verify(fieldInfo).setCurrentValue(SequenceGeneratorValueHandler.ALLOCATION_SIZE, ALLOCATION_SIZE_VALUE);
        verify(view).hide();
        verify(command).execute();
    }

    @Test
    public void testOnCancel() {
        dialog.onCancel();
        verify(view).hide();
    }

    @Test
    public void testOnInitialValueChangeOK() {
        when(view.getInitialValue()).thenReturn(INITIAL_VALUE.toString());
        dialog.onInitialValueChange();
        verify(view).clearInitialValueError();
        verify(view).enableOkAction(true);
        verify(view, never()).enableOkAction(false);
        verify(view, never()).setInitialValueError(anyString());
    }

    @Test
    public void testOnInitialValueChangeError() {
        when(view.getInitialValue()).thenReturn("non integer value");
        dialog.onInitialValueChange();
        verify(view).clearInitialValueError();
        verify(view).enableOkAction(true);
        verify(view).enableOkAction(false);
        verify(view).setInitialValueError(Constants.INSTANCE.persistence_domain_relationship_sequence_generator_dialog_initial_value_error());
    }

    @Test
    public void testOnAllocationsSizeChangeOK() {
        when(view.getAllocationSize()).thenReturn(ALLOCATION_SIZE_VALUE.toString());
        dialog.onAllocationSizeChange();
        verify(view).clearAllocationSizeError();
        verify(view).enableOkAction(true);
        verify(view, never()).enableOkAction(false);
        verify(view, never()).setAllocationSizeError(anyString());
    }

    @Test
    public void testOnAllocationsSizeChangeError() {
        when(view.getAllocationSize()).thenReturn("non integer value");
        dialog.onAllocationSizeChange();
        verify(view).clearAllocationSizeError();
        verify(view).enableOkAction(true);
        verify(view).enableOkAction(false);
        verify(view).setAllocationSizeError(Constants.INSTANCE.persistence_domain_relationship_sequence_generator_dialog_allocation_size_error());
    }
}
