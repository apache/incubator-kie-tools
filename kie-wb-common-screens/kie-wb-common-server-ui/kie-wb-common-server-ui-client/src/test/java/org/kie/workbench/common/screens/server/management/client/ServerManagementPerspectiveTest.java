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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagementPerspectiveTest {

    @Mock
    private NewServerTemplateWizard newServerTemplateWizard;
    @Mock
    private NewContainerWizard newContainerWizard;

    private ServerManagementPerspective perspective;

    @Before
    public void init() {
        perspective = new ServerManagementPerspective( newServerTemplateWizard, newContainerWizard );
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
