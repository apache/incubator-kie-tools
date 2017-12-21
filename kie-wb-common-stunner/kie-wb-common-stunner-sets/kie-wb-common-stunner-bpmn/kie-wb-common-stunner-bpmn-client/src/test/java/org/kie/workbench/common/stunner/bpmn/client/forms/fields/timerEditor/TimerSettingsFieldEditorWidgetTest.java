/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimerSettingsFieldEditorWidgetTest {

    @Mock
    private TimerSettingsFieldEditorPresenter.View view;

    @Mock
    private HTMLElement element;

    @Mock
    private Widget wrapperWidget;

    @Mock
    private TimerSettingsFieldEditorPresenter editor;

    private TimerSettingsFieldEditorWidget widget;

    @Before
    public void setUp() {
        when(editor.getView()).thenReturn(view);
        when(view.getElement()).thenReturn(element);
        widget = spy(new TimerSettingsFieldEditorWidget(editor) {
            @Override
            protected void initWidget(Widget widget) {
                //avoid GWT client processing for testing purposes.
            }

            @Override
            protected Widget getWrapperWidget(HTMLElement element) {
                //avoid GWT client processing for testing purposes.
                return wrapperWidget;
            }
        });
        widget.init();
        verify(editor,
               times(1)).addChangeHandler(any(TimerSettingsFieldEditorPresenter.ValueChangeHandler.class));
    }

    @Test
    public void testGetValue() {
        TimerSettingsValue value = mock(TimerSettingsValue.class);
        when(editor.getValue()).thenReturn(value);
        assertEquals(value,
                     widget.getValue());
    }

    @Test
    public void testSetValueWithoutNotification() {
        TimerSettingsValue oldValue = mock(TimerSettingsValue.class);
        when(editor.getValue()).thenReturn(oldValue);
        TimerSettingsValue value = mock(TimerSettingsValue.class);
        widget.setValue(value);
        verify(widget,
               never()).notifyChange(any(),
                                     any());
    }

    @Test
    public void testSetValueWithNotification() {
        TimerSettingsValue oldValue = mock(TimerSettingsValue.class);
        when(editor.getValue()).thenReturn(oldValue);
        TimerSettingsValue value = mock(TimerSettingsValue.class);
        widget.setValue(value,
                        true);
        verify(widget,
               times(1)).notifyChange(oldValue,
                                      value);
    }
}
