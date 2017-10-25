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

package org.guvnor.ala.ui.wildfly.client.provider;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.wildfly.service.TestConnectionResult;
import org.guvnor.ala.ui.wildfly.service.WildflyClientService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.ala.ui.wildfly.client.provider.WF10ProviderConfigPresenter.HOST;
import static org.guvnor.ala.ui.wildfly.client.provider.WF10ProviderConfigPresenter.MANAGEMENT_PORT;
import static org.guvnor.ala.ui.wildfly.client.provider.WF10ProviderConfigPresenter.PASSWORD;
import static org.guvnor.ala.ui.wildfly.client.provider.WF10ProviderConfigPresenter.PASSWORD_MASK;
import static org.guvnor.ala.ui.wildfly.client.provider.WF10ProviderConfigPresenter.PORT;
import static org.guvnor.ala.ui.wildfly.client.provider.WF10ProviderConfigPresenter.USER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WF10ProviderConfigPresenterTest {

    private static final String PROVIDER_NAME_VALUE = "PROVIDER_NAME_VALUE";

    private static final String HOST_VALUE = "HOST_VALUE";

    private static final String PORT_VALUE = "8080";

    private static final String MANAGEMENT_PORT_VALUE = "9990";

    private static final String USER_VALUE = "USER_VALUE";

    private static final String PASSWORD_VALUE = "PASSWORD_VALUE";

    private static final String EMPTY_VALUE = "";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    private static final String ERROR_MESSAGE_TRANSFORMED = "ERROR_MESSAGE_TRANSFORMED";

    private static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";

    private static final String SUCCESS_MESSAGE_TRANSFORMED = "SUCCESS_MESSAGE_TRANSFORMED";

    @Mock
    private WF10ProviderConfigPresenter.View view;

    @Mock
    private WildflyClientService wildflyClientService;

    private Caller<WildflyClientService> wildflyClientServiceCaller;

    private WF10ProviderConfigPresenter presenter;

    @Mock
    private ContentChangeHandler changeHandler;

    @Before
    public void setUp() {
        wildflyClientServiceCaller = spy(new CallerMock<>(wildflyClientService));
        presenter = new WF10ProviderConfigPresenter(view,
                                                    wildflyClientServiceCaller);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        presenter.addContentChangeHandler(changeHandler);
    }

    @Test
    public void testBuildProviderConfiguration() {
        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);
        when(view.getHost()).thenReturn(HOST_VALUE);
        when(view.getPort()).thenReturn(PORT_VALUE);
        when(view.getManagementPort()).thenReturn(MANAGEMENT_PORT_VALUE);
        when(view.getUsername()).thenReturn(USER_VALUE);
        when(view.getPassword()).thenReturn(PASSWORD_VALUE);

        ProviderConfiguration configuration = presenter.buildProviderConfiguration();
        assertEquals(PROVIDER_NAME_VALUE,
                     presenter.getProviderName());
        assertEquals(HOST_VALUE,
                     configuration.getValues().get(HOST));
        assertEquals(PORT_VALUE,
                     configuration.getValues().get(PORT));
        assertEquals(MANAGEMENT_PORT_VALUE,
                     configuration.getValues().get(MANAGEMENT_PORT));
        assertEquals(USER_VALUE,
                     configuration.getValues().get(USER));
        assertEquals(PASSWORD_VALUE,
                     configuration.getValues().get(PASSWORD));
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view,
               times(1)).clear();
    }

    @Test
    public void testLoad() {

        Map<String, Object> values = new HashMap<>();
        values.put(HOST,
                   HOST_VALUE);
        values.put(PORT,
                   PORT_VALUE);
        values.put(MANAGEMENT_PORT,
                   MANAGEMENT_PORT_VALUE);
        values.put(USER,
                   USER_VALUE);
        values.put(PASSWORD,
                   PASSWORD_VALUE);

        Provider provider = mock(Provider.class);
        ProviderKey providerKey = mock(ProviderKey.class);
        ProviderConfiguration configuration = mock(ProviderConfiguration.class);

        when(provider.getKey()).thenReturn(providerKey);
        when(providerKey.getId()).thenReturn(PROVIDER_NAME_VALUE);
        when(provider.getConfiguration()).thenReturn(configuration);
        when(configuration.getValues()).thenReturn(values);

        presenter.load(provider);

        verify(view,
               times(1)).setProviderName(PROVIDER_NAME_VALUE);
        verify(view,
               times(1)).setHost(HOST_VALUE);
        verify(view,
               times(1)).setPort(PORT_VALUE);
        verify(view,
               times(1)).setManagementPort(MANAGEMENT_PORT_VALUE);
        verify(view,
               times(1)).setUsername(USER_VALUE);
        verify(view,
               times(1)).setPassword(PASSWORD_MASK);
    }

    @Test
    public void testGetProviderName() {
        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);
        assertEquals(PROVIDER_NAME_VALUE,
                     presenter.getProviderName());
    }

    @Test
    public void testGetHost() {
        when(view.getHost()).thenReturn(HOST_VALUE);
        assertEquals(HOST_VALUE,
                     presenter.getHost());
    }

    @Test
    public void testGetPort() {
        when(view.getPort()).thenReturn(PORT_VALUE);
        assertEquals(PORT_VALUE,
                     presenter.getPort());
    }

    @Test
    public void testGetManagementPort() {
        when(view.getManagementPort()).thenReturn(MANAGEMENT_PORT_VALUE);
        assertEquals(MANAGEMENT_PORT_VALUE,
                     presenter.getManagementPort());
    }

    @Test
    public void testGetUser() {
        when(view.getUsername()).thenReturn(USER_VALUE);
        assertEquals(USER_VALUE,
                     presenter.getUsername());
    }

    @Test
    public void testGetPassword() {
        when(view.getPassword()).thenReturn(PASSWORD_VALUE);
        assertEquals(PASSWORD_VALUE,
                     presenter.getPassword());
    }

    @Test
    public void testIsValid() {
        when(view.getProviderName()).thenReturn(EMPTY_VALUE);
        when(view.getHost()).thenReturn(EMPTY_VALUE);
        when(view.getPort()).thenReturn(EMPTY_VALUE);
        when(view.getManagementPort()).thenReturn(EMPTY_VALUE);
        when(view.getUsername()).thenReturn(EMPTY_VALUE);
        when(view.getPassword()).thenReturn(EMPTY_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getHost()).thenReturn(HOST_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getPort()).thenReturn(PORT_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getManagementPort()).thenReturn(MANAGEMENT_PORT_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getUsername()).thenReturn(USER_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getPassword()).thenReturn(PASSWORD_VALUE);

        //valid when al values are in place.
        presenter.isValid(Assert::assertTrue);
    }

    @Test
    public void testDisable() {
        presenter.disable();
        verify(view,
               times(1)).disable();
    }

    @Test
    public void testOnProviderNameChangeValid() {
        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);
        presenter.onProviderNameChange();
        verify(view,
               times(1)).setProviderNameStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnProviderNameChangeInvalid() {
        when(view.getProviderName()).thenReturn(EMPTY_VALUE);
        presenter.onProviderNameChange();
        verify(view,
               times(1)).setProviderNameStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnHostChangeValid() {
        when(view.getHost()).thenReturn(HOST_VALUE);
        presenter.onHostChange();
        verify(view,
               times(1)).setHostStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnHostChangeInvalid() {
        when(view.getHost()).thenReturn(EMPTY_VALUE);
        presenter.onHostChange();
        verify(view,
               times(1)).setHostStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnPortChangeValid() {
        when(view.getPort()).thenReturn(PORT_VALUE);
        presenter.onPortChange();
        verify(view,
               times(1)).setPortStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnPortChangeInvalid() {
        when(view.getPort()).thenReturn(EMPTY_VALUE);
        presenter.onPortChange();
        verify(view,
               times(1)).setPortStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnManagementPortChangeValid() {
        when(view.getManagementPort()).thenReturn(MANAGEMENT_PORT_VALUE);
        presenter.onManagementPortChange();
        verify(view,
               times(1)).setManagementPortStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnManagementPortChangeInvalid() {
        when(view.getManagementPort()).thenReturn(EMPTY_VALUE);
        presenter.onManagementPortChange();
        verify(view,
               times(1)).setManagementPortStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnUserChangeValid() {
        when(view.getUsername()).thenReturn(USER_VALUE);
        presenter.onUserNameChange();
        verify(view,
               times(1)).setUsernameStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnUserChangeInvalid() {
        when(view.getUsername()).thenReturn(EMPTY_VALUE);
        presenter.onUserNameChange();
        verify(view,
               times(1)).setUsernameStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnPasswordChangeValid() {
        when(view.getPassword()).thenReturn(PASSWORD_VALUE);
        presenter.onPasswordChange();
        verify(view,
               times(1)).setPasswordStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnPasswordChangeInvalid() {
        when(view.getPassword()).thenReturn(EMPTY_VALUE);
        presenter.onPasswordChange();
        verify(view,
               times(1)).setPasswordStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnTestConnectionWithMissingParams() {
        when(view.getParamsNotCompletedErrorMessage()).thenReturn(ERROR_MESSAGE);
        presenter.onTestConnection();
        verify(view,
               times(1)).showInformationPopup(ERROR_MESSAGE);
    }

    @Test
    public void testOnTestConnectionWhenConnectionFailed() {
        when(view.getTestConnectionFailMessage(ERROR_MESSAGE)).thenReturn(ERROR_MESSAGE_TRANSFORMED);

        TestConnectionResult result = mock(TestConnectionResult.class);
        when(result.getManagementConnectionError()).thenReturn(true);
        when(result.getManagementConnectionMessage()).thenReturn(ERROR_MESSAGE);

        when(wildflyClientService.testConnection(HOST_VALUE,
                                                 Integer.parseInt(PORT_VALUE),
                                                 Integer.parseInt(MANAGEMENT_PORT_VALUE),
                                                 USER_VALUE,
                                                 PASSWORD_VALUE)).thenReturn(result);
        fillConnectionParams();
        presenter.onTestConnection();
        verify(view,
               times(1)).showInformationPopup(ERROR_MESSAGE_TRANSFORMED);
    }

    @Test
    public void testOnTestConnectionWhenConnectionSuccessful() {
        when(view.getTestConnectionSuccessfulMessage(SUCCESS_MESSAGE)).thenReturn(SUCCESS_MESSAGE_TRANSFORMED);

        TestConnectionResult result = mock(TestConnectionResult.class);
        when(result.getManagementConnectionError()).thenReturn(false);
        when(result.getManagementConnectionMessage()).thenReturn(SUCCESS_MESSAGE);

        when(wildflyClientService.testConnection(HOST_VALUE,
                                                 Integer.parseInt(PORT_VALUE),
                                                 Integer.parseInt(MANAGEMENT_PORT_VALUE),
                                                 USER_VALUE,
                                                 PASSWORD_VALUE)).thenReturn(result);
        fillConnectionParams();
        presenter.onTestConnection();
        verify(view,
               times(1)).showInformationPopup(SUCCESS_MESSAGE_TRANSFORMED);
    }

    private void fillConnectionParams() {
        when(view.getHost()).thenReturn(HOST_VALUE);
        when(view.getPort()).thenReturn(PORT_VALUE);
        when(view.getManagementPort()).thenReturn(MANAGEMENT_PORT_VALUE);
        when(view.getUsername()).thenReturn(USER_VALUE);
        when(view.getPassword()).thenReturn(PASSWORD_VALUE);
    }
}
