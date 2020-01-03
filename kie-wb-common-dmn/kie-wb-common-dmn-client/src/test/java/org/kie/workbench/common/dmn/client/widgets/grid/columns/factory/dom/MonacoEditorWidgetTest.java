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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoStandaloneCodeEditor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MonacoEditorWidgetTest {

    @Mock
    private MonacoStandaloneCodeEditor codeEditor;

    private MonacoEditorWidget widget;

    @Before
    public void setup() {
        widget = spy(new MonacoEditorWidget());
        widget.setCodeEditor(codeEditor);
    }

    @Test
    public void testSetValueWhenCodeEditorIsPresent() {

        final String value = "value";

        widget.setValue(value);

        verify(codeEditor).setValue(value);
    }

    @Test
    public void testSetValueWhenCodeEditorIsNotPresent() {

        final String value = "value";

        widget.setCodeEditor(null);
        widget.setValue(value);

        verifyNoMoreInteractions(codeEditor);
    }

    @Test
    public void testGetValueWhenCodeEditorIsPresent() {

        final String expectedValue = "value";

        when(codeEditor.getValue()).thenReturn(expectedValue);

        final String actualValue = widget.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetValueWhenCodeEditorIsNotPresent() {

        final String expectedValue = "";

        widget.setCodeEditor(null);
        when(widget.getValue()).thenReturn(expectedValue);

        final String actualValue = widget.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetFocusWhenFocusIsEnabled() {
        widget.setFocus(true);
        verify(codeEditor).focus();
    }

    @Test
    public void testSetFocusWhenFocusIsNotEnabled() {
        widget.setFocus(false);
        verify(codeEditor, never()).focus();
    }

    @Test
    public void testSetFocusWhenCodeEditorIsNotPresent() {
        widget.setCodeEditor(null);
        widget.setFocus(true);
        verify(codeEditor, never()).focus();
    }
}
