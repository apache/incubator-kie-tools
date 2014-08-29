package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

/**
 * Tests for the general contract that any implementation of {@link AbstractMultiPartWorkbenchPanelPresenter} must
 * follow. Tests for each concrete implementation of AbstractMultiPartWorkbenchPanelPresenter should extend this one to
 * verify they follow the contract.
 */
public abstract class AbstractMultiPartWorkbenchPanelPresenterTest extends AbstractDockingWorkbenchPanelPresenterTest {

    @Mock(name="view")
    protected WorkbenchPanelView<MultiListWorkbenchPanelPresenter> view;

    /**
     * Narrowing return type to multi-part presenters.
     */
    @Override
    abstract AbstractMultiPartWorkbenchPanelPresenter<?> getPresenterToTest();

    @Test
    public void knownContextualPartsShouldResolveToTheirOwnContext() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        final ContextActivity myContextActivity = mock( ContextActivity.class );
        when( mockActivityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest( "myContextId" ) ) ).thenReturn( myContextActivity );

        presenter.addPart( mockPartPresenter, "myContextId" );

        final ContextActivity resolvedContextActivity = presenter.resolveContext( partPresenterPartDefinition );
        assertSame( myContextActivity, resolvedContextActivity );
    }

    /**
     * This requirement holds when there is no panel context.
     */
    @Test
    public void unknownPartsShouldResolveToPerspectiveContext() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        final ContextActivity resolvedPerspectiveContextActivity = presenter.resolveContext( new PartDefinitionImpl( new DefaultPlaceRequest( "randomUnknownPlace" ) ) );
        assertSame( perspectiveContextActivity, resolvedPerspectiveContextActivity );
    }

    /**
     * If there is a panel context, it overrides the perspective context.
     */
    @Test
    public void unknownPartsShouldResolveToPanelContextWhenThereIsOne() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        ContextDefinition panelContextDefinition = new ContextDefinitionImpl( new DefaultPlaceRequest( "panelDefinition" ) );
        ContextActivity panelContextActivity = mock( ContextActivity.class );

        when( mockActivityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest("panelDefinition") ) ).thenReturn( panelContextActivity );

        panelPresenterPanelDefinition.setContextDefinition( panelContextDefinition );
        presenter.setDefinition( panelPresenterPanelDefinition );

        final ContextActivity resolvedContextActivity = presenter.resolveContext( new PartDefinitionImpl( new DefaultPlaceRequest( "randomUnknownPlace" ) ) );
        assertSame( panelContextActivity, resolvedContextActivity );
    }

    @Test
    public void presenterShouldFreeRemovedParts() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        ContextActivity fakeContextActivity = mock( ContextActivity.class );
        when( mockActivityManager.getActivity( eq( ContextActivity.class ), any( PlaceRequest.class ) ) ).thenReturn( fakeContextActivity );
        presenter.addPart( mockPartPresenter, "randomContextId" );

        presenter.removePart( mockPartPresenter.getDefinition() );

        // if the part we added and removed is now unknown, we should get the perspective's context
        assertSame( perspectiveContextActivity, presenter.resolveContext( mockPartView.getPresenter().getDefinition() ) );

    }

    @SuppressWarnings("unchecked")
    @Test
    public void addedChildPanelsShouldBeRemembered() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        when( childPanelPresenter.getDefinition() ).thenReturn( childPanelDef );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        presenter.addPanel( childPanelPresenter, CompassPosition.NORTH );

        assertSame( childPanelPresenter, presenter.getPanels().get( CompassPosition.NORTH ) );
        assertEquals( childPanelDef, presenter.getDefinition().getChild( CompassPosition.NORTH ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removedChildPanelsShouldBeForgotten() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        when( childPanelPresenter.getDefinition() ).thenReturn( childPanelDef );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        presenter.addPanel( childPanelPresenter, CompassPosition.NORTH );
        boolean removed = presenter.removePanel( childPanelPresenter );

        assertTrue( removed );
        assertNull( presenter.getPanels().get( CompassPosition.NORTH ) );
        assertNull( presenter.getDefinition().getChild( CompassPosition.NORTH ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addingChildPanelShouldUpdateParentPointers() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        when( childPanelPresenter.getDefinition() ).thenReturn( childPanelDef );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        presenter.addPanel( childPanelPresenter, CompassPosition.NORTH );

        verify( childPanelPresenter ).setParent( presenter );
        assertSame( presenter.getDefinition(), childPanelDef.getParent() );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removingChildPanelShouldClearParentPointers() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        presenter.addPanel( childPanelPresenter, CompassPosition.NORTH );
        presenter.removePanel( childPanelPresenter );

        verify( childPanelPresenter ).setParent( null );
        assertNull( childPanelDef.getParent() );
    }

    @Test
    public void removingUnknownPanelShouldReturnFalse() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );

        boolean removed = presenter.removePanel( childPanelPresenter );

        assertFalse( removed );
    }

    @Test
    public void removingUnknownPanelShouldNotAffectExistingOnes() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        when( childPanelPresenter.getDefinition() ).thenReturn( childPanelDef );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        WorkbenchPanelPresenter unknownPanelPresenter = mock( WorkbenchPanelPresenter.class );

        presenter.addPanel( childPanelPresenter, CompassPosition.NORTH );
        boolean removed = presenter.removePanel( unknownPanelPresenter );

        assertFalse( removed );
        assertSame( childPanelPresenter, presenter.getPanels().get( CompassPosition.NORTH ) );
        assertEquals( childPanelDef, presenter.getDefinition().getChild( CompassPosition.NORTH ) );
        verify( childPanelPresenter, never() ).setParent( null );
    }

}
