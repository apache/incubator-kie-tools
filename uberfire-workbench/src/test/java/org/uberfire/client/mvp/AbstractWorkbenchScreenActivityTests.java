package org.uberfire.client.mvp;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.WorkbenchPartPresenter;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * Initial (poor coverage) integration tests for PlaceManager, PanelManager and
 * life-cycle events. There remains a lot more work to do in this class.
 */
public class AbstractWorkbenchScreenActivityTests {

    private PlaceHistoryHandler                  placeHistoryHandler;
    private ActivityManager                      activityManager;
    private BeanFactory                          factory;
    private Event<WorkbenchPanelOnFocusEvent>    workbenchPanelOnFocusEvent;
    private Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;
    private Event<WorkbenchPartOnFocusEvent>     workbenchPartOnFocusEvent;
    private Event<WorkbenchPartLostFocusEvent>   workbenchPartLostFocusEvent;
    private Event<SelectWorkbenchPartEvent>      selectWorkbenchPartEvent;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );
        factory = mock( BeanFactory.class );
        workbenchPanelOnFocusEvent = mock( Event.class );
        workbenchPartBeforeCloseEvent = mock( Event.class );
        workbenchPartOnFocusEvent = mock( Event.class );
        workbenchPartLostFocusEvent = mock( Event.class );
        selectWorkbenchPartEvent = mock( Event.class );
    }

    @Test
    //Reveal a Place once. It should be launched, OnStart and OnReveal called once.
    public void testGoToOnePlace() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final PanelManager panelManager = new PanelManager( factory,
                                                            workbenchPanelOnFocusEvent,
                                                            workbenchPartBeforeCloseEvent,
                                                            workbenchPartOnFocusEvent,
                                                            workbenchPartLostFocusEvent,
                                                            selectWorkbenchPartEvent );
        final PanelDefinition root = new PanelDefinitionImpl( true );
        final WorkbenchPanelPresenter rootPresenter = mock( WorkbenchPanelPresenter.class );
        final WorkbenchPartPresenter partPresenter = mock( WorkbenchPartPresenter.class );

        when( factory.newWorkbenchPanel( root ) ).thenReturn( rootPresenter );
        when( factory.newWorkbenchPart( any( String.class ),
                                        any( PartDefinition.class ) ) ).thenReturn( partPresenter );

        panelManager.setRoot( root );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    selectWorkbenchPartEvent,
                                                                    panelManager );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );

        verify( spy ).launch( any( AcceptItem.class ),
                              eq( somewhere ),
                              isNull( Command.class ) );
        verify( spy ).onStart( eq( somewhere ) );
        verify( spy ).onReveal();

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectWorkbenchPartEvent.class ) );
    }

    @Test
    //Reveal the same Place twice. It should be launched, OnStart and OnReveal called once.
    public void testGoToOnePlaceTwice() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        final PlaceRequest somewhereTheSame = new DefaultPlaceRequest( "Somewhere" );

        final PanelManager panelManager = new PanelManager( factory,
                                                            workbenchPanelOnFocusEvent,
                                                            workbenchPartBeforeCloseEvent,
                                                            workbenchPartOnFocusEvent,
                                                            workbenchPartLostFocusEvent,
                                                            selectWorkbenchPartEvent );
        final PanelDefinition root = new PanelDefinitionImpl( true );
        final WorkbenchPanelPresenter rootPresenter = mock( WorkbenchPanelPresenter.class );
        final WorkbenchPartPresenter partPresenter = mock( WorkbenchPartPresenter.class );

        when( factory.newWorkbenchPanel( root ) ).thenReturn( rootPresenter );
        when( factory.newWorkbenchPart( any( String.class ),
                                        any( PartDefinition.class ) ) ).thenReturn( partPresenter );

        panelManager.setRoot( root );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    selectWorkbenchPartEvent,
                                                                    panelManager );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereTheSame );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( spy,
                times( 1 ) ).onStart( eq( somewhere ) );
        verify( spy,
                times( 1 ) ).onReveal();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

    }

    @Test
    //Reveal two different Places. Each should be launched, OnStart and OnReveal called once.
    public void testGoToTwoDifferentPlaces() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
        final PlaceRequest somewhereElse = new DefaultPlaceRequest( "SomewhereElse" );

        final PanelManager panelManager = new PanelManager( factory,
                                                            workbenchPanelOnFocusEvent,
                                                            workbenchPartBeforeCloseEvent,
                                                            workbenchPartOnFocusEvent,
                                                            workbenchPartLostFocusEvent,
                                                            selectWorkbenchPartEvent );
        final PanelDefinition root = new PanelDefinitionImpl( true );
        final WorkbenchPanelPresenter rootPresenter = mock( WorkbenchPanelPresenter.class );
        final WorkbenchPartPresenter partPresenter = mock( WorkbenchPartPresenter.class );

        when( factory.newWorkbenchPanel( root ) ).thenReturn( rootPresenter );
        when( factory.newWorkbenchPart( any( String.class ),
                                        any( PartDefinition.class ) ) ).thenReturn( partPresenter );

        panelManager.setRoot( root );
        
        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    selectWorkbenchPartEvent,
                                                                    panelManager );

        //The first place
        final WorkbenchScreenActivity activity1 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy1 = spy( activity1 );

        //The second place
        final WorkbenchScreenActivity activity2 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy2 = spy( activity2 );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy1 );
        when( activityManager.getActivity( somewhereElse ) ).thenReturn( spy2 );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereElse );

        verify( spy1,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( spy1,
                times( 1 ) ).onStart( eq( somewhere ) );
        verify( spy1,
                times( 1 ) ).onReveal();

        verify( spy2,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhereElse ),
                                     isNull( Command.class ) );
        verify( spy2,
                times( 1 ) ).onStart( eq( somewhereElse ) );
        verify( spy2,
                times( 1 ) ).onReveal();

        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

    }

}
