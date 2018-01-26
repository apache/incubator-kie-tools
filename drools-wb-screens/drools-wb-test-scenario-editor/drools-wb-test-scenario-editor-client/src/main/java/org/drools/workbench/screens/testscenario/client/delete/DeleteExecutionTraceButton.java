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

package org.drools.workbench.screens.testscenario.client.delete;

import com.google.gwt.user.client.Window;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.ScenarioParentWidget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

public class DeleteExecutionTraceButton extends Button {

    public DeleteExecutionTraceButton() {
        setType(ButtonType.DANGER);
        setText(TestScenarioConstants.INSTANCE.DeleteScenarioBlockAbove());
    }

    public void init(final Scenario scenario,
                     final ExecutionTrace executionTrace,
                     final ScenarioParentWidget parentWidgetToRender) {
        addClickHandler(clickE -> {
            if (deleteOperationConfirmed()) {
                scenario.removeExecutionTrace(executionTrace);
                parentWidgetToRender.renderEditor();
            }
        });
    }

    boolean deleteOperationConfirmed() {
        return Window.confirm(TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisItem());
    }
}