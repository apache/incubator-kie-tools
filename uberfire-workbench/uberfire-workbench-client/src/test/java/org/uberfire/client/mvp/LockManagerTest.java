package org.uberfire.client.mvp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.mvp.LockTarget.TitleProvider;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeProvider;

@RunWith(GwtMockitoTestRunner.class)
@SuppressWarnings("unchecked")
public class LockManagerTest {

    @InjectMocks
    private LockManagerImpl lockManager;
    
    @Mock
    private LockDemandDetector lockDemandDetector;

    @Mock
    private User user;

    @Mock
    private VFSLockServiceProxy lockService;

    @Mock
    private Path path;

    @Mock
    private EventSourceMock<NotificationEvent> lockNotification;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleEvent;

    @Mock
    private EventSourceMock<UpdatedLockStatusEvent> updatedLockStatusEvent;

    @GwtMock
    private TextArea widget;

    @GwtMock
    private Event event;

    private LockTarget target;

    private int reloads = 0;

    @Before
    public void setup() throws Exception {
        mockTimer();

        GwtMockito.useProviderForType( WorkbenchResources.class,
                                       new FakeProvider<WorkbenchResources>() {

                                           @Override
                                           public WorkbenchResources getFake( Class<?> type ) {
                                               return null;
                                           }
                                       } );

        final Runnable reloadRunnable = new Runnable() {

            @Override
            public void run() {
                reloads++;
            }
        };

        final TitleProvider titleProvider = new TitleProvider() {

            @Override
            public String getTitle() {
                return "";
            }
        };

        target = new LockTarget( path,
                                 widget,
                                 new DefaultPlaceRequest( "mockPlace" ),
                                 titleProvider,
                                 reloadRunnable );

        lockManager.init( target );

        when( user.getIdentifier() ).thenReturn( "mockedUser" );
        when( lockDemandDetector.isLockRequired( any( Event.class ) ) ).thenReturn( true );
    }

    @Test
    public void updateLockInfoOnInit() {
        verify( lockService, times(1) ).retrieveLockInfo( any( Path.class ),
                                                          any( ParameterizedCommand.class ) );
    }

    @Test
    public void acquireLockOnDemand() {
        lockManager.acquireLockOnDemand();

        simulateLockDemand();

        verify( lockService, times(1) ).acquireLock( any( Path.class ),
                                                     any( ParameterizedCommand.class ) );
    }
    
    @Test
    public void acquireLockDoesNotHitServerIfLocked() {
        lockManager.acquireLockOnDemand();

        simulateLockFailure();
        simulateLockDemand();
        verify( lockService, times(1) ).acquireLock( any( Path.class ),
                                                     any( ParameterizedCommand.class ) );
        
        simulateLockDemand();
        verify( lockService, times(1) ).acquireLock( any( Path.class ),
                                                     any( ParameterizedCommand.class ) );
    }

    @Test
    public void notifyLockFailure() throws Exception {
        lockManager.acquireLockOnDemand();

        simulateLockFailure();
        simulateLockDemand();

        verify( lockNotification, times(1) ).fire( any( NotificationEvent.class ) );
    }
    
    @Test
    public void notifyLockError() throws Exception {
        lockManager.acquireLockOnDemand();

        simulateLockError();
        simulateLockDemand();

        verify( lockNotification, times(1) ).fire( any( NotificationEvent.class ) );
    }

    @Test
    public void reloadOnLockFailure() throws Exception {
        lockManager.acquireLockOnDemand();

        assertEquals( 0, reloads );

        simulateLockFailure();
        simulateLockDemand();

        assertEquals( 1, reloads );
    }

    @Test
    public void updateTitleOnFocus() {
        verify( changeTitleEvent, never() ).fire( any( ChangeTitleWidgetEvent.class ) );
        lockManager.onFocus();
        verify( changeTitleEvent, times(1) ).fire( any( ChangeTitleWidgetEvent.class ) );
    }

    @Test
    public void releaseLockOnSave() {
        lockManager.acquireLockOnDemand();
        
        simulateLockSuccess();
        simulateLockDemand();
        
        lockManager.onSaveInProgress( new SaveInProgressEvent(path) );
        
        verify( lockService, times(1) ).releaseLock( any( Path.class ),
                                                     any( ParameterizedCommand.class ) );
    }
    
    @Test
    public void releaseLockOnUpdate() {
        lockManager.acquireLockOnDemand();
        simulateLockSuccess();
        simulateLockDemand();
        
        lockManager.onResourceUpdated( new ResourceUpdatedEvent( path,
                                                                 "",
                                                                 new SessionInfoImpl( user ) ) );
        
        verify( lockService, times(1) ).releaseLock( any( Path.class ),
                                                     any( ParameterizedCommand.class ) );
    }
    
    @Test
    public void reloadEditorOnUpdateFromDifferentUser() {
        lockManager.acquireLockOnDemand();
        simulateLockSuccess();
        simulateLockDemand();
        
        lockManager.onResourceUpdated( new ResourceUpdatedEvent( path,
                                                                 "",
                                                                 new SessionInfoImpl( user ) ) );
        
        assertEquals(0, reloads);
        
        lockManager.onResourceUpdated( new ResourceUpdatedEvent( path,
                                                                 "",
                                                                 new SessionInfoImpl( new UserImpl ("differentUser") ) ) );
        
        assertEquals(1, reloads);
    }
    
    @Test
    public void releaseOwnedLockOnly() {
        lockManager.acquireLockOnDemand();
        simulateLockFailure();
        simulateLockDemand();
        
        lockManager.onResourceUpdated( new ResourceUpdatedEvent( path,
                                                                 "",
                                                                 new SessionInfoImpl( user ) ) );
        
        verify( lockService, never() ).releaseLock( any( Path.class ),
                                                    any( ParameterizedCommand.class ) );
    }

    @Test
    public void requestAcquireLockNoMoreThanOnce() {
        lockManager.acquireLockOnDemand();
        
        simulateLockNoResponse();
        simulateLockDemand();
        simulateLockDemand();
        
        verify( lockService, times(1) ).acquireLock( any( Path.class ),
                                                     any( ParameterizedCommand.class ) );
    }
    
    private void simulateLockDemand() {
        EventListener listener = lockManager.acquireLockOnDemand( widget.getElement() );
        listener.onBrowserEvent( event );
    }

    private void simulateLockFailure() {
        doAnswer( new Answer<Object>() {

            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                final Object[] args = invocation.getArguments();
                LockInfo lockInfo = new LockInfo( true,
                                                  "somebody",
                                                  path );
                final LockResult failed = LockResult.failed( lockInfo );
                ((ParameterizedCommand<LockResult>) args[1]).execute( failed );
                return null;
            }
        } ).when( lockService ).acquireLock( any( Path.class ),
                                             any( ParameterizedCommand.class ) );
    }
    
    private void simulateLockSuccess() {
        doAnswer( new Answer<Object>() {

            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                final Object[] args = invocation.getArguments();
                final LockResult acquired = LockResult.acquired( path, user.getIdentifier() );
                ((ParameterizedCommand<LockResult>) args[1]).execute( acquired );
                return null;
            }
        } ).when( lockService ).acquireLock( any( Path.class ),
                                             any( ParameterizedCommand.class ) );
    }
    
    private void simulateLockError() {
        doAnswer( new Answer<Object>() {

            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                final Object[] args = invocation.getArguments();
                final LockResult acquired = LockResult.error();
                ((ParameterizedCommand<LockResult>) args[1]).execute( acquired );
                return null;
            }
        } ).when( lockService ).acquireLock( any( Path.class ),
                                             any( ParameterizedCommand.class ) );
    }
    
    private void simulateLockNoResponse() {
        doAnswer( new Answer<Object>() {

            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                return null;
            }
        } ).when( lockService ).acquireLock( any( Path.class ),
                                             any( ParameterizedCommand.class ) );
    }

    private void mockTimer() throws Exception {
        final Timer mockTimer = new Timer() {

            @Override
            public void run() {
                target.getReloadRunnable().run();
            }

            @Override
            public void schedule( int delayMillis ) {
                run();
            }
        };
        final Field reloadTimer = LockManagerImpl.class.getDeclaredField( "reloadTimer" );
        reloadTimer.setAccessible( true );
        reloadTimer.set( lockManager,
                         mockTimer );

    }
    
}