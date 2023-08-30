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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorEditorWidgetBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

import static org.mockito.Mockito.mock;

public class TimerSettingsFieldEditorWidgetTest
        extends FieldEditorEditorWidgetBaseTest<TimerSettingsValue, TimerSettingsFieldEditorPresenter, TimerSettingsFieldEditorWidget, TimerSettingsFieldEditorPresenter.View> {

    @Override
    public TimerSettingsFieldEditorPresenter.View mockEditorView() {
        return mock(TimerSettingsFieldEditorPresenter.View.class);
    }

    @Override
    public TimerSettingsFieldEditorPresenter mockEditorPresenter() {
        return mock(TimerSettingsFieldEditorPresenter.class);
    }

    @Override
    public TimerSettingsFieldEditorWidget newEditorWidget(TimerSettingsFieldEditorPresenter editor) {
        return new TimerSettingsFieldEditorWidget(editor) {
            @Override
            protected void initWidget(Widget widget) {
                //avoid GWT client processing for testing purposes.
            }

            @Override
            protected Widget getWrapperWidget(HTMLElement element) {
                //avoid GWT client processing for testing purposes.
                return wrapperWidget;
            }
        };
    }

    @Override
    public TimerSettingsValue mockValue() {
        return mock(TimerSettingsValue.class);
    }
}
