/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.AbstractDataSetTest;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.formatter.ValueFormatterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
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
}