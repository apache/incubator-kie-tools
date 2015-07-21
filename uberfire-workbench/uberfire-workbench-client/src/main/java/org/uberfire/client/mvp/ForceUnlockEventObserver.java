package org.uberfire.client.mvp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.backend.vfs.impl.ForceUnlockEvent;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Observes {@link ForceUnlockEvent}s and tries to release the corresponding
 * locks.
 */
@ApplicationScoped
public class ForceUnlockEventObserver {

    @Inject
    private VFSLockServiceProxy lockService;

    @Inject
    private ErrorPopupPresenter errorPopupPresenter;

    @SuppressWarnings("unused")
    private void onForceUnlock( @Observes final ForceUnlockEvent e ) {
        final ParameterizedCommand<LockResult> cmd = new ParameterizedCommand<LockResult>() {

            @Override
            public void execute( LockResult result ) {
                if ( !result.isSuccess() && result.getLockInfo().isLocked() ) {
                    errorPopupPresenter.showMessage( "Failed to release lock for " + e.getPath()
                                                                                      .getFileName() );
                }
            }
        };
        lockService.forceReleaseLock( e.getPath(), 
                                      cmd );
    }
}
