package org.uberfire.client.workbench;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractPanelManagerTest {

    @Mock BeanFactory beanFactory;
    @Mock StubPlaceGainFocusEvent placeGainFocusEvent;
    @Mock StubPlaceLostFocusEvent placeLostFocusEvent;
    @Mock StubSelectPlaceEvent selectPlaceEvent;
    @Mock StubPanelFocusEvent panelFocusEvent;
    @Mock WorkbenchStatusBarPresenter statusBar;
    @Mock SimpleWorkbenchPanelPresenter workbenchPanelPresenter;
    @Mock(answer=Answers.RETURNS_DEEP_STUBS) LayoutSelection layoutSelection;

    @InjectMocks
    TestingPanelManagerImpl panelManager;

    /**
     * This is the part presenter that will be returned by the mock BeanFactory in response to any newWorkbenchPart()
     * call. Tests that work with more than one part will have to create their own more specific when/then rules.
     */
    private WorkbenchPartPresenter partPresenter;

    /**
     * This perspective is set as the current perspective on the PanelManager before each test is run.
     */
    private PerspectiveDefinition testPerspectiveDef;

    /**
     * This is the Panel Presenter returned by the mock BeanFactory when asked for <tt>newWorkbenchPanel( testPerspectiveDef.getRoot() ) )</tt>.
     */
    private WorkbenchPanelPresenter testPerspectiveRootPanelPresenter;

    @Before
    public void setup() {
        when( layoutSelection.get().getPerspectiveContainer() ).thenReturn( mock( HasWidgets.class ) );

        testPerspectiveDef = new PerspectiveDefinitionImpl( PanelType.ROOT_SIMPLE );
        testPerspectiveRootPanelPresenter = mock( WorkbenchPanelPresenter.class );

        when( beanFactory.newWorkbenchPanel( testPerspectiveDef.getRoot() )).thenReturn( testPerspectiveRootPanelPresenter );
        when( testPerspectiveRootPanelPresenter.getDefinition() ).thenReturn( testPerspectiveDef.getRoot() );
        when( testPerspectiveRootPanelPresenter.getPanelView() ).thenReturn( mock( WorkbenchPanelView.class ) );

        partPresenter = mock( WorkbenchPartPresenter.class);
        when( beanFactory.newWorkbenchPart( any( Menus.class ),
                                            any( String.class ),
                                            any( IsWidget.class ),
                                            any( PartDefinition.class ) ) ).thenReturn( partPresenter );

        panelManager.setPerspective( testPerspectiveDef );
    }

    @Test
    public void addPartToRootPanelShouldWork() throws Exception {
        PlaceRequest rootPartPlace = new DefaultPlaceRequest( "rootPartPlace" );
        PartDefinition rootPart = new PartDefinitionImpl( rootPartPlace );
        Menus rootPartMenus = MenuFactory.newContributedMenu( "RootPartMenu" ).endMenu().build();
        UIPart rootUiPart = new UIPart( "RootUiPart", null, mock(IsWidget.class) );
        panelManager.addWorkbenchPart( rootPartPlace, rootPart, panelManager.getRoot(), rootPartMenus, rootUiPart, "rootContextId" );

        // the presenter should have been created and configured for the rootPart
        verify( partPresenter ).setWrappedWidget( rootUiPart.getWidget() );
        verify( partPresenter ).setContextId( "rootContextId" );

        // the part's new presenter should have been added to the root panel presenter
        verify( testPerspectiveRootPanelPresenter ).addPart( partPresenter.getPartView(), "rootContextId" );

        // the root panel's definition should have been updated to include the new part
        assertEquals( rootPart, testPerspectiveDef.getRoot().getParts().iterator().next() );

        // the panel manager should be aware of the place/part mapping for the added part
        assertEquals( rootPart, panelManager.getPartForPlace( rootPartPlace ) );

        // the panel manager should select the place, firing a general notification
        verify( selectPlaceEvent ).fire( refEq( new SelectPlaceEvent( rootPartPlace ) ) );
    }

    /**
     * Tests that PanelManager avoids duplicating PartDefinitions inside already-populated PanelDefinitions when
     * building up a perspective.
     */
    @Test
    public void addPartThatIsAlreadyInPanelDefShouldNotChangePanelDef() throws Exception {
        PlaceRequest rootPartPlace = new DefaultPlaceRequest( "rootPartPlace" );
        PartDefinition rootPart = new PartDefinitionImpl( rootPartPlace );
        Menus rootPartMenus = MenuFactory.newContributedMenu( "RootPartMenu" ).endMenu().build();
        UIPart rootUiPart = new UIPart( "RootUiPart", null, mock(IsWidget.class) );

        // pre-adding the part def to the panel def to simulate perspective building operation
        panelManager.getRoot().addPart( rootPart );

        panelManager.addWorkbenchPart( rootPartPlace, rootPart, panelManager.getRoot(), rootPartMenus, rootUiPart, "rootContextId" );

        // the presenter should have been created and configured for the rootPart
        verify( partPresenter ).setWrappedWidget( rootUiPart.getWidget() );
        verify( partPresenter ).setContextId( "rootContextId" );

        // the part's new presenter should have been added to the root panel presenter
        verify( testPerspectiveRootPanelPresenter ).addPart( partPresenter.getPartView(), "rootContextId" );

        // there should still only be 1 part
        assertEquals( 1, testPerspectiveDef.getRoot().getParts().size() );

        // the panel manager should be aware of the place/part mapping for the added part
        assertEquals( rootPart, panelManager.getPartForPlace( rootPartPlace ) );

        // the panel manager should select the place, firing a general notification
        verify( selectPlaceEvent ).fire( refEq( new SelectPlaceEvent( rootPartPlace ) ) );
    }

    @Test
    public void addMinimizedPartToRootPanelShouldWork() throws Exception {
        PlaceRequest rootPartPlace = new DefaultPlaceRequest( "rootPartPlace" );
        PartDefinition rootPart = new PartDefinitionImpl( rootPartPlace );
        rootPart.setMinimized( true );
        Menus rootPartMenus = MenuFactory.newContributedMenu( "RootPartMenu" ).endMenu().build();
        UIPart rootUiPart = new UIPart( "RootUiPart", null, mock(IsWidget.class) );
        panelManager.addWorkbenchPart( rootPartPlace, rootPart, panelManager.getRoot(), rootPartMenus, rootUiPart, "rootContextId" );

        // the presenter should have been created and configured for the rootPart
        verify( partPresenter ).setWrappedWidget( rootUiPart.getWidget() );
        verify( partPresenter ).setContextId( "rootContextId" );

        // minimized parts do not belong to the target panel when minimized, but they do go to the status bar
        verify( testPerspectiveRootPanelPresenter, never() ).addPart( partPresenter.getPartView(), "rootContextId" );
        verify( statusBar ).addMinimizedPlace( rootPartPlace );

        // even so, it should have been added to the root panel's definition
        assertEquals( rootPart, testPerspectiveDef.getRoot().getParts().iterator().next() );

        // the panel manager should be aware of the place/part mapping for the added part
        assertEquals( rootPart, panelManager.getPartForPlace( rootPartPlace ) );

        // the panel manager should select the place, firing a general notification
        verify( selectPlaceEvent ).fire( refEq( new SelectPlaceEvent( rootPartPlace ) ) );
    }

    @Test
    public void addPartToUnknownPanelShouldFail() throws Exception {
        PlaceRequest partPlace = new DefaultPlaceRequest( "partPlace" );
        PartDefinition part = new PartDefinitionImpl( partPlace );
        Menus partMenus = MenuFactory.newContributedMenu( "PartMenu" ).endMenu().build();
        UIPart uiPart = new UIPart( "uiPart", null, mock(IsWidget.class) );
        PanelDefinition randomUnattachedPanel = new PanelDefinitionImpl( PanelType.SIMPLE );

        try {
            panelManager.addWorkbenchPart( partPlace, part, randomUnattachedPanel, partMenus, uiPart, "contextId" );
            fail();
        } catch ( IllegalArgumentException e ) {
            assertEquals( "Target panel is not part of the layout", e.getMessage() );
        }

        // the presenter should not have been created and configured for the rootPart
        verify( partPresenter, never() ).setWrappedWidget( uiPart.getWidget() );
        verify( partPresenter, never() ).setContextId( "rootContextId" );

        // the panel manager should not be aware of the place/part mapping for the failed add
        assertEquals( null, panelManager.getPartForPlace( partPlace ) );

        // the failed part/place should not be selected
        verify( selectPlaceEvent, never() ).fire( refEq( new SelectPlaceEvent( partPlace ) ) );
    }

    @Test
    public void removingLastPartFromRootPanelShouldLeaveRootPanel() throws Exception {
        // add
        PlaceRequest rootPartPlace = new DefaultPlaceRequest( "rootPartPlace" );
        PartDefinition rootPart = new PartDefinitionImpl( rootPartPlace );
        Menus rootPartMenus = MenuFactory.newContributedMenu( "RootPartMenu" ).endMenu().build();
        UIPart rootUiPart = new UIPart( "RootUiPart", null, mock(IsWidget.class) );
        panelManager.addWorkbenchPart( rootPartPlace, rootPart, panelManager.getRoot(), rootPartMenus, rootUiPart, "rootContextId" );

        panelManager.removePartForPlace( rootPartPlace );

        // the panel manager should not know about the part/place mapping anymore
        assertEquals( null, panelManager.getPartForPlace( rootPartPlace ) );

        // the part's presenter bean should be destroyed
        verify( beanFactory ).destroy( partPresenter );

        // the root panel itself, although empty, should remain (because it is the root panel)
        verify( beanFactory, never() ).destroy( testPerspectiveRootPanelPresenter );
    }

    @Test
    public void addingAPanelShouldWork() throws Exception {
        PanelDefinition subPanel = new PanelDefinitionImpl( PanelType.SIMPLE );
        testPerspectiveDef.getRoot().appendChild( CompassPosition.WEST, subPanel );
        panelManager.addWorkbenchPanel( panelManager.getRoot(), subPanel, CompassPosition.WEST );

        // need to remember this for later
        WorkbenchPanelPresenter subPanelPresenter = panelManager.mapPanelDefinitionToPresenter.get( subPanel );
    }

    @Test
    public void removingLastPartFromPanelShouldRemovePanelToo() throws Exception {
        PanelDefinition subPanel = new PanelDefinitionImpl( PanelType.SIMPLE );
        testPerspectiveDef.getRoot().appendChild( CompassPosition.WEST, subPanel );
        panelManager.addWorkbenchPanel( panelManager.getRoot(), subPanel, CompassPosition.WEST );

        // need to remember this for later
        WorkbenchPanelPresenter subPanelPresenter = panelManager.mapPanelDefinitionToPresenter.get( subPanel );

        PlaceRequest partPlace = new DefaultPlaceRequest( "partPlace" );
        PartDefinition part = new PartDefinitionImpl( partPlace );
        Menus partMenus = MenuFactory.newContributedMenu( "PartMenu" ).endMenu().build();
        UIPart uiPart = new UIPart( "uiPart", null, mock(IsWidget.class) );

        panelManager.addWorkbenchPart( partPlace, part, subPanel, partMenus, uiPart, "contextId" );

        panelManager.removePartForPlace( partPlace );

        // the panel manager should not know about the part/place mapping anymore
        assertEquals( null, panelManager.getPartForPlace( partPlace ) );

        // the part's presenter bean should be destroyed
        verify( beanFactory ).destroy( partPresenter );

        // the empty panel should be gone from the layout and its bean destroyed
        verify( beanFactory ).destroy( subPanelPresenter );
        assertFalse( panelManager.mapPanelDefinitionToPresenter.containsKey( subPanel ) );
    }

    // After UF-117:
    // TODO test part disposal (not NORTH/SOUTH/EAST/WEST) side effect of AbstractPanelManagerImpl.removePart()
    // TODO test part reattachment (NORTH/SOUTH/EAST/WEST) side effect of AbstractPanelManagerImpl.removePart()

    public static class TestingPanelManagerImpl extends AbstractPanelManagerImpl {

        private BeanFactory beanFactory;

        Multimap<PanelDefinition, PanelDefinition> panelHierarchy = ArrayListMultimap.create();
        PanelDefinition rootPanel = new PanelDefinitionImpl();

        @Override
        public boolean removePartForPlace( PlaceRequest toRemove ) {
            PartDefinition part = getPartForPlace( toRemove );
            if ( part != null ) {
                super.removePart( part );
                return true;
            }
            return false;
        }

        @Override
        public PanelDefinition addWorkbenchPanel( PanelDefinition targetPanel,
                                                  PanelDefinition childPanel,
                                                  Position position ) {
            panelHierarchy.put( targetPanel, childPanel );

            WorkbenchPanelPresenter childPanelPresenter = mock( WorkbenchPanelPresenter.class );
            when( childPanelPresenter.getDefinition() ).thenReturn( childPanel );

            mapPanelDefinitionToPresenter.put( childPanel, childPanelPresenter );
            return childPanel;
        }

        @Override
        protected BeanFactory getBeanFactory() {
            return beanFactory;
        }
    };

    /**
     * Mockito fails to produce a valid mock for a raw {@code Event<Anything>} due to classloader issues. Trivial
     * subclasses of this class provide Mockito something that it can mock successfully and inject into our
     * {@code @InjectMocks} object.
     */
    static class StubEventSource<T> implements Event<T> {

        @Override
        public void fire( T event ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public Event<T> select( Annotation... qualifiers ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public <U extends T> Event<U> select( Class<U> subtype, Annotation... qualifiers ) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    static class StubPlaceGainFocusEvent extends StubEventSource<PlaceGainFocusEvent> {}
    static class StubPlaceLostFocusEvent extends StubEventSource<PlaceLostFocusEvent> {}
    static class StubSelectPlaceEvent extends StubEventSource<SelectPlaceEvent> {}
    static class StubPanelFocusEvent extends StubEventSource<PanelFocusEvent> {}
}
