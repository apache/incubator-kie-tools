package org.uberfire.client.mvp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Test;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * Tests that the PanelManager keeps the underlying Perspective model consistent
 * when WorkbenchPanel(s) and/or WorkbenchPart(s) are added to or removed from
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

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );

        final PanelDefinition root = panelManager.getRoot();
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

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        //Goto Place once
        placeManager.goTo( somewhere );

        final PanelDefinition root = panelManager.getRoot();
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
        placeManager.goTo( somewhere );

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
        when( activityManager.getActivity( somewhere ) ).thenReturn( spy1 );

        final WorkbenchScreenActivity activity2 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy2 = spy( activity2 );
        when( spy2.getDefaultPosition() ).thenReturn( Position.ROOT );
        when( activityManager.getActivity( elsewhere ) ).thenReturn( spy2 );

        //Goto first Place
        placeManager.goTo( somewhere );

        final PanelDefinition root = panelManager.getRoot();
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
        placeManager.goTo( elsewhere );

        assertNotNull( root );
        assertTrue( root.isRoot() );
        assertEquals( 2,
                      root.getParts().size() );
        assertNull( root.getChild( Position.NORTH ) );
        assertNull( root.getChild( Position.SOUTH ) );
        assertNull( root.getChild( Position.EAST ) );
        assertNull( root.getChild( Position.WEST ) );

        assertEquals( somewhere,
                      getPart( root.getParts(),
                               0 ).getPlace() );
        assertEquals( elsewhere,
                      getPart( root.getParts(),
                               1 ).getPlace() );
    }

    private PartDefinition getPart(final Set<PartDefinition> parts,
                                   final int index) {
        final PartDefinition[] arrayParts = new PartDefinition[parts.size()];
        parts.toArray( arrayParts );
        return arrayParts[index];
    }

}
