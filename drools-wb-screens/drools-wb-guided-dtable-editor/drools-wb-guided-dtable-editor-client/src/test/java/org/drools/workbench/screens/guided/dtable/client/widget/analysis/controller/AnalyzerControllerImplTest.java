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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.controller;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DecisionTableAnalyzer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class AnalyzerControllerImplTest {

    @Mock
    DecisionTableAnalyzer analyzer;

    @Mock
    EventBus eventBus;

    private AnalyzerControllerImpl controller;

    @Before
    public void setUp() throws Exception {
        controller = new AnalyzerControllerImpl( analyzer,
                                                 eventBus );
    }

    @Test
    public void areHandlersAreSetUp() throws Exception {
        verify( eventBus ).addHandler( ValidateEvent.TYPE,
                                       controller );
        verify( eventBus ).addHandler( DeleteRowEvent.TYPE,
                                       controller );
        verify( eventBus ).addHandler( AfterColumnDeleted.TYPE,
                                       controller );
        verify( eventBus ).addHandler( UpdateColumnDataEvent.TYPE,
                                       controller );
        verify( eventBus ).addHandler( AppendRowEvent.TYPE,
                                       controller );
        verify( eventBus ).addHandler( InsertRowEvent.TYPE,
                                       controller );
        verify( eventBus ).addHandler( AfterColumnInserted.TYPE,
                                       controller );

    }

    @Test
    public void start() throws Exception {
        controller.initialiseAnalysis();
        verify( analyzer ).start();
    }

    @Test
    public void terminate() throws Exception {
        controller.terminateAnalysis();
        verify( analyzer ).terminate();
    }

    @Test
    public void analyze() throws Exception {
        final ArrayList<Coordinate> updates = new ArrayList<>();
        controller.onValidate( new ValidateEvent( updates ) );
        verify( analyzer ).analyze( updates );
    }

    @Test
    public void deleteColumns() throws Exception {
        controller.onAfterDeletedColumn( new AfterColumnDeleted( 1,
                                                                 2 ) );
        verify( analyzer ).deleteColumns( 1,
                                          2 );
    }

    @Test
    public void appendRow() throws Exception {
        controller.onAppendRow( new AppendRowEvent() );
        verify( analyzer ).appendRow();
    }

    @Test
    public void deleteRow() throws Exception {
        controller.onDeleteRow( new DeleteRowEvent( 10 ) );
        verify( analyzer ).deleteRow( 10 );
    }

    @Test
    public void insertRow() throws Exception {
        controller.onInsertRow( new InsertRowEvent( 10 ) );
        verify( analyzer ).insertRow( 10 );
    }

    @Test
    public void updateColumns() throws Exception {
        final ArrayList<CellValue<? extends Comparable<?>>> columnData = new ArrayList<>();
        columnData.add( mock( CellValue.class ) );
        controller.onUpdateColumnData( new UpdateColumnDataEvent( 10,
                                                                  columnData ) );
        verify( analyzer ).updateColumns( 1 );
    }

}