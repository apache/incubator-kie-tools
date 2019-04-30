/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.client;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.client.resources.i18n.AppConstants;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.jsbridge.client.AppFormerJsBridge;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.ConstantsAnswerMock;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DroolsWorkbenchEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    private WorkbenchMegaMenuPresenter menuBar;

    @Mock
    private AdminPage adminPage;

    @Mock
    protected DefaultAdminPageHelper adminPageHelper;

    @Mock
    protected PreferenceScopeFactory scopeFactory;

    @Mock
    protected WorkbenchConfigurationPresenter workbenchConfigurationPresenter;

    @Mock
    protected LanguageConfigurationHandler languageConfigurationHandler;

    @Mock
    protected DefaultWorkbenchErrorCallback defaultWorkbenchErrorCallback;

    private DroolsWorkbenchEntryPoint droolsWorkbenchEntryPoint;

    @Before
    public void setup() {
        appConfigServiceCallerMock = new CallerMock<>(appConfigService);

        droolsWorkbenchEntryPoint = spy(new DroolsWorkbenchEntryPoint(appConfigServiceCallerMock,
                                                                      activityBeansCache,
                                                                      placeManager,
                                                                      menusHelper,
                                                                      menuBar,
                                                                      adminPage,
                                                                      adminPageHelper,
                                                                      scopeFactory,
                                                                      workbenchConfigurationPresenter,
                                                                      languageConfigurationHandler,
                                                                      defaultWorkbenchErrorCallback,
                                                                      mock(AppFormerJsBridge.class)));
        mockMenuHelper();
        mockConstants();
    }

    @Test
    public void setupMenuTest() {
        droolsWorkbenchEntryPoint.setupMenu();

        ArgumentCaptor<Menus> menusCaptor = ArgumentCaptor.forClass(Menus.class);
        verify(menuBar).addMenus(menusCaptor.capture());

        Menus menus = menusCaptor.getValue();

        assertEquals(1,
                     menus.getItems().size());

        assertEquals(droolsWorkbenchEntryPoint.constants.Perspectives(),
                     menus.getItems().get(0).getCaption());

        verify(menusHelper).addUtilitiesMenuItems();
    }

    private void mockMenuHelper() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(mock(MenuItem.class));
        doReturn(menuItems).when(menusHelper).getPerspectivesMenuItems();
    }

    private void mockConstants() {
        droolsWorkbenchEntryPoint.constants = mock(AppConstants.class,
                                                   new ConstantsAnswerMock());
    }
}
