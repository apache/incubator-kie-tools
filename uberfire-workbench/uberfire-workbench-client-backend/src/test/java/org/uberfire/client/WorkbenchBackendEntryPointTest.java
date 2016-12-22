package org.uberfire.client;

import org.jboss.errai.bus.client.api.BusLifecycleEvent;
import org.jboss.errai.bus.client.api.BusLifecycleListener;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.TransportError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchBackendEntryPointTest {

    @Mock
    private Logger logger;

    @Mock
    private ClientMessageBus bus;

    @Mock
    private WorkbenchServicesProxy workbenchServices;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Test
    public void testErrorDisplay() {
        final WorkbenchBackendEntryPoint workbenchBackendEntryPoint = new WorkbenchBackendEntryPoint( logger, bus, workbenchServices, errorPopupPresenter );

        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                final Object[] args = invocation.getArguments();
                final ParameterizedCommand<Boolean> obj = (ParameterizedCommand<Boolean>) args[ 0 ];
                obj.execute( false );
                return null;
            }
        } ).when( workbenchServices ).isWorkbenchOnCluster( any( ParameterizedCommand.class ) );

        workbenchBackendEntryPoint.postConstruct();
        workbenchBackendEntryPoint.init();

        final ArgumentCaptor<BusLifecycleListener> captor = ArgumentCaptor.forClass( BusLifecycleListener.class );
        verify( bus ).addLifecycleListener( captor.capture() );

        final BusLifecycleListener listener = captor.getValue();
        final TransportError error = mock( TransportError.class );
        final BusLifecycleEvent event = new BusLifecycleEvent( bus, error );
        listener.busOffline( event );

        verify( logger, times( 1 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 1 ) ).showMessage( anyString() );

        listener.busOffline( event );

        verify( logger, times( 1 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 1 ) ).showMessage( anyString() );

        listener.busOnline( event );

        verify( logger, times( 2 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 1 ) ).showMessage( anyString() );

        listener.busOffline( event );
        listener.busOffline( event );

        verify( logger, times( 3 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 2 ) ).showMessage( anyString() );
    }

    @Test
    public void testNoErrorDisplay() {
        final WorkbenchBackendEntryPoint workbenchBackendEntryPoint = new WorkbenchBackendEntryPoint( logger, bus, workbenchServices, errorPopupPresenter );

        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                final Object[] args = invocation.getArguments();
                final ParameterizedCommand<Boolean> obj = (ParameterizedCommand<Boolean>) args[ 0 ];
                obj.execute( true );
                return null;
            }
        } ).when( workbenchServices ).isWorkbenchOnCluster( any( ParameterizedCommand.class ) );

        workbenchBackendEntryPoint.postConstruct();
        workbenchBackendEntryPoint.init();

        final ArgumentCaptor<BusLifecycleListener> captor = ArgumentCaptor.forClass( BusLifecycleListener.class );
        verify( bus ).addLifecycleListener( captor.capture() );

        final BusLifecycleListener listener = captor.getValue();
        final TransportError error = mock( TransportError.class );
        final BusLifecycleEvent event = new BusLifecycleEvent( bus, error );
        listener.busOffline( event );

        verify( logger, times( 1 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 0 ) ).showMessage( anyString() );

        listener.busOffline( event );

        verify( logger, times( 1 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 0 ) ).showMessage( anyString() );

        listener.busOnline( event );

        verify( logger, times( 2 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 0 ) ).showMessage( anyString() );

        listener.busOffline( event );
        listener.busOffline( event );

        verify( logger, times( 3 ) ).error( ( anyString() ) );
        verify( errorPopupPresenter, times( 0 ) ).showMessage( anyString() );
    }

}
