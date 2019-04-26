/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.explorer;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.explorer.global.GlobalDataSourceExplorer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants.DataSourceDefExplorerScreen_Refresh;
import static org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants.DataSourceDefExplorerScreen_Title;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceDefExplorerScreenTest {

    private static final String CAPTION = "CAPTION";

    private static final String TITLE = "TITLE";

    private DataSourceDefExplorerScreen screen;

    @Mock
    private DataSourceDefExplorerScreenView view;

    @Mock
    private GlobalDataSourceExplorer explorer;

    @Mock
    private TranslationService translationService;

    @Before
    public void setUp() {
        when(translationService.getTranslation(DataSourceDefExplorerScreen_Refresh)).thenReturn(CAPTION);
        when(translationService.getTranslation(DataSourceDefExplorerScreen_Title)).thenReturn(TITLE);

        screen = new DataSourceDefExplorerScreen(view,
                                                 explorer,
                                                 translationService);
    }

    @Test
    public void testInit() {
        screen.init();
        verify(view,
               times(1)).init(screen);
        verify(view,
               times(1)).setGlobalExplorer(explorer);
    }

    @Test
    public void testOnStartup() {
        screen.init();
        screen.onStartup();
        verify(explorer,
               times(1)).refresh();
        screen.getMenus(Assert::assertNotNull);
    }

    @Test
    public void testGetTitle() {
        screen.init();
        assertEquals(TITLE,
                     screen.getTitle());
    }

    @Test
    public void testGetView() {
        screen.init();
        assertEquals(view,
                     screen.getView());
    }

    @Test
    public void testRefresh() {
        screen.init();
        screen.getRefreshCommand().execute();
        verify(explorer,
               times(1)).refresh();
    }
}
