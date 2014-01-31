package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests that the PanelManager keeps the underlying Perspective model consistent
 * when DecoratedWorkbenchPanel(s) and/or WorkbenchPart(s) are added to or removed from
 * the Workbench.
 */
public class PanelManagerTest extends BaseWorkbenchTest {

    @Test
    //Test Perspective model is correct following addition of one part
    public void testAddOnePartToRootPanel() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );
        when( spy.getDefaultPosition() ).thenReturn( Position.ROOT );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager, (Event<SelectPlaceEvent>) null );

        final PanelDefinition root = panelManager.getRoot();

        placeManager.goTo( somewhere, root );


        assertNotNull( root );
        assertTrue( root.isRoot() );
        assertEquals( 1,
                      root.getParts().size() );
        assertNull( root.getChild( Position.NORTH ) );
        assertNull( root.getChild( Position.SOUTH ) );
        assertNull( root.getChild( Position.EAST ) );
        assertNull( root.getChild( Position.WEST ) );

        assertEquals( somewhere,
                      getPart( root.getParts(),
                               0 ).getPlace() );
    }

    @Test
    //Test Perspective model is correct following addition of one part twice
    public void testAddOnePartToRootPanelTwice() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );
        when( spy.getDefaultPosition() ).thenReturn( Position.ROOT );

        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager = new PlaceManagerImplUnitTestWrapper( spy, panelManager, selectWorkbenchPartEvent );

        final PanelDefinition root = panelManager.getRoot();

        //Goto Place once
        placeManager.goTo( somewhere, root );


        assertNotNull( root );
        assertTrue( root.isRoot() );
        assertEquals( 1,
                      root.getParts().size() );
        assertNull( root.getChild( Position.NORTH ) );
        assertNull( root.getChild( Position.SOUTH ) );
        assertNull( root.getChild( Position.EAST ) );
        assertNull( root.getChild( Position.WEST ) );

        assertEquals( somewhere,
                      getPart( root.getParts(),
                               0 ).getPlace() );

        //Goto Place again
        placeManager.goTo( somewhere ,root );

        assertNotNull( root );
        assertTrue( root.isRoot() );
        assertEquals( 1,
                      root.getParts().size() );
        assertNull( root.getChild( Position.NORTH ) );
        assertNull( root.getChild( Position.SOUTH ) );
        assertNull( root.getChild( Position.EAST ) );
        assertNull( root.getChild( Position.WEST ) );

        assertEquals( somewhere,
                      getPart( root.getParts(),
                               0 ).getPlace() );

    }

    @Test
    //Test Perspective model is correct following addition of two parts
    public void testAddTwoPartsToRootPanel() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        final PlaceRequest elsewhere = new DefaultPlaceRequest( "Elsewhere" );

        final WorkbenchScreenActivity activity1 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy1 = spy( activity1 );
        when( spy1.getDefaultPosition() ).thenReturn( Position.ROOT );
        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy1 );
        }} );

        final WorkbenchScreenActivity activity2 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy2 = spy( activity2 );
        when( spy2.getDefaultPosition() ).thenReturn( Position.ROOT );
        when( activityManager.getActivities( elsewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy2 );
        }} );


        placeManager = new PlaceManagerImplUnitTestWrapper( spy1, panelManager, (Event<SelectPlaceEvent>) null );

        final PanelDefinition root = panelManager.getRoot();

        //Goto first Place
        placeManager.goTo( somewhere, root );


        assertNotNull( root );
        assertTrue( root.isRoot() );
        assertEquals( 1,
                      root.getParts().size() );
        assertNull( root.getChild( Position.NORTH ) );
        assertNull( root.getChild( Position.SOUTH ) );
        assertNull( root.getChild( Position.EAST ) );
        assertNull( root.getChild( Position.WEST ) );

        assertEquals( somewhere,
                      getPart( root.getParts(),
                               0 ).getPlace() );

        //Goto second Place
        placeManager.goTo( elsewhere , root);

        assertNotNull( root );
        assertTrue( root.isRoot() );
        assertEquals( 2,
                      root.getParts().size() );
        assertNull( root.getChild( Position.NORTH ) );
        assertNull( root.getChild( Position.SOUTH ) );
        assertNull( root.getChild( Position.EAST ) );
        assertNull( root.getChild( Position.WEST ) );

        final List<PlaceRequest> places =  toPlaces( root.getParts() );

        assertTrue( places.contains( somewhere ) );
        assertTrue( places.contains( elsewhere ) );

    }

    private List<PlaceRequest> toPlaces( Set<PartDefinition> parts ) {

        List<PlaceRequest> places = new ArrayList<PlaceRequest>( );
        for (PartDefinition part: parts ){
            places.add( part.getPlace() );
        }
        return places;
    }

    private PartDefinition getPart( final Set<PartDefinition> parts,
                                    final int index ) {
        final PartDefinition[] arrayParts = new PartDefinition[ parts.size() ];
        parts.toArray( arrayParts );
        return arrayParts[ index ];
    }

}
