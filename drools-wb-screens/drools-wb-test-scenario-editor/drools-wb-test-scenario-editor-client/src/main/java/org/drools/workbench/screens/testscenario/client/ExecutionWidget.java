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

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioImages;
import org.uberfire.client.common.SmallLabel;
import org.uberfire.client.common.popups.errors.ErrorPopup;

public class ExecutionWidget extends Composite {

    private final ExecutionTrace executionTrace;

    public ExecutionWidget( final ExecutionTrace executionTrace,
                            final boolean showResults ) {

        this.executionTrace = executionTrace;

        final HorizontalPanel simulDatePanel = simulDate();
        simulDatePanel.setVisible( isScenarioSimulatedDateSet() );

        final ListBox choice = new ListBox();

        choice.addItem( TestScenarioConstants.INSTANCE.UseRealDateAndTime() );
        choice.addItem( TestScenarioConstants.INSTANCE.UseASimulatedDateAndTime() );
        choice.setSelectedIndex( ( executionTrace.getScenarioSimulatedDate() == null ) ? 0 : 1 );
        choice.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                if ( choice.getSelectedIndex() == 0 ) {
                    simulDatePanel.setVisible( false );
                    executionTrace.setScenarioSimulatedDate( null );
                } else {
                    simulDatePanel.setVisible( true );
                }
            }
        } );

        HorizontalPanel layout = new HorizontalPanel();
        layout.add( new Image( TestScenarioImages.INSTANCE.executionTrace() ) );
        layout.add( choice );
        layout.add( simulDatePanel );

        if ( showResults && isResultNotNullAndHaveRulesFired() ) {
            VerticalPanel replacingLayout = new VerticalPanel();

            replacingLayout.add( new FiredRulesPanel( executionTrace ) );
            replacingLayout.add( layout );
            initWidget( replacingLayout );
        } else {
            initWidget( layout );
        }
    }

    private boolean isResultNotNullAndHaveRulesFired() {
        return executionTrace.getExecutionTimeResult() != null && executionTrace.getNumberOfRulesFired() != null;
    }

    private boolean isScenarioSimulatedDateSet() {
        return executionTrace.getScenarioSimulatedDate() != null;
    }

    private HorizontalPanel simulDate() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        final String format = "yyyy-MM-dd HH:mm"; //NON-NLS
        final TextBox textBox = new TextBox();
        if ( executionTrace.getScenarioSimulatedDate() == null ) {
            textBox.setText( "<" + format + ">" );
        } else {
            textBox.setText( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( executionTrace.getScenarioSimulatedDate() ) );
        }
        final SmallLabel dateHint = new SmallLabel();
        textBox.addKeyUpHandler( new KeyUpHandler() {

            public void onKeyUp( KeyUpEvent event ) {
                try {
                    String exampleDate = DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( new Date() );
                    String suggestedDate = textBox.getText() + exampleDate.substring( textBox.getText().length() );
                    Date d = DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).parse( suggestedDate );
                    dateHint.setText( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( d ) );
                } catch ( Exception e ) {
                    dateHint.setText( "..." );
                }
            }
        } );

        textBox.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                if ( textBox.getText().trim().equals( "" ) ) {
                    textBox.setText( TestScenarioConstants.INSTANCE.currentDateAndTime() );
                } else {
                    try {
                        //Date d1 = new Date();
                        Date d = DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).parse( textBox.getText() );
                        executionTrace.setScenarioSimulatedDate( d );
                        textBox.setText( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( d ) );
                        dateHint.setText( "" );
                    } catch ( Exception e ) {
                        ErrorPopup.showMessage( TestScenarioConstants.INSTANCE.BadDateFormatPleaseTryAgainTryTheFormatOf0( format ) );
                    }
                }
            }
        } );
        horizontalPanel.add( textBox );
        horizontalPanel.add( dateHint );
        return horizontalPanel;
    }

}
