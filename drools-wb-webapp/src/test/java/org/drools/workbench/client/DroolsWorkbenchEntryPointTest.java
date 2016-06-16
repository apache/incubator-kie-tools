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

package org.drools.workbench.client;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.client.resources.i18n.AppConstants;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.ConstantsAnswerMock;
import org.uberfire.mocks.IocTestingUtils;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DroolsWorkbenchEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private PlaceManagerActivityService pmas;
    private CallerMock<PlaceManagerActivityService> pmasCallerMock;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    private WorkbenchMenuBarPresenter menuBar;

    private DroolsWorkbenchEntryPoint droolsWorkbenchEntryPoint;

    @Before
    public void setup() {
        appConfigServiceCallerMock = new CallerMock<>( appConfigService );
        pmasCallerMock = new CallerMock<>( pmas );

        droolsWorkbenchEntryPoint = spy( new DroolsWorkbenchEntryPoint( appConfigServiceCallerMock,
                                                                        pmasCallerMock,
                                                                        activityBeansCache,
                                                                        placeManager,
                                                                        iocManager,
                                                                        menusHelper,
                                                                        menuBar ) );
        mockMenuHelper();
        mockConstants();
        IocTestingUtils.mockIocManager( iocManager );
    }

    @Test
    public void setupMenuTest() {
        droolsWorkbenchEntryPoint.setupMenu();

        ArgumentCaptor<Menus> menusCaptor = ArgumentCaptor.forClass( Menus.class );
        verify( menuBar ).addMenus( menusCaptor.capture() );

        Menus menus = menusCaptor.getValue();

        assertEquals( 3, menus.getItems().size() );

        assertEquals( droolsWorkbenchEntryPoint.constants.Home(), menus.getItems().get( 0 ).getCaption() );
        assertEquals( droolsWorkbenchEntryPoint.constants.Perspectives(), menus.getItems().get( 1 ).getCaption() );

        verify( menusHelper ).addRolesMenuItems();
        verify( menusHelper ).addUtilitiesMenuItems();
    }

    private void mockMenuHelper() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add( mock( MenuItem.class ) );
        doReturn( menuItems ).when( menusHelper ).getPerspectivesMenuItems();
    }

    private void mockConstants() {
        droolsWorkbenchEntryPoint.constants = mock( AppConstants.class, new ConstantsAnswerMock() );
    }

}
