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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import org.drools.workbench.models.testscenarios.shared.ActivateRuleFlowGroup;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.utils.ScenarioUtils;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class ActivateRuleFlowWidget
        extends Composite {

    private final ScenarioParentWidget parent;

    public ActivateRuleFlowWidget( final FixtureList retList,
                                   final Scenario sc,
                                   final ScenarioParentWidget parent ) {
        FlexTable outer = new FlexTable();
        render( retList,
                outer,
                sc );

        this.parent = parent;

        initWidget( outer );
    }

    private void render( final FixtureList retList,
                         final FlexTable outer,
                         final Scenario sc ) {
        outer.clear();
        outer.getCellFormatter().setStyleName( 0,
                                               0,
                                               "modeller-fact-TypeHeader" );
        outer.getCellFormatter().setAlignment( 0,
                                               0,
                                               HasHorizontalAlignment.ALIGN_CENTER,
                                               HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName( "modeller-fact-pattern-Widget" );
        outer.setWidget( 0,
                         0,
                         new SmallLabel( TestScenarioConstants.INSTANCE.ActivateRuleFlowGroup() ) );
        outer.getFlexCellFormatter().setColSpan( 0,
                                                 0,
                                                 2 );

        int row = 1;
        for ( Fixture fixture : retList ) {
            final ActivateRuleFlowGroup acticateRuleFlowGroup = (ActivateRuleFlowGroup) fixture;
            outer.setWidget( row,
                             0,
                             new SmallLabel( acticateRuleFlowGroup.getName() ) );

            Button deleteButton = new Button();
            deleteButton.setIcon(IconType.TRASH);
            deleteButton.setTitle(TestScenarioConstants.INSTANCE.RemoveThisRuleFlowActivation());
            deleteButton.addClickHandler(clickEvent -> {
                retList.remove(acticateRuleFlowGroup);
                sc.getFixtures().remove(acticateRuleFlowGroup);
                render(retList,
                       outer,
                       sc);
                parent.renderEditor();
            });

            outer.setWidget( row,
                             1,
                             deleteButton );

            row++;
        }

        ScenarioUtils.addBottomAndRightPaddingToTableCells(outer);
    }
}
