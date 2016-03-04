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

package org.kie.workbench.common.screens.server.management.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.AddNewContainer;
import org.kie.workbench.common.screens.server.management.client.events.AddNewServerTemplate;
import org.kie.workbench.common.screens.server.management.client.wizard.NewContainerWizard;
import org.kie.workbench.common.screens.server.management.client.wizard.NewServerTemplateWizard;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagementPerspectiveTest {

    @Mock
    Logger logger;
    @Mock
    private NewServerTemplateWizard newServerTemplateWizard;
    @Mock
    private NewContainerWizard newContainerWizard;

    private ServerManagementPerspective perspective;

    @Before
    public void init() {
        perspective = new ServerManagementPerspective( logger, newServerTemplateWizard, newContainerWizard );
    }

    @Test
    public void testOnNewContainer() {
        final ServerTemplate template = mock( ServerTemplate.class );
        perspective.onNewContainer( new AddNewContainer( template ) );
        verify( newContainerWizard ).clear();
        verify( newContainerWizard ).setServerTemplate( eq( template ) );
        verify( newContainerWizard ).start();
    }

    @Test
    public void testOnNewTemplate() {
        perspective.onNewTemplate( new AddNewServerTemplate() );
        verify( newServerTemplateWizard ).clear();
        verify( newServerTemplateWizard ).start();
    }
}
