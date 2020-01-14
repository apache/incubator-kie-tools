/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.verifier.reporting.client.controller;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.DecisionTableAnalyzer;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnalyzerControllerImplTest {

    @Mock
    DecisionTableAnalyzer analyzer;

    @Mock
    EventBus eventBus;

    private AnalyzerControllerImpl controller;

    @Before
    public void setUp() throws Exception {
        controller = new AnalyzerControllerImpl(analyzer,
                                                eventBus);
    }

    @Test
    public void doNotSetUpHandlersDuringConstruction() throws
            Exception {
        verify(eventBus,
               never()).addHandler(ValidateEvent.TYPE,
                                   controller);
        verify(eventBus,
               never()).addHandler(DeleteRowEvent.TYPE,
                                   controller);
        verify(eventBus,
               never()).addHandler(AfterColumnDeleted.TYPE,
                                   controller);
        verify(eventBus,
               never()).addHandler(UpdateColumnDataEvent.TYPE,
                                   controller);
        verify(eventBus,
               never()).addHandler(AppendRowEvent.TYPE,
                                   controller);
        verify(eventBus,
               never()).addHandler(InsertRowEvent.TYPE,
                                   controller);
        verify(eventBus,
               never()).addHandler(AfterColumnInserted.TYPE,
                                   controller);
    }

    @Test
    public void areHandlersAreSetUpOnInit() throws
            Exception {
        controller.initialiseAnalysis();

        verify(eventBus).addHandler(ValidateEvent.TYPE,
                                    controller);
        verify(eventBus).addHandler(DeleteRowEvent.TYPE,
                                    controller);
        verify(eventBus).addHandler(AfterColumnDeleted.TYPE,
                                    controller);
        verify(eventBus).addHandler(UpdateColumnDataEvent.TYPE,
                                    controller);
        verify(eventBus).addHandler(AppendRowEvent.TYPE,
                                    controller);
        verify(eventBus).addHandler(InsertRowEvent.TYPE,
                                    controller);
        verify(eventBus).addHandler(AfterColumnInserted.TYPE,
                                    controller);
    }

    @Test
    public void areHandlersTornDownOnTerminate() throws
            Exception {

        final HandlerRegistration validateEvent = mock(HandlerRegistration.class);
        when(eventBus.addHandler(ValidateEvent.TYPE,
                                 controller)).thenReturn(validateEvent);

        final HandlerRegistration deleteRowEvent = mock(HandlerRegistration.class);
        when(eventBus.addHandler(DeleteRowEvent.TYPE,
                                 controller)).thenReturn(deleteRowEvent);

        final HandlerRegistration afterColumnDeleted = mock(HandlerRegistration.class);
        when(eventBus.addHandler(AfterColumnDeleted.TYPE,
                                 controller)).thenReturn(afterColumnDeleted);

        final HandlerRegistration updateColumnDataEvent = mock(HandlerRegistration.class);
        when(eventBus.addHandler(UpdateColumnDataEvent.TYPE,
                                 controller)).thenReturn(updateColumnDataEvent);

        final HandlerRegistration appendRowEvent = mock(HandlerRegistration.class);
        when(eventBus.addHandler(AppendRowEvent.TYPE,
                                 controller)).thenReturn(appendRowEvent);

        final HandlerRegistration insertRowEvent = mock(HandlerRegistration.class);
        when(eventBus.addHandler(InsertRowEvent.TYPE,
                                 controller)).thenReturn(insertRowEvent);

        final HandlerRegistration afterColumnInserted = mock(HandlerRegistration.class);
        when(eventBus.addHandler(AfterColumnInserted.TYPE,
                                 controller)).thenReturn(afterColumnInserted);

        controller.initialiseAnalysis();
        controller.terminateAnalysis();

        verify(validateEvent).removeHandler();
        verify(deleteRowEvent).removeHandler();
        verify(afterColumnDeleted).removeHandler();
        verify(updateColumnDataEvent).removeHandler();
        verify(appendRowEvent).removeHandler();
        verify(insertRowEvent).removeHandler();
        verify(afterColumnDeleted).removeHandler();
    }

    @Test
    public void start() throws Exception {
        controller.initialiseAnalysis();
        verify(analyzer).activate();
    }

    @Test
    public void terminate() throws Exception {
        controller.terminateAnalysis();
        verify(analyzer).terminate();
    }

    @Test
    public void analyze() throws Exception {
        final ArrayList<Coordinate> updates = new ArrayList<>();
        controller.onValidate(new ValidateEvent(updates));
        verify(analyzer).analyze(updates);
    }

    @Test
    public void deleteColumns() throws Exception {
        controller.onAfterDeletedColumn(new AfterColumnDeleted(1,
                                                               2));
        verify(analyzer).deleteColumns(1,
                                       2);
    }

    @Test
    public void appendRow() throws Exception {
        controller.onAppendRow(new AppendRowEvent());
        verify(analyzer).appendRow();
    }

    @Test
    public void deleteRow() throws Exception {
        controller.onDeleteRow(new DeleteRowEvent(10));
        verify(analyzer).deleteRow(10);
    }

    @Test
    public void insertRow() throws Exception {
        controller.onInsertRow(new InsertRowEvent(10));
        verify(analyzer).insertRow(10);
    }

    @Test
    public void updateColumns() throws Exception {
        final ArrayList<CellValue<? extends Comparable<?>>> columnData = new ArrayList<>();
        columnData.add(mock(CellValue.class));
        controller.onUpdateColumnData(new UpdateColumnDataEvent(10,
                                                                columnData));
        verify(analyzer).updateColumns(1);
    }
}