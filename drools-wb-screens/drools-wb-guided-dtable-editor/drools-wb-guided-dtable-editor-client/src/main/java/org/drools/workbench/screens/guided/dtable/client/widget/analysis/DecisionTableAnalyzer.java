/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnDeleted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnInserted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

public class DecisionTableAnalyzer
        implements ValidateEvent.Handler,
                   DeleteRowEvent.Handler,
                   AfterColumnDeleted.Handler,
                   UpdateColumnDataEvent.Handler,
                   AppendRowEvent.Handler,
                   InsertRowEvent.Handler,
                   AfterColumnInserted.Handler {

    private final CheckRunner                  checkRunner  = getCheckRunner();
    private final ParameterizedCommand<Status> onStatus     = getOnStatusCommand();
    private final Command                      onCompletion = getOnCompletionCommand();

    private final PlaceRequest          place;
    private final RuleInspectorCache    cache;
    private final GuidedDecisionTable52 model;
    private final EventManager eventManager = new EventManager();

    public DecisionTableAnalyzer( final PlaceRequest place,
                                  final AsyncPackageDataModelOracle oracle,
                                  final GuidedDecisionTable52 model,
                                  final EventBus eventBus ) {
        this.place = PortablePreconditions.checkNotNull( "place", place );
        this.model = PortablePreconditions.checkNotNull( "model", model );

        cache = new RuleInspectorCache( oracle,
                                        model,
                                        checkRunner );

        eventBus.addHandler( ValidateEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( AfterColumnDeleted.TYPE,
                             this );
        eventBus.addHandler( UpdateColumnDataEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( AfterColumnInserted.TYPE,
                             this );
    }

    //Override for tests where we do not want to perform checks using a Scheduled RepeatingCommand
    protected CheckRunner getCheckRunner() {
        return new CheckRunner();
    }

    //Override for tests where we do not want to provide feedback
    protected ParameterizedCommand<Status> getOnStatusCommand() {
        return new ParameterizedCommand<Status>() {

            @Override
            public void execute( final Status status ) {
                Window.setTitle( AnalysisConstants.INSTANCE.AnalysingChecks0To1Of2( status.getStart(),
                                                                                    status.getEnd(),
                                                                                    status.getTotalCheckCount() ) );
            }
        };
    }

    //Override for tests where we do not want to provide feedback
    protected Command getOnCompletionCommand() {
        return new Command() {

            @Override
            public void execute() {
                Window.setTitle( "" );
                sendReport( makeAnalysisReport() );
            }
        };
    }

    private void resetChecks() {
        for ( final RuleInspector ruleInspector : cache.all() ) {
            checkRunner.add( ruleInspector );
        }
    }

    private void analyze() {
        this.checkRunner.run( onStatus,
                              onCompletion );
    }

    protected AnalysisReport makeAnalysisReport() {
        final AnalysisReport report = new AnalysisReport( place );
        final Set<Issue> unorderedIssues = new HashSet<Issue>();

        for ( final RuleInspector ruleInspector : cache.all() ) {
            for ( final Check check : checkRunner.get( ruleInspector ) ) {
                if ( check.hasIssues() ) {
                    unorderedIssues.add( check.getIssue() );
                }
            }
        }

        report.setIssues( unorderedIssues );

        return report;
    }

    protected void sendReport( final AnalysisReport report ) {
        IOC.getBeanManager().lookupBean( AnalysisReportScreen.class ).getInstance().showReport( report );
    }

    @Override
    public void onValidate( final ValidateEvent event ) {
        if ( event.getUpdates().isEmpty() || checkRunner.isEmpty() ) {
            resetChecks();
        } else {
            cache.updateRuleInspectors( event.getUpdates() );
        }

        analyze();
    }

    @Override
    public void onAfterDeletedColumn( final AfterColumnDeleted event ) {
        cache.deleteColumns( event.getFirstColumnIndex(),
                             event.getNumberOfColumns() );
        resetChecks();
        analyze();
    }

    @Override
    public void onAfterColumnInserted( final AfterColumnInserted event ) {
        cache.newColumn( event.getIndex() );
        resetChecks();
        analyze();
    }

    @Override
    public void onUpdateColumnData( final UpdateColumnDataEvent event ) {
        if ( hasTheRowCountIncreased( event ) ) {
            addRow( eventManager.getNewIndex() );
            analyze();

        } else if ( hasTheRowCountDecreased( event ) ) {
            final RuleInspector removed = cache.removeRow( eventManager.rowDeleted );
            checkRunner.remove( removed );
            analyze();
        }

        eventManager.clear();
    }

    private boolean hasTheRowCountDecreased( final UpdateColumnDataEvent event ) {
        return cache.all().size() > event.getColumnData().size();
    }

    private boolean hasTheRowCountIncreased( final UpdateColumnDataEvent event ) {
        return cache.all().size() < event.getColumnData().size();
    }

    private void addRow( final int index ) {
        final RuleInspector ruleInspector = cache.addRow( index );
        checkRunner.add( ruleInspector );
    }

    @Override
    public void onDeleteRow( final DeleteRowEvent event ) {
        checkRunner.cancelExistingAnalysis();
        eventManager.rowDeleted = event.getIndex();
    }

    @Override
    public void onAppendRow( final AppendRowEvent event ) {
        checkRunner.cancelExistingAnalysis();
        eventManager.rowAppended = true;
    }

    @Override
    public void onInsertRow( final InsertRowEvent event ) {
        checkRunner.cancelExistingAnalysis();
        eventManager.rowInserted = event.getIndex();
    }

    public void onFocus() {
        if ( checkRunner.isEmpty() ) {
            resetChecks();
            analyze();
        } else {
            sendReport( makeAnalysisReport() );
        }
    }

    public void onClose() {
        checkRunner.cancelExistingAnalysis();
    }

    class EventManager {

        boolean rowAppended = false;
        Integer rowInserted = null;
        Integer rowDeleted = null;

        public void clear() {

            rowAppended = false;
            rowInserted = null;
            rowDeleted = null;
        }

        int getNewIndex() {
            if ( eventManager.rowAppended ) {
                return model.getData().size() - 1;
            } else if ( eventManager.rowInserted != null ) {
                return eventManager.rowInserted;
            }

            throw new IllegalStateException( "There are no active updates" );
        }
    }
}
