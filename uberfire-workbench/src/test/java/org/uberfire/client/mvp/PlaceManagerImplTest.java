package org.uberfire.client.mvp;

import static org.junit.Assert.assertTrue;
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
import org.uberfire.client.workbench.Position;
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

import com.google.gwt.event.shared.EventBus;

public class PlaceManagerImplTest {

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
    public void testGoToSomeWhere() throws Exception {
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

        final WorkbenchEditorActivity activity = mock( WorkbenchEditorActivity.class );
        when( activity.getDefaultPosition() ).thenReturn( Position.ROOT );
        when( activityManager.getActivity( somewhere ) ).thenReturn( activity );

        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              selectWorkbenchPartEvent,
                                                              panelManager );
        placeManager.goTo( somewhere );

        verify( activity ).launch( any( AcceptItem.class ),
                                   eq( somewhere ),
                                   isNull( Command.class ) );

    }

    @Test
    public void testGoToNoWhere() throws Exception {
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

        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              selectWorkbenchPartEvent,
                                                              panelManager );
        placeManager.goTo( DefaultPlaceRequest.NOWHERE );

        assertTrue( "Just checking we get no NPEs",
                    true );
    }

    @Test
    public void testPlaceManagerGetInitializedToADefaultPlace() throws Exception {
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

        new PlaceManagerImpl( activityManager,
                              placeHistoryHandler,
                              selectWorkbenchPartEvent,
                              panelManager );
        verify( placeHistoryHandler ).register( any( PlaceManager.class ),
                                                any( EventBus.class ),
                                                any( PlaceRequest.class ) );
    }

    @Test
    public void testGoToPreviouslyOpenedPlace() throws Exception {
        PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

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

        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              selectWorkbenchPartEvent,
                                                              panelManager );
        
        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );
        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );
        
        placeManager.goTo( somewhere );
        
        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

        PlaceRequest somewhereSecondCall = new DefaultPlaceRequest( "Somewhere" );
        placeManager.goTo( somewhereSecondCall );
        
        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );
    }

    // TODO: Close
    // TODO: History

}
