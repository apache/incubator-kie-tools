/*
 * Copyright 2010 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.testscenarios.shared.ExecutionTrace;
import org.drools.guvnor.models.testscenarios.shared.FactAssignmentField;
import org.kie.guvnor.commons.ui.client.resources.CommonsResources;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.drools.guvnor.models.testscenarios.shared.FixtureList;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.uberfire.client.common.DirtyableFlexTable;


public class FactAssignmentFieldWidget implements IsWidget {

    private final DirtyableFlexTable widget;

    public FactAssignmentFieldWidget(final FactAssignmentField factAssignmentField,
                                     final FixtureList definitionList,
                                     Scenario scenario,
                                     PackageDataModelOracle dmo,
                                     final ScenarioParentWidget parent,
                                     ExecutionTrace executionTrace) {

        widget = new DirtyableFlexTable();

        widget.setStyleName(CommonsResources.INSTANCE.css().greyBorderWithRoundCorners());
        new FactDataWidgetFactory(
                scenario,
                dmo,
                definitionList,
                executionTrace,
                parent,
                widget
        ).build(
                factAssignmentField.getFact().getType(),
                factAssignmentField.getFact());

    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
