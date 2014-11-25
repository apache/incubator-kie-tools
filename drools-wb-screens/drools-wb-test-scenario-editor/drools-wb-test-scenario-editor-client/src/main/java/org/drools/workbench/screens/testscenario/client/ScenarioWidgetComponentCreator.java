/*
 * Copyright 2010 JBoss Inc
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

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.CallFixtureMap;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.resources.ImageResources;
import org.uberfire.backend.vfs.Path;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class ScenarioWidgetComponentCreator {

    private final ScenarioParentWidget scenarioWidget;
    private final AsyncPackageDataModelOracle oracle;
    private final Caller<RuleNamesService> ruleNamesService;
    private final Path path;

    private boolean hasRules;
    private HandlerRegistration availableRulesHandlerRegistration;

    private boolean showResults;
    private Scenario scenario;

    protected ScenarioWidgetComponentCreator( final ScenarioParentWidget scenarioWidget,
                                              final Path path,
                                              final AsyncPackageDataModelOracle oracle,
                                              final Scenario scenario,
                                              final Caller<RuleNamesService> ruleNamesService ) {
        this.scenarioWidget = scenarioWidget;
        this.oracle = oracle;
        this.scenario = scenario;
        this.ruleNamesService = ruleNamesService;
        this.path = path;

        this.ruleNamesService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( final Collection<String> ruleNames ) {
                hasRules = !( ruleNames == null || ruleNames.isEmpty() );
            }
        } ).getRuleNames( path,
                          scenario.getPackageName() );
    }

    protected GlobalPanel createGlobalPanel( final ScenarioHelper scenarioHelper,
                                             final ExecutionTrace previousExecutionTrace ) {
        return new GlobalPanel( scenarioHelper.lumpyMapGlobals( getScenario().getGlobals() ),
                                getScenario(),
                                previousExecutionTrace,
                                this.oracle,
                                this.scenarioWidget );
    }

    protected HorizontalPanel createHorizontalPanel() {
        HorizontalPanel h = new HorizontalPanel();
        h.add( new GlobalButton( getScenario(), this.scenarioWidget, oracle ) );
        h.add( new SmallLabel( TestScenarioConstants.INSTANCE.globals() ) );
        return h;
    }

    protected SmallLabel createSmallLabel() {
        return new SmallLabel( TestScenarioConstants.INSTANCE.configuration() );
    }

    protected ConfigWidget createConfigWidget() {
        return new ConfigWidget( getScenario(), this );
    }

    protected AddExecuteButton createAddExecuteButton() {
        return new AddExecuteButton( getScenario(), this.scenarioWidget );
    }

    protected VerifyRulesFiredWidget createVerifyRulesFiredWidget( final FixtureList fixturesList ) {
        return new VerifyRulesFiredWidget( fixturesList,
                                           getScenario(),
                                           isShowResults() );
    }

    protected VerifyFactsPanel createVerifyFactsPanel( final List<ExecutionTrace> listExecutionTrace,
                                                       final int executionTraceLine,
                                                       final FixtureList fixturesList ) {
        return new VerifyFactsPanel( fixturesList,
                                     listExecutionTrace.get( executionTraceLine ),
                                     getScenario(),
                                     this.scenarioWidget,
                                     isShowResults(),
                                     oracle );
    }

    protected CallMethodLabelButton createCallMethodLabelButton( final List<ExecutionTrace> listExecutionTrace,
                                                                 final int executionTraceLine,
                                                                 final ExecutionTrace previousExecutionTrace ) {
        return new CallMethodLabelButton( previousExecutionTrace,
                                          getScenario(),
                                          listExecutionTrace.get( executionTraceLine ),
                                          this.scenarioWidget,
                                          oracle );
    }

    protected GivenLabelButton createGivenLabelButton( final List<ExecutionTrace> listExecutionTrace,
                                                       final int executionTraceLine,
                                                       final ExecutionTrace previousExecutionTrace ) {
        return new GivenLabelButton( previousExecutionTrace,
                                     getScenario(),
                                     listExecutionTrace.get( executionTraceLine ),
                                     this.scenarioWidget,
                                     oracle );
    }

    protected ExecutionWidget createExecutionWidget( final ExecutionTrace currentExecutionTrace ) {
        return new ExecutionWidget( currentExecutionTrace,
                                    isShowResults() );
    }

    protected ExpectPanel createExpectPanel( final ExecutionTrace currentExecutionTrace ) {
        return new ExpectPanel( currentExecutionTrace,
                                getScenario(), this.scenarioWidget,
                                this,
                                oracle );
    }

    protected FlexTable createFlexTable() {
        FlexTable editorLayout = new FlexTable();
        editorLayout.clear();
        editorLayout.setWidth( "100%" );
        editorLayout.setStyleName( "model-builder-Background" );
        return editorLayout;
    }

    protected Widget createGivenPanel( final List<ExecutionTrace> listExecutionTrace,
                                       final int executionTraceLine,
                                       final FixturesMap given ) {

        if ( given.size() > 0 ) {
            return new GivenPanel( listExecutionTrace,
                                   executionTraceLine,
                                   given,
                                   getScenario(),
                                   this.oracle,
                                   this.scenarioWidget );

        } else {
            return new HTML( "<i><small>" + TestScenarioConstants.INSTANCE.AddInputDataAndExpectationsHere() + "</small></i>" );
        }
    }

    protected Widget createCallMethodOnGivenPanel( final List<ExecutionTrace> listExecutionTrace,
                                                   final int executionTraceLine,
                                                   final CallFixtureMap given ) {

        if ( given.size() > 0 ) {
            return new CallMethodOnGivenPanel( listExecutionTrace,
                                               executionTraceLine,
                                               given,
                                               getScenario(),
                                               this.scenarioWidget,
                                               oracle );

        } else {
            return new HTML( "<i><small>" + TestScenarioConstants.INSTANCE.AddInputDataAndExpectationsHere() + "</small></i>" );
        }
    }

    protected TextBox createRuleNameTextBox() {
        final TextBox ruleNameTextBox = new TextBox();
        ruleNameTextBox.setTitle( TestScenarioConstants.INSTANCE.EnterRuleNameScenario() );
        return ruleNameTextBox;
    }

    protected Button createOkButton( final RuleSelectionEvent selected,
                                     final TextBox ruleNameTextBox ) {
        Button ok = new Button( TestScenarioConstants.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                selected.ruleSelected( ruleNameTextBox.getText() );
            }
        } );
        return ok;
    }

    protected ChangeHandler createRuleChangeHandler( final TextBox ruleNameTextBox,
                                                     final ListBox availableRulesBox ) {
        final ChangeHandler ruleSelectionCL = new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                ruleNameTextBox.setText( availableRulesBox.getItemText( availableRulesBox.getSelectedIndex() ) );
            }
        };
        return ruleSelectionCL;
    }

    protected ListBox createAvailableRulesBox() {
        final ListBox availableRulesBox = new ListBox();
        availableRulesBox.addItem( TestScenarioConstants.INSTANCE.pleaseChoose1() );

        ruleNamesService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> ruleNames ) {
                if ( ruleNames == null || ruleNames.isEmpty() ) {
                    return;
                }
                for ( final String ruleName : ruleNames ) {
                    availableRulesBox.addItem( ruleName );
                }
            }
        } ).getRuleNames( path, scenario.getPackageName() );

        return availableRulesBox;
    }

    public void setShowResults( final boolean showResults ) {
        this.showResults = showResults;
    }

    public boolean isShowResults() {
        return this.showResults;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario( final Scenario scenario ) {
        this.scenario = scenario;
    }

    public Widget getRuleSelectionWidget( final RuleSelectionEvent selected ) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        final TextBox ruleNameTextBox = createRuleNameTextBox();
        horizontalPanel.add( ruleNameTextBox );
        if ( hasRules ) {
            final ListBox availableRulesBox = createAvailableRulesBox();
            availableRulesBox.setSelectedIndex( 0 );
            if ( availableRulesHandlerRegistration != null ) {
                availableRulesHandlerRegistration.removeHandler();
            }
            final ChangeHandler ruleSelectionCL = createRuleChangeHandler( ruleNameTextBox,
                                                                           availableRulesBox );

            availableRulesHandlerRegistration = availableRulesBox.addChangeHandler( ruleSelectionCL );
            horizontalPanel.add( availableRulesBox );

        } else {
            final Button showList = new Button( TestScenarioConstants.INSTANCE.showListButton() );
            horizontalPanel.add( showList );
            showList.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    horizontalPanel.remove( showList );
                    final Image busy = new Image( ImageResources.INSTANCE.searching() );
                    final Label loading = new SmallLabel( TestScenarioConstants.INSTANCE.loadingList1() );
                    horizontalPanel.add( busy );
                    horizontalPanel.add( loading );

                    final ListBox availableRulesBox = createAvailableRulesBox();

                    final ChangeHandler ruleSelectionCL = new ChangeHandler() {
                        public void onChange( ChangeEvent event ) {
                            ruleNameTextBox.setText( availableRulesBox.getItemText( availableRulesBox.getSelectedIndex() ) );
                        }
                    };
                    availableRulesHandlerRegistration = availableRulesBox.addChangeHandler( ruleSelectionCL );
                    availableRulesBox.setSelectedIndex( 0 );
                    horizontalPanel.add( availableRulesBox );
                    horizontalPanel.remove( busy );
                    horizontalPanel.remove( loading );

                }
            } );

        }

        Button ok = createOkButton( selected,
                                    ruleNameTextBox );
        horizontalPanel.add( ok );
        return horizontalPanel;
    }
}
