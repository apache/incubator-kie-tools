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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.kie.workbench.common.widgets.client.resources.ItemAltedImages;
import org.kie.workbench.common.widgets.metadata.client.resources.Images;
import org.uberfire.client.common.FormStylePopup;

public class ConfigWidget extends Composite {

    private final ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;

    public ConfigWidget( final Scenario sc,
                         final ScenarioWidgetComponentCreator scenarioWidgetComponentCreator ) {

        this.scenarioWidgetComponentCreator = scenarioWidgetComponentCreator;

        final ListBox box = new ListBox( true );

        for ( int i = 0; i < sc.getRules().size(); i++ ) {
            box.addItem( sc.getRules().get( i ) );
        }
        HorizontalPanel filter = new HorizontalPanel();

        final Image add = ItemAltedImages.INSTANCE.NewItem();
        add.setAltText( TestScenarioConstants.INSTANCE.AddANewRule() );
        add.setTitle( TestScenarioConstants.INSTANCE.AddANewRule() );
        add.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                showRulePopup(
                        box,
                        sc.getRules() );
            }
        } );

        final Image remove = Images.INSTANCE.Trash();
        remove.setAltText( TestScenarioConstants.INSTANCE.RemoveSelectedRule() );
        remove.setTitle( TestScenarioConstants.INSTANCE.RemoveSelectedRule() );
        remove.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( box.getSelectedIndex() == -1 ) {
                    Window.alert( TestScenarioConstants.INSTANCE.PleaseChooseARuleToRemove() );
                } else {
                    String r = box.getItemText( box.getSelectedIndex() );
                    sc.getRules().remove( r );
                    box.removeItem( box.getSelectedIndex() );
                }
            }
        } );
        VerticalPanel actions = new VerticalPanel();
        actions.add( add );
        actions.add( remove );

        final ListBox drop = new ListBox();
        drop.addItem( TestScenarioConstants.INSTANCE.AllowTheseRulesToFire(),
                      "inc" ); //NON-NLS
        drop.addItem( TestScenarioConstants.INSTANCE.PreventTheseRulesFromFiring(),
                      "exc" ); //NON-NLS
        drop.addItem( TestScenarioConstants.INSTANCE.AllRulesMayFire() );
        drop.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                String s = drop.getValue( drop.getSelectedIndex() );
                if ( s.equals( "inc" ) ) { //NON-NLS
                    sc.setInclusive( true );
                    add.setVisible( true );
                    remove.setVisible( true );
                    box.setVisible( true );
                } else if ( s.equals( "exc" ) ) { //NON-NLS
                    sc.setInclusive( false );
                    add.setVisible( true );
                    remove.setVisible( true );
                    box.setVisible( true );
                } else {
                    sc.getRules().clear();
                    box.clear();
                    box.setVisible( false );
                    add.setVisible( false );
                    remove.setVisible( false );
                }
            }
        } );

        if ( sc.getRules().size() > 0 ) {
            drop.setSelectedIndex( ( sc.isInclusive() ) ? 0 : 1 );
        } else {
            drop.setSelectedIndex( 2 );
            box.setVisible( false );
            add.setVisible( false );
            remove.setVisible( false );
        }

        filter.add( drop );
        filter.add( box );
        filter.add( actions );

        initWidget( filter );
    }

    private void showRulePopup( final ListBox box,
                                final List<String> filterList ) {
        final FormStylePopup pop = new FormStylePopup( TestScenarioAltedImages.INSTANCE.RuleAsset(),
                                                       TestScenarioConstants.INSTANCE.SelectRule() );

        Widget ruleSelector = scenarioWidgetComponentCreator.getRuleSelectionWidget(
                new RuleSelectionEvent() {
                    public void ruleSelected( String r ) {
                        filterList.add( r );
                        box.addItem( r );
                        pop.hide();

                    }
                } );

        pop.addRow( ruleSelector );

        pop.show();

    }

}
