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
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactAssignmentField;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonsResources;
import com.google.gwt.user.client.ui.FlexTable;

public class FactAssignmentFieldWidget implements IsWidget {

    private final FlexTable widget;

    public FactAssignmentFieldWidget( final FactAssignmentField factAssignmentField,
                                      final FixtureList definitionList,
                                      final Scenario scenario,
                                      final AsyncPackageDataModelOracle oracle,
                                      final ScenarioParentWidget parent,
                                      final ExecutionTrace executionTrace ) {

        widget = new FlexTable();

        widget.setStyleName( CommonsResources.INSTANCE.css().greyBorderWithRoundCorners() );
        new FactDataWidgetFactory( scenario,
                                   oracle,
                                   definitionList,
                                   executionTrace,
                                   parent,
                                   widget
        ).build( factAssignmentField.getFact().getType(),
                 factAssignmentField.getFact() );

    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
