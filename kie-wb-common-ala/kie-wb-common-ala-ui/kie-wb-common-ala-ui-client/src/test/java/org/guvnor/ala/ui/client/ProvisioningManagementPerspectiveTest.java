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

package org.guvnor.ala.ui.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.AddNewProviderEvent;
import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.guvnor.ala.ui.client.events.AddNewRuntimeEvent;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.wizard.EnableProviderTypeWizard;
import org.guvnor.ala.ui.client.wizard.NewDeployWizard;
import org.guvnor.ala.ui.client.wizard.NewProviderWizard;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderType;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProvisioningManagementPerspectiveTest {

    private static final int PROVIDER_TYPE_COUNT = 10;

    @Mock
    private ProviderTypeService providerTypeService;

    @Mock
    private RuntimeService runtimeService;

    @Mock
    private EnableProviderTypeWizard enableProviderTypeWizard;

    @Mock
    private NewProviderWizard newProviderWizard;

    @Mock
    private NewDeployWizard newDeployWizard;

    @Mock
    private ClientProviderHandlerRegistry handlerRegistry;

    private ProvisioningManagementPerspective perspective;

    private ArgumentCaptor<List> providerTypesCaptor;

    @Before
    public void setUp() {
        perspective = new ProvisioningManagementPerspective(new CallerMock<>(providerTypeService),
                                                            new CallerMock<>(runtimeService),
                                                            enableProviderTypeWizard,
                                                            newProviderWizard,
                                                            newDeployWizard,
                                                            handlerRegistry);
        providerTypesCaptor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void testAddNewProviderType() {
        List<ProviderType> providerTypes = mockProviderTypeList(PROVIDER_TYPE_COUNT);

        Map<ProviderType, ProviderTypeStatus> providerTypeStatusMap = new HashMap<>();
        providerTypes.forEach(providerType -> providerTypeStatusMap.put(providerType,
                                                                        mock(ProviderTypeStatus.class)));
        when(providerTypeService.getProviderTypesStatus()).thenReturn(providerTypeStatusMap);

        //select an arbitrary set of provider types as the properly installed.
        List<ProviderType> properlyInstalledProviderTypes = new ArrayList<>();
        properlyInstalledProviderTypes.add(providerTypes.get(0));
        properlyInstalledProviderTypes.add(providerTypes.get(3));
        properlyInstalledProviderTypes.add(providerTypes.get(5));
        properlyInstalledProviderTypes.forEach(providerType -> when(handlerRegistry.isProviderInstalled(providerType.getKey())).thenReturn(true));

        perspective.onAddNewProviderType(mock(AddNewProviderTypeEvent.class));

        verify(providerTypeService,
               times(1)).getProviderTypesStatus();
        verify(enableProviderTypeWizard,
               times(1)).start(providerTypesCaptor.capture());
        //only the properly installed provider types should be used for the wizard setup
        assertEquals(properlyInstalledProviderTypes.size(),
                     providerTypesCaptor.getValue().size());
        @SuppressWarnings("unchecked")
        List<Pair<ProviderType, ProviderTypeStatus>> capturedValues = providerTypesCaptor.getValue();
        capturedValues.forEach(value -> assertTrue(properlyInstalledProviderTypes.contains(value.getK1())));
    }

    @Test
    public void testAddNewProvider() {
        ProviderType providerType = mockProviderType("");
        perspective.onAddNewProvider(new AddNewProviderEvent(providerType));

        verify(newProviderWizard,
               times(1)).start(providerType);
    }

    @Test
    public void testAddNewRuntime() {
        Provider provider = new Provider(mockProviderKey(mockProviderTypeKey(""),
                                                         ""),
                                         mock(ProviderConfiguration.class));
        @SuppressWarnings("unchecked")
        List<PipelineKey> pipelines = mock(List.class);
        when(runtimeService.getPipelines(provider.getKey().getProviderTypeKey())).thenReturn(pipelines);

        perspective.onAddNewRuntime(new AddNewRuntimeEvent(provider));

        verify(newDeployWizard,
               times(1)).start(provider,
                               pipelines);
    }
}
