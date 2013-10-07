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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.uberfire.client.common.ImageButton;

public abstract class FactWidget extends HorizontalPanel {

    protected final ScenarioParentWidget parent;
    protected final Scenario scenario;
    protected final FixtureList definitionList;

    public FactWidget( final String factType,
                       final FixtureList definitionList,
                       final Scenario scenario,
                       final AsyncPackageDataModelOracle oracle,
                       final ScenarioParentWidget parent,
                       final ExecutionTrace executionTrace,
                       final String headerText ) {
        this.parent = parent;
        this.scenario = scenario;
        this.definitionList = definitionList;

        add( new DataInputWidget( factType,
                                  definitionList,
                                  scenario,
                                  oracle,
                                  parent,
                                  executionTrace,
                                  headerText ) );
        add( new DeleteButton( definitionList ) );
    }

    protected void onDelete() {
        if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisBlockOfData() ) ) {
            for ( Fixture f : definitionList ) {
                scenario.removeFixture( f );
            }
            parent.renderEditor();
        }
    }

    class DeleteButton
            extends ImageButton {

        public DeleteButton( final FixtureList definitionList ) {
            super( CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   TestScenarioConstants.INSTANCE.RemoveThisBlockOfData() );

            addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    onDelete();
                }
            } );
        }
    }
}
