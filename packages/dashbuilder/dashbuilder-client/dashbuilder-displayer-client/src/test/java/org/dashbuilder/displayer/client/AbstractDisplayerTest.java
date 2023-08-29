/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.displayer.client;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.client.AbstractDataSetTest;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.impl.DataColumnImpl;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.formatter.ValueFormatterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractDisplayerTest extends AbstractDataSetTest {

    @Mock
    protected RendererManager rendererManager;

    @Mock
    protected RendererLibrary rendererLibrary;

    @Mock
    protected ValueFormatterRegistry formatterRegistry;

    protected DisplayerLocator displayerLocator;

    @Before
    public void init() throws Exception {
        super.init();

        displayerLocator = new DisplayerLocator(clientServices,
                clientDataSetManager,
                rendererManager,
                formatterRegistry);

        when(rendererManager.getRendererForDisplayer(any(DisplayerSettings.class))).thenReturn(rendererLibrary);

        doAnswer(mock ->  createNewDisplayer((DisplayerSettings) mock.getArguments()[0]))
                .when(rendererLibrary).lookupDisplayer(any(DisplayerSettings.class));

        doAnswer(mock -> createNewDisplayer((DisplayerSettings) mock.getArguments()[0]))
                .when(rendererLibrary).lookupDisplayer(any(DisplayerSettings.class));

        doAnswer(mock -> {
            List<Displayer> displayerList = (List<Displayer>) mock.getArguments()[0];
            for (Displayer displayer : displayerList) {
                displayer.draw();
            }
            return null;
        }).when(rendererLibrary).draw(anyListOf(Displayer.class));
    }

    public AbstractDisplayer createNewDisplayer(DisplayerSettings settings) {
        return initDisplayer(new DisplayerMock(mock(AbstractDisplayer.View.class), null), settings);
    }
    
    public AbstractDisplayer createNewDisplayer(DisplayerSettings settings, boolean ignoreError) {
        return initDisplayer(new DisplayerMock(mock(AbstractDisplayer.View.class), null, ignoreError), settings);
    }

    public <D extends AbstractDisplayer> D initDisplayer(D displayer, DisplayerSettings settings) {
        displayer.setEvaluator(new DisplayerEvaluatorMock());
        displayer.setFormatter(new DisplayerFormatterMock());
        displayer.addListener(new AbstractDisplayerListener() {
            public void onError(Displayer displayer, ClientRuntimeError error) {
                throw new RuntimeException(error.getRootCause());
            }
        });
        if (settings != null) {
            displayer.setDisplayerSettings(settings);
            displayer.setDataSetHandler(new DataSetHandlerImpl(clientServices, settings.getDataSetLookup()));
        }
        return displayer;
    }
    
    @Test
    public void callbackOnErrorTest() throws Exception {
        DataSetHandler dataSetHandler = mock(DataSetHandler.class);
        DisplayerSettings simpleSettings = DisplayerSettingsFactory.newTableSettings()
        .dataset(EXPENSES)
        .filterOn(true, false, true)
        .buildSettings();
        AbstractDisplayer simpleDisplayer = createNewDisplayer(simpleSettings, true);
        
        simpleDisplayer.listenerList = java.util.Collections.emptyList();
        simpleDisplayer.setDataSetHandler(dataSetHandler);
        simpleDisplayer.redraw();
        assertTrue(simpleDisplayer.isDrawn());
        
        doAnswer((InvocationOnMock invocation) -> {
            DataSetReadyCallback callback =  (DataSetReadyCallback) invocation.getArguments()[0];
            callback.onError(new ClientRuntimeError("test"));
            return null;
        }).when(dataSetHandler).lookupDataSet(any());
        
        simpleDisplayer.redraw();
        assertTrue(!simpleDisplayer.isDrawn());
    }
    
    @Test
    public void testFormatValue() throws Exception {
        DisplayerSettings simpleSettings = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .filterOn(true, false, true)
                .buildSettings();
        AbstractDisplayer simpleDisplayer = createNewDisplayer(simpleSettings, true);
    
        DataColumn dateColumn = new DataColumnImpl();
        dateColumn.setId("proceesedDate");
        dateColumn.setColumnType(ColumnType.DATE);
        Date date1= new SimpleDateFormat("dd/MM/yyyy")
                .parse("28/10/2020");
        dateColumn.setValues(Arrays.asList(date1));
        assertEquals("Oct 28, 2020 00:00", simpleDisplayer.formatValue(date1, dateColumn));
        
        DataColumn column = new DataColumnImpl();
        column.setId("slaCompliance");
        column.setColumnType(ColumnType.NUMBER);
        column.setValues(Arrays.asList(0));
        assertEquals("0.00", simpleDisplayer.formatValue(0, column));

        DataColumn numberColumn = new DataColumnImpl();
        numberColumn.setId("slaCompliance");
        numberColumn.setColumnType(ColumnType.NUMBER);
        numberColumn.setValues(Arrays.asList("0"));
        assertEquals("0.00", simpleDisplayer.formatValue("0", numberColumn));
    
        DataColumn emptyNumberColumn = new DataColumnImpl();
        emptyNumberColumn.setId("dataId");
        emptyNumberColumn.setColumnType(ColumnType.NUMBER);
        emptyNumberColumn.setValues(Arrays.asList(""));
        assertEquals("", simpleDisplayer.formatValue("", emptyNumberColumn));
        
        DataColumn textColumn = new DataColumnImpl();
        textColumn.setId("dataId");
        textColumn.setColumnType(ColumnType.TEXT);
        textColumn.setValues(Arrays.asList("test"));
        assertEquals("test", simpleDisplayer.formatValue("test", textColumn));
    }
}