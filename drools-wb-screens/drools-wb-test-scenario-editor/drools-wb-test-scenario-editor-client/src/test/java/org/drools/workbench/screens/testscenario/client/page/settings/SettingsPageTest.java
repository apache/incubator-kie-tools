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

package org.drools.workbench.screens.testscenario.client.page.settings;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.ScenarioParentWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@WithClassesToStub(RootPanel.class)
@RunWith(GwtMockitoTestRunner.class)
public class SettingsPageTest {

    @Mock
    private SettingsPage.SettingsPageView settingsPageView;

    private SettingsPage settingsPage;

    @Before
    public void setUp() throws Exception {
        settingsPage = new SettingsPage(settingsPageView);
    }

    @Test
    public void testRefresh() throws Exception {
        final ScenarioParentWidget parentWidget = mock(ScenarioParentWidget.class);
        final Path path = mock(Path.class);
        final Scenario scenario = mock(Scenario.class);

        settingsPage.refresh(parentWidget, path, scenario);

        verify(settingsPageView).refresh(parentWidget, path, scenario);
    }
}
