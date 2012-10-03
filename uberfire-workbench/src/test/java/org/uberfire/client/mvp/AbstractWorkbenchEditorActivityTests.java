package org.uberfire.client.mvp;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * Initial (poor coverage) integration tests for PlaceManager, PanelManager and
 * life-cycle events. There remains a lot more work to do in this class.
 */
public class AbstractWorkbenchEditorActivityTests {

    private PlaceHistoryHandler             placeHistoryHandler;
    private ActivityManager                 activityManager;
    private Event<SelectWorkbenchPartEvent> event;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );
        event = mock( Event.class );
    }

    @Test
    //Reveal a Place once. It should be launched, OnStart and OnReveal called once.
    public void tesGoToOnePlace() throws Exception {
        final String uri = "a/path/to/somewhere";
        final PanelManager panelManager = mock( PanelManager.class );
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        somewhere.addParameter( "path",
                                uri );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    event,
                                                                    panelManager );

        final Path path = mock( Path.class );
        doReturn( uri ).when( path ).toURI();
        final WorkbenchEditorActivity activity = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy = spy( activity );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );

        verify( spy ).launch( any( AcceptItem.class ),
                              eq( somewhere ),
                              isNull( Command.class ) );
        verify( spy ).onStart( argThat( new EqualPaths( path ) ),
                               eq( somewhere ) );
        verify( spy ).onReveal();

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( event,
                times( 1 ) ).fire( any( SelectWorkbenchPartEvent.class ) );
    }

    @Test
    //Reveal the same Place twice. It should be launched, OnStart and OnReveal called once.
    public void tesGoToOnePlaceTwice() throws Exception {
        final String uri = "a/path/to/somewhere";
        final PanelManager panelManager = mock( PanelManager.class );
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        somewhere.addParameter( "path",
                                uri );
        final PlaceRequest somewhereTheSame = new DefaultPlaceRequest( "Somewhere" );
        somewhereTheSame.addParameter( "path",
                                       uri );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    event,
                                                                    panelManager );

        final Path path = mock( Path.class );
        doReturn( uri ).when( path ).toURI();
        final WorkbenchEditorActivity activity = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy = spy( activity );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereTheSame );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( spy,
                times( 1 ) ).onStart( argThat( new EqualPaths( path ) ),
                                      eq( somewhere ) );
        verify( spy,
                times( 1 ) ).onReveal();

        verify( event,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

    }

    @Test
    //Reveal two different Places. Each should be launched, OnStart and OnReveal called once.
    public void tesGoToTwoDifferentPlaces() throws Exception {
        final String uri1 = "a/path/to/somewhere";
        final String uri2 = "a/path/to/somewhere/else";
        final PanelManager panelManager = mock( PanelManager.class );
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        somewhere.addParameter( "path",
                                uri1 );
        final PlaceRequest somewhereElse = new DefaultPlaceRequest( "SomewhereElse" );
        somewhereElse.addParameter( "path",
                                    uri2 );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    event,
                                                                    panelManager );

        //The first place
        final Path path1 = mock( Path.class );
        doReturn( uri1 ).when( path1 ).toURI();
        final WorkbenchEditorActivity activity1 = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy1 = spy( activity1 );

        //The second place
        final Path path2 = mock( Path.class );
        doReturn( uri2 ).when( path2 ).toURI();
        final WorkbenchEditorActivity activity2 = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy2 = spy( activity2 );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy1 );
        when( activityManager.getActivity( somewhereElse ) ).thenReturn( spy2 );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereElse );

        verify( spy1,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( spy1,
                times( 1 ) ).onStart( argThat( new EqualPaths( path1 ) ),
                                      eq( somewhere ) );
        verify( spy1,
                times( 1 ) ).onReveal();

        verify( spy2,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhereElse ),
                                     isNull( Command.class ) );
        verify( spy2,
                times( 1 ) ).onStart( argThat( new EqualPaths( path2 ) ),
                                      eq( somewhereElse ) );
        verify( spy2,
                times( 1 ) ).onReveal();

        verify( event,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

    }

    private class EqualPaths extends ArgumentMatcher<Path> {

        private Path path;

        private EqualPaths(final Path path) {
            this.path = path;
        }

        @Override
        public boolean matches(Object argument) {
            if ( argument instanceof Path ) {
                final Path that = (Path) argument;
                return that.toURI().equals( path.toURI() );
            }
            return false;
        }

    }

}
