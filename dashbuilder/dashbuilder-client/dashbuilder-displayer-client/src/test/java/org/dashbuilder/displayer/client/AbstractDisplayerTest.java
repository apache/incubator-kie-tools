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

import java.util.List;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.client.AbstractDataSetTest;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.formatter.ValueFormatterRegistry;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public abstract class AbstractDisplayerTest extends AbstractDataSetTest {

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
}