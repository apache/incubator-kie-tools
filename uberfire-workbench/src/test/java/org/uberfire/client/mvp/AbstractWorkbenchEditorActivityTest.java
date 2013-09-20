package org.uberfire.client.mvp;

import java.util.HashSet;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.SelectPlaceEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

/**
 * Initial (poor coverage) integration tests for PlaceManager, PanelManager and
 * life-cycle events. There remains a lot more work to do in this class.
 */
@Ignore
public class AbstractWorkbenchEditorActivityTest extends BaseWorkbenchTest {

    @Test
    //Reveal a Place once. It should be launched, OnStartup and OnOpen called once.
    public void testGoToOnePlace() throws Exception {
        final String uri = "a/path/to/somewhere";
        final PlaceRequest somewhere = new PathPlaceRequest( new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public String getFileName() {
                return "somewhere";
            }

            @Override
            public String toURI() {
                return uri;
            }

            @Override
            public int compareTo( final Path o ) {
                return 0;
            }

        } );

        final ObservablePath path = mock( ObservablePath.class );
        doReturn( uri ).when( path ).toURI();
        final WorkbenchEditorActivity activity = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy = spy( activity );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager.goTo( somewhere );

        verify( spy ).launch( any( AcceptItem.class ),
                              eq( somewhere ),
                              isNull( Command.class ) );
        verify( spy ).onStartup( argThat( new EqualPaths( path ) ),
                                 eq( somewhere ) );
        verify( spy ).onOpen();

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectPlaceEvent.class ) );
    }

    @Test
    //Reveal the same Place twice. It should be launched, OnStartup and OnOpen called once.
    public void testGoToOnePlaceTwice() throws Exception {
        final String uri = "a/path/to/somewhere";
        final PlaceRequest somewhere = new PathPlaceRequest( new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public String getFileName() {
                return "somewhere";
            }

            @Override
            public String toURI() {
                return uri;
            }

            @Override
            public int compareTo( final Path o ) {
                return 0;
            }
        } );
        final PlaceRequest somewhereTheSame = somewhere.clone();

        final ObservablePath path = mock( ObservablePath.class );
        doReturn( uri ).when( path ).toURI();
        final WorkbenchEditorActivity activity = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy = spy( activity );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        when( activityManager.getActivities( somewhereTheSame ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereTheSame );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( spy,
                times( 1 ) ).onStartup( argThat( new EqualPaths( path ) ),
                                        eq( somewhere ) );
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
        final PlaceRequest somewhere = new PathPlaceRequest( new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public String getFileName() {
                return "somewhere";
            }

            @Override
            public String toURI() {
                return uri1;
            }

            @Override
            public int compareTo( final Path o ) {
                return 0;
            }
        } );

        final PlaceRequest somewhereElse = new PathPlaceRequest( new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public String getFileName() {
                return "else";
            }

            @Override
            public String toURI() {
                return uri2;
            }

            @Override
            public int compareTo( final Path o ) {
                return 0;
            }
        } );

        //The first place
        final ObservablePath path1 = mock( ObservablePath.class );
        doReturn( uri1 ).when( path1 ).toURI();
        final WorkbenchEditorActivity activity1 = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy1 = spy( activity1 );

        //The second place
        final ObservablePath path2 = mock( ObservablePath.class );
        doReturn( uri2 ).when( path2 ).toURI();
        final WorkbenchEditorActivity activity2 = new MockWorkbenchEditorActivity( placeManager );
        final WorkbenchEditorActivity spy2 = spy( activity2 );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy1 );
        }} );
        when( activityManager.getActivities( somewhereElse ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy2 );
        }} );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereElse );

        verify( spy1,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( spy1,
                times( 1 ) ).onStartup( argThat( new EqualPaths( path1 ) ),
                                        eq( somewhere ) );
        verify( spy1,
                times( 1 ) ).onOpen();

        verify( spy2,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhereElse ),
                                     isNull( Command.class ) );
        verify( spy2,
                times( 1 ) ).onStartup( argThat( new EqualPaths( path2 ) ),
                                        eq( somewhereElse ) );
        verify( spy2,
                times( 1 ) ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );

    }

    private class EqualPaths extends ArgumentMatcher<ObservablePath> {

        private Path path;

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
