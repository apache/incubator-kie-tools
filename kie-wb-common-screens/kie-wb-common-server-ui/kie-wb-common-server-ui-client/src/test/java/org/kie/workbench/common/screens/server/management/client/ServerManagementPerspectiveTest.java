package org.kie.workbench.common.screens.server.management.client;

import org.junit.Test;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;

import static org.junit.Assert.*;

public class ServerManagementPerspectiveTest {

    @Test
    public void checkPerspectiveDefinition() {
        final ServerManagementPerspective perspective = new ServerManagementPerspective();
        final PerspectiveDefinition definition = perspective.buildPerspective();

        assertNotNull( definition );

        assertEquals( "ServerManagementPerspective", definition.getName() );
        assertEquals( SimpleWorkbenchPanelPresenter.class.getName(), definition.getRoot().getPanelType() );

        assertEquals( 1, definition.getRoot().getParts().size() );

        final PartDefinition partDefinition = definition.getRoot().getParts().iterator().next();

        assertTrue( partDefinition.getPlace() instanceof DefaultPlaceRequest );

        assertEquals( "ServerManagementBrowser", partDefinition.getPlace().getIdentifier() );
    }
}
