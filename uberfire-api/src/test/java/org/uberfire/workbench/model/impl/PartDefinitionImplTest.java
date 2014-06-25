package org.uberfire.workbench.model.impl;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PartDefinitionImplTest {

    private PartDefinitionImpl part;
    private PanelDefinition parent;
    private PanelDefinition anotherParent;

    @Before
    public void setUp() throws Exception {
        PlaceRequest placeRequest = mock( PlaceRequest.class );
        part = new PartDefinitionImpl( placeRequest );
        parent = new PanelDefinitionImpl();
        anotherParent = new PanelDefinitionImpl();
    }

    @Test(expected = IllegalStateException.class)
    public void defineParentPanelWithOldParent() throws Exception {
        PanelDefinitionImpl parentMock = createParentMock();

        part.setParentPanel( parentMock );
        part.setParentPanel( anotherParent );
    }

    @Test(expected = IllegalStateException.class)
    public void defineParentPanelWithOldParentWithoutPart() throws Exception {
        parent = createParentMock();
        part.setParentPanel( parent );
        when( parent.getParts() ).thenReturn( new HashSet<PartDefinition>() );
        PanelDefinitionImpl anotherParent = createParentMock();
        part.setParentPanel( anotherParent );
    }

    private PanelDefinitionImpl createParentMock() {
        PanelDefinitionImpl parentMock = mock( PanelDefinitionImpl.class );
        HashSet<PartDefinition> mockSet = new HashSet<PartDefinition>();
        mockSet.add( part );
        when( parentMock.getParts() ).thenReturn( mockSet );
        return parentMock;
    }

    @Test
    public void defineParentPanel() throws Exception {
        PanelDefinitionImpl parentMock = createParentMock();
        part.setParentPanel( parentMock );
        assertEquals( parentMock, part.getParentPanel() );
    }

}
