/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
