package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;

/**
 * Initial (poor coverage) integration tests for PlaceManager, PanelManager and
 * life-cycle events. There remains a lot more work to do in this class.
 */
public class AbstractWorkbenchScreenActivityTest extends BaseWorkbenchTest {

    @Test
    //Reveal a Place once. It should be launched, OnStartup and OnOpen called once.
    public void testGoToOnePlace() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        activity.onStartup( somewhere ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchScreenActivity spy = spy( activity );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );


        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager );

        final PanelDefinition root = panelManager.getRoot();

        placeManager.goTo( somewhere, root );

        verify( spy, never() ).onStartup( any( PlaceRequest.class ) );
        verify( spy ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectPlaceEvent.class ) );
    }

    @Test
    //Reveal the same Place twice. It should be launched, OnStartup and OnOpen called once.
    public void testGoToOnePlaceTwice() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        final PlaceRequest somewhereTheSame = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        activity.onStartup( somewhere ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchScreenActivity spy = spy( activity );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager, selectWorkbenchPartEvent );

        final PanelDefinition root = panelManager.getRoot();

        placeManager.goTo( somewhere, root );

        placeManager.goTo( somewhereTheSame, root );


        verify( spy, never() ).onStartup( any( PlaceRequest.class ) );
        verify( spy,
                times( 1 ) ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );

    }

    @Test
    //Reveal two different Places. Each should be launched, OnStartup and OnOpen called once.
    public void testGoToTwoDifferentPlaces() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        final PlaceRequest somewhereElse = new DefaultPlaceRequest( "SomewhereElse" );

        //The first place
        final WorkbenchScreenActivity activity1 = new MockWorkbenchScreenActivity( placeManager );
        activity1.onStartup( somewhere ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchScreenActivity spy1 = spy( activity1 );

        //The second place
        final WorkbenchScreenActivity activity2 = new MockWorkbenchScreenActivity( placeManager );
        activity2.onStartup( somewhereElse ); // normally, ActivityManager calls this before returning the activity
        final WorkbenchScreenActivity spy2 = spy( activity2 );

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
        placeManager.goTo( somewhereElse, root  );

        verify( spy1, never() ).onStartup( any( PlaceRequest.class ) );
        verify( spy1,
                times( 1 ) ).onOpen();

        verify( spy2, never() ).onStartup( any( PlaceRequest.class ) );
        verify( spy2,
                times( 1 ) ).onOpen();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );

    }

}
