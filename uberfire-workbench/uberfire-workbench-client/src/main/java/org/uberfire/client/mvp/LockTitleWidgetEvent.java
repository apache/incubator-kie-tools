package org.uberfire.client.mvp;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;

import com.google.gwt.user.client.ui.Image;

/**
 * Utility to create {@link ChangeTitleWidgetEvent}s in response to lock status
 * changes.
 */
public class LockTitleWidgetEvent {

    private static final Image lockImage = new Image( WorkbenchResources.INSTANCE.images().lock() );

    private LockTitleWidgetEvent() {
    };

    public static ChangeTitleWidgetEvent create( final LockTarget lockTarget,
                                                 final LockInfo lockInfo,
                                                 final User user) {

        final String lockedBy = lockInfo.lockedBy();
        if (user.getIdentifier().equals( lockedBy )) {
            lockImage.setTitle(  WorkbenchConstants.INSTANCE.lockOwnedHint() );
        }
        else {
            lockImage.setTitle(  WorkbenchConstants.INSTANCE.lockHint() + " " + lockedBy );   
        }

        return new ChangeTitleWidgetEvent( lockTarget.getPlace(),
                                           lockTarget.getTitle(),
                                           (lockInfo.isLocked()) ? lockImage : null );
    }
}