/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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