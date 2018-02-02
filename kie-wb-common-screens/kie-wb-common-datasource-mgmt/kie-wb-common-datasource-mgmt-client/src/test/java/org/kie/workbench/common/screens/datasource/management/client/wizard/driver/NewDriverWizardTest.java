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

package org.kie.workbench.common.screens.datasource.management.client.wizard.driver;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewDriverWizardTest
        extends DriverWizardTestBase {

    @Mock
    private DriverDefEditorService driverDefService;

    private Caller<DriverDefEditorService> driverDefServiceCaller;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> statusChangeEvent;

    private NewDriverDefWizard driverDefWizard;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private Module module;

    @Mock
    private Path path;

    @GwtMock
    private WizardView wizardView;

    @GwtMock
    private PopupsUtil popupsUtil;

    @Before
    public void setup() {
        super.setup();
        driverDefServiceCaller = new CallerMock<>(driverDefService);
        driverDefWizard = new NewDriverDefWizard(defPage,
                                                 driverDefServiceCaller,
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
     * Emulates the wizard completion and creation of a Driver related to a project.
     */
    @Test
    public void testCreateProjectDriver() {
        testCreate(module);
    }

    /**
     * Emulates the wizard completion and creation of a global Driver.
     */
    @Test
    public void testCreateGlobalDriver() {
        testCreate(null);
    }

    /**
     * Emulates a sequence of valid data entering and the wizard completion.
     */
    private void testCreate(final Module module) {

        when(path.toString()).thenReturn("target_driver_path");
        when(translationService.format(eq(DataSourceManagementConstants.NewDriverDefWizard_DriverCreatedMessage),
                                       anyVararg())).thenReturn("OkMessage");

        if (module != null) {
            when(driverDefService.create(any(DriverDef.class),
                                         eq(module))).thenReturn(path);
            driverDefWizard.setModule(module);
        } else {
            when(driverDefService.createGlobal(any(DriverDef.class))).thenReturn(path);
        }

        driverDefWizard.start();

        completeValidDefPage();

        driverDefWizard.complete();

        DriverDef expectedDriverDef = new DriverDef();
        expectedDriverDef.setName(NAME);
        expectedDriverDef.setGroupId(GROUP_ID);
        expectedDriverDef.setArtifactId(ARTIFACT_ID);
        expectedDriverDef.setVersion(VERSION);
        expectedDriverDef.setDriverClass(DRIVER_CLASS);

        if (module != null) {
            verify(driverDefService,
                   times(1)).create(expectedDriverDef,
                                    module);
        } else {
            verify(driverDefService,
                   times(1)).createGlobal(expectedDriverDef);
        }
        verify(notificationEvent,
               times(1)).fire(
                new NotificationEvent("OkMessage"));
    }
}