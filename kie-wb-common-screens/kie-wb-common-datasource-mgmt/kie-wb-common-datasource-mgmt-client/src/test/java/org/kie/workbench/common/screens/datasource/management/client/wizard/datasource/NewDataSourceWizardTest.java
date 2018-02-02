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

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewDataSourceWizardTest
        extends DataSourceWizardTestBase {

    private NewDataSourceDefWizard dataSourceDefWizard;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private Module module;

    @GwtMock
    private WizardView wizardView;

    @GwtMock
    private PopupsUtil popupsUtil;

    @Before
    public void setup() {
        super.setup();
        dataSourceDefWizard = new NewDataSourceDefWizard(defPage,
                                                         editorServiceCaller,
                                                         translationService,
                                                         popupsUtil,
                                                         notificationEvent) {
            {
                this.view = wizardView;
            }
        };
        when(module.getRootPath()).thenReturn(path);
    }

    /**
     * Emulates the wizard completion and creation of a DataSource related to a module.
     */
    @Test
    public void testCreateModuleDataSource() {
        testCreate(module);
    }

    /**
     * Emulates the wizard completion and creation of a global DataSource.
     */
    @Test
    public void testCreateGlobalDataSource() {
        testCreate(null);
    }

    /**
     * Emulates a sequence of valid data entering and the wizard completion.
     */
    private void testCreate(final Module module) {

        when(path.toString()).thenReturn("target_data_source_path");
        when(translationService.format(eq(DataSourceManagementConstants.NewDataSourceDefWizard_DataSourceCreatedMessage),
                                       anyVararg())).thenReturn("OkMessage");

        if (module != null) {
            when(editorService.create(any(DataSourceDef.class),
                                      eq(module))).thenReturn(path);
            dataSourceDefWizard.setModule(module);
        } else {
            when(editorService.createGlobal(any(DataSourceDef.class))).thenReturn(path);
        }

        dataSourceDefWizard.start();

        //emulates the completion of the wizard page
        completeValidDefPage();

        //emulates the user pressing the finish button
        dataSourceDefWizard.complete();

        DataSourceDef expectedDataSourceDef = new DataSourceDef();
        expectedDataSourceDef.setName(NAME);
        expectedDataSourceDef.setConnectionURL(CONNECTION_URL);
        expectedDataSourceDef.setUser(USER);
        expectedDataSourceDef.setPassword(PASSWORD);
        expectedDataSourceDef.setDriverUuid(DRIVER_UUID);

        if (module != null) {
            verify(editorService,
                   times(1)).create(expectedDataSourceDef,
                                    module);
        } else {
            verify(editorService,
                   times(1)).createGlobal(expectedDataSourceDef);
        }
        verify(notificationEvent,
               times(1)).fire(
                new NotificationEvent("OkMessage"));
    }
}
