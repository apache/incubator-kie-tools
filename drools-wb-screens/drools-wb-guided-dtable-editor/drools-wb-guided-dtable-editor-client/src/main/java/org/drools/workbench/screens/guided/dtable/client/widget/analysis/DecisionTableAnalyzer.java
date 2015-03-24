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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RowInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Checks;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
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
    private final EventBus eventBus;
    private final Checks checks = new Checks();
    private final EvenManager eventManager = new EvenManager();

    public DecisionTableAnalyzer( AsyncPackageDataModelOracle oracle,
                                  GuidedDecisionTable52 model,
                                  EventBus eventBus ) {
        this.model = model;
        this.eventBus = eventBus;

        cache = new RowInspectorCache( oracle,
                                       model,
                                       new UpdateHandler() {
                                           @Override
                                           public void updateRow( RowInspector oldRowInspector,
                                                                  RowInspector newRowInspector ) {
                                               checks.update( oldRowInspector, newRowInspector );
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

    private List<Analysis> analyze() {

        final List<Analysis> analysisData = new ArrayList<Analysis>();

        this.checks.run();

        for ( RowInspector rowInspector : cache.all() ) {
            Analysis analysis = new Analysis();
            for ( Check check : checks.get( rowInspector ) ) {
                if ( check.hasIssues() ) {
                    analysis.addRowMessage( check.getIssue() );
                }
            }
            analysisData.add( analysis );
        }

        return analysisData;
    }

    private void updateAnalysisColumn() {
        model.getAnalysisData().clear();
        model.getAnalysisData().addAll( analyze() );

        eventBus.fireEvent( new UpdateColumnDataEvent( getAnalysisColumnIndex(),
                                                       getAnalysisColumnData() ) );

    }

    // Retrieve the data for the analysis column
    private List<CellValue<? extends Comparable<?>>> getAnalysisColumnData() {
        List<CellValue<? extends Comparable<?>>> columnData = new ArrayList<CellValue<? extends Comparable<?>>>();
        List<Analysis> analysisData = model.getAnalysisData();
        for ( int i = 0; i < analysisData.size(); i++ ) {
            Analysis analysis = analysisData.get( i );
            CellValue<Analysis> cell = new CellValue<Analysis>( analysis );
            columnData.add( cell );
        }
        return columnData;
    }

    private int getAnalysisColumnIndex() {
        return model.getExpandedColumns().indexOf( model.getAnalysisCol() );
    }

    @Override
    public void onValidate( ValidateEvent event ) {

        if ( event.getUpdates().isEmpty() || checks.isEmpty() ) {
            resetChecks();
        } else {
            cache.updateRowInspectors( event.getUpdates().keySet(),
                                       model.getData() );
        }

        updateAnalysisColumn();
    }

    @Override
    public void onAfterDeletedColumn( AfterColumnDeleted event ) {

        cache.reset();

        resetChecks();

        updateAnalysisColumn();
    }

    @Override
    public void onAfterColumnInserted( AfterColumnInserted event ) {

        cache.reset();

        resetChecks();

        updateAnalysisColumn();
    }

    @Override
    public void onUpdateColumnData( UpdateColumnDataEvent event ) {

        if ( hasTheRowCountIncreased( event ) ) {

            addRow( eventManager.getNewIndex() );
            updateAnalysisColumn();

        } else if ( hasTheRowCountDecreased( event ) ) {

            RowInspector removed = cache.removeRow( eventManager.rowDeleted );
            checks.remove( removed );

            updateAnalysisColumn();

        }

        eventManager.clear();
    }

    private boolean hasTheRowCountDecreased( UpdateColumnDataEvent event ) {
        return cache.all().size() > event.getColumnData().size();
    }

    private boolean hasTheRowCountIncreased( UpdateColumnDataEvent event ) {
        return cache.all().size() < event.getColumnData().size();
    }

    private void addRow( int index ) {
        RowInspector rowInspector = cache.addRow( index,
                                                  model.getData().get( index ) );
        checks.add( rowInspector );
    }

    @Override
    public void onDeleteRow( DeleteRowEvent event ) {
        eventManager.rowDeleted = event.getIndex();
    }

    @Override
    public void onAppendRow( AppendRowEvent event ) {
        eventManager.rowAppended = true;
    }

    @Override
    public void onInsertRow( InsertRowEvent event ) {
        eventManager.rowInserted = event.getIndex();
    }

    class EvenManager {

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

            throw new IllegalStateException( "There is no active updates" );
        }
    }
}
