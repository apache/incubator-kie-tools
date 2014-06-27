package org.uberfire.workbench.model;

import org.junit.Test;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;


import static org.junit.Assert.*;

/**
 *  Test panel hierarchy constraints.
 */
public class TestWorkbenchModel {


    @Test
    public void testPanelHierarchyInsert() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(PanelType.ROOT_SIMPLE);
        perspective.setTransient(false);
        perspective.setName("perspective");


        PanelDefinition westPanel = new PanelDefinitionImpl(PanelType.MULTI_LIST);
        PanelDefinition eastPanel = new PanelDefinitionImpl(PanelType.MULTI_LIST);

        PartDefinition part1 = new PartDefinitionImpl(new DefaultPlaceRequest("one"));
        PartDefinition part2 = new PartDefinitionImpl(new DefaultPlaceRequest("two"));

        perspective.getRoot().insertChild(CompassPosition.WEST, westPanel);
        perspective.getRoot().insertChild(CompassPosition.EAST, eastPanel);

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
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(PanelType.ROOT_SIMPLE);
        perspective.setTransient(false);
        perspective.setName("perspective");


        PanelDefinition westPanel = new PanelDefinitionImpl(PanelType.MULTI_LIST);
        PanelDefinition eastPanel = new PanelDefinitionImpl(PanelType.MULTI_LIST);

        PartDefinition part1 = new PartDefinitionImpl(new DefaultPlaceRequest("one"));
        PartDefinition part2 = new PartDefinitionImpl(new DefaultPlaceRequest("two"));

        perspective.getRoot().appendChild(CompassPosition.WEST, westPanel);
        perspective.getRoot().appendChild(CompassPosition.EAST, eastPanel);

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
    @Test(expected=IllegalStateException.class)
    public void testDetachedPanels() {
        PanelDefinition westPanel = new PanelDefinitionImpl(PanelType.MULTI_LIST);
        PartDefinition part1 = new PartDefinitionImpl(new DefaultPlaceRequest("one"));

        westPanel.addPart(part1);

        assertTrue(part1.getParentPanel() == westPanel);
        westPanel.getParent(); // does blow up
    }

}

