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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.models.testscenarios.shared.ActivateRuleFlowGroup;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldPlaceHolder;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.RetractFact;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.ItemAltedImages;
import org.uberfire.client.common.SmallLabel;

/**
 * This button gives a choice of modifying data, based on the positional
 * context.
 */
public class NewDataButton extends TestScenarioButton {

    private final ExecutionTrace currentEx;

    public NewDataButton( final ExecutionTrace previousEx,
                          final Scenario scenario,
                          final ExecutionTrace currentEx,
                          final ScenarioParentWidget scenarioWidget,
                          final AsyncPackageDataModelOracle oracle ) {
        super( ItemAltedImages.INSTANCE.NewItem(),
               TestScenarioConstants.INSTANCE.AddANewDataInputToThisScenario(),
               previousEx,
               scenario,
               scenarioWidget,
               oracle );

        this.currentEx = currentEx;
    }

    @Override
    protected TestScenarioButtonPopup getPopUp() {
        return new NewInputPopup();
    }

    class NewInputPopup extends TestScenarioButtonPopup {

        public NewInputPopup() {
            super( TestScenarioAltedImages.INSTANCE.RuleAsset(),
                   TestScenarioConstants.INSTANCE.NewInput() );

            addAttribute( TestScenarioConstants.INSTANCE.InsertANewFact1(),
                          new InsertFactPanel() );

            List<String> varsInScope = scenario.getFactNamesInScope( currentEx,
                                                                     false );
            //now we do modifies & retracts
            if ( varsInScope.size() > 0 ) {
                addAttribute( TestScenarioConstants.INSTANCE.ModifyAnExistingFactScenario(),
                              new ModifyFactPanel( varsInScope ) );

                addAttribute( TestScenarioConstants.INSTANCE.DeleteAnExistingFactScenario(),
                              new ExtractFactPanel( varsInScope ) );

            }

            addAttribute( TestScenarioConstants.INSTANCE.ActivateRuleFlowGroup(),
                          new ActivateRuleFlowPanel() );
        }

        class ActivateRuleFlowPanel extends BasePanel<TextBox> {

            @Override
            public TextBox getWidget() {
                return new TextBox();
            }

            @Override
            public Fixture getFixture() {
                return new ActivateRuleFlowGroup( valueWidget.getText() );
            }
        }

        class ExtractFactPanel extends ListBoxBasePanel {

            public ExtractFactPanel( List<String> listItems ) {
                super( listItems );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );

                return new RetractFact( factName );
            }
        }

        class ModifyFactPanel extends ListBoxBasePanel {

            public ModifyFactPanel( List<String> listItems ) {
                super( listItems );
            }

            @Override
            public Fixture getFixture() {
                String factName = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                String type = scenario.getVariableTypes().get( factName );

                return new FactData( type,
                                     factName,
                                     true );

            }
        }

        class InsertFactPanel extends ListBoxBasePanel {

            TextBox factNameTextBox;

            public InsertFactPanel() {
                super( oracle.getFactTypes() );
            }

            @Override
            protected void addAddButtonClickHandler() {
                add.addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        String factName = ( "" + factNameTextBox.getText() ).trim();
                        if ( factName.equals( "" ) || factNameTextBox.getText().indexOf( ' ' ) > -1 ) {
                            Window.alert( TestScenarioConstants.INSTANCE.YouMustEnterAValidFactName() );
                        } else {
                            if ( scenario.isFactNameReserved( factName ) ) {
                                Window.alert( TestScenarioConstants.INSTANCE.TheFactName0IsAlreadyInUsePleaseChooseAnotherName( factName ) );
                            } else {
                                scenario.insertBetween( previousEx,
                                                        getFixture() );
                                parent.renderEditor();
                                hide();
                            }
                        }
                    }
                } );

            }

            @Override
            protected void initWidgets() {
                factNameTextBox = new TextBox();
                factNameTextBox.setVisibleLength( 5 );

                add( valueWidget );
                add( new SmallLabel( TestScenarioConstants.INSTANCE.FactName() ) );
                add( factNameTextBox );
                add( add );
            }

            @Override
            public Fixture getFixture() {
                String factType = valueWidget.getItemText( valueWidget.getSelectedIndex() );
                FactData factData = new FactData(
                        factType,
                        factNameTextBox.getText(),
                        false );

                //Create new Field objects for new Fixture based upon the first existing of the same data-type
                //Only the "first" existing of the same data-type is checked as second, third etc should have been
                //based upon the first if they were all created after this fix for GUVNOR-1139 was implemented.
                List<FactData> existingFactData = scenario.getFactTypesToFactData().get( factType );
                if ( existingFactData != null && existingFactData.size() > 0 ) {
                    for ( Field field : existingFactData.get( 0 ).getFieldData() ) {
                        factData.getFieldData().add(
                                new FieldPlaceHolder( field.getName() ) );
                    }
                }

                return factData;
            }
        }
    }

}
