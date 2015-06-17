/*
 * Copyright 2011 JBoss Inc
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

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RowInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Checks;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnDeleted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnInserted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;

public class DecisionTableAnalyzer
        implements ValidateEvent.Handler,
                   DeleteRowEvent.Handler,
                   AfterColumnDeleted.Handler,
                   UpdateColumnDataEvent.Handler,
                   AppendRowEvent.Handler,
                   InsertRowEvent.Handler,
                   AfterColumnInserted.Handler {

    private final RowInspectorCache cache;
    private final GuidedDecisionTable52 model;
    private final Checks checks = new Checks();
    private final EventManager eventManager = new EventManager();

    public DecisionTableAnalyzer( final AsyncPackageDataModelOracle oracle,
                                  final GuidedDecisionTable52 model,
                                  final EventBus eventBus ) {
        this.model = model;

        cache = new RowInspectorCache( oracle,
                                       model,
                                       new UpdateHandler() {
                                           @Override
                                           public void updateRow( final RowInspector oldRowInspector,
                                                                  final RowInspector newRowInspector ) {
                                               checks.update( oldRowInspector,
                                                              newRowInspector );
                                           }
                                       } );

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

    private void resetChecks() {
        for ( RowInspector rowInspector : cache.all() ) {
            checks.add( rowInspector );
        }
    }

    private void analyze() {

        final AnalysisReport report = new AnalysisReport();

        this.checks.run();

        for ( RowInspector rowInspector : cache.all() ) {
            for ( Check check : checks.get( rowInspector ) ) {
                if ( check.hasIssues() ) {
                    report.addIssue( check.getIssue() );
                }
            }
        }

        sendReport( report );
    }

    protected void sendReport( final AnalysisReport report ) {
        IOC.getBeanManager().lookupBean( AnalysisReportScreen.class ).getInstance().showReport( report );
    }

    @Override
    public void onValidate( final ValidateEvent event ) {

        if ( event.getUpdates().isEmpty() || checks.isEmpty() ) {
            resetChecks();
        } else {
            cache.updateRowInspectors( event.getUpdates().keySet(),
                                       model.getData() );
        }

        analyze();
    }

    @Override
    public void onAfterDeletedColumn( final AfterColumnDeleted event ) {

        cache.reset();

        resetChecks();

        analyze();
    }

    @Override
    public void onAfterColumnInserted( final AfterColumnInserted event ) {

        cache.reset();

        resetChecks();

        analyze();
    }

    @Override
    public void onUpdateColumnData( final UpdateColumnDataEvent event ) {

        if ( hasTheRowCountIncreased( event ) ) {

            addRow( eventManager.getNewIndex() );
            analyze();

        } else if ( hasTheRowCountDecreased( event ) ) {

            RowInspector removed = cache.removeRow( eventManager.rowDeleted );
            checks.remove( removed );

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
        RowInspector rowInspector = cache.addRow( index,
                                                  model.getData().get( index ) );
        checks.add(rowInspector);
    }

    @Override
    public void onDeleteRow( final DeleteRowEvent event ) {
        eventManager.rowDeleted = event.getIndex();
    }

    @Override
    public void onAppendRow( final AppendRowEvent event ) {
        eventManager.rowAppended = true;
    }

    @Override
    public void onInsertRow( final InsertRowEvent event ) {
        eventManager.rowInserted = event.getIndex();
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
