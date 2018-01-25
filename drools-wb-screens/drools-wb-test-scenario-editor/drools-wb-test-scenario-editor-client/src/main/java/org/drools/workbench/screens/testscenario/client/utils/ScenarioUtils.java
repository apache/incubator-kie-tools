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

package org.drools.workbench.screens.testscenario.client.utils;

import com.google.gwt.user.client.ui.FlexTable;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;

public class ScenarioUtils {

    public static final String BOTTOM_RIGHT_PADDING = "padding-right: 10px; padding-bottom: 10px";

    public static ExecutionTrace findExecutionTrace(final Scenario scenario) {
        return scenario.getFixtures().stream()
                .filter(f -> f instanceof ExecutionTrace)
                .map(f -> (ExecutionTrace) f)
                .findFirst().orElse(new ExecutionTrace());
    }

    public static void addBottomAndRightPaddingToTableCells(final FlexTable table) {
        for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
            for (int cellIndex = 0; cellIndex < table.getCellCount(rowIndex); cellIndex++) {
                table.getCellFormatter().getElement(rowIndex, cellIndex).setAttribute("style", BOTTOM_RIGHT_PADDING);
            }
        }
    }
}
