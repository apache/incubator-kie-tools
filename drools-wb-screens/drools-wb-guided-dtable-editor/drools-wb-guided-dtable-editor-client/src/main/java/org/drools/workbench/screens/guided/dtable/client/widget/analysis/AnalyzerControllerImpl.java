/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import com.google.gwt.event.shared.EventBus;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnDeleted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnInserted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.uberfire.commons.validation.PortablePreconditions;

public class AnalyzerControllerImpl
        implements AnalyzerController,
                   ValidateEvent.Handler,
                   DeleteRowEvent.Handler,
                   AfterColumnDeleted.Handler,
                   UpdateColumnDataEvent.Handler,
                   AppendRowEvent.Handler,
                   InsertRowEvent.Handler,
                   AfterColumnInserted.Handler {


    private final DecisionTableAnalyzer decisionTableAnalyzer;

    public AnalyzerControllerImpl( final DecisionTableAnalyzer decisionTableAnalyzer,
                                   final EventBus eventBus ) {
        this.decisionTableAnalyzer = PortablePreconditions.checkNotNull( "decisionTableAnalyzer", decisionTableAnalyzer );
        PortablePreconditions.checkNotNull( "eventBus", eventBus );

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

    @Override
    public void initialiseAnalysis() {
        decisionTableAnalyzer.start();
    }

    @Override
    public void terminateAnalysis() {
        decisionTableAnalyzer.stop();
    }

    @Override
    public void onValidate( final ValidateEvent event ) {
        decisionTableAnalyzer.analyze( event.getUpdates() );

    }

    @Override
    public void onAfterDeletedColumn( final AfterColumnDeleted event ) {
        decisionTableAnalyzer.deleteColumns( event.getFirstColumnIndex(),
                                             event.getNumberOfColumns() );
    }

    @Override
    public void onAfterColumnInserted( final AfterColumnInserted event ) {
        decisionTableAnalyzer.insertColumn( event.getIndex() );
    }

    @Override
    public void onAppendRow( final AppendRowEvent event ) {
        decisionTableAnalyzer.appendRow();
    }

    @Override
    public void onDeleteRow( final DeleteRowEvent event ) {
        decisionTableAnalyzer.deleteRow( event.getIndex() );
    }

    @Override
    public void onInsertRow( final InsertRowEvent event ) {
        decisionTableAnalyzer.insertRow( event.getIndex() );
    }

    @Override
    public void onUpdateColumnData( final UpdateColumnDataEvent event ) {
        decisionTableAnalyzer.updateColumns( event.getColumnData().size() );
    }
}
