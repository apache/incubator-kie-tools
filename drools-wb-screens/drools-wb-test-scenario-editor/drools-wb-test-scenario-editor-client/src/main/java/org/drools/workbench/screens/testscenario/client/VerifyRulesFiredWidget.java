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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioImages;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class VerifyRulesFiredWidget extends Composite {

    private Grid outer;
    private boolean showResults;

    /**
     * @param rfl List<VeryfyRuleFired>
     * @param scenario = the scenario to add/remove from
     */
    public VerifyRulesFiredWidget( final FixtureList rfl,
                                   final Scenario scenario,
                                   final boolean showResults ) {
        outer = new Grid( 2,
                          1 );
        this.showResults = showResults;
        outer.getCellFormatter().setStyleName( 0,
                                               0,
                                               "modeller-fact-TypeHeader" ); //NON-NLS
        outer.getCellFormatter().setAlignment( 0,
                                               0,
                                               HasHorizontalAlignment.ALIGN_CENTER,
                                               HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName( "modeller-fact-pattern-Widget" ); //NON-NLS

        outer.setWidget( 0,
                         0,
                         new SmallLabel( TestScenarioConstants.INSTANCE.ExpectRules() ) );
        initWidget( outer );

        FlexTable data = render( rfl,
                                 scenario );
        outer.setWidget( 1,
                         0,
                         data );
    }

    private FlexTable render( final FixtureList rfl,
                              final Scenario sc ) {
        FlexTable data = new FlexTable();

        for ( int i = 0; i < rfl.size(); i++ ) {
            final VerifyRuleFired v = (VerifyRuleFired) rfl.get( i );

            if ( showResults && v.getSuccessResult() != null ) {
                if ( !v.getSuccessResult().booleanValue() ) {
                    data.setWidget( i,
                                    0,
                                    new Image( CommonImages.INSTANCE.warning() ) );
                    data.setWidget( i,
                                    4,
                                    new HTML( TestScenarioConstants.INSTANCE.ActualResult( v.getActualResult().toString() ) ) );

                    data.getCellFormatter().addStyleName( i,
                                                          4,
                                                          "testErrorValue" ); //NON-NLS

                } else {
                    data.setWidget( i,
                                    0,
                                    new Image( TestScenarioImages.INSTANCE.testPassed() ) );
                }

            }
            data.setWidget( i,
                            1,
                            new SmallLabel( v.getRuleName() + ":" ) );
            data.getFlexCellFormatter().setAlignment( i,
                                                      1,
                                                      HasHorizontalAlignment.ALIGN_RIGHT,
                                                      HasVerticalAlignment.ALIGN_MIDDLE );

            final ListBox b = new ListBox();
            b.addItem( TestScenarioConstants.INSTANCE.firedAtLeastOnce(),
                       "y" );
            b.addItem( TestScenarioConstants.INSTANCE.didNotFire(),
                       "n" );
            b.addItem( TestScenarioConstants.INSTANCE.firedThisManyTimes(),
                       "e" );
            final TextBox num = new TextBox();
            num.setVisibleLength( 5 );

            if ( v.getExpectedFire() != null ) {
                b.setSelectedIndex( ( v.getExpectedFire().booleanValue() ) ? 0 : 1 );
                num.setVisible( false );
            } else {
                b.setSelectedIndex( 2 );
                String xc = ( v.getExpectedCount() != null ) ? "" + v.getExpectedCount().intValue() : "0";
                num.setText( xc );
            }

            b.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    String s = b.getValue( b.getSelectedIndex() );
                    if ( s.equals( "y" ) || s.equals( "n" ) ) {
                        num.setVisible( false );
                        v.setExpectedFire( ( s.equals( "y" ) ) ? Boolean.TRUE : Boolean.FALSE );
                        v.setExpectedCount( null );
                    } else {
                        num.setVisible( true );
                        v.setExpectedFire( null );
                        num.setText( "1" );
                        v.setExpectedCount( Integer.valueOf( 1 ) );
                    }
                }
            } );

            b.addItem( TestScenarioConstants.INSTANCE.ChooseDotDotDot() );

            num.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    v.setExpectedCount( Integer.valueOf( num.getText() ) );
                }
            } );

            HorizontalPanel h = new HorizontalPanel();
            h.add( b );
            h.add( num );
            data.setWidget( i,
                            2,
                            h );

            Image del = CommonAltedImages.INSTANCE.DeleteItemSmall();
            del.setAltText( TestScenarioConstants.INSTANCE.RemoveThisRuleExpectation() );
            del.setTitle( TestScenarioConstants.INSTANCE.RemoveThisRuleExpectation() );
            del.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent w ) {
                    if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveThisRuleExpectation() ) ) {
                        rfl.remove( v );
                        sc.removeFixture( v );
                        outer.setWidget( 1,
                                         0,
                                         render( rfl,
                                                 sc ) );
                    }
                }
            } );

            data.setWidget( i,
                            3,
                            del );

            //we only want numbers here...
            num.addKeyPressHandler( new KeyPressHandler() {
                public void onKeyPress( KeyPressEvent event ) {
                    if ( Character.isLetter( event.getCharCode() ) ) {
                        ( (TextBox) event.getSource() ).cancelKey();
                    }
                }
            } );
        }
        return data;
    }
}
