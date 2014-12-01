/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public class TypeChoiceFormPopup
        extends FormStylePopup
        implements HasSelectionHandlers<Integer> {

    public TypeChoiceFormPopup( final FieldConstraintHelper helper ) {
        super( TestScenarioAltedImages.INSTANCE.Wizard(),
               TestScenarioConstants.INSTANCE.FieldValue() );

        addLiteralValueSelection();

        addRow( new HTML( "<hr/>" ) );
        addRow( new SmallLabel( TestScenarioConstants.INSTANCE.AdvancedOptions() ) );

        // If we are here, then there must be a bound variable compatible with
        // me
        if ( helper.isThereABoundVariableToSet() ) {
            addBoundVariableSelection();
        }
        if ( helper.isItAList() && !helper.isTheParentAList() ) {
            addListSelection();
        }

        if ( !helper.isTheParentAList() ) {
            addCreateNewObject();
        }
    }

    private void addCreateNewObject() {
        Button button = new Button( TestScenarioConstants.INSTANCE.CreateNewFact() );
        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                fireSelection( FieldData.TYPE_FACT );
            }

        } );
        addAttribute( TestScenarioConstants.INSTANCE.Fact(),
                      widgets( button,
                               new InfoPopup( TestScenarioConstants.INSTANCE.CreateNewFact(),
                                              TestScenarioConstants.INSTANCE.CreateNewFactTip() ) ) );
    }

    private void addLiteralValueSelection() {
        Button lit = new Button( TestScenarioConstants.INSTANCE.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                fireSelection( FieldData.TYPE_LITERAL );
            }
        } );
        addAttribute( TestScenarioConstants.INSTANCE.LiteralValue() + ":",
                      widgets( lit,
                               new InfoPopup( TestScenarioConstants.INSTANCE.LiteralValue(),
                                              TestScenarioConstants.INSTANCE.LiteralValTip() ) ) );
    }

    private void addListSelection() {
        Button variable = new Button( TestScenarioConstants.INSTANCE.GuidedList() );
        variable.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                fireSelection( FieldData.TYPE_COLLECTION );
            }
        } );
        addAttribute( TestScenarioConstants.INSTANCE.AVariable(),
                      widgets( variable,
                               new InfoPopup( TestScenarioConstants.INSTANCE.AGuidedList(),
                                              TestScenarioConstants.INSTANCE.AGuidedListTip() ) ) );
    }

    private void addBoundVariableSelection() {
        Button variable = new Button( GuidedRuleEditorResources.CONSTANTS.BoundVariable() );
        variable.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                fireSelection( FieldData.TYPE_VARIABLE );
            }
        } );
        addAttribute( GuidedRuleEditorResources.CONSTANTS.AVariable(),
                      widgets( variable,
                               new InfoPopup( GuidedRuleEditorResources.CONSTANTS.ABoundVariable(),
                                              GuidedRuleEditorResources.CONSTANTS.BoundVariableTip() ) ) );
    }

    private void fireSelection( final int type ) {
        SelectionEvent.fire( this, type );
        hide();
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<Integer> selectionHandler ) {
        return addHandler( selectionHandler,
                           SelectionEvent.getType() );
    }

    private Panel widgets( final IsWidget left,
                           final IsWidget right ) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( left );
        panel.add( right );
        panel.setWidth( "100%" );
        return panel;
    }
}
