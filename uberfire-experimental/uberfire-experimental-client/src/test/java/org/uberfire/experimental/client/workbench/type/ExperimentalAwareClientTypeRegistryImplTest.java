/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.experimental.client.workbench.type;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.workbench.type.test.client.*;
import org.uberfire.experimental.client.workbench.type.test.api.DiagramResourceType;
import org.uberfire.experimental.client.workbench.type.test.api.FormResourceType;
import org.uberfire.experimental.client.workbench.type.test.api.JavaResourceType;
import org.uberfire.experimental.client.workbench.type.test.api.TextFileResourceType;
import org.uberfire.experimental.client.workbench.type.test.api.WrongClientResourceType;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeaturesRegistryImpl;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentalAwareClientTypeRegistryImplTest {

    private ExperimentalAwareClientTypeRegistryImpl clientTypeRegistry;

    @Mock
    private SyncBeanManager manager;

    @Mock
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    private ClientJavaResourceType clientJavaResourceType = new ClientJavaResourceType();
    private ClientFormResourceType clientFormResourceType = new ClientFormResourceType();
    private ClientDiagramResourceType clientDiagramResourceType = new ClientDiagramResourceType();
    private ClientTextFileResourceType clientTextFileResourceType = new ClientTextFileResourceType();
    private ClientSpreadSheetResourceType clientSpreadSheetResourceType = new ClientSpreadSheetResourceType();

    private List<SyncBeanDef<ClientResourceType>> clientTypes = new ArrayList<>();
    private List<SyncBeanDef<ResourceTypeDefinition>> allResourceTypes = new ArrayList<>();

    @Before
    public void init() {

        List<ExperimentalFeatureImpl> features = new ArrayList<>();
        features.add(new ExperimentalFeatureImpl(JavaResourceType.class.getName(), true));
        features.add(new ExperimentalFeatureImpl(ClientFormResourceType.class.getName(), false));
        features.add(new ExperimentalFeatureImpl(TextFileResourceType.class.getName(), false));
        features.add(new ExperimentalFeatureImpl(ClientSpreadSheetResourceType.class.getName(), true));

        ExperimentalFeaturesRegistryImpl experimentalFeaturesRegistry = new ExperimentalFeaturesRegistryImpl(features);

        when(experimentalFeaturesRegistryService.getFeaturesRegistry()).thenReturn(experimentalFeaturesRegistry);
        when(experimentalFeaturesRegistryService.isFeatureEnabled(anyString())).thenAnswer((Answer<Boolean>) invocationOnMock -> experimentalFeaturesRegistry.isFeatureEnabled(invocationOnMock.getArguments()[0].toString()));

        clientTypes.add((SyncBeanDef<ClientResourceType>) createBeanDef(ClientJavaResourceType.class, clientJavaResourceType));
        clientTypes.add((SyncBeanDef<ClientResourceType>) createBeanDef(ClientFormResourceType.class, clientFormResourceType));
        clientTypes.add((SyncBeanDef<ClientResourceType>) createBeanDef(ClientDiagramResourceType.class, clientDiagramResourceType));
        clientTypes.add((SyncBeanDef<ClientResourceType>) createBeanDef(ClientTextFileResourceType.class, clientTextFileResourceType));
        clientTypes.add((SyncBeanDef<ClientResourceType>) createBeanDef(ClientSpreadSheetResourceType.class, clientSpreadSheetResourceType));

        when(manager.lookupBeans(ClientResourceType.class)).thenReturn(clientTypes);

        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(ClientJavaResourceType.class, clientJavaResourceType));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(ClientFormResourceType.class, clientFormResourceType));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(ClientDiagramResourceType.class, clientDiagramResourceType));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(ClientTextFileResourceType.class, clientTextFileResourceType));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(ClientSpreadSheetResourceType.class, clientSpreadSheetResourceType));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(JavaResourceType.class, null));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(FormResourceType.class, null));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(DiagramResourceType.class, null));
        allResourceTypes.add((SyncBeanDef<ResourceTypeDefinition>) createBeanDef(TextFileResourceType.class, null));

        when(manager.lookupBeans(ResourceTypeDefinition.class)).thenReturn(allResourceTypes);

        clientTypeRegistry = new ExperimentalAwareClientTypeRegistryImpl(manager, experimentalFeaturesRegistryService);

        clientTypeRegistry.init();
    }

    @Test
    public void testResourceTypes() {
        Assertions.assertThat(clientTypeRegistry.getRegisteredTypes())
                .hasSize(clientTypes.size());
    }

    @Test
    public void testIsEnabled() {
        assertTrue(clientTypeRegistry.isEnabled(clientJavaResourceType));
        assertFalse(clientTypeRegistry.isEnabled(clientFormResourceType));
        assertTrue(clientTypeRegistry.isEnabled(clientDiagramResourceType));
        assertFalse(clientTypeRegistry.isEnabled(clientTextFileResourceType));
        assertTrue(clientTypeRegistry.isEnabled(clientSpreadSheetResourceType));
        assertFalse(clientTypeRegistry.isEnabled(new WrongClientResourceType()));
    }

    private SyncBeanDef<?> createBeanDef(final Class resourceType, Object instance) {

        SyncBeanDef def = mock(SyncBeanDef.class);
        when(def.getBeanClass()).thenReturn(resourceType);
        when(def.getInstance()).thenReturn(instance);
        when(def.isAssignableTo(any())).thenAnswer((Answer<Boolean>) invocationOnMock -> {
            Class parentType = (Class) invocationOnMock.getArguments()[0];
            return parentType.isAssignableFrom(resourceType);
        });

        return def;
    }
}
