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

package org.uberfire.client.workbench.panels.impl;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests behaviours required by all subclasses of AbstractDockingWorkbenchPanelPresenter.
 */
public abstract class AbstractDockingWorkbenchPanelPresenterTest extends AbstractWorkbenchPanelPresenterTest {

    @Mock
    protected PanelManager panelManager;

    /**
     * The individual test classes that extend this base class implement this method by returning the implementation of
     * AbstractDockingWorkbenchPanelPresenter that they want to test. The protected mock objects set up by this base class
     * should be given to the presenter's constructor.
     */
    @Override
    abstract AbstractDockingWorkbenchPanelPresenter<?> getPresenterToTest();

    @Test
    public void removingLastPartFromPanelShouldRemovePanelToo() throws Exception {
        WorkbenchPanelPresenter westChildPanelPresenter = mock( SimpleWorkbenchPanelPresenter.class );
        WorkbenchPanelPresenter parentPanelPresenter = mock( SimpleWorkbenchPanelPresenter.class );

        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.setDefinition( panelPresenterPanelDefinition );

        panelPresenter.setParent( parentPanelPresenter );
        panelPresenter.addPanel( westChildPanelPresenter, CompassPosition.WEST );
        panelPresenter.addPart( mockPartPresenter );

        panelPresenter.removePart( mockPartPresenter.getDefinition() );

        // the now-empty panel should have removed itself
        verify( panelManager ).removeWorkbenchPanel( panelPresenterPanelDefinition );
    }

    @Test
    public void childrenOfRemovedPanelsShouldBeRescued() throws Exception {
        WorkbenchPanelPresenter westChildPanelPresenter = mock( SimpleWorkbenchPanelPresenter.class );
        WorkbenchPanelPresenter westChildChild = mock( SimpleWorkbenchPanelPresenter.class );

        when( westChildPanelPresenter.getPanels() ).thenReturn( ImmutableMap.of( (Position) CompassPosition.WEST, westChildChild ) );

        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.addPanel( westChildPanelPresenter, CompassPosition.WEST );
        panelPresenter.removePanel( westChildPanelPresenter );

        // the child of the removed child should have been placed into our WEST child slot
        assertEquals( CompassPosition.WEST, panelPresenter.positionOf( westChildChild ) );
    }

    @Test
    public void addingMultiplePanelsInTheSamePosition() throws Exception {
        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.setDefinition( panelPresenterPanelDefinition );

        WorkbenchPanelPresenter southRootChild1 = mock( SimpleWorkbenchPanelPresenter.class );
        WorkbenchPanelPresenter southRootChild2 = mock( SimpleWorkbenchPanelPresenter.class );

        final PanelDefinition southRootChild1PanelDefinition = new PanelDefinitionImpl();
        final PanelDefinition southRootChild2PanelDefinition = new PanelDefinitionImpl();
        when( southRootChild1.getDefinition() ).thenReturn( southRootChild1PanelDefinition );
        when( southRootChild2.getDefinition() ).thenReturn( southRootChild2PanelDefinition );

        panelPresenter.addPanel( southRootChild1, CompassPosition.SOUTH );
        panelPresenter.addPanel( southRootChild2, CompassPosition.SOUTH );

        verify( panelPresenter.getPanelView() ).addPanel( southRootChild1PanelDefinition,
                                                          southRootChild1.getPanelView(),
                                                          CompassPosition.SOUTH );
        verify( panelPresenter.getPanelView() ).addPanel( southRootChild2PanelDefinition,
                                                          southRootChild2.getPanelView(),
                                                          CompassPosition.SOUTH );

        //Root contains 1 child, which is the last Panel to be added to Root
        assertTrue( panelPresenter.getDefinition().getChildren().contains( southRootChild2.getDefinition() ) );

        //Root's View contains 2 Panels in the SOUTH position therefore the SOUTH position should be DEFAULT_CHILD_SIZE * 2 in size
        verify( panelPresenter.getPanelView() ).setChildSize( southRootChild1.getPanelView(),
                                                              Layouts.DEFAULT_CHILD_SIZE * 2 );
    }

}
