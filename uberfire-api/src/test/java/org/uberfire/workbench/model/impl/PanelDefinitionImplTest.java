package org.uberfire.workbench.model.impl;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PanelDefinitionImplTest {

    private PanelDefinitionImpl panelDefinition;
    private PanelDefinitionImpl otherPanel;
    private PartDefinition part;
    private PlaceRequest placeRequest;

    @Before
    public void setUp() throws Exception {
        panelDefinition = new PanelDefinitionImpl();
        otherPanel = new PanelDefinitionImpl();
        placeRequest = mock( PlaceRequest.class );
        part = new PartDefinitionImpl( placeRequest );
    }

    @Test
    public void addPartTest() throws Exception {
        assertFalse( panelDefinition.getParts().contains( part ) );
        panelDefinition.addPart( part );
        assertTrue( panelDefinition.getParts().contains( part ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPartToADifferentPanelShouldFireExceptionTest() throws Exception {
        assertFalse( panelDefinition.getParts().contains( part ) );
        panelDefinition.addPart( part );
        assertTrue( panelDefinition.getParts().contains( part ) );
        otherPanel.addPart( part );
    }

    @Test
    public void addPartTwiceShouldWork() throws Exception {
        assertFalse( panelDefinition.getParts().contains( part ) );
        panelDefinition.addPart( part );
        panelDefinition.addPart( part );
        assertTrue( panelDefinition.getParts().contains( part ) );
    }

}
