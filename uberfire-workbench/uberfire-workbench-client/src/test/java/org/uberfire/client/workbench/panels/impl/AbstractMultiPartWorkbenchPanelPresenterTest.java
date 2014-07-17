package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * Tests for the general contract that any implementation of {@link AbstractMultiPartWorkbenchPanelPresenter} must
 * follow. Tests for each concrete implementation of AbstractMultiPartWorkbenchPanelPresenter should extend this one to
 * verify they follow the contract.
 */
public abstract class AbstractMultiPartWorkbenchPanelPresenterTest {

    @Mock(name="view")
    protected WorkbenchPanelView<MultiListWorkbenchPanelPresenter> view;

    @Mock protected ActivityManager mockActivityManager;
    @Mock protected PerspectiveManager mockPerspectiveManager;
    @Mock protected Event<MaximizePlaceEvent> maximizePanelEvent;
    @Mock protected View mockPartView;
    @Mock protected WorkbenchPartPresenter mockPartPresenter;
    @Mock protected ContextActivity perspectiveContextActivity;

    protected final PerspectiveDefinition panelManagerPerspectiveDefinition = new PerspectiveDefinitionImpl();
    protected final PartDefinition partPresenterPartDefinition = new PartDefinitionImpl( new DefaultPlaceRequest( "belongs_to_mockPartPresenter" ) );
    protected final PanelDefinition panelPresenterPanelDefinition = new PanelDefinitionImpl();
    protected final ContextDefinition perspectiveContextDefinition = new ContextDefinitionImpl( new DefaultPlaceRequest( "Perspective Context" ) );

    /**
     * The individual test classes that extend this base class implement this method by returning the implementation of
     * MultiPartWorkbenchPanelPresenter that they want to test. The protected mock objects set up by this base class
     * should be given to the presenter's constructor.
     */
    abstract AbstractMultiPartWorkbenchPanelPresenter<?> getPresenterToTest();

    @Before
    public void setUp() {
        when( mockPerspectiveManager.getLivePerspectiveDefinition() ).thenReturn( panelManagerPerspectiveDefinition );
        panelManagerPerspectiveDefinition.setContextDefinition( perspectiveContextDefinition );
        when( mockActivityManager.getActivity( ContextActivity.class, perspectiveContextDefinition.getPlace() ) ).thenReturn( perspectiveContextActivity );
        when( mockPartView.getPresenter() ).thenReturn( mockPartPresenter );
        when( mockPartPresenter.getDefinition() ).thenReturn( partPresenterPartDefinition );
    }

    @Test
    public void knownContextualPartsShouldResolveToTheirOwnContext() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        final ContextActivity myContextActivity = mock( ContextActivity.class );
        when( mockActivityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest( "myContextId" ) ) ).thenReturn( myContextActivity );

        presenter.addPart( mockPartView, "myContextId" );

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
        presenter.addPart( mockPartView, "randomContextId" );

        presenter.removePart( mockPartView.getPresenter().getDefinition() );

        // if the part we added and removed is now unknown, we should get the perspective's context
        assertSame( perspectiveContextActivity, presenter.resolveContext( mockPartView.getPresenter().getDefinition() ) );

    }

    @SuppressWarnings("unchecked")
    @Test
    public void addedChildPanelsShouldBeRemembered() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        presenter.addPanel( childPanelDef, childPanelView, CompassPosition.NORTH );

        assertSame( childPanelPresenter, presenter.getPanels().get( CompassPosition.NORTH ) );
        assertEquals( childPanelDef, presenter.getDefinition().getChild( CompassPosition.NORTH ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removedChildPanelsShouldBeForgotten() throws Exception {
        AbstractMultiPartWorkbenchPanelPresenter<?> presenter = getPresenterToTest();

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        presenter.addPanel( childPanelDef, childPanelView, CompassPosition.NORTH );
        boolean removed = presenter.removePanel( childPanelPresenter );

        assertTrue( removed );
        assertNull( presenter.getPanels().get( CompassPosition.NORTH ) );
        assertNull( presenter.getDefinition().getChild( CompassPosition.NORTH ) );
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

        PanelDefinitionImpl childPanelDef = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
        WorkbenchPanelView<WorkbenchPanelPresenter> childPanelView = mock( WorkbenchPanelView.class );
        when( childPanelView.getPresenter() ).thenReturn( childPanelPresenter );

        WorkbenchPanelPresenter unknownPanelPresenter = mock( WorkbenchPanelPresenter.class );

        presenter.addPanel( childPanelDef, childPanelView, CompassPosition.NORTH );
        boolean removed = presenter.removePanel( unknownPanelPresenter );

        assertFalse( removed );
        assertSame( childPanelPresenter, presenter.getPanels().get( CompassPosition.NORTH ) );
        assertEquals( childPanelDef, presenter.getDefinition().getChild( CompassPosition.NORTH ) );
    }

}
