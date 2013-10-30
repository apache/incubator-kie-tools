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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.RetractFact;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.SmallLabel;

public class RetractWidget extends FlexTable {

    protected final FixtureList retractList;
    protected final Scenario scenario;
    protected final ScenarioParentWidget parent;

    public RetractWidget( final FixtureList retractList,
                          final Scenario scenario,
                          final ScenarioParentWidget parent ) {

        this.retractList = retractList;
        this.scenario = scenario;
        this.parent = parent;

        render();
    }

    private void render() {

        clear();

        getCellFormatter().setStyleName( 0,
                                         0,
                                         "modeller-fact-TypeHeader" );
        getCellFormatter().setAlignment( 0,
                                         0,
                                         HasHorizontalAlignment.ALIGN_CENTER,
                                         HasVerticalAlignment.ALIGN_MIDDLE );
        setStyleName( "modeller-fact-pattern-Widget" );
        setWidget( 0,
                   0,
                   new SmallLabel( TestScenarioConstants.INSTANCE.DeleteFacts() ) );
        getFlexCellFormatter().setColSpan( 0,
                                           0,
                                           2 );

        int row = 1;
        for ( Fixture fixture : retractList ) {
            if ( fixture instanceof RetractFact ) {
                final RetractFact retractFact = (RetractFact) fixture;
                setWidget( row,
                           0,
                           new SmallLabel( retractFact.getName() ) );

                setWidget( row,
                           1,
                           new DeleteButton( retractFact ) );

                row++;
            }
        }
    }

    class DeleteButton extends ImageButton {

        public DeleteButton( final RetractFact retractFact ) {
            super( CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   TestScenarioConstants.INSTANCE.RemoveThisDeleteStatement() );

            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    retractList.remove( retractFact );
                    scenario.getFixtures().remove( retractFact );
                    parent.renderEditor();
                }
            } );
        }
    }
}
