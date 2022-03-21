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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationViewImplTest extends AbstractScenarioSimulationTest {

    public static final int WIDTH = 44;
    public static final int HEIGHT = 82;

    @Mock
    private Widget parentWidget;

    private ScenarioSimulationViewImpl scenarioViewImpl;

    @Before
    public void setup() {
        super.setup();
        scenarioViewImpl = spy(new ScenarioSimulationViewImpl() {
            {
                this.scenarioGridWidget = scenarioGridWidgetSpy;
            }
        });
        when(scenarioViewImpl.getParent()).thenReturn(parentWidget);
        when(parentWidget.getOffsetHeight()).thenReturn(HEIGHT);
        when(parentWidget.getOffsetWidth()).thenReturn(WIDTH);
    }

    @Test
    public void onResize() {
        scenarioViewImpl.onResize();
        verify(scenarioViewImpl, times(1)).setPixelSize(eq(WIDTH), eq(HEIGHT));
        verify(scenarioGridWidgetSpy, times(1)).onResize();
    }
}
