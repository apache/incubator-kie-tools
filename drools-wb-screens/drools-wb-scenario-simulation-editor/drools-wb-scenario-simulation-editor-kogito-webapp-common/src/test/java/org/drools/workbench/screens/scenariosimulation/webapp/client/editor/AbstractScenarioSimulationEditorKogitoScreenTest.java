/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.bridge.Notification;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.AbstractScenarioSimulationEditorKogitoScreen.TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractScenarioSimulationEditorKogitoScreenTest {

    @Mock
    private ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapperMock;
    @Mock
    private PlaceRequest placeRequestMock;

    private AbstractScenarioSimulationEditorKogitoScreen abstractScenarioSimulationEditorKogitoScreenSpy;

    @Before
    public void setup() {
        abstractScenarioSimulationEditorKogitoScreenSpy = spy(new AbstractScenarioSimulationEditorKogitoScreen() {
            @Override
            public String getIdentifier() {
                return null;
            }

            @Override
            public Promise<String> getPreview() {
                return null;
            }

            @Override
            public Promise<List<Notification>> validate() {
                return null;
            }

            {
                scenarioSimulationEditorKogitoWrapper = scenarioSimulationEditorKogitoWrapperMock;
            }
        });
    }

    @Test
    public void onStartup() {
        abstractScenarioSimulationEditorKogitoScreenSpy.onStartup(placeRequestMock);
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).onStartup(eq(placeRequestMock));
    }

    @Test
    public void mayClose() {
        abstractScenarioSimulationEditorKogitoScreenSpy.mayClose();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).mayClose();
    }

    @Test
    public void getTitleText() {
        assertEquals(TITLE, abstractScenarioSimulationEditorKogitoScreenSpy.getTitleText() );
    }

    @Test
    public void getWidget() {
        abstractScenarioSimulationEditorKogitoScreenSpy.getWidget();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).getWidget();
    }

    @Test
    public void getContent() {
        abstractScenarioSimulationEditorKogitoScreenSpy.getContent();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).getContent();
    }

    @Test
    public void setContent() {
        abstractScenarioSimulationEditorKogitoScreenSpy.setContent("fullPath", "content");
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).setContent(eq("fullPath"),
                                                                                                    eq("content"));
    }
}
