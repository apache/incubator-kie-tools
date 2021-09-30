/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.workbench.model;

import org.junit.Test;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.junit.Assert.*;

/**
 * Test panel hierarchy constraints.
 */
public class TestWorkbenchModel {

    @Test
    public void testPanelHierarchyInsert() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl("what.ever.panel.PresenterClass");
        perspective.setName("perspective");

        PanelDefinition westPanel = new PanelDefinitionImpl("what.ever.panel.PresenterClass");
        PanelDefinition eastPanel = new PanelDefinitionImpl("what.ever.panel.PresenterClass");

        PartDefinition part1 = new PartDefinitionImpl(new DefaultPlaceRequest("one"));
        PartDefinition part2 = new PartDefinitionImpl(new DefaultPlaceRequest("two"));

        perspective.getRoot().insertChild(CompassPosition.WEST,
                                          westPanel);
        perspective.getRoot().insertChild(CompassPosition.EAST,
                                          eastPanel);

        westPanel.addPart(part1);
        eastPanel.addPart(part2);

        assertTrue(part1.getParentPanel() == westPanel);
        assertTrue(westPanel.getParent() == perspective.getRoot()); // TODO: panel equality?

        assertTrue(part2.getParentPanel() == eastPanel);
        assertTrue(eastPanel.getParent() == perspective.getRoot());

        assertTrue(perspective.getRoot().getParent() == null);
    }

    @Test
    public void testPanelHierarchyAppend() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl("what.ever.panel.PresenterClass");
        perspective.setName("perspective");

        PanelDefinition westPanel = new PanelDefinitionImpl("what.ever.panel.PresenterClass");
        PanelDefinition eastPanel = new PanelDefinitionImpl("what.ever.panel.PresenterClass");

        PartDefinition part1 = new PartDefinitionImpl(new DefaultPlaceRequest("one"));
        PartDefinition part2 = new PartDefinitionImpl(new DefaultPlaceRequest("two"));

        perspective.getRoot().appendChild(CompassPosition.WEST,
                                          westPanel);
        perspective.getRoot().appendChild(CompassPosition.EAST,
                                          eastPanel);

        westPanel.addPart(part1);
        eastPanel.addPart(part2);

        assertTrue(part1.getParentPanel() == westPanel);
        assertTrue(westPanel.getParent() == perspective.getRoot()); // TODO: panel equality?

        assertTrue(part2.getParentPanel() == eastPanel);
        assertTrue(eastPanel.getParent() == perspective.getRoot());

        assertTrue(perspective.getRoot().getParent() == null);
    }

    /**
     * Parent traversal {@link PanelDefinition#getParent()} does yield IllegalStateException when the panel is not
     * part of a hierarchy. There might be two reasons for this: Either the implicit parent/child wiring is broken
     * or the explicit perspective setup is not yet completed.
     */
    @Test(expected = IllegalStateException.class)
    public void testDetachedPanels() {
        PanelDefinition westPanel = new PanelDefinitionImpl("what.ever.panel.PresenterClass");
        PartDefinition part1 = new PartDefinitionImpl(new DefaultPlaceRequest("one"));

        westPanel.addPart(part1);

        assertTrue(part1.getParentPanel() == westPanel);
        westPanel.getParent(); // does blow up
    }
}

