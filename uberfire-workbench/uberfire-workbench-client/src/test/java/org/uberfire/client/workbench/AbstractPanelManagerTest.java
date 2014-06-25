package org.uberfire.client.workbench;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
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

    @InjectMocks
    TestingPanelManagerImpl panelManager;

    @Before
    public void setup() {
        PerspectiveDefinition testPerspectiveDef = new PerspectiveDefinitionImpl( PanelType.ROOT_SIMPLE );
        WorkbenchPanelPresenter testPerspectiveRootPanelPresenter = mock( WorkbenchPanelPresenter.class );

        when( beanFactory.newWorkbenchPanel( testPerspectiveDef.getRoot() )).thenReturn( testPerspectiveRootPanelPresenter );

        when( beanFactory.newWorkbenchPart( any( Menus.class ),
                                            any( String.class ),
                                            any( IsWidget.class ),
                                            any( PartDefinition.class ) ) ).thenReturn( mock( WorkbenchPartPresenter.class) );

        panelManager.setPerspective( testPerspectiveDef );
    }

    @Test
    public void shouldAddPanel() throws Exception {
        PlaceRequest westPlace = new DefaultPlaceRequest( "westPlace" );
        PartDefinition westPart = new PartDefinitionImpl( westPlace );
        Menus westMenus = MenuFactory.newContributedMenu( "WestMenu" ).endMenu().build();
        UIPart westUiPart = new UIPart( "West", null, mock(IsWidget.class) );
        panelManager.addWorkbenchPart( westPlace, westPart, panelManager.getRoot(), westMenus, westUiPart, "westContextId" );

        verify( selectPlaceEvent ).fire( refEq( new SelectPlaceEvent( westPlace) ) );
        //TODO: verify other side effects
    }


    public static class TestingPanelManagerImpl extends AbstractPanelManagerImpl {

        private BeanFactory beanFactory;

        TestingPanelManagerImpl() {
            super( (Panel) GWT.create( Panel.class ),
                   (Panel) GWT.create( Panel.class ),
                   (Panel) GWT.create( Panel.class ) );
        }

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
            System.out.println("ADDING PANEL " + childPanel);
            return null;
        }

        @Override
        protected BeanFactory getBeanFactory() {
            return beanFactory;
        }

        // TODO remove the following 2 methods after WorkbenchLayout merge from Heiko

        @Override
        public void setWorkbenchSize( int width,
                                      int height ) {
            throw new UnsupportedOperationException( "Not implemented." );
        }

        @Override
        protected void arrangePanelsInDOM() {
            // no op
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
