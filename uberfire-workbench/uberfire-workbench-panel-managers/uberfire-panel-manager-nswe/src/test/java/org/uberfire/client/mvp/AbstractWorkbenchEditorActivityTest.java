package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;

/**
 * Initial (poor coverage) integration tests for PlaceManager, PanelManager and
 * life-cycle events. There remains a lot more work to do in this class.
 */
public class AbstractWorkbenchEditorActivityTest extends BaseWorkbenchTest {

    @Test
    //Reveal a Place once. It should be launched, OnStartup and OnOpen called once.
    public void testGoToOnePlace() throws Exception {
        final String uri = "a/path/to/somewhere";
        final ObservablePath path = mock( ObservablePath.class );
        final PlaceRequest somewhere = new PathPlaceRequestUnitTestWrapper(path);


        doReturn( uri ).when( path ).toURI();
        final WorkbenchEditorActivity activity = new MockWorkbenchEditorActivity( placeManager );
        activity.onStartup( somewhere ); // normally, ActivityManager calls this before returning the activity

        final WorkbenchEditorActivity spy = spy( activity );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );


        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager, selectWorkbenchPartEvent );

        final PanelDefinition root = panelManager.getRoot();

        placeManager.goTo( somewhere, root );

        verify( spy, never() ).onStartup( any( ObservablePath.class ),
                                          any( PlaceRequest.class ) );
        verify( spy ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectPlaceEvent.class ) );
    }

    @Test
    //Reveal the same Place twice. It should be launched, OnStartup and OnOpen called once.
    public void testGoToOnePlaceTwice() throws Exception {
        final String uri = "a/path/to/somewhere";
        final ObservablePath path = mock( ObservablePath.class );
        final PlaceRequest somewhere =  new PathPlaceRequestUnitTestWrapper(path);

        final PlaceRequest somewhereTheSame = somewhere.clone();

        doReturn( uri ).when( path ).toURI();
        final WorkbenchEditorActivity activity = new MockWorkbenchEditorActivity( placeManager );
        activity.onStartup( somewhere ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchEditorActivity spy = spy( activity );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        when( activityManager.getActivities( somewhereTheSame ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );


        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager, selectWorkbenchPartEvent );
        final PanelDefinition root = panelManager.getRoot();

        placeManager.goTo( somewhere, root );
        placeManager.goTo( somewhereTheSame , root);

        verify( spy, never() ).onStartup( any( ObservablePath.class ),
                                          any( PlaceRequest.class ) );
        verify( spy,
                times( 1 ) ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );

    }

    @Test
    //Reveal two different Places. Each should be launched, OnStartup and OnOpen called once.
    public void testGoToTwoDifferentPlaces() throws Exception {
        final String uri1 = "a/path/to/somewhere";
        final String uri2 = "a/path/to/somewhere/else";

        //The first place

        final ObservablePath path1 = mock( ObservablePath.class );
        doReturn( uri1 ).when( path1 ).toURI();
        final PlaceRequest somewhere =  new PathPlaceRequestUnitTestWrapper(path1);
        final WorkbenchEditorActivity activity1 = new MockWorkbenchEditorActivity( placeManager );
        activity1.onStartup( somewhere ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchEditorActivity spy1 = spy( activity1 );

        //The second place
        final ObservablePath path2 = mock( ObservablePath.class );
        doReturn( uri2 ).when( path2 ).toURI();
        final PlaceRequest  somewhereElse =  new PathPlaceRequestUnitTestWrapper(path2);
        final WorkbenchEditorActivity activity2 = new MockWorkbenchEditorActivity( placeManager );
        activity2.onStartup( somewhereElse ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchEditorActivity spy2 = spy( activity2 );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy1 );
        }} );
        when( activityManager.getActivities( somewhereElse ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy2 );
        }} );

        placeManager = new PlaceManagerImplUnitTestWrapper( spy1, panelManager, selectWorkbenchPartEvent );
        final PanelDefinition root = panelManager.getRoot();

        placeManager.goTo( somewhere, root );
        //just to change the activity mock
        placeManager = new PlaceManagerImplUnitTestWrapper( spy2, panelManager, selectWorkbenchPartEvent );
        placeManager.goTo( somewhereElse , root);

        verify( spy1, never() ).onStartup( any( ObservablePath.class ),
                                           any( PlaceRequest.class ) );
        verify( spy1,
                times( 1 ) ).onOpen();

        verify( spy2, never() ).onStartup( any( ObservablePath.class ),
                                           any( PlaceRequest.class ) );
        verify( spy2,
                times( 1 ) ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );

    }

    private class EqualPaths extends ArgumentMatcher<ObservablePath> {

        private final Path path;

        private EqualPaths( final ObservablePath path ) {
            this.path = path;
        }

        @Override
        public boolean matches( Object argument ) {
            if ( argument instanceof ObservablePath ) {
                final ObservablePath that = (ObservablePath) argument;
                return that.toURI().equals( path.toURI() );
            }
            return false;
        }

    }

}
