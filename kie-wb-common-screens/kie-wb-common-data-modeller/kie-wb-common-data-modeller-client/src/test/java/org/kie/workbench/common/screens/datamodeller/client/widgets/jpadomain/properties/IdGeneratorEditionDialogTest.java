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
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IdGeneratorEditionDialogTest {

    private static final String STRATEGY = "strategy";

    private static final String GENERATOR = "generator";

    private static final String STRATEGY_VALUE = "STRATEGY_VALUE";

    private static final String GENERATOR_VALUE = "GENERATOR_VALUE";

    @Mock
    private IdGeneratorEditionDialog.View view;

    @Mock
    private DataModelerPropertyEditorFieldInfo fieldInfo;

    private IdGeneratorEditionDialog dialog;

    @Before
    public void setUp() {
        dialog = new IdGeneratorEditionDialog(view);
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
        when(fieldInfo.getCurrentValue(IdGeneratorField.STRATEGY)).thenReturn(STRATEGY_VALUE);
        when(fieldInfo.getCurrentValue(IdGeneratorField.GENERATOR)).thenReturn(GENERATOR_VALUE);
        when(fieldInfo.isDisabled()).thenReturn(false);
        dialog.setProperty(fieldInfo);
        dialog.show();
        verify(view).setGeneratorType(STRATEGY_VALUE);
        verify(view).setGeneratorName(GENERATOR_VALUE);
        verify(view).setEnabled(!fieldInfo.isDisabled());
        verify(view).show();
    }

    @Test
    public void testGetStringValue() {
        testGetStringValue("someValue", "someValue");
    }

    @Test
    public void testGetStringValueWhenNotSet() {
        testGetStringValue(IdGeneratorField.NOT_CONFIGURED_LABEL, UIUtil.NOT_SELECTED);
    }

    private void testGetStringValue(String expectedValue, String currentValue) {
        when(view.getGeneratorType()).thenReturn(currentValue);
        assertEquals(expectedValue, dialog.getStringValue());
    }

    @Test
    public void testOnOK() {
        Command command = mock(Command.class);
        when(view.getGeneratorType()).thenReturn(STRATEGY_VALUE);
        when(view.getGeneratorName()).thenReturn(GENERATOR_VALUE);
        dialog.setProperty(fieldInfo);
        dialog.setOkCommand(command);
        dialog.onOK();
        verify(fieldInfo).setCurrentValue(STRATEGY, STRATEGY_VALUE);
        verify(fieldInfo).setCurrentValue(GENERATOR, GENERATOR_VALUE);
        verify(view).hide();
        verify(command).execute();
    }

    @Test
    public void testOnCancel() {
        dialog.onCancel();
        verify(view).hide();
    }
}
