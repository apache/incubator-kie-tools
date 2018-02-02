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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.ClientValidationServiceMock;
import org.kie.workbench.common.screens.datasource.management.client.util.DataSourceManagementTestConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.client.validation.ClientValidationService;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceDefEditorHelperTest
        implements DataSourceManagementTestConstants {

    @Mock
    private TranslationService translationService;

    @Mock
    private DataSourceDefEditorService editorService;

    private Caller<DataSourceDefEditorService> editorServiceCaller;

    @Mock
    private DataSourceDefQueryService queryService;

    private Caller<DataSourceDefQueryService> queryServiceCaller;

    private ClientValidationService clientValidationService;

    @Mock
    private DataSourceDefMainPanel mainPanel;

    private DataSourceDefEditorHelper editorHelper;

    @Mock
    private PopupsUtil popupsUtil;

    @Mock
    private DataSourceDefMainPanelView.Handler handler;

    private DataSourceDef dataSourceDef;

    @Mock
    private DriverDefInfo driverDefInfo;

    @Before
    public void setup() {

        clientValidationService = new ClientValidationServiceMock();
        editorServiceCaller = new CallerMock<>(editorService);

        queryServiceCaller = new CallerMock<>(queryService);

        when(driverDefInfo.getUuid()).thenReturn(DRIVER_UUID);
        when(driverDefInfo.getName()).thenReturn("DriverName");
        List<DriverDefInfo> drivers = new ArrayList<>();
        drivers.add(driverDefInfo);

        when(queryService.findGlobalDrivers()).thenReturn(drivers);
        when(queryService.findModuleDrivers(any(Path.class))).thenReturn(drivers);

        editorHelper = new DataSourceDefEditorHelper(translationService,
                                                     editorServiceCaller, queryServiceCaller, clientValidationService, popupsUtil);
        editorHelper.setHandler(handler);
        editorHelper.init(mainPanel);
        editorHelper.loadDrivers(new Command() {
            @Override
            public void execute() {
                //do nothing
            }
        }, new ParameterizedCommand<Throwable>() {
            @Override
            public void execute(Throwable parameter) {
                //do nothing
            }
        });

        dataSourceDef = new DataSourceDef();
        editorHelper.setDataSourceDef(dataSourceDef);

        verify(mainPanel, times(1)).clear();
        verify(mainPanel, times(1)).setName(dataSourceDef.getName());
        verify(mainPanel, times(1)).setConnectionURL(dataSourceDef.getConnectionURL());
        verify(mainPanel, times(1)).setUser(dataSourceDef.getUser());
        verify(mainPanel, times(1)).setPassword(dataSourceDef.getPassword());
        verify(mainPanel, times(1)).setDriver(dataSourceDef.getDriverUuid());
    }

    @Test
    public void testValidNameChange() {
        testNameChange(true);
    }

    @Test
    public void testInvalidNameChange() {
        testNameChange(false);
    }

    private void testNameChange(boolean isValid) {
        if (isValid) {
            when(mainPanel.getName()).thenReturn(NAME);
        } else {
            when(mainPanel.getName()).thenReturn(INVALID_NAME);
        }
        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_InvalidNameMessage)).thenReturn(ERROR);

        //emulates the helper receiving the change event
        editorHelper.onNameChange();

        if (isValid) {
            assertTrue(editorHelper.isNameValid());
            assertEquals(NAME, dataSourceDef.getName());
            verify(mainPanel, times(1)).clearNameErrorMessage();
        } else {
            assertFalse(editorHelper.isNameValid());
            assertEquals(INVALID_NAME, dataSourceDef.getName());
            verify(mainPanel, times(1)).setNameErrorMessage(ERROR);
        }
        verify(handler, times(1)).onNameChange();
    }

    @Test
    public void testValidConnectionURLChange() {
        testConnectionURLChange(true);
    }

    @Test
    public void testInvalidConnectionURLChange() {
        testConnectionURLChange(false);
    }

    private void testConnectionURLChange(boolean isValid) {
        if (isValid) {
            when(mainPanel.getConnectionURL()).thenReturn(CONNECTION_URL);
        } else {
            when(mainPanel.getConnectionURL()).thenReturn(INVALID_CONNECTION_URL);
        }
        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_InvalidConnectionURLMessage)).thenReturn(ERROR);

        //emulates the helper receiving the change event
        editorHelper.onConnectionURLChange();

        if (isValid) {
            assertTrue(editorHelper.isConnectionURLValid());
            assertEquals(CONNECTION_URL, dataSourceDef.getConnectionURL());
            verify(mainPanel, times(1)).clearConnectionURLErrorMessage();
        } else {
            assertFalse(editorHelper.isConnectionURLValid());
            assertEquals(INVALID_CONNECTION_URL, dataSourceDef.getConnectionURL());
            verify(mainPanel, times(1)).setConnectionURLErrorMessage(ERROR);
        }
        verify(handler, times(1)).onConnectionURLChange();
    }

    @Test
    public void testValidUserChange() {
        testUserChange(true);
    }

    @Test
    public void testInvalidUserChange() {
        testUserChange(false);
    }

    private void testUserChange(boolean isValid) {
        if (isValid) {
            when(mainPanel.getUser()).thenReturn(USER);
        } else {
            when(mainPanel.getUser()).thenReturn("");
        }
        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_InvalidUserMessage)).thenReturn(ERROR);

        //emulates the helper receiving the change event
        editorHelper.onUserChange();

        if (isValid) {
            assertTrue(editorHelper.isUserValid());
            assertEquals(USER, dataSourceDef.getUser());
            verify(mainPanel, times(1)).clearUserErrorMessage();
        } else {
            assertFalse(editorHelper.isUserValid());
            assertEquals("", dataSourceDef.getUser());
            verify(mainPanel, times(1)).setUserErrorMessage(ERROR);
        }
        verify(handler, times(1)).onUserChange();
    }

    @Test
    public void testValidPasswordChange() {
        testPasswordChange(true);
    }

    @Test
    public void testInvalidPassword() {
        testPasswordChange(false);
    }

    private void testPasswordChange(boolean isValid) {
        if (isValid) {
            when(mainPanel.getPassword()).thenReturn(PASSWORD);
        } else {
            when(mainPanel.getPassword()).thenReturn("");
        }
        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_InvalidPasswordMessage)).thenReturn(ERROR);

        //emulates the helper receiving the change event
        editorHelper.onPasswordChange();

        if (isValid) {
            assertTrue(editorHelper.isPasswordValid());
            assertEquals(PASSWORD, dataSourceDef.getPassword());
            verify(mainPanel, times(1)).clearPasswordErrorMessage();
        } else {
            assertFalse(editorHelper.isPasswordValid());
            assertEquals("", dataSourceDef.getPassword());
            verify(mainPanel, times(1)).setPasswordErrorMessage(ERROR);
        }
        verify(handler, times(1)).onPasswordChange();
    }

    @Test
    public void testValidDriverChange() {
        testDriverChange(true);
    }

    @Test
    public void testInvalidDriverChange() {
        testDriverChange(false);
    }

    private void testDriverChange(boolean isValid) {
        if (isValid) {
            when(mainPanel.getDriver()).thenReturn(DRIVER_UUID);
        } else {
            when(mainPanel.getDriver()).thenReturn(null);
        }
        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_DriverRequiredMessage)).thenReturn(ERROR);

        //emulates the helper receiving the change event
        editorHelper.onDriverChange();

        if (isValid) {
            assertTrue(editorHelper.isDriverValid());
            assertEquals(DRIVER_UUID, dataSourceDef.getDriverUuid());
            verify(mainPanel, times(1)).clearDriverErrorMessage();
        } else {
            assertFalse(editorHelper.isDriverValid());
            assertNull(dataSourceDef.getDriverUuid());
            verify(mainPanel, times(1)).setDriverErrorMessage(ERROR);
        }
        verify(handler, times(1)).onDriverChange();
    }

    @Test
    public void testValidConnection() {
        testConnection(true);
    }

    @Test
    public void testInvalidConnection() {
        testConnection(false);
    }

    private void testConnection(boolean isValid) {

        TestResult result = new TestResult(isValid);
        result.setMessage("Message");
        when(editorService.testConnection(any(DataSourceDef.class))).thenReturn(result);
        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_ConnectionTestFailedMessage)).thenReturn(ERROR);

        when(translationService.getTranslation(
                DataSourceManagementConstants.DataSourceDefEditor_ConnectionTestSuccessfulMessage)).thenReturn("OK");

        editorHelper.onTestConnection();

        if (isValid) {
            verify(popupsUtil, times(1)).showInformationPopup(
                    new SafeHtmlBuilder().appendEscapedLines("OK" + "\n" + "Message").toSafeHtml().asString());
        } else {
            verify(popupsUtil, times(1)).showInformationPopup(
                    new SafeHtmlBuilder().appendEscapedLines(ERROR + "\n" + "Message").toSafeHtml().asString());
        }
    }
}