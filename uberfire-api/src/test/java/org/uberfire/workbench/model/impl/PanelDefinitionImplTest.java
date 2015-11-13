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

    @Test
    public void addPartToADifferentPanelChangePanel() throws Exception {
        assertFalse( panelDefinition.getParts().contains( part ) );
        panelDefinition.addPart( part );
        assertTrue( panelDefinition.getParts().contains( part ) );
        otherPanel.addPart( part );
        assertTrue( otherPanel.getParts().contains( part ) );
        assertEquals( otherPanel, part.getParentPanel() );
        assertFalse( panelDefinition.getParts().contains( part ) );
    }

    @Test
    public void addPartTwiceShouldWork() throws Exception {
        assertFalse( panelDefinition.getParts().contains( part ) );
        panelDefinition.addPart( part );
        panelDefinition.addPart( part );
        assertTrue( panelDefinition.getParts().contains( part ) );
    }

    @Test
    public void widthShouldNotRevertToNullOnceSet() throws Exception {
        assertNull( panelDefinition.getWidth() );
        panelDefinition.setWidth( 1234 );
        panelDefinition.setWidth( null );
        assertEquals( (Integer) 1234, panelDefinition.getWidth() );
    }

    @Test
    public void heightShouldNotRevertToNullOnceSet() throws Exception {
        assertNull( panelDefinition.getHeight() );
        panelDefinition.setHeight( 1234 );
        panelDefinition.setHeight( null );
        assertEquals( (Integer) 1234, panelDefinition.getHeight() );
    }

}
