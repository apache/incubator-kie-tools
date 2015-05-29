package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.EditorLockInfo;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Default implementation of {@link EditorLockManager} using the
 * {@link VFSLockServiceProxy} for lock management.
 */
@Dependent
public class EditorLockManagerImpl implements EditorLockManager {

    private static final List<String> TAG_CLICK_LOCK_EXCLUSIONS =
            Arrays.asList( "a",
                           "select",
                           "table",
                           "tbody",
                           "tfoot",
                           "td",
                           "tr" );

    @Inject
    private VFSLockServiceProxy lockService;

    @Inject
    private javax.enterprise.event.Event<ChangeTitleWidgetEvent> changeTitleEvent;

    @Inject
    private javax.enterprise.event.Event<EditorLockInfo> editorLockInfoEvent;

    @Inject
    private javax.enterprise.event.Event<NotificationEvent> lockNotification;

    @Inject
    private User user;

    private AbstractWorkbenchEditorActivity activity;

    private LockInfo lockInfo = LockInfo.unlocked();
    private HandlerRegistration closeHandler;

    private boolean lockRequestPending;
    private boolean unlockRequestPending;

    private boolean lockSyncComplete;
    private List<Runnable> syncCompleteRunnables = new ArrayList<Runnable>();

    private Timer reloadTimer;

    @Override
    public void init( final AbstractWorkbenchEditorActivity activity ) {
        this.activity = activity;

        final ParameterizedCommand<LockInfo> command = new ParameterizedCommand<LockInfo>() {

            @Override
            public void execute( final LockInfo lockInfo ) {
                if ( !lockRequestPending && !unlockRequestPending ) {
                    updateLockInfo( lockInfo );
                }
            }
        };
        lockService.retrieveLockInfo( activity.getPath(),
                                      command );
    }

    @Override
    public void onFocus() {
        publishJsApi();
        fireEditorLockEvent();
    }
    
    @Override
    public void acquireLockOnDemand() {
        final Element element = activity.getWidget().asWidget().getElement();
        Event.sinkEvents( element,
                          Event.KEYEVENTS | Event.ONCHANGE | Event.ONCLICK );

        EventListener lockDemandListener = new EventListener() {

            @Override
            public void onBrowserEvent( Event event ) {
                if ( isLockRequired( event ) ) {
                    acquireLock();
                }
            }
        };

        Event.setEventListener( element,
                                lockDemandListener );
    }

    private void acquireLock() {
        if ( lockInfo.isLocked() ) {
            handleLockFailure();
        } 
        else if ( !lockRequestPending ) {
            lockRequestPending = true;
            final ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute( final LockResult result ) {
                    updateLockInfo( result.getLockInfo() );

                    if ( result.isSuccess() ) {
                        releaseLockOnClose();
                    } 
                    else {
                        handleLockFailure();
                    }
                    lockRequestPending = false;
                }
            };
            lockService.acquireLock( activity.getPath(),
                                     command );
        }
    }

    @Override
    public void releaseLock() {
        final Runnable releaseLock = new Runnable() {

            @Override
            public void run() {
                releaseLockInternal();
            }
        };
        if ( lockSyncComplete ) {
            releaseLock.run();
        } 
        else {
            syncCompleteRunnables.add( releaseLock );
        }
    }

    private void releaseLockInternal() {
        if ( isLockedByCurrentUser() && !unlockRequestPending ) {
            unlockRequestPending = true;

            ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute( final LockResult result ) {
                    updateLockInfo( result.getLockInfo() );

                    if ( result.isSuccess() ) {
                        if ( closeHandler != null ) {
                            closeHandler.removeHandler();
                        }
                    }

                    unlockRequestPending = false;
                }
            };
            lockService.releaseLock( activity.getPath(),
                                     command );
        }
    }

    private void releaseLockOnClose() {
        closeHandler = Window.addCloseHandler( new CloseHandler<Window>() {

            @Override
            public void onClose( CloseEvent<Window> event ) {
                releaseLock();
            }
        } );
    }

    private void handleLockFailure() {
        lockNotification.fire( new NotificationEvent( WorkbenchConstants.INSTANCE.lockedMessage( lockInfo.lockedBy() ),
                                                      NotificationEvent.NotificationType.INFO,
                                                      true,
                                                      activity.getPlace(),
                                                      20) );

        // Delay reloading slightly in case we're dealing with a flood of events
        if ( reloadTimer == null ) {
            reloadTimer = new Timer() {

                public void run() {
                    activity.onStartup( activity.getPath(),
                                        activity.getPlace() );
                }
            };
        }

        if ( !reloadTimer.isRunning() ) {
            reloadTimer.schedule( 250 );
        }
    }

    private boolean isLockedByCurrentUser() {
        return lockInfo.isLocked() && lockInfo.lockedBy().equals( user.getIdentifier() );
    }

    private boolean isLockRequired( Event event ) {
        if ( isLockedByCurrentUser() ) {
            return false;
        }

        final Element target = Element.as( event.getEventTarget() );
        final String lockAttribute = findLockAttribute( target );
        if ( lockAttribute != null && !lockAttribute.isEmpty() ) {
            return Boolean.parseBoolean( lockAttribute );
        }

        boolean eventExcluded = ( event.getTypeInt() == Event.ONCLICK &&
                TAG_CLICK_LOCK_EXCLUSIONS.contains( target.getTagName().toLowerCase() ) );

        return !eventExcluded;
    }

    private String findLockAttribute( final Element element ) {
        if ( element == null ) {
            return null;
        }

        final String lockAttribute = element.getAttribute( "data-uf-lock" );
        if ( lockAttribute != null && !lockAttribute.isEmpty() ) {
            return lockAttribute;
        }

        return findLockAttribute( element.getParentElement() );
    }

    private void updateLockInfo( @Observes LockInfo lockInfo ) {
        if ( lockInfo.getFile().equals( activity.getPath() ) ) {
            this.lockInfo = lockInfo;
            this.lockSyncComplete = true;

            if ( activity.isOpen() ) {
                changeTitleEvent.fire( LockTitleWidgetEvent.create( activity,
                                                                    lockInfo ) );
            }
            
            fireEditorLockEvent();
            
            for ( Runnable runnable : syncCompleteRunnables ) {
                runnable.run();
            }
            syncCompleteRunnables.clear();
        }
    }

    private void onResourceAdded( @Observes ResourceAddedEvent res ) {
        if ( activity != null && res.getPath().equals( activity.getPath() ) ) {
            releaseLock();
        }
    }

    private void onResourceUpdated( @Observes ResourceUpdatedEvent res ) {
        if ( activity != null && res.getPath().equals( activity.getPath() ) ) {
            activity.onStartup( activity.getPath(),
                                activity.getPlace() );
            releaseLock();
        }
    }

    private native void publishJsApi()/*-{
        var lockManager = this;
        $wnd.isLocked = function () {
            return lockManager.@org.uberfire.client.mvp.EditorLockManagerImpl::isLocked()();
        }
        $wnd.isLockedByCurrentUser = function () {
            return lockManager.@org.uberfire.client.mvp.EditorLockManagerImpl::isLockedByCurrentUser()();
        }
        $wnd.acquireLock = function () {
            lockManager.@org.uberfire.client.mvp.EditorLockManagerImpl::acquireLock()();
        }
        $wnd.releaseLock = function () {
            lockManager.@org.uberfire.client.mvp.EditorLockManagerImpl::releaseLock()();
        }
    }-*/;

    private boolean isLocked() {
        return lockInfo.isLocked();
    }
    
    private void fireEditorLockEvent() {
        Element element = activity.getWidget().asWidget().getElement();
        boolean visible = UIObject.isVisible( element ) && 
                (element.getAbsoluteLeft() > 0) && (element.getAbsoluteTop() > 0);
        
        if ( visible ) {
            editorLockInfoEvent.fire( new EditorLockInfo( lockInfo.isLocked(),
                                                          isLockedByCurrentUser() ) );
        }
    }
}