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

package org.kie.workbench.common.screens.datasource.management.client.wizard.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.editor.datasource.DataSourceDefEditorHelper;
import org.kie.workbench.common.screens.datasource.management.client.editor.datasource.DataSourceDefMainPanel;
import org.kie.workbench.common.screens.datasource.management.client.editor.datasource.DataSourceDefMainPanelView;
import org.kie.workbench.common.screens.datasource.management.client.util.ClientValidationServiceMock;
import org.kie.workbench.common.screens.datasource.management.client.util.DataSourceManagementTestConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

public abstract class DataSourceWizardTestBase
        implements DataSourceManagementTestConstants {

    @GwtMock
    protected DataSourceDefPageView view;

    @GwtMock
    protected DataSourceDefMainPanelView mainPanelView;

    protected DataSourceDefMainPanel mainPanel;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected DataSourceDefEditorService editorService;

    protected Caller<DataSourceDefEditorService> editorServiceCaller;

    @Mock
    protected DataSourceDefQueryService queryService;

    @Mock
    protected Caller<DataSourceDefQueryService> queryServiceCaller;

    @Mock
    protected PopupsUtil popupsUtil;

    @Mock
    protected EventSourceMock<WizardPageStatusChangeEvent> statusChangeEvent;

    protected DataSourceDefEditorHelper editorHelper;

    protected DataSourceDefPage defPage;

    protected DataSourceDef dataSourceDef;

    @Mock
    protected Path path;

    @Mock
    protected DriverDefInfo driver1;

    @Mock
    protected DriverDefInfo driver2;

    protected List<DriverDefInfo> drivers;

    protected List<Pair<String, String>> options;

    protected boolean defPageLoadedOK[] = new boolean[1];

    /**
     * Initializes the services, the wizard pages, and drivers information.
     */
    protected void setup() {
        //initialize the services
        editorServiceCaller = new CallerMock<>(editorService);
        queryServiceCaller = new CallerMock<>(queryService);

        //initialize the wizard page
        mainPanel = new DataSourceDefMainPanel(mainPanelView);
        dataSourceDef = new DataSourceDef();

        editorHelper = new DataSourceDefEditorHelper(translationService,
                                                     editorServiceCaller, queryServiceCaller, new ClientValidationServiceMock(), popupsUtil);
        defPage = new DataSourceDefPage(view, mainPanel, editorHelper, statusChangeEvent);
        defPage.setDataSourceDef(dataSourceDef);

        //prepare the drivers info
        drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);

        options = new ArrayList<>();
        options.add(new Pair("Driver1.name", DRIVER_UUID));
        options.add(new Pair("Driver2.name", DRIVER_UUID_2));

        when(driver1.getName()).thenReturn("Driver1.name");
        when(driver1.getUuid()).thenReturn(DRIVER_UUID);
        when(driver2.getName()).thenReturn("Driver2.name");
        when(driver2.getUuid()).thenReturn(DRIVER_UUID_2);

        //emulates the service returning the requested drivers.
        when(queryService.findModuleDrivers(path)).thenReturn(drivers);
        when(queryService.findGlobalDrivers()).thenReturn(drivers);
    }

    /**
     * Emulates the user completing the page by entering valid values in all fields
     */
    protected void completeValidDefPage() {
        when(mainPanelView.getName()).thenReturn(NAME);
        when(mainPanelView.getConnectionURL()).thenReturn(CONNECTION_URL);
        when(mainPanelView.getUser()).thenReturn(USER);
        when(mainPanelView.getPassword()).thenReturn(PASSWORD);
        when(mainPanelView.getDriver()).thenReturn(DRIVER_UUID);

        mainPanel.onNameChange();
        mainPanel.onConnectionURLChange();
        mainPanel.onUserChange();
        mainPanel.onPasswordChange();
        mainPanel.onDriverChange();
    }
}
