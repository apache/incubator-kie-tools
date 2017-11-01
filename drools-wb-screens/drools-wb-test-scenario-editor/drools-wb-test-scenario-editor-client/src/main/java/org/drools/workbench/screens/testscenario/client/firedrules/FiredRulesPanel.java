/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.testscenario.client.firedrules;

import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;

public class FiredRulesPanel extends HorizontalPanel {

    private ExecutionTrace executionTrace;

    private HideFiredRulesButton hideButton;

    private ShowFiredRulesButton showButton;

    private FiredRulesTable firedRulesTable;

    public FiredRulesPanel( final ExecutionTrace executionTrace ) {
        this.executionTrace = executionTrace;
    }

    public void init() {
        firedRulesTable = firedRulesTable();
        hideButton = hideFiredRulesButton();
        showButton = showFiredRulesButton();

        firedRulesTable.init();
        hideButton.init(firedRulesTable, showButton);
        showButton.init(firedRulesTable, hideButton);

        add(hideButton);
        add(showButton);
        add(firedRulesTable);
    }

    FiredRulesTable firedRulesTable() {
        return new FiredRulesTable(executionTrace);
    }

    HideFiredRulesButton hideFiredRulesButton() {
        return new HideFiredRulesButton();
    }

    ShowFiredRulesButton showFiredRulesButton() {
        return new ShowFiredRulesButton();
    }
}
