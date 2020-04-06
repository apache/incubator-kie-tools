/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.client.dropdown;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoRuntimeDmnAssetsDropdownProviderImplTest {
    
    @Mock
    private KogitoResourceContentService kogitoResourceContentServiceMock;
    @Mock
    private ErrorPopupPresenter errorPopupPresenterMock;
    @Mock
    private Consumer<List<KieAssetsDropdownItem>> assetConsumer;
    @Captor
    private ArgumentCaptor<List<KieAssetsDropdownItem>> dropDownListCaptor;

    private KogitoRuntimeDmnAssetsDropdownProviderImplKogito kogitoRuntimeDmnAssetsDropdownProviderImplSpy;


    @Before
    public void setup() {
        kogitoRuntimeDmnAssetsDropdownProviderImplSpy = spy(new KogitoRuntimeDmnAssetsDropdownProviderImplKogito() {
            {
                this.resourceContentService = kogitoResourceContentServiceMock;
                this.errorPopupPresenter = errorPopupPresenterMock;
            }
        });
    }

    @Test
    public void getItems() {
        kogitoRuntimeDmnAssetsDropdownProviderImplSpy.getItems(assetConsumer);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(KogitoRuntimeDmnAssetsDropdownProviderImplKogito.DMN_FILE_EXTENSION),
                                                                                                 isA(RemoteCallback.class),
                                                                                                 isA(ErrorCallback.class));
    }

    @Test
    public void getRemoteCallBack() {
        RemoteCallback<List<String>> remoteCallBack = kogitoRuntimeDmnAssetsDropdownProviderImplSpy.getRemoteCallback(assetConsumer);
        remoteCallBack.callback(Arrays.asList("a", "b"));
        verify(assetConsumer, times(1)).accept(dropDownListCaptor.capture());
        assertTrue(dropDownListCaptor.getValue().size() == 2);
        assertEquals("a", dropDownListCaptor.getValue().get(0).getText());
        assertEquals("", dropDownListCaptor.getValue().get(0).getSubText());
        assertEquals("a", dropDownListCaptor.getValue().get(0).getValue());
        assertEquals("b", dropDownListCaptor.getValue().get(1).getText());
        assertEquals("", dropDownListCaptor.getValue().get(1).getSubText());
        assertEquals("b", dropDownListCaptor.getValue().get(1).getValue());
    }

    @Test
    public void getErrorCallback() {
        ErrorCallback<Object> errorCallback = kogitoRuntimeDmnAssetsDropdownProviderImplSpy.getErrorCallback();
        assertFalse(errorCallback.error("message", new Throwable("ex")));
        verify(errorPopupPresenterMock, times(1)).showMessage(eq("message: ex"));
    }
}
