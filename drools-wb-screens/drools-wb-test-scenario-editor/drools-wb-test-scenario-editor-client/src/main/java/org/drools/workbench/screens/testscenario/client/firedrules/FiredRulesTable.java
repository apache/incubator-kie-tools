/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.testscenario.client.firedrules;

import java.util.Arrays;

import com.google.gwt.user.cellview.client.TextColumn;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

public class FiredRulesTable extends CellTable<String> {

    static final String MAX_WIDTH = "100%";
    private final TextColumn<String> firedRuleColumn = new TextColumn<String>() {
        @Override
        public String getValue(String firedRule) {
            return firedRule;
        }
    };

    private ExecutionTrace executionTrace;

    public FiredRulesTable(final ExecutionTrace executionTrace) {
        this.executionTrace = executionTrace;
    }

    public void init() {
        setStriped(true);
        setCondensed(true);
        setBordered(true);
        setVisible(false);
        setWidth(MAX_WIDTH);

        addColumn(firedRuleColumn,
                  TestScenarioConstants.INSTANCE.property0RulesFiredIn1Ms(
                          executionTrace.getNumberOfRulesFired(),
                          executionTrace.getExecutionTimeResult()));

        setRowData(Arrays.asList(executionTrace.getRulesFired()));
    }


}
