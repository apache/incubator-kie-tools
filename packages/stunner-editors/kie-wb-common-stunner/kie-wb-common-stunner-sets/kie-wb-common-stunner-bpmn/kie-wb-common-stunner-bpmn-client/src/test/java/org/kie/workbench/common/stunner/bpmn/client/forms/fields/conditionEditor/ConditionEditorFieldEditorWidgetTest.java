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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorEditorWidgetBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.mockito.Mockito.mock;

public class ConditionEditorFieldEditorWidgetTest
        extends FieldEditorEditorWidgetBaseTest<ScriptTypeValue, ConditionEditorFieldEditorPresenter, ConditionEditorFieldEditorWidget, ConditionEditorFieldEditorPresenter.View> {

    @Override
    public ConditionEditorFieldEditorPresenter.View mockEditorView() {
        return mock(ConditionEditorFieldEditorPresenter.View.class);
    }

    @Override
    public ConditionEditorFieldEditorPresenter mockEditorPresenter() {
        return mock(ConditionEditorFieldEditorPresenter.class);
    }

    @Override
    public ConditionEditorFieldEditorWidget newEditorWidget(ConditionEditorFieldEditorPresenter editor) {
        return new ConditionEditorFieldEditorWidget(editor) {
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
    public ScriptTypeValue mockValue() {
        return mock(ScriptTypeValue.class);
    }
}
