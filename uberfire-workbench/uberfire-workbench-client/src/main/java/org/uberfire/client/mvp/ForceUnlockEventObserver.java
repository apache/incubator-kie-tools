/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
