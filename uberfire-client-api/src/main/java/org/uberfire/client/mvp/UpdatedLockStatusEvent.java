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

/**
 * Client-local event to inform UI components of a lock status change. 
 */
public class UpdatedLockStatusEvent {

    private final boolean locked;
    private final boolean lockedByCurrentUser;

    public UpdatedLockStatusEvent( boolean locked, boolean lockedByCurrentUser ) {
        this.locked = locked;
        this.lockedByCurrentUser = lockedByCurrentUser;
    }

    public boolean isLocked() {
        return locked;
    }
    
    public boolean isLockedByCurrentUser() {
        return lockedByCurrentUser;
    }

}
