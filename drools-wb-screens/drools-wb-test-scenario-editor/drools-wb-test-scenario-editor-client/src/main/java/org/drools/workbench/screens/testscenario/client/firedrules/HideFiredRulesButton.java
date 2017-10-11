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

import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class HideFiredRulesButton extends Button {

    private FiredRulesTable firedRulesTable;
    private Button showFiredRulesButton;

    public void init(final FiredRulesTable firedRulesTable,
                     final Button showFiredRulesButton) {
        this.firedRulesTable = firedRulesTable;
        this.showFiredRulesButton = showFiredRulesButton;
        setText(TestScenarioConstants.INSTANCE.HideFiredRules());
        setIcon(IconType.ANGLE_LEFT);
        setVisible(false);
        addClickHandler(clickEvent -> hideFiredRules());
    }

    public void hideFiredRules() {
        firedRulesTable.setVisible(false);
        setVisible(false);
        showFiredRulesButton.setVisible(true);
    }

}
