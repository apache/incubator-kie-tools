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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.ItemAltedImages;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;

class GlobalButton
        extends ImageButton {

    private final Scenario scenario;
    private final ScenarioParentWidget parent;

    private final AsyncPackageDataModelOracle oracle;

    public GlobalButton( final Scenario scenario,
                         final ScenarioParentWidget parent,
                         final AsyncPackageDataModelOracle oracle ) {
        super( ItemAltedImages.INSTANCE.NewItem(),
               TestScenarioConstants.INSTANCE.AddANewGlobalToThisScenario() );

        this.scenario = scenario;
        this.parent = parent;
        this.oracle = oracle;

        addGlobalButtonClickHandler();
    }

    private void addGlobalButtonClickHandler() {
        addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final FormStylePopup popup = new NewGlobalPopup();
                popup.show();
            }
        } );
    }

    class NewGlobalPopup extends FormStylePopup {

        final ListBox factTypes;
        private Button addButton;
        private Widget warning;

        public NewGlobalPopup() {
            super( TestScenarioAltedImages.INSTANCE.RuleAsset(),
                   TestScenarioConstants.INSTANCE.NewGlobal() );

            factTypes = new ListBox();
            addButton = new AddButton();
            warning = getMissingGlobalsWarning();

            fillFactTypes();

            addRow( warning );

            addAttribute( TestScenarioConstants.INSTANCE.GlobalColon(),
                          getHorizontalPanel() );
        }

        private HorizontalPanel getHorizontalPanel() {
            HorizontalPanel insertFact = new HorizontalPanel();
            insertFact.add( factTypes );
            insertFact.add( addButton );
            return insertFact;
        }

        private void fillFactTypes() {
            if ( oracle.getGlobalVariables().length == 0 ) {
                addButton.setEnabled( false );
                factTypes.setEnabled( false );
                warning.setVisible( true );
            } else {
                addButton.setEnabled( true );
                factTypes.setEnabled( true );
                warning.setVisible( false );
                for ( String globals : oracle.getGlobalVariables() ) {
                    factTypes.addItem( globals );
                }
            }
        }

        class AddButton extends Button {

            public AddButton() {
                super( TestScenarioConstants.INSTANCE.Add() );
                addAddClickHandler();
            }

            private void addAddClickHandler() {
                addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        String text = factTypes.getItemText( factTypes.getSelectedIndex() );
                        if ( scenario.isFactNameReserved( text ) ) {
                            Window.alert( TestScenarioConstants.INSTANCE.TheName0IsAlreadyInUsePleaseChooseAnotherName( text ) );
                        } else {
                            FactData factData = new FactData( oracle.getGlobalVariable( text ),
                                                              text,
                                                              false );
                            scenario.getGlobals().add( factData );
                            parent.renderEditor();

                            hide();
                        }
                    }
                } );
            }
        }

    }

    //A simple banner to alert users that no Globals have been defined
    private Widget getMissingGlobalsWarning() {
        HTML warning = new HTML( TestScenarioConstants.INSTANCE.missingGlobalsWarning() );
        warning.getElement().setClassName( "missingGlobalsWarning" );
        return warning;
    }
}
