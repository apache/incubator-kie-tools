/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.workbench.model.impl;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PanelDefinitionImplTest {

    private PanelDefinitionImpl panelDefinition;
    private PanelDefinitionImpl otherPanel;
    private PartDefinition part;
    private PlaceRequest placeRequest;
    private PanelDefinitionImpl parent;

    @Before
    public void setUp() throws Exception {
        panelDefinition = new PanelDefinitionImpl();
        otherPanel = new PanelDefinitionImpl();
        parent = new PanelDefinitionImpl();
        placeRequest = mock(PlaceRequest.class);
        part = new PartDefinitionImpl(placeRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void settingTwoDifferentParentsShouldThrowException() throws Exception {
        panelDefinition.setParent(otherPanel);
        panelDefinition.setParent(parent);
    }

    @Test
    public void addPartTest() throws Exception {
        assertFalse(panelDefinition.getParts().contains(part));
        panelDefinition.addPart(part);
        assertTrue(panelDefinition.getParts().contains(part));
    }

    @Test
    public void addPartToADifferentPanelChangePanel() throws Exception {
        assertFalse(panelDefinition.getParts().contains(part));
        panelDefinition.addPart(part);
        assertTrue(panelDefinition.getParts().contains(part));
        otherPanel.addPart(part);
        assertTrue(otherPanel.getParts().contains(part));
        assertEquals(otherPanel,
                     part.getParentPanel());
        assertFalse(panelDefinition.getParts().contains(part));
    }

    @Test
    public void addPartTwiceShouldWork() throws Exception {
        assertFalse(panelDefinition.getParts().contains(part));
        panelDefinition.addPart(part);
        panelDefinition.addPart(part);
        assertTrue(panelDefinition.getParts().contains(part));
    }

    @Test
    public void partShouldNotBePresentAfterRemoval() throws Exception {
        panelDefinition.addPart(part);
        assertNotNull(part.getParentPanel());
        assertTrue(panelDefinition.getParts().contains(part));
        panelDefinition.removePart(part);
        assertNull(part.getParentPanel());
        assertFalse(panelDefinition.getParts().contains(part));
    }

    @Test
    public void removeNonexistentPartShouldDoNothingAndReturnFalse() throws Exception {
        boolean result = panelDefinition.removePart(part);
        assertEquals(false,
                     result);
    }

    @Test
    public void widthShouldNotRevertOnceSet() throws Exception {
        assertNull(panelDefinition.getWidth());
        panelDefinition.setWidth(1234);
        panelDefinition.setWidth(null);
        assertEquals((Integer) 1234,
                     panelDefinition.getWidth());
    }

    @Test
    public void heightShouldNotRevertOnceSet() throws Exception {
        assertNull(panelDefinition.getHeight());
        panelDefinition.setHeight(1234);
        panelDefinition.setHeight(null);
        assertEquals((Integer) 1234,
                     panelDefinition.getHeight());
    }

    @Test
    public void appendChildShouldAddPanelToChildren() {
        panelDefinition.appendChild(otherPanel);
        assertTrue(panelDefinition.getChildren().contains(otherPanel));
        assertEquals(panelDefinition,
                     otherPanel.getParent());
    }

    @Test
    public void appendChildToPanelTwiceShouldWork() {
        panelDefinition.appendChild(otherPanel);
        assertTrue(panelDefinition.getChildren().contains(otherPanel));
        panelDefinition.appendChild(otherPanel);
        assertTrue(panelDefinition.getChildren().contains(otherPanel));
    }
}
