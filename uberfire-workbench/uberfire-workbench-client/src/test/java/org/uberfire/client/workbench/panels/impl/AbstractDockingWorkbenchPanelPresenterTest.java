package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behaviours required by all subclasses of AbstractDockingWorkbenchPanelPresenter.
 */
public abstract class AbstractDockingWorkbenchPanelPresenterTest extends AbstractWorkbenchPanelPresenterTest {

    @Mock protected PanelManager panelManager;

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

        when( westChildPanelPresenter.getPanels() ).thenReturn( ImmutableMap.of( (Position) CompassPosition.WEST, westChildChild) );

        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.addPanel( westChildPanelPresenter, CompassPosition.WEST );
        panelPresenter.removePanel( westChildPanelPresenter );

        // the child of the removed child should have been placed into our WEST child slot
        assertEquals( CompassPosition.WEST, panelPresenter.positionOf( westChildChild ) );
    }

}
