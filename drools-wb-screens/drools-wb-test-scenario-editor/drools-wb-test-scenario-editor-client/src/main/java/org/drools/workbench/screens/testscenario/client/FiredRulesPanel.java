/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class FiredRulesPanel extends HorizontalPanel {

    private final ExecutionTrace executionTrace;

    public FiredRulesPanel( final ExecutionTrace executionTrace ) {
        this.executionTrace = executionTrace;

        add( createText() );
        add( createShowButton() );
    }

    private HTML createText() {
        return new HTML( "<i><small>" + TestScenarioConstants.INSTANCE.property0RulesFiredIn1Ms(
                executionTrace.getNumberOfRulesFired(), executionTrace.getExecutionTimeResult() ) + "</small></i>" );
    }

    private Button createShowButton() {
        final Button show = new Button( TestScenarioConstants.INSTANCE.ShowRulesFired() );
        show.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                ListBox rules = new ListBox( true );
                for ( String ruleName : executionTrace.getRulesFired() ) {
                    rules.addItem( ruleName );
                }
                add( new SmallLabel( "&nbsp:" + TestScenarioConstants.INSTANCE.RulesFired() ) );
                add( rules );
                show.setVisible( false );
            }
        } );

        return show;
    }
}
