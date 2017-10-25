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

package org.guvnor.ala.ui.openshift.client.provider;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.openshift.client.provider.OpenShiftProviderConfigPresenter.MASTER_URL;
import static org.guvnor.ala.ui.openshift.client.provider.OpenShiftProviderConfigPresenter.PASSWORD;
import static org.guvnor.ala.ui.openshift.client.provider.OpenShiftProviderConfigPresenter.USER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class OpenShiftProviderConfigPresenterTest {

    private static final String PROVIDER_NAME_VALUE = "PROVIDER_NAME_VALUE";

    private static final String MASTER_URL_VALUE = "MASTER_URL_VALUE";

    private static final String USER_VALUE = "USER_VALUE";

    private static final String PASSWORD_VALUE = "PASSWORD_VALUE";

    @Mock
    private OpenShiftProviderConfigPresenter.View view;

    private OpenShiftProviderConfigPresenter presenter;

    @Mock
    private ContentChangeHandler changeHandler;

    @Before
    public void setUp() {
        presenter = new OpenShiftProviderConfigPresenter(view);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        presenter.addContentChangeHandler(changeHandler);
    }

    @Test
    public void testBuildProviderConfiguration() {
        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);
        when(view.getMasterURL()).thenReturn(MASTER_URL_VALUE);
        when(view.getUsername()).thenReturn(USER_VALUE);
        when(view.getPassword()).thenReturn(PASSWORD_VALUE);

        ProviderConfiguration configuration = presenter.buildProviderConfiguration();
        assertEquals(PROVIDER_NAME_VALUE,
                     presenter.getProviderName());
        assertEquals(MASTER_URL_VALUE,
                     configuration.getValues().get(MASTER_URL));
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
        values.put(MASTER_URL,
                   MASTER_URL_VALUE);
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
               times(1)).clear();
        verify(view,
               times(1)).setProviderName(PROVIDER_NAME_VALUE);
        verify(view,
               times(1)).setMasterURL(MASTER_URL_VALUE);
        verify(view,
               times(1)).setUsername(USER_VALUE);
        verify(view,
               times(1)).setPassword(PASSWORD_VALUE);
    }

    @Test
    public void testGetProviderName() {
        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);
        assertEquals(PROVIDER_NAME_VALUE,
                     presenter.getProviderName());
    }

    @Test
    public void testGetMasterURL() {
        when(view.getMasterURL()).thenReturn(MASTER_URL_VALUE);
        assertEquals(MASTER_URL_VALUE,
                     presenter.getMasterURL());
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
        when(view.getProviderName()).thenReturn(EMPTY_STRING);
        when(view.getMasterURL()).thenReturn(EMPTY_STRING);
        when(view.getUsername()).thenReturn(EMPTY_STRING);
        when(view.getPassword()).thenReturn(EMPTY_STRING);

        presenter.isValid(Assert::assertFalse);

        when(view.getProviderName()).thenReturn(PROVIDER_NAME_VALUE);

        presenter.isValid(Assert::assertFalse);

        when(view.getMasterURL()).thenReturn(MASTER_URL_VALUE);

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
        when(view.getProviderName()).thenReturn(EMPTY_STRING);
        presenter.onProviderNameChange();
        verify(view,
               times(1)).setProviderNameStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnMasterURLChangeValid() {
        when(view.getMasterURL()).thenReturn(MASTER_URL_VALUE);
        presenter.onMasterURLChange();
        verify(view,
               times(1)).setMasterURLStatus(FormStatus.VALID);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnMasterURLChangeInvalid() {
        when(view.getMasterURL()).thenReturn(EMPTY_STRING);
        presenter.onMasterURLChange();
        verify(view,
               times(1)).setMasterURLStatus(FormStatus.ERROR);
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
        when(view.getUsername()).thenReturn(EMPTY_STRING);
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
        when(view.getPassword()).thenReturn(EMPTY_STRING);
        presenter.onPasswordChange();
        verify(view,
               times(1)).setPasswordStatus(FormStatus.ERROR);
        verify(changeHandler,
               times(1)).onContentChange();
    }
}